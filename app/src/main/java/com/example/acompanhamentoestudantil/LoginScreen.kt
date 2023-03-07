package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginScreen : AppCompatActivity() {
    val Req_Code: Int = 123;
    lateinit var mGoogleSignInClient: GoogleSignInClient;
    lateinit var  firebaseAuth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        supportActionBar?.hide()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseAuth = FirebaseAuth.getInstance();

        findViewById<View>(R.id.btnLogin).setOnClickListener {
            var emailView = findViewById<android.widget.EditText>(R.id.etEmail)
            var senhaView = findViewById<android.widget.EditText>(R.id.etPassword)
            val activity = Intent(this, Dashboard::class.java)
            startActivity(activity)
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

        findViewById<View>(R.id.btnGoogleSignIn).setOnClickListener {
            signInGoogle();
        }
    }

    private fun signInGoogle(){
        val signIntent: Intent = mGoogleSignInClient.signInIntent;
        startActivityForResult(signIntent, Req_Code);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Req_Code){
            val result = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleResult(result);
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try{
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java);
            Toast.makeText(this, "Logado com sucesso", Toast.LENGTH_SHORT).show();
            if(account != null){
                UpdateUser(account)
            }
        }catch (e: ApiException){
            println(e)
            Toast.makeText(this, "Falha ao logar", Toast.LENGTH_SHORT).show();
        }
    }

    private fun UpdateUser(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task ->
            if(task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java);
                startActivity(intent);
                finish()
            }
        }
    }
}