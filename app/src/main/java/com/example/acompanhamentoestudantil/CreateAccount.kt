package com.example.acompanhamentoestudantil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.acompanhamentoestudantil.R.id.btnSignIn

class CreateAccount : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        supportActionBar?.hide()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        findViewById<View>(R.id.btnSignIn).setOnClickListener {
            Toast.makeText(this,"deu certo!", Toast.LENGTH_SHORT).show()
        }
    }
}