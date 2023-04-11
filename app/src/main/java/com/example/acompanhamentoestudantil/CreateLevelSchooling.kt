package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CreateLevelSchooling : AppCompatActivity() {
    private var gson = Gson()
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignClient: GoogleSignInClient
    private var uid = FirebaseAuth.getInstance().currentUser?.uid
    private val ref = FirebaseDatabase.getInstance().getReference("users/$uid/level")
    private val listItems = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_level_schooling)
        supportActionBar?.hide()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        firebaseAuth = FirebaseAuth.getInstance()
        mGoogleSignClient = GoogleSignIn.getClient(this, gso)
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, listItems)
        val listView = findViewById<android.widget.ListView>(R.id.list)
        listView.adapter = adapter

        findViewById<View>(R.id.logout).setOnClickListener {
            firebaseAuth.signOut()
            mGoogleSignClient.signOut()
            val activity = Intent(this, LoginScreen::class.java)
            startActivity(activity)
            finish()
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            val activity = Intent(this, Dashboard::class.java)
            startActivity(activity)
            finish()
        }

        findViewById<View>(R.id.add_supplies).setOnClickListener {
            val activity = Intent(this, LevelSchooling::class.java)
            startActivity(activity)
            finish()
        }

        ref.addValueEventListener(object: ValueEventListener {
            val ctx = this@CreateLevelSchooling;

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listItems.clear()

                for(child in dataSnapshot.children){
                    listItems.add(child.child("description").value.toString())
                }

                adapter.notifyDataSetChanged()

                listView.setOnItemLongClickListener { parent, view, position, id ->
                    val itemId =  dataSnapshot.children.toList()[position].key

                    if(itemId != null){
                        AlertDialog.Builder(ctx)
                            .setTitle(R.string.delete_schooling)
                            .setMessage(R.string.wish_delete_schooling)
                            .setPositiveButton(R.string.yes){ dialog, which ->
                                ref.child(itemId).removeValue()
                                Toast.makeText(ctx, R.string.sl_delete_success, Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton(R.string.no){ dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }

                    true
                }
                listView.setOnItemClickListener { parent, view, position, id ->
                    val itemId =  dataSnapshot.children.toList()[position].key

                    val activity = Intent(ctx, LevelSchooling::class.java)
                    activity.putExtra("id", itemId)
                    startActivity(activity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(ctx, R.string.sl_load_error, Toast.LENGTH_SHORT).show()
            }
        })
    }
}