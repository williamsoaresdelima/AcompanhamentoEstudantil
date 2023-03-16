package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class Dashboard : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar?.hide()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        firebaseAuth = FirebaseAuth.getInstance()
        mGoogleSignClient = GoogleSignIn.getClient(this, gso)

        findViewById<View>(R.id.createSchoolSupplies).setOnClickListener {
            val activity = Intent(this, CreateSchoolSupplies::class.java)
            startActivity(activity)
        }

        findViewById<View>(R.id.createLevelSchooling).setOnClickListener {
            val activity = Intent(this, CreateLevelSchooling::class.java)
            startActivity(activity)
        }

        findViewById<View>(R.id.logout).setOnClickListener {
            firebaseAuth.signOut()
            mGoogleSignClient.signOut()
            val activity = Intent(this, LoginScreen::class.java)
            startActivity(activity)
        }

        findViewById<View>(R.id.profile).setOnClickListener {
            val activity = Intent(this, Profile::class.java)
            startActivity(activity)
        }
    }
}