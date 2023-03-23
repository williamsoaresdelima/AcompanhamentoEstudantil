package com.example.acompanhamentoestudantil

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class LevelSchooling : AppCompatActivity() {
    private lateinit var  year: EditText
    private lateinit var  name: EditText
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val db_ref = FirebaseDatabase.getInstance().getReference("/users/$uid/level")
    private var levelId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_schooling)

        loadLevel()
        name = findViewById(R.id.etDescription)
        year = findViewById(R.id.etTitle)
        val in_date = findViewById<EditText>(R.id.etTitle)

        val current_date_time = Calendar.getInstance()
        val year = current_date_time.get(Calendar.YEAR)
        val day = current_date_time.get(Calendar.DAY_OF_MONTH)
        val month = current_date_time.get(Calendar.MONTH)
        in_date.setText(String.format("%04d", year))

        findViewById<Button>(R.id.btn_date).setOnClickListener{
            val datePickerDialog = DatePickerDialog(this, {_, yearOfYear, monthOfYear, dayOfMonth ->
                in_date.setText(String.format("%04d", yearOfYear))
            }, year, month, day)
            datePickerDialog.show()
        }

        findViewById<Button>(R.id.btn_save_level).setOnClickListener{
            createUpdateLevel()
        }
    }

    private fun loadLevel (){
        this.levelId = intent.getStringExtra("id") ?: ""
        if(levelId === "") return
        //TODO: Carregar tarefa
    }

    private fun createUpdateLevel (){
        if(levelId !== ""){
            //TODO: Atualizar tarefa
        }else{
            name = findViewById(R.id.etDescription)
            year = findViewById(R.id.etTitle)

            val level =  hashMapOf(
                "name" to name.text.toString(),
                "year" to year.text.toString()
            )

            val novoElemento = db_ref.push()
            novoElemento.setValue(level)

            Toast.makeText(this, R.string.creat_level_succes, Toast.LENGTH_SHORT).show()
        }
    }
}