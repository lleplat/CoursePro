package com.example.coursepro.lists

import java.io.Serializable

class ItemToDo (var description : String = "", var fait : Boolean = false) : Serializable {

    override fun toString(): String = "Tâche $description ${if (!fait) "non" else ""} effectuée"

}