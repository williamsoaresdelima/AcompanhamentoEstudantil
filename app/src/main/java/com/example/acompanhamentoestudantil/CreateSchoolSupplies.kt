package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter

class CreateSchoolSupplies : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_school_supplies)
        supportActionBar?.hide()

        var listView = findViewById<android.widget.ListView>(R.id.list)
        var editText = findViewById<android.widget.EditText>(R.id.textInput)

        var listAdapter = arrayListOf<String>()

        var adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_multiple_choice,
            listAdapter
        )

        findViewById<android.widget.Button>(R.id.btnAdd).setOnClickListener{
            listAdapter.add(editText.text.toString())
            listView.adapter = adapter
            adapter.notifyDataSetChanged()

            editText.text.clear()
        }

        findViewById<View>(R.id.logout).setOnClickListener {
            val activity = Intent(this, LoginScreen::class.java)
            startActivity(activity)
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            val activity = Intent(this, Dashboard::class.java)
            startActivity(activity)
        }
    }
}