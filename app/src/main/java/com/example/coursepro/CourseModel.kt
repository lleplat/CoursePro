package com.example.coursepro

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.coursepro.lists.ItemToDo
import com.example.coursepro.lists.ListeToDo
import com.example.coursepro.lists.ProfilListeToDo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File


class CourseModel(file: File): AppCompatActivity() {
    private val saveFile: File = file
    var usersList: MutableList<ProfilListeToDo> = loadOrCreateSave(file)
        private set
    var currentUser: ProfilListeToDo? = null
        private set
    var currentList: ListeToDo? = null
        private set


    private fun loadOrCreateSave(file: File): MutableList<ProfilListeToDo> {
        // Check if the players file exist
        if (!file.exists()) {
            file.createNewFile()
        }

        // Read file and get the list of ProfilListeToDo
        val jsonProfiles : String = file.readText()
        val usersListType = object : TypeToken<MutableList<ProfilListeToDo>>() {}.type

        return Gson().fromJson<MutableList<ProfilListeToDo>>(jsonProfiles, usersListType) ?: mutableListOf()
    }

    // Get the list of player's lists and update profilListeToDo


    // Intents

    fun saveUsersData() {
        // Update the file
        try {
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()
            val jsonProfiles = gsonPretty.toJson(usersList)
            saveFile.outputStream().write(jsonProfiles.toByteArray())
        } catch (e: Exception) {
            throw e
        }
    }

    // Add user to list of users
    fun add(user: ProfilListeToDo) {
        usersList.add(user)
    }

    // TODO : Those must be methods of the ListeToDo class, not of the model
    // Add new list
    fun addList(title: String) {
        // Check if a list with the same title already exists and throws an exception
        if (currentUser!!.listAlreadyExists(title)) {
            throw Exception("Did you use a try-catch with this function?")
        }
        // Add the new list
        currentUser?.ajouteListe(ListeToDo(title))
    }
/*    fun addList(listeToDo: ListeToDo) {
        // Check if a list with the same title already exists
        if (currentUser.listAlreadyExists(listeToDo)) {
            throw Exception("List already exists, did you use a try-catch with this function?")
        }
        // Add the new list
        currentUser.ajouteListe(listeToDo)
    }*/

    fun addItem(name: String) {
        // Check if an item with the same title already exists
        if (currentList!!.rechercherItem(name)) {
            throw Exception("Item already exists, did you use a try-catch with this function?")
        }
        // Add the new item
        currentList?.ajoutItem(ItemToDo(name))
    }
    // TODO : Create remove user from list.

    // Set currentUser
    fun setCurrentUser(user: ProfilListeToDo?): ProfilListeToDo? {
        currentUser = usersList.firstOrNull{it.login == user?.login} ?: run {
            add(user!!)
            user
        }
        return currentUser

    }

    // TODO : Current list must be a nullable variable of the class ProfilListeToDo, those sets must be changed

    fun setCurrentUser(name: String) {
       setCurrentUser(ProfilListeToDo(name))
    }

    fun setCurrentList(list: ListeToDo?) {
        currentList = currentUser?.mesListeToDo?.firstOrNull { it.titreListeToDo == list?.titreListeToDo }
    }

}

