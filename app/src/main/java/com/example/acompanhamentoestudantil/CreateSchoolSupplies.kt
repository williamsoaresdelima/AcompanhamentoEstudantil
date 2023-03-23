package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.View
import android.widget.ArrayAdapter
import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CreateSchoolSupplies : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var gson = Gson()
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_school_supplies)
        supportActionBar?.hide()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        firebaseAuth = FirebaseAuth.getInstance()
        mGoogleSignClient = GoogleSignIn.getClient(this, gso)
        sharedPreferences = getSharedPreferences("materiais-escolares", Context.MODE_PRIVATE)

        val listView = findViewById<android.widget.ListView>(R.id.list)
        val editText = findViewById<android.widget.EditText>(R.id.textInput)
        val listAdapter = getData()
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_multiple_choice,
            listAdapter
        )

        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        findViewById<View>(R.id.logout).setOnClickListener {
            firebaseAuth.signOut()
            mGoogleSignClient.signOut()
            val activity = Intent(this, LoginScreen::class.java)
            startActivity(activity)
            finish()
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            val activity = Intent(this, Dashboard::class.java)
            startActivity(activity)
            finish()
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
                    adapter.remove(listAdapter[item])
                }
                item--
            }
            saveData(listAdapter)
            position.clear()
            adapter.notifyDataSetChanged()
        }

        findViewById<View>(R.id.btnClean).setOnClickListener {
            listAdapter.clear()
            saveData(listAdapter)
            adapter.notifyDataSetChanged()
        }

        findViewById<View>(R.id.add_supplies).setOnClickListener {
            val activity = Intent(this, SchoolSuplies::class.java)
            startActivity(activity)
            finish()
        }
    }

    private fun getData(): ArrayList<String> {
        val arrayJson = sharedPreferences.getString("lista", null)
        return if (arrayJson.isNullOrEmpty()) {
            arrayListOf()
        } else {
            gson.fromJson(arrayJson, object : TypeToken<ArrayList<String>>(){}.type)
        }
    }
    private fun saveData(array: ArrayList<String>) {
        val arrayJson = gson.toJson(array)
        val editor = sharedPreferences.edit()
        editor.putString("lista", arrayJson)
        editor.apply()
    }
}