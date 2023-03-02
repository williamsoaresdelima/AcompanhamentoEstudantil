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



        findViewById<View>(R.id.btnLogin).setOnClickListener {
            var emailView = findViewById<android.widget.EditText>(R.id.etEmail)
            var senhaView = findViewById<android.widget.EditText>(R.id.etPassword)

            if(senhaView.text.toString() == "123" && emailView.text.toString() == "teste@gmail.com")
            {
                val activity = Intent(this, Dashboard::class.java)
                startActivity(activity)
            }
        }

        findViewById<View>(R.id.btnSignIn).setOnClickListener {
            val activity = Intent(this, CreateAccount::class.java)
            startActivity(activity)
        }
    }
}