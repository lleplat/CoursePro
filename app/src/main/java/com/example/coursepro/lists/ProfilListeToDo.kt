package com.example.coursepro.lists

import android.util.Log
import java.io.Serializable

class ProfilListeToDo(var login : String = "") : Serializable {

    var mesListeToDo : MutableList<ListeToDo> = mutableListOf()

    constructor(log: String, mesListes: MutableList<ListeToDo>) : this() {
        login = log
        mesListeToDo = mesListes
    }

    fun ajouteListe(uneListe : ListeToDo) {
        mesListeToDo.add(uneListe)
    }

    // Add unItem to the list uneListe if it is present in mesListeToDo
    fun ajoutItem(uneListe: ListeToDo?, unItem : ItemToDo) {
        val updatedListes : MutableList<ListeToDo> = mutableListOf()
        for (list : ListeToDo in mesListeToDo) {
            if (list.titreListeToDo == uneListe!!.titreListeToDo) {
                uneListe.ajoutItem((unItem))
                updatedListes.add(uneListe)
            }
            else {
                updatedListes.add(list)
            }
        }
        mesListeToDo = updatedListes
    }

    // Delete unItem to the list uneListe if it is present in mesListeToDo
    fun deleteItem(uneListe: ListeToDo?, unItem : ItemToDo) {
        val updatedListes : MutableList<ListeToDo> = mutableListOf()
        for (list : ListeToDo in mesListeToDo) {
            if (list.titreListeToDo == uneListe!!.titreListeToDo) {
                uneListe.deleteItem(unItem)
                updatedListes.add(uneListe)
            }
            else {
                updatedListes.add(list)
            }
        }
        mesListeToDo = updatedListes
    }

    // Update unItem from the list uneListe
    fun updateItem(uneListe: ListeToDo?, unItem : ItemToDo) {
        val updatedListes : MutableList<ListeToDo> = mutableListOf()
        for (list : ListeToDo in mesListeToDo) {
            if (list.titreListeToDo == uneListe!!.titreListeToDo) {
                list.updateItem(unItem)
                uneListe.updateItem((unItem))
            }
            updatedListes.add(list)
        }
        mesListeToDo = updatedListes
    }

    // Check if a list with the given title already exists
    fun listAlreadyExists(title : String) : Boolean {
        for (list : ListeToDo in mesListeToDo) {
            if (list.titreListeToDo == title) {
                return true
            }
        }
        return false
    }

    override fun toString(): String = "Listes du profil $login : $mesListeToDo"



}