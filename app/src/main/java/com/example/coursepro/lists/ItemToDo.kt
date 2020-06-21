package com.example.coursepro.lists
import android.util.Log
import com.example.coursepro.MyApp
import com.example.coursepro.guidage.Guidage
import java.io.*

class ItemToDo (var description : String = "", var fait : Boolean = false, var header : Boolean = false, var headerName : String = "") : Serializable{



    fun setSectionName() {
        if (!header) {

            val sectionList = listOf("baby", "drinks", "freshProducts", "frozenFood", "fruitVegetables", "housePastimes", "hygieneBeauty", "maintenance", "market", "petShop", "saltedGrocery", "sugaryGrocery")
            val sectionNames = listOf("Bébé", "Boissons", "Produits frais", "Surgelés", "Fruits et légumes", "Maison & loisirs", "Hygiène & beauté", "Entretien", "Le marché", "Animalerie", "Epicerie salée", "Epicerie sucrée")


            for ((index, section) in sectionList.withIndex()) {
                val response = convertStreamToString(MyApp.appContext!!.assets.open(section))!!.toLowerCase()
                if (response?.contains(description.toLowerCase())!!) {
                    headerName = sectionNames[index]
                }
            }
        }
    }

    override fun toString(): String = "Tâche $description ${if (!fait) "non" else ""} effectuée"

    private fun convertStreamToString(`is`: InputStream): String? {
        val reader = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()
        var line: String? = null
        try {
            while (reader.readLine().also({ line = it }) != null) {
                sb.append(line).append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
}