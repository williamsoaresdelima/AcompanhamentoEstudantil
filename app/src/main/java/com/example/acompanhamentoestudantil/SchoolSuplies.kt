package com.example.acompanhamentoestudantil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SchoolSuplies : AppCompatActivity() {
    private lateinit var  name: EditText
    private lateinit var  description: EditText
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val db_ref = FirebaseDatabase.getInstance().getReference("/users/$uid/supplies")
    private var supplieId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_suplies)
        loadSupplie()
        name = findViewById(R.id.etTitle)
        description = findViewById(R.id.etDescription)

        findViewById<Button>(R.id.btn_save_supplies).setOnClickListener{
            createUpdateSupplie()
        }
    }

    private fun loadSupplie (){
        this.supplieId = intent.getStringExtra("id") ?: ""
        if(supplieId === "") return
            //TODO: Carregar tarefa
    }

    private fun createUpdateSupplie (){
        if(supplieId !== ""){
            //TODO: Atualizar tarefa
        }else{
            name = findViewById(R.id.etTitle)
            description = findViewById(R.id.etDescription)

            val supplie =  hashMapOf(
                "name" to name.text.toString(),
                "description" to description.text.toString()
            )

            val novoElemento = db_ref.push()
            novoElemento.setValue(supplie)

            Toast.makeText(this, R.string.creat_supplie_succes, Toast.LENGTH_SHORT).show()
        }
    }
}