package com.example.coursepro.lists

import java.io.Serializable

class ListeToDo (var titreListeToDo: String = "",  var lesItems: MutableList<ItemToDo> = mutableListOf<ItemToDo>()) : Serializable {

    fun rechercherItem(descriptionItem : String): Boolean {
        for (item in lesItems) {
            if (item.description == descriptionItem) {
                return true
            }
        }
        return false
    }

    fun ajoutItem(unItem : ItemToDo) {
        lesItems.add(unItem)
    }

    fun deleteItem(unItem : ItemToDo) {
        lesItems.remove(unItem)
    }

    fun updateItem(unItem : ItemToDo) {
        var updatedItem : MutableList<ItemToDo> = mutableListOf()
        for (item : ItemToDo in lesItems) {
            if (item.description == unItem.description) {
                updatedItem.add(unItem)
            }
            else {
                updatedItem.add(item)
            }
        }
        lesItems = updatedItem
    }

    fun addSection(section : String) {
        if (!rechercherItem(section)) {
            ajoutItem(ItemToDo(section, header = true))
        }
    }


    override fun toString(): String = "Liste $titreListeToDo composé de $lesItems"

}