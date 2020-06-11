package com.example.coursepro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.coursepro.lists.ProfilListeToDo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {

    private var refBtnOK: Button? = null
    private var refPseudoInput: AutoCompleteTextView? = null
    private var prefs : SharedPreferences ?= null
    private var filename : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        Declarations
         */
        refBtnOK = findViewById(R.id.OKBtnMain)
        refPseudoInput = findViewById(R.id.pseudoInputMain)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        filename = "players"

        /*
        Check if the players file exist
         */
        val file = File(filesDir, filename)
        if (!file.exists()) {
            file.createNewFile()
        }

        /*
        Set the auto-completion
         */
        autoCompletion()
    }


    override fun onStart() {
        super.onStart()

        val pseudoPref : String? = prefs!!.getString("pseudo", "Pseudo")
        refPseudoInput?.setText(pseudoPref)
        autoCompletion()
    }




    /*
    Set the auto-completion
     */
    private fun autoCompletion() {
        val file = File(filesDir, filename)
        var jsonProfiles : String = file.readText()
        val gson = Gson()
        val listPlayerType = object : TypeToken<List<ProfilListeToDo>>() {}.type
        var listPlayer : MutableList<ProfilListeToDo>? = gson.fromJson(jsonProfiles, listPlayerType)
        if (listPlayer == null) {
            listPlayer = mutableListOf()
        }
        var pseudoList : MutableList<String> = mutableListOf<String>()
        listPlayer.forEach { profilListe ->
            pseudoList.add(profilListe.login)
        }
        val adapter : ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, pseudoList)
        refPseudoInput!!.setAdapter(adapter)
    }

    // Intents

    /*
    Pseudo OK button click
     */
    fun pseudoOKButtonClick(view: View) {
        val pseudo = refPseudoInput!!.text.toString()

        val editor : SharedPreferences.Editor = prefs!!.edit()
        editor.putString("pseudo", pseudo)
        editor.commit()

        val bundle = Bundle()
        bundle.putString("pseudo", pseudo)
        val intent = Intent(this, ChoixListActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

}

