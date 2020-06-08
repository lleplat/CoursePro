package com.example.coursepro.fragments

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.coursepro.R
import java.io.File

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val resetPseudoBtn : Preference? = findPreference("resetPseudo")

        resetPseudoBtn!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val filename : String = "players"
            val file = File(activity!!.filesDir, filename)
            activity!!.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write("".toByteArray())
            }

            Toast.makeText(activity, R.string.pseudoDeleted, Toast.LENGTH_LONG).show()
            true
        }
    }

}