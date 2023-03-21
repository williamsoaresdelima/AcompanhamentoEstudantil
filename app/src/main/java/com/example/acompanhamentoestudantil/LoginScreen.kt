package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class LoginScreen : AppCompatActivity() {
    val req_Code: Int = 123
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        supportActionBar?.hide()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        findViewById<View>(R.id.btnLogin).setOnClickListener {
            sign()
        }

        findViewById<View>(R.id.btnSignIn).setOnClickListener {
            val activity = Intent(this, CreateAccount::class.java)
            startActivity(activity)
        }

        findViewById<View>(R.id.btnGoogleSignIn).setOnClickListener {
            signInGoogle()
        }

        findViewById<View>(R.id.forget_password).setOnClickListener {
            val activity = Intent(this, ForgotPassword::class.java)
            startActivity(activity)
        }
    }

    private fun signInGoogle(){
        val signIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signIntent, req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == req_Code){
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(result)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try{
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            Toast.makeText(this, R.string.success_login, Toast.LENGTH_SHORT).show()
            if(account != null){
                UpdateUser(account)
            }
        }catch (e: ApiException){
            println(e)
            Toast.makeText(this, R.string.fail_login, Toast.LENGTH_SHORT).show()
        }
    }

    private fun UpdateUser(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task ->
            if(task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun sign() {
        if(isEmpyt()) {
            val userEmail = etEmail.text.toString().trim()
            val userPassword = etPassword.text.toString().trim()
            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                    if (firebaseUser != null && firebaseUser.isEmailVerified) {
                        val activity = Intent(this, Dashboard::class.java)
                        startActivity(activity)
                    } else if (firebaseUser != null && !firebaseUser.isEmailVerified) {
                        firebaseAuth.signOut()
                        Toast.makeText(this, R.string.unverified_email, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, R.string.fail_login, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, R.string.unfilled_fields, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isEmpyt(): Boolean {
        return  etEmail.text.toString().trim().isNotEmpty() &&
                etPassword.text.toString().trim().isNotEmpty()
    }
}