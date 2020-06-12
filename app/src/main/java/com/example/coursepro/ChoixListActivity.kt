package com.example.coursepro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coursepro.adapters.ListeAdapter
import com.example.coursepro.lists.ListeToDo
import kotlinx.android.synthetic.main.activity_choix_list.*
import java.lang.Exception

class ChoixListActivity : GenericActivity(), ListeAdapter.ActionListener {
    private var model = courseModel
    private var adapter : ListeAdapter? = null
    private var prefs : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)

        // Declarations
        adapter = newAdapter()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // RecyclerView
        setRecyclerView()
    }

    // Used to update the list when coming from ShowListActivity
    override fun onResume() {
        super.onResume()
        model.setCurrentList(null)
        // RecyclerView
        setRecyclerView()

    }

    private fun newAdapter() : ListeAdapter = ListeAdapter(actionListener = this)

    private fun setRecyclerView() {
        val list = listOfList
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)

        val dataSet : List<ListeToDo>? = model.currentUser?.mesListeToDo
        adapter!!.setData(dataSet)
    }


    // Item listener
    override fun onItemClicked(listeToDo: ListeToDo) {
        model.setCurrentList(listeToDo)
        startActivity(Intent(this, ShowListActivity::class.java))
    }



    // Intents :

    fun View.newList() {
        val listName = listInputChoixList.text.toString()
        try {
            model.addList(listName)
        } catch (e : Exception) {
            Toast.makeText(applicationContext, R.string.listAlreadyExist, Toast.LENGTH_LONG).show()
        }
        setRecyclerView()
    }

}