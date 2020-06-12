package com.example.coursepro

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coursepro.adapters.ItemAdapter
import com.example.coursepro.lists.ItemToDo
import com.example.coursepro.lists.ListeToDo
import com.example.coursepro.lists.ProfilListeToDo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import github.com.vikramezhil.dks.speech.Dks
import github.com.vikramezhil.dks.speech.DksListener
import kotlinx.android.synthetic.main.activity_show_list.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ShowListActivity : GenericActivity(), ItemAdapter.ActionListener {
    private var model = courseModel
    private var adapter : ItemAdapter? = null
    private var refBtnOK: Button? = null
    private var refListInput: EditText? = null
    private var prefs : SharedPreferences?= null
    private var profilListeToDo : ProfilListeToDo? = null
    private var listeToDo : ListeToDo? = null
    private var filename : String? = null
    private lateinit var dks: Dks



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

        /*
        Declarations
         */
        refBtnOK = findViewById(R.id.OKBtnShowList)
        refListInput = findViewById(R.id.listInputShowList)
        adapter = newAdapter()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        filename = "players"


        /*
        Get info from ChoixListActivity
         */
        val bundle = this.intent.extras
        listeToDo = model.currentList
        profilListeToDo = model.currentUser

        /*
        RecyclerView
         */
        val list = listOfItem

        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)

        // getPlayerList()
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
        // Update itemToDo, listeToDo and profilListeToDo
        itemToDo.fait = value
    }

    /*
    OK Button listener
    */
    fun onAddListButtonClick(v: View) {
        val descItem = refListInput!!.text.toString()
        createItem(descItem)
    }

    override fun onStop() {
        super.onStop()
        dks.closeSpeechOperations()
    }

    private fun createItem(descItem : String) {
        //val listPlayer = getPlayerList()

        // Check if an item with the same description doesn't already exists
        try {
            model.addItem(descItem)
        } catch (e : Exception) {
            Toast.makeText(applicationContext, R.string.itemAlreadyExist, Toast.LENGTH_LONG).show()
            return
        }
        try {
            model.saveUsersData()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Failed to save, error : $e.message", Toast.LENGTH_LONG).show()
        }
        val dataSet: List<ItemToDo>? = model.currentList?.lesItems
        adapter!!.setData(dataSet)
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