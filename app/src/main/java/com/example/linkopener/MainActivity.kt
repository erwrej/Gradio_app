package com.example.linkopener

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var inputLink: EditText
    private lateinit var btnOpen: Button
    private lateinit var listView: ListView
    private lateinit var prefs: SharedPreferences
    private val PREFS_NAME = "LinkPrefs"
    private val KEY_LINKS = "recent_links"

    private var recentLinks: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputLink = findViewById(R.id.inputLink)
        btnOpen = findViewById(R.id.btnOpen)
        listView = findViewById(R.id.listView)

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadRecentLinks()
        updateListView()

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedLink = recentLinks[position]
            inputLink.setText(selectedLink)
        }

        btnOpen.setOnClickListener {
            var url = inputLink.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(this, "Введите ссылку", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://$url"
            }

            saveLink(url)

            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
        }
    }

    private fun loadRecentLinks() {
        val saved = prefs.getString(KEY_LINKS, null)
        if (!saved.isNullOrEmpty()) {
            recentLinks = saved.split("|").toMutableList()
        }
    }

    private fun saveLink(newLink: String) {
        recentLinks.remove(newLink)
        recentLinks.add(0, newLink)

        if (recentLinks.size > 5) {
            recentLinks = recentLinks.subList(0, 5)
        }

        prefs.edit().putString(KEY_LINKS, recentLinks.joinToString("|")).apply()
        updateListView()
    }

    private fun updateListView() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, recentLinks)
        listView.adapter = adapter
    }
}