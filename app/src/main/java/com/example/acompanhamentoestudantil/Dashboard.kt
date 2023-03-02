package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter

class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar?.hide()

        findViewById<View>(R.id.createSchoolSupplies).setOnClickListener {
            val activity = Intent(this, CreateSchoolSupplies::class.java)
            startActivity(activity)
        }

        findViewById<View>(R.id.createLevelSchooling).setOnClickListener {
            val activity = Intent(this, CreateLevelSchooling::class.java)
            startActivity(activity)
        }

        findViewById<View>(R.id.logout).setOnClickListener {
            val activity = Intent(this, LoginScreen::class.java)
            startActivity(activity)
        }
    }
}