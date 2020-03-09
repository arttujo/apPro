package com.approteam.appro.fragments

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.graphics.drawable.ClipDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.R
import com.approteam.appro.adapters.CreateApproAdapter
import com.approteam.appro.data_models.Bar
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson
import kotlinx.android.synthetic.main.createappro_bars_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.File
import java.net.URL

class CreateApproBarsFragment(ctx: Context) : Fragment(){

    private val c = ctx
    private lateinit var allBars: List<Bar>



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        return inflater.inflate(R.layout.createappro_bars_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createApproBarsRV.layoutManager = LinearLayoutManager(c)
        val itemDecor = DividerItemDecoration(c, ClipDrawable.HORIZONTAL)
        createApproBarsRV.addItemDecoration(itemDecor)
        doAsync {
            val json = URL("http://Foxer153.asuscomm.com:3001/bars").readText()
            val bars = Gson().fromJson(json,Array<Bar>::class.java).toList()
            uiThread {
                createApproBarsProgressBar.visibility = View.INVISIBLE
                createApproBarsRV.adapter = CreateApproAdapter(bars,c){}
                allBars=bars
                Log.d("DBG",bars.toString())
            }
        }
        createApproButton.setOnClickListener {
            buildAlert(c,allBars)
        }

        super.onViewCreated(view, savedInstanceState)
    }
    //Builds the list of selected bars
    private fun buildSelectedBarsList(bars: List<Bar>):List<Bar>{
        val selectedBars: MutableList<Bar> = ArrayList()
        bars.forEach {
            when(it.isSelected){
                true -> selectedBars.add(it)
                else -> Log.d("DBG","${it.name} wasn't added")
            }
        }
        return selectedBars
    }
    //Gets the real file path of the file. Used to get in fileupload
    private fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } catch (e: Exception) {
            Log.e("DBG", "getRealPathFromURI Exception : $e")
            ""
        } finally {
            cursor?.close()
        }
    }

    //Main function for appro creation. Also uses appro helper so that the code isn't too long
    //This function first uploads the appro image to the server then waits for the response. Response contains the uri for the uploaded
    //Imaage and is then passed to the appro helper function
    private fun createAppro(selectedBars: List<Bar>){
        val image = arguments?.getString("IMAGEURI")
        val barIds: MutableList<Int> = ArrayList()
        selectedBars.forEach { barIds.add(it.id!!)}
        val uri = Uri.parse(image)
        val real = getRealPathFromURI(c,uri)
        Log.d("DBG",real!!)
        doAsync {
            Fuel.upload("http://foxer153.asuscomm.com:3001/upload",Method.POST)
                .add(FileDataPart(File(real),name = "photo"))
                .response { result ->
                    val (bytes,error) = result
                    if(bytes!=null){
                        Log.d("DBG", String(bytes))
                        val json = JSONObject(String(bytes))
                        val uriFromJson = json.getString("uri")
                        Log.d("DBG", uriFromJson)
                        approHelper(uriFromJson,barIds)

                    } else {
                        Log.d("DBG", error.toString())
                    }
                }
        }
    }
    //Finishes up appro creation by sending all the rest of the data to the backend
    private fun approHelper(imageUri: String,bars:MutableList<Int>){
        val name = arguments?.getString("NAME")
        val desc = arguments?.getString("DESC")
        val price = arguments?.getDouble("PRICE")
        val location = arguments?.getString("LOC")
        val date = arguments?.getString("DATE")
        val time = arguments?.getString("TIME")
        val homef = HomeFragment(c)
        doAsync {
            Fuel.post("http://foxer153.asuscomm.com:3001/createAppro")
                .jsonBody("{\"name\":\"${name}\", \"desc\":\"${desc}\", \"price\":$price, \"location\":\"${location}\", \"image\":\"${imageUri}\",\"time\":\"${time}\", \"date\":\"${date}\",\"bars\": $bars  }")
                .response { result ->
                    val (bytes, error) = result
                    if (bytes != null) {
                        val responseJson = JSONObject(String(bytes))
                        val response = responseJson.getString("response")
                        val exists = responseJson.getBoolean("exists")
                        Log.d("DBG", response)
                        Log.d("DBG", exists.toString())
                        uiThread {
                            if (!exists) {
                                makeToast(getString(R.string.approCreated))
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.container, homef)?.commit()
                            } else {
                                makeToast(getString(R.string.approNotCreated))
                            }
                        }
                    } else {
                        Log.d("DBG", error.toString())
                    }
                }
        }
    }

    //Creates a toast with the given text
    private fun makeToast(text: String){
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, text,duration)
        toast.show()
    }

    //Builds the alert which confirms that the user wants to create the appro with the selected bars
    private fun buildAlert(ctx: Context,bars: List<Bar>){
        val builder = AlertDialog.Builder(ctx)
        builder.setTitle(R.string.create)
        val selectedBars = buildSelectedBarsList(bars)
        val barString = arrayListOf<String>()
        selectedBars.forEach { barString.add(it.name!!)}
        val formattedArray = barString.toString().replace("[","").replace("]","")
        Log.d("DBG","Bar String: $formattedArray")
        builder.setMessage(getString(R.string.aboutToCreate) + " " +  formattedArray)
        builder.setPositiveButton("Ok"){ _, _ -> createAppro(selectedBars) }
        builder.setNegativeButton(R.string.cancel){_, _ -> Log.d("DBG","Cancelled creation") }
        val alert: AlertDialog = builder.create()
        alert.setCancelable(true)
        alert.show()
    }
}