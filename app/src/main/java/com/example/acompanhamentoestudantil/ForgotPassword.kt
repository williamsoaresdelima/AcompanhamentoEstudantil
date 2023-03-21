package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        firebaseAuth = FirebaseAuth.getInstance()

        findViewById<View>(R.id.btn_forgot_password).setOnClickListener {
            val emailAddress = findViewById<EditText>(R.id.etEmail).text.toString()
            firebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, R.string.email_sent_password, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.send_email_password_error, Toast.LENGTH_SHORT).show()
                }
            }

            val activity = Intent(this, LoginScreen::class.java)
            startActivity(activity)
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            val activity = Intent(this, LoginScreen::class.java)
            startActivity(activity)
        }


    }
}