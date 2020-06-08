package com.example.coursepro

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursepro.adapters.ItemAdapter
import com.example.coursepro.lists.ItemToDo
import com.example.coursepro.lists.ListeToDo
import com.example.coursepro.lists.ProfilListeToDo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class ShowListActivity : GenericActivity(), ItemAdapter.ActionListener, View.OnClickListener {

    private var adapter : ItemAdapter? = null
    private var refBtnOK: Button? = null
    private var refListInput: EditText? = null
    private var prefs : SharedPreferences?= null
    private var profilListeToDo : ProfilListeToDo? = null
    private var listeToDo : ListeToDo? = null
    private var filename : String? = null



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

        refBtnOK?.let { btn -> btn.setOnClickListener(this) }


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
        val file = File(filesDir, filename)
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

                val listPlayer = getPlayerList()

                val descItem = refListInput!!.text.toString()
                // Check if an item with the same description doesn't already exists
                if (!listeToDo!!.rechercherItem(descItem)) {
                    // Update listeToDo and profilListeToDo
                    val newItem : ItemToDo = ItemToDo(descItem)
                    profilListeToDo!!.ajoutItem(listeToDo, newItem)

                    // Serialize the new list of players
                    val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                    listPlayer.add(profilListeToDo!!)
                    val jsonProfiles = gsonPretty.toJson(listPlayer)

                    /// Update the file
                    val file = File(filesDir, filename)
                    openFileOutput(filename, Context.MODE_PRIVATE).use {
                        it.write(jsonProfiles.toByteArray())
                    }

                    val dataSet : List<ItemToDo>? = listeToDo!!.lesItems
                    adapter!!.setData(dataSet)
                }
                else {
                    Toast.makeText(applicationContext, R.string.itemAlreadyExist, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Get the list of player's lists and remove the current player
    private fun getPlayerList() : MutableList<ProfilListeToDo> {
        val file = File(filesDir, filename)
        var jsonProfiles : String = file.readText()
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