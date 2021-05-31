package com.capstone101.bebas.background.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.capstone101.bebas.R
import com.capstone101.bebas.ui.main.MainViewModel
import com.capstone101.bebas.ui.welcome.WelcomeActivity
import com.capstone101.core.data.network.firebase.RelativesFire
import com.capstone101.core.utils.MapVal
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class AutoService : LifecycleService() {

    companion object {
        const val NOTIFICATION_FORE = 16
        const val CHANNEL_FORE_ID = "CHANNEL_FORE_1"
        const val CHANNEL_FORE_NAME = "Channel for Foreground Service"
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private val mainViewModel: MainViewModel by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val db = Firebase.firestore

        db.collection("users").whereEqualTo("inDanger", true)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Toast.makeText(applicationContext, "Terjadi error\n$e", Toast.LENGTH_SHORT)
                        .show()
                    return@addSnapshotListener
                }
                mainViewModel.getUser.observe(this) {
                    if (it != null) {
                        db.collection(RelativesFire.COLLECTION).document(it.username).get()
                            .addOnSuccessListener { relDoc ->
                                val relatives =
                                    MapVal.relativesFireToDom(
                                        relDoc.toObject(RelativesFire::class.java)
                                            ?: RelativesFire()
                                    )
                                for (i in value!!) {
                                    if (i["username"] in relatives.pure)
                                        Toast.makeText(
                                            applicationContext,
                                            "${i["username"]} dalam bahaya",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                }
                            }
                        mainViewModel.getUser.removeObservers(this)
                    }
                }

            }
        startForeground()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, WelcomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val notification = NotificationCompat.Builder(this, CHANNEL_FORE_ID).apply {
            setSmallIcon(R.mipmap.ic_launcher_round)
            setContentTitle("BeBaS Run")
            setContentText("This application run on your background process")
            setContentIntent(pendingIntent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_FORE_ID,
                    CHANNEL_FORE_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                setChannelId(CHANNEL_FORE_ID)
                manager.createNotificationChannel(channel)
            }
        }.build()
        startForeground(NOTIFICATION_FORE, notification)
    }
}