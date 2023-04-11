package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.acompanhamentoestudantil.fragment.PasswordDifficulty
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser

class CreateAccount : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: PasswordDifficulty
    private lateinit var etConfirmPassword: EditText
    lateinit var createAccountInputArray: Array<Any>
    private val req_Code: Int = 123
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var  firebaseAuth: FirebaseAuth
    //private lateinit var createAccountInputArray: Array<EditText>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        supportActionBar?.hide()
        FirebaseApp.initializeApp((this))

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = supportFragmentManager.findFragmentById(R.id.etPassword) as PasswordDifficulty
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        createAccountInputArray = arrayOf(etEmail, etPassword, etConfirmPassword)

        findViewById<View>(R.id.btnSignIn).setOnClickListener {
            signIn()
        }

        findViewById<View>(R.id.btnGoogleSignIn).setOnClickListener {
            signInGoogle()
        }
    }

    private fun isEmpyt(): Boolean {
        return  etEmail.text.toString().trim().isNotEmpty() &&
                etPassword.text.toString().trim().isNotEmpty() &&
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

    private fun signIn() {
        if(isEmpyt()) {
            if(verifyIdenticalPassword()) {
                if(verifySizePassword()) {
                    val userEmail = etEmail.text.toString().trim()
                    val userPassword = etPassword.text.toString().trim()
                    firebaseAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful) {
                                sendEmailVerification()
                                Toast.makeText(this,R.string.success_login_need_email_verification, Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                val exception = task.exception
                                if(exception is FirebaseAuthException && exception.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                                    Toast.makeText(this,R.string.firebase_email_error, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this,R.string.fail_signin, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                }
            }
        } else {
        Toast.makeText(this,R.string.unfilled_fields, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmailVerification() {
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this, R.string.email_sent, Toast.LENGTH_SHORT).show()
                }
            }
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
                updateUser(account)
            }
        }catch (e: ApiException){
            println(e)
            Toast.makeText(this, R.string.fail_login, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUser(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task ->
            if(task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}