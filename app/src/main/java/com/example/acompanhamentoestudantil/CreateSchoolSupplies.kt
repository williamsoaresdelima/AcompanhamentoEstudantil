package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.View
import android.widget.ArrayAdapter
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CreateSchoolSupplies : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences;
    private var gson = Gson();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_school_supplies)
        supportActionBar?.hide()

        sharedPreferences = getSharedPreferences("materiais-escolares", Context.MODE_PRIVATE)

        val listView = findViewById<android.widget.ListView>(R.id.list)
        val editText = findViewById<android.widget.EditText>(R.id.textInput)

        val listAdapter = getData();

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_multiple_choice,
            listAdapter
        )

        listView.adapter = adapter;
        adapter.notifyDataSetChanged()

        findViewById<View>(R.id.logout).setOnClickListener {
            val activity = Intent(this, LoginScreen::class.java)
            startActivity(activity)
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            val activity = Intent(this, Dashboard::class.java)
            startActivity(activity)
        }

        findViewById<android.widget.Button>(R.id.btnAdd).setOnClickListener{
            listAdapter.add(editText.text.toString())
            listView.adapter = adapter
            adapter.notifyDataSetChanged()

            saveData(listAdapter)
            editText.text.clear()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            val position: SparseBooleanArray = listView.checkedItemPositions
            val count = listView.count
            var item = count - 1
            while (item >= 0) {
                if (position.get(item)) {
                    adapter.remove(listAdapter.get(item));
                }
                item--;
            }
            saveData(listAdapter)
            position.clear();
            adapter.notifyDataSetChanged();
        }

        findViewById<View>(R.id.btnClean).setOnClickListener {
            listAdapter.clear();
            saveData(listAdapter);
            adapter.notifyDataSetChanged();
        }
    }

    private fun getData(): ArrayList<String> {
        val arrayJson = sharedPreferences.getString("lista", null);
        return if (arrayJson.isNullOrEmpty()) {
            arrayListOf<String>();
        } else {
            gson.fromJson(arrayJson, object : TypeToken<ArrayList<String>>(){}.type)
        }
    }
    private fun saveData(array: ArrayList<String>) {
        var arrayJson = gson.toJson(array);
        var editor = sharedPreferences.edit();
        editor.putString("lista", arrayJson);
        editor.apply();
    }
}