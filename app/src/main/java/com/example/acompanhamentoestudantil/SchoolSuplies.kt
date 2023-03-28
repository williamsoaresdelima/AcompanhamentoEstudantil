package com.example.acompanhamentoestudantil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/supplies/$supplieId")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) return

                findViewById<EditText>(R.id.etTitle).setText(snapshot.child("name").value.toString())
                findViewById<EditText>(R.id.etDescription).setText(snapshot.child("description").value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SchoolSuplies, "Erro ao carregar tarefa", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createUpdateSupplie (){
        if(supplieId !== ""){
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/supplies/$supplieId")

            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()) return
                    val task = snapshot.value as HashMap<String, String>


                    task["name"] = findViewById<EditText>(R.id.etTitle).text.toString()
                    task["description"] = findViewById<EditText>(R.id.etDescription).text.toString()

                    ref.setValue(task)
                    Toast.makeText(this@SchoolSuplies, "Tarefa atualizada com sucesso", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SchoolSuplies, "Erro ao atualizar tarefa", Toast.LENGTH_SHORT).show()
                }
            })

            Intent(this, CreateSchoolSupplies::class.java).also {
                startActivity(it)
            }

        }else{
            name = findViewById(R.id.etTitle)
            description = findViewById(R.id.etDescription)

            val supplie =  hashMapOf(
                "name" to name.text.toString(),
                "description" to description.text.toString()
            )

            val newElement = db_ref.push()
            newElement.setValue(supplie)

            Toast.makeText(this, R.string.creat_supplie_succes, Toast.LENGTH_SHORT).show()
            Intent(this, CreateSchoolSupplies::class.java).also {
                startActivity(it)
            }
        }
    }
}