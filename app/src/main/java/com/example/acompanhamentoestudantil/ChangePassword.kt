package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ChangePassword : AppCompatActivity() {
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        findViewById<View>(R.id.btn_change_password).setOnClickListener {
            changePassword()
        }
    }

    private fun isEmpyt(): Boolean {
        return  etPassword.text.toString().trim().isNotEmpty() &&
                etConfirmPassword.text.toString().trim().isNotEmpty()
    }

    private fun verifyIdenticalPassword(): Boolean {
        var identical = false
        if(etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()) {
            identical = true
        } else {
            Toast.makeText(this,R.string.different_passwords, Toast.LENGTH_SHORT).show()
        }

        return identical
    }

    private fun verifySizePassword(): Boolean {
        var correct = false
        if (etPassword.text.toString().trim().length >= 6) {
            correct = true
        } else {
            Toast.makeText(this, R.string.short_password, Toast.LENGTH_SHORT).show()
        }
        return correct
    }

    fun changePassword(){
        if(isEmpyt()) {
            if (verifySizePassword()) {
                if (etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()) {
                    val user = firebaseAuth.currentUser

                    user!!.updatePassword(etPassword.text.toString().trim())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, R.string.changed_password, Toast.LENGTH_SHORT).show()
                            }
                        }

                    val activity = Intent(this, Profile::class.java);
                    startActivity(activity)
                    finish()
                } else {
                    Toast.makeText(this, R.string.different_passwords, Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, R.string.short_password, Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, R.string.unfilled_fields, Toast.LENGTH_SHORT).show()
        }
    }
}