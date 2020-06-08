package com.example.coursepro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursepro.adapters.ListeAdapter
import com.example.coursepro.lists.ListeToDo
import com.example.coursepro.lists.ProfilListeToDo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class ChoixListActivity : GenericActivity(), ListeAdapter.ActionListener, View.OnClickListener {

    private var adapter : ListeAdapter? = null
    private var refBtnOK: Button? = null
    private var refListInput: EditText? = null
    private var prefs : SharedPreferences? = null
    private var pseudo : String? = null
    private var profilListeToDo : ProfilListeToDo? = null
    private var filename : String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)

        /*
        Declarations
         */
        refBtnOK = findViewById(R.id.OKBtnChoixList)
        refListInput = findViewById(R.id.listInputChoixList)
        adapter = newAdapter()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        filename = "players"

        refBtnOK!!.setOnClickListener(this)


        /*
        Get the pseudo from MainActivity
         */
        val bundle = this.intent.extras
        pseudo = bundle!!.getString("pseudo")


        /*
        RecyclerView
         */
        setRecyclerView()
    }

    // Used to update the list when coming from ShowListActivity
    override fun onResume() {
        super.onResume()

        /*
        RecyclerView
         */
        setRecyclerView()

    }


    private fun newAdapter() : ListeAdapter = ListeAdapter(actionListener = this)


    private fun setRecyclerView() {
        val list : RecyclerView = findViewById(R.id.listOfList)

        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)

        getPlayerList()
        val dataSet : List<ListeToDo>? = profilListeToDo?.mesListeToDo
        adapter!!.setData(dataSet)
    }


    /*
    Item listener
     */
    override fun onItemClicked(listeToDo: ListeToDo) {
        val bundle = Bundle()
        bundle.putSerializable("liste", listeToDo)
        bundle.putSerializable("profilListe", profilListeToDo)

        val intent = Intent(this, ShowListActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    /*
    OK Button listener
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.OKBtnChoixList -> {

                val listPlayer : MutableList<ProfilListeToDo> = getPlayerList()

                val title = refListInput!!.text.toString()
                // Check if a list with the same title doesn't already exists
                if (!profilListeToDo!!.listAlreadyExists(title)) {
                    // Add the new list
                    profilListeToDo!!.ajouteListe(ListeToDo(title))
                    // Update the json list of profiles list
                    listPlayer.add(profilListeToDo!!)

                    // Update the file
                    val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                    val jsonProfiles = gsonPretty.toJson(listPlayer)
                    val file = File(filesDir, filename)
                    openFileOutput(filename, Context.MODE_PRIVATE).use {
                        it.write(jsonProfiles.toByteArray())
                    }

                    // Update display
                    val dataSet : List<ListeToDo>? = profilListeToDo?.mesListeToDo
                    adapter!!.setData(dataSet)
                }
                else {
                    Toast.makeText(applicationContext, R.string.listAlreadyExist, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Get the list of player's lists and update profilListeToDo
    private fun getPlayerList() : MutableList<ProfilListeToDo> {

        val file = File(filesDir, filename)
        val jsonProfiles : String = file.readText()
        val gson = Gson()
        val listPlayerType = object : TypeToken<List<ProfilListeToDo>>() {}.type
        var listPlayer : MutableList<ProfilListeToDo>? = gson.fromJson(jsonProfiles, listPlayerType)
        if (listPlayer == null) {
            listPlayer = mutableListOf()
        }
        // Select the correct ProfilListeToDo or create it if it doesn't already exist
        profilListeToDo = null
        listPlayer.forEach { profilListe ->
            if (profilListe.login == pseudo) {
                profilListeToDo = profilListe
                listPlayer.remove(profilListe)
            }
        }
        if (profilListeToDo == null) {
            profilListeToDo = pseudo?.let { ProfilListeToDo(it) }
        }
        return listPlayer
    }
}