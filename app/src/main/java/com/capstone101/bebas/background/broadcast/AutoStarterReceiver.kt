package com.capstone101.bebas.background.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.capstone101.bebas.background.service.AutoService

class AutoStarterReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(Intent(context, AutoService::class.java))
            else context.startService(Intent(context, AutoService::class.java))
        }
    }
}