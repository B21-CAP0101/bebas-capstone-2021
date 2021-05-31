package com.capstone101.bebas.background.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleService
import com.capstone101.bebas.R
import com.capstone101.bebas.main.MainViewModel
import com.capstone101.bebas.welcome.WelcomeActivity
import com.capstone101.core.data.network.firebase.DangerFire
import com.capstone101.core.data.network.firebase.RelativesFire
import com.capstone101.core.data.network.firebase.UserFire
import com.capstone101.core.utils.MapVal
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class AutoService : LifecycleService() {

    companion object {
        const val NOTIFICATION_FORE = 16
        const val NOTIFICATION_REMIND = 20
        const val CHANNEL_FORE_ID = "CHANNEL_FORE_1"
        const val CHANNEL_REMIND_ID = "CHANNEL_REMIND_1"
        const val CHANNEL_FORE_NAME = "Channel for Foreground Service"
        const val CHANNEL_REMIND_NAME = "Channel for Alert Reminder"
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private val mainViewModel: MainViewModel by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val fs = Firebase.firestore

        fs.collection(UserFire.COLLECTION).whereEqualTo(UserFire.DANGER, true)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Toast.makeText(applicationContext, "Error occurred\n$e", Toast.LENGTH_SHORT)
                        .show()
                    return@addSnapshotListener
                }
                mainViewModel.getUser.observe(this) {
                    if (it != null) {
                        MapVal.user = it
                        fs.collection(RelativesFire.COLLECTION).document(it.username).get()
                            .addOnSuccessListener { relDoc ->
                                val relatives =
                                    MapVal.relativesFireToDom(
                                        relDoc.toObject(RelativesFire::class.java)
                                            ?: RelativesFire()
                                    )
                                val users =
                                    value!!.map { user -> user.toObject(UserFire::class.java) }
                                for (user in users) {
                                    if (user.username in relatives.pure) {
                                        fs.collection(DangerFire.COLLECTION)
                                            .document(user.username!!)
                                            .collection(DangerFire.SUB_COLLECTION)
                                            .orderBy(DangerFire.TIME, Query.Direction.DESCENDING)
                                            .limit(1).get().addOnSuccessListener { dataDang ->
                                                val danger =
                                                    dataDang.documents[0].toObject(DangerFire::class.java)
                                                notificationReminder(
                                                    user,
                                                    danger ?: DangerFire(null)
                                                )
                                            }
                                        Toast.makeText(
                                            applicationContext,
                                            "${user.username} dalam bahaya",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
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

    private fun notificationReminder(user: UserFire, danger: DangerFire) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, WelcomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val soundURI =
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${applicationContext.packageName}/${R.raw.danger}".toUri()
        val vibration = longArrayOf(5000, 3000)
        val type = when (danger.type) {
            0 -> "BEGAL"
            1 -> "RAMPOK"
            2 -> "KDRT"
            else -> "Unidentified"
        }  // parse from ML
        val notification = NotificationCompat.Builder(this, CHANNEL_REMIND_ID).apply {
            setSmallIcon(R.mipmap.ic_launcher_round)
            setContentTitle("${user.username} in danger")
            setContentText("${user.username} danger type is \"$type\"")
            setContentIntent(pendingIntent)
            setVibrate(vibration)
            setSound(soundURI)
            setStyle(NotificationCompat.BigTextStyle().bigText("ID: ${danger.id}"))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_REMIND_ID,
                    CHANNEL_REMIND_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    val atr = AudioAttributes.Builder().apply {
                        setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    }.build()
                    setSound(soundURI, atr)
                    enableLights(true)
                    enableVibration(true)
                    vibrationPattern = vibration
                    lightColor = R.color.red
                }
                setChannelId(CHANNEL_REMIND_ID)
                manager.createNotificationChannel(channel)
            }
        }.build()
        manager.notify(NOTIFICATION_REMIND, notification)
    }
}