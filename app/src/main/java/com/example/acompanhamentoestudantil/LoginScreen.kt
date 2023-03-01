package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        supportActionBar?.hide()

        findViewById<View>(R.id.btnSignIn).setOnClickListener {
            val activity = Intent(this, CreateAccount::class.java)
            startActivity(activity)
        }
    }
}