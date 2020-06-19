package com.example.coursepro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.coursepro.adapters.ItemAdapter
import com.example.coursepro.lists.ItemToDo
import com.example.coursepro.lists.ListeToDo
import com.example.coursepro.lists.ProfilListeToDo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import github.com.vikramezhil.dks.speech.Dks
import github.com.vikramezhil.dks.speech.DksListener
import kotlinx.coroutines.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class ShowListActivity : GenericActivity(), ItemAdapter.ActionListener, View.OnClickListener {

    private var adapter : ItemAdapter? = null
    private var refBtnOK: Button? = null
    private var refBtnBarcode: Button? = null
    private var refListInput: EditText? = null
    private var prefs : SharedPreferences?= null
    private var profilListeToDo : ProfilListeToDo? = null
    private var listeToDo : ListeToDo? = null
    private var filename : String? = null
    private lateinit var dks: Dks

    val activityScope = CoroutineScope(
        SupervisorJob()
                + Dispatchers.Main
                + CoroutineExceptionHandler { _, throwable ->
            Log.e("PMR", "Coroutine exception : ", throwable);
        }
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

        /*
        Declarations
         */
        refBtnOK = findViewById(R.id.OKBtnShowList)
        refBtnBarcode = findViewById(R.id.BarcodeBtnShowList)
        refListInput = findViewById(R.id.listInputShowList)
        adapter = newAdapter()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        filename = "players"

        refBtnOK?.setOnClickListener(this)
        refBtnBarcode?.setOnClickListener(this)


        /*
        Get info from ChoixListActivity
         */
        val bundle = this.intent.extras
        listeToDo = bundle!!.getSerializable("liste") as ListeToDo
        profilListeToDo = bundle.getSerializable("profilListe") as ProfilListeToDo


        /*
        RecyclerView
         */
        val list : RecyclerView = findViewById(R.id.listOfItem)

        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)

        getPlayerList()
        val dataSet : List<ItemToDo>? = listeToDo!!.lesItems
        adapter!!.setData(dataSet)


        /*
        Speech recognition
         */
        dks = Dks(application, supportFragmentManager, object: DksListener {
            override fun onDksLiveSpeechResult(liveSpeechResult: String) {
                //Log.d("DKS", "Speech result - $liveSpeechResult")
            }

            override fun onDksFinalSpeechResult(speechResult: String) {
                Log.d("DKS", "Final speech result - $speechResult")
                //checkItemVoice(speechResult)
                addItemVoice(speechResult)
                deleteItemVoice(speechResult)
            }

            override fun onDksLiveSpeechFrequency(frequency: Float) {
                //Log.d("DKS", "frequency - $frequency")
            }

            override fun onDksLanguagesAvailable(defaultLanguage: String?, supportedLanguages: ArrayList<String>?) {
                //Log.d("DKS", "defaultLanguage - $defaultLanguage")
                //Log.d("DKS", "supportedLanguages - $supportedLanguages")
            }

            override fun onDksSpeechError(errMsg: String) {
                Toast.makeText(applicationContext, "Erreur avec la reconnaissance vocal : $errMsg", Toast.LENGTH_LONG).show()
                Log.d("DKS", "errMsg - $errMsg")
            }
        })

        dks.startSpeechRecognition()
    }

    /*
    Voice listener : check / uncheck an item if the works spoken match one of the item
     */
    private fun checkItemVoice(sentence : String) {

        val wordsSpoken = sentence.toLowerCase(Locale.getDefault()).split(" ")

        // Iterate over all item to check if one is spoken
        for (item in listeToDo!!.lesItems) {
            var check = true
            val itemDescs = item.description.toLowerCase(Locale.getDefault()).split(" ")

            // Check if all words of the item is spoken
            for (itemDesc in itemDescs) {
                if (!wordsSpoken.contains(itemDesc)) {
                    check = false
                }
            }
            if (check) {
                onItemClicked(item, !item.fait)
                val dataSet : List<ItemToDo>? = listeToDo!!.lesItems
                adapter!!.setData(dataSet)
                return
            }
        }
    }

    /*
    Voice listener : create an item if the word "ajouter" is spoken
     */
    private fun addItemVoice(sentence: String) {

        val wordsSpoken = sentence.toLowerCase(Locale.getDefault()).split(" ")

        // Detect the word "ajouter" and add an item with the words after "ajouter"
        for ((index, word) in wordsSpoken.withIndex()) {
            if (word == "ajouter") {
                var item = ""
                val nbWords = wordsSpoken.size
                var i = index + 1

                while (i < nbWords) {
                    item += wordsSpoken[i] + " "
                    i++
                }

                if (item != "") {
                    item = item.dropLast(1)
                    createItem(item)
                }
                return
            }
        }
    }

    /*
    Voice listener : delete an item if the word "supprimer" is spoken
     */
    private fun deleteItemVoice(sentence: String) {

        val wordsSpoken = sentence.toLowerCase(Locale.getDefault()).split(" ")

        for ((index, word) in wordsSpoken.withIndex()) {
            if (word == "supprimer") {
                val itemSpoken = mutableListOf<String>()
                val nbWords = wordsSpoken.size
                var i = index

                // Get all the words after "supprimer"
                while (i < nbWords) {
                    itemSpoken.add(wordsSpoken[i])
                    i++
                }

                if (itemSpoken.size > 0) {

                    // Iterate over all item to check if one match with the one spoken
                    for (item in listeToDo!!.lesItems) {
                        var check = true
                        val itemDescs = item.description.toLowerCase(Locale.getDefault()).split(" ")

                        // Check if all words of the item is spoken
                        for (itemDesc in itemDescs) {
                            if (!itemSpoken.contains(itemDesc)) {
                                check = false
                            }
                        }
                        if (check) {
                            deleteItem(item)
                            return
                        }
                    }

                }
                return
            }
        }
    }



    private fun newAdapter() : ItemAdapter = ItemAdapter(actionListener = this)

    /*
    Item listener
     */
    override fun onItemClicked(itemToDo: ItemToDo, value : Boolean) {

        val listPlayer = getPlayerList()

        // Update itemToDo, listeToDo and profilListeToDo
        itemToDo.fait = value
        profilListeToDo!!.updateItem(listeToDo, itemToDo)

        // Serialize the new list of players
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        listPlayer.add(profilListeToDo!!)
        val jsonProfiles = gsonPretty.toJson(listPlayer)

        // Update the file
        openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(jsonProfiles.toByteArray())
        }
    }

    /*
    OK Button listener
    */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.OKBtnShowList -> {
                val descItem = refListInput!!.text.toString()
                createItem(descItem)
            }
            R.id.BarcodeBtnShowList -> {
                val intent = Intent(this, BarcodeActivity::class.java)
                val requestCode = 1
                startActivityForResult(intent, requestCode)
            }
        }
    }

    private fun convertInputStreamToString(inputStream: InputStream): String {
        val bufferedReader:BufferedReader? = BufferedReader(InputStreamReader(inputStream))

        var line:String? = bufferedReader?.readLine()
        var result:String = ""

        while (line != null) {
            result += line
            line = bufferedReader?.readLine()
        }

        inputStream.close()
        return result
    }

    private suspend fun httpGet(myURL: String?): String? {

        val result = withContext(Dispatchers.IO) {
            val inputStream: InputStream


            // create URL
            val url: URL = URL(myURL)

            // create HttpURLConnection
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

            // make GET request to the given URL
            conn.connect()

            // receive response as inputStream
            inputStream = conn.inputStream

            // convert inputstream to string
            if (inputStream != null)
                convertInputStreamToString(inputStream)
            else
                "Did not work!"


        }
        return result
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            val barcode: String? = data.getStringExtra("barcode")


            // Get product info from Google
            activityScope.launch {
                runCatching {
                    httpGet("https://search.lilo.org/results.php?q=" + barcode)
                }.fold(
                    onSuccess = {
                        Log.i("PMR", it!!.contains("pains aux lait").toString())
                    },
                    onFailure = {
                        Log.e("PMR", "erreur", it)
                    }
                )
            }
            // Get product info from API
            /*
            activityScope.launch {

                runCatching {
                    DataProvider.getProductInfo(barcode!!)
                }.fold(
                    onSuccess = {
                        checkItemVoice(it)
                    },
                    onFailure = {
                        Toast.makeText(applicationContext, "Le produit n'a pas été trouvé", Toast.LENGTH_LONG).show()
                    }
                )
            }
            */
        }
    }

    override fun onStop() {
        super.onStop()
        dks.closeSpeechOperations()
    }

    private fun createItem(descItem : String) {
        val listPlayer = getPlayerList()

        // Check if an item with the same description doesn't already exists
        if (!listeToDo!!.rechercherItem(descItem)) {

            // Update listeToDo and profilListeToDo
            val newItem = ItemToDo(descItem)
            newItem.setSectionName()
            if (newItem.headerName != "") {
                listeToDo!!.addSection(newItem.headerName)
            }
            profilListeToDo!!.ajoutItem(listeToDo, newItem)

            // Serialize the new list of players
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()
            listPlayer.add(profilListeToDo!!)
            val jsonProfiles = gsonPretty.toJson(listPlayer)

            /// Update the file
            openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(jsonProfiles.toByteArray())
            }

            val dataSet: List<ItemToDo>? = listeToDo!!.lesItems
            adapter!!.setData(dataSet)
        } else {
            Toast.makeText(applicationContext, R.string.itemAlreadyExist, Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteItem(item : ItemToDo) {
        val listPlayer = getPlayerList()

        // Update listeToDo and profilListeToDo
        profilListeToDo!!.deleteItem(listeToDo, item)

        // Serialize the new list of players
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        listPlayer.add(profilListeToDo!!)
        val jsonProfiles = gsonPretty.toJson(listPlayer)

        /// Update the file
        openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(jsonProfiles.toByteArray())
        }

        val dataSet: List<ItemToDo>? = listeToDo!!.lesItems
        adapter!!.setData(dataSet)

    }


    // Get the list of player's lists and remove the current player
    private fun getPlayerList() : MutableList<ProfilListeToDo> {
        val file = File(filesDir, filename)
        val jsonProfiles : String = file.readText()
        val gson = Gson()
        val listPlayerType = object : TypeToken<List<ProfilListeToDo>>() {}.type
        var listPlayer : MutableList<ProfilListeToDo>? = gson.fromJson(jsonProfiles, listPlayerType)
        if (listPlayer == null) {
            listPlayer = mutableListOf()
        }
        for (player : ProfilListeToDo in listPlayer) {
            if (player.login == profilListeToDo!!.login) {
                listPlayer.remove(player)
            }
        }
        return listPlayer
    }
}