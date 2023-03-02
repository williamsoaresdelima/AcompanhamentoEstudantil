package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.acompanhamentoestudantil.R.id.logout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val activity = Intent(this, LoginScreen::class.java)
        startActivity(activity)
    }
}