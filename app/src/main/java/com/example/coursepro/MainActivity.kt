package com.example.coursepro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.coursepro.lists.ProfilListeToDo
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


var courseModel: CourseModel =
    CourseModel(File.createTempFile("Log-", "Course"))

class MainActivity : AppCompatActivity() {
    private lateinit var prefs : SharedPreferences
    private val saveFileName : String = "players"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setReferences()

        // Set the auto-completion
        autoCompletion(courseModel.usersList)
    }


    override fun onStart() {
        super.onStart()
        val pseudoPref : String = prefs.getString("pseudo", "Pseudo") ?: ""
        pseudoInputMain.setText(pseudoPref)
        autoCompletion(courseModel.usersList)
    }

    // Internal functions:

    private fun setReferences() {
        // Declarations
        courseModel =
            CourseModel(File(filesDir, saveFileName))
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    /*
    Set the auto-completion
     */
    private fun autoCompletion(usersList: List<ProfilListeToDo>) {
        val pseudoList : MutableList<String> = mutableListOf<String>()
        usersList.forEach { profilListe ->
            pseudoList.add(profilListe.login)
        }
        val adapter : ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, pseudoList)
        pseudoInputMain.setAdapter(adapter)
    }

    // Intents

    /*
    Pseudo OK button click
     */
    fun pseudoOKButtonClick(view: View) {
        val pseudo = pseudoInputMain.text.toString()

        courseModel.setCurrentUser(ProfilListeToDo(pseudo))

        if (setNicknameAsDefault.isChecked) {
            val editor : SharedPreferences.Editor = prefs.edit()
            editor.putString("pseudo", pseudo)
            editor.apply()
        }

        startActivity(Intent(this, ChoixListActivity::class.java))
    }

}

