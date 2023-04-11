package com.example.acompanhamentoestudantil

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.acompanhamentoestudantil.services.Notification
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class LevelSchooling : AppCompatActivity() {
    private lateinit var  year: EditText
    private lateinit var  description: EditText
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val db_ref = FirebaseDatabase.getInstance().getReference("/users/$uid/level")
    private var levelId: String = ""
    private lateinit var firebaseAnalytics: FirebaseAnalytics;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_schooling)

        loadLevel()
        description = findViewById(R.id.etDescription)
        year = findViewById(R.id.in_date)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val in_date = findViewById<EditText>(R.id.in_date)
        val in_time = findViewById<EditText>(R.id.in_time)

        val current_date_time = Calendar.getInstance()
        val day = current_date_time.get(Calendar.DAY_OF_MONTH)
        val month = current_date_time.get(Calendar.MONTH)
        val year = current_date_time.get(Calendar.YEAR)
        val hour = current_date_time.get(Calendar.HOUR_OF_DAY)
        val minute = current_date_time.get(Calendar.MINUTE)

        in_date.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
        in_time.setText(String.format("%02d:%02d", hour, minute))

        findViewById<Button>(R.id.btn_date).setOnClickListener{
            val datePickerDialog = DatePickerDialog(this, {_, yearOfYear, monthOfYear, dayOfMonth ->
                in_date.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, yearOfYear))
            }, year, month, day)
            datePickerDialog.show()
        }

        findViewById<Button>(R.id.btn_time).setOnClickListener{
            val timePickerDialog = TimePickerDialog(this, {_, hourOfDay, minuteOfHour ->
                in_time.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour))
            }, hour, minute, true)
            timePickerDialog.show()
        }

        findViewById<Button>(R.id.btn_save_level).setOnClickListener{
            createUpdateLevel()
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        val name = R.string.notification_channel
        val descriptionText = R.string.notification_channel_description
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("DEFAULT", name.toString(), importance).apply {
            description = descriptionText.toString()
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleNotification(data: String, hora: String){
        val intent = Intent(this, Notification::class.java)
        val title = R.string.app_name
        val message = R.string.noti_creat_supplie

        intent.putExtra("title", title)
        intent.putExtra("message", message)


        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        val data_dia = data.substring(0, 2).toInt()
        val data_mes = data.substring(3, 5).toInt() - 1
        val data_ano = data.substring(6, 10).toInt()
        val hora_hora = hora.substring(0, 2).toInt()
        val hora_minuto = hora.substring(3, 5).toInt()

        calendar.set(
            data_ano,
            data_mes,
            data_dia,
            hora_hora,
            hora_minuto,
            0
        )
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
        Toast.makeText(this, "Notificação agendada", Toast.LENGTH_SHORT).show()
    }

    private fun loadLevel (){
        this.levelId = intent.getStringExtra("id") ?: ""
        if(levelId === "") return
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/level/$levelId")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) return

                findViewById<EditText>(R.id.in_date).setText(snapshot.child("year").value.toString())
                findViewById<EditText>(R.id.etDescription).setText(snapshot.child("description").value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LevelSchooling, R.string.sl_load_error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createUpdateLevel (){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, uid)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "create_level")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        if(levelId !== ""){
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/level/$levelId")

            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()) return
                    val task = snapshot.value as HashMap<String, String>


                    task["year"] = findViewById<EditText>(R.id.in_date).text.toString()
                    task["description"] = findViewById<EditText>(R.id.etDescription).text.toString()

                    ref.setValue(task)
                    Toast.makeText(this@LevelSchooling, R.string.success_edit_level, Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LevelSchooling, R.string.error_edit_level_success, Toast.LENGTH_SHORT).show()
                }
            })

            Intent(this, CreateLevelSchooling::class.java).also {
                startActivity(it)
            }

        }else{
            description = findViewById(R.id.etDescription)
            year = findViewById(R.id.in_date)

            val levelSchooling =  hashMapOf(
                "year" to year.text.toString(),
                "description" to description.text.toString()
            )

            val newElement = db_ref.push()
            newElement.setValue(levelSchooling)

            Toast.makeText(this, R.string.creat_level_succes, Toast.LENGTH_SHORT).show()
            Intent(this, CreateLevelSchooling::class.java).also {
                startActivity(it)
            }
        }
        scheduleNotification(findViewById<EditText>(R.id.in_date).text.toString(), findViewById<EditText>(R.id.in_time).text.toString())

    }
}