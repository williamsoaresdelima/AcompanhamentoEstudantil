package com.example.acompanhamentoestudantil.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.acompanhamentoestudantil.R

class Notification: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Notification", "Notification received")

        if (context != null) {
            val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, "DEFAULT")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Criado com sucesso!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, notification.build())
        }
    }
}