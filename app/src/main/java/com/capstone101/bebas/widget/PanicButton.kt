package com.capstone101.bebas.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import android.widget.Toast
import com.capstone101.bebas.R
import com.capstone101.bebas.home.HomeActivity

class PanicButton : AppWidgetProvider() {

    companion object {
        private const val KEY_ACTION = "com.capstone101.bebas.widget.action_record"
        private const val KEY_ID = "com.capstone101.bebas.widget.id_record"

        @Volatile
        private var count = 0
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == KEY_ACTION) {
            count++
            Handler(Looper.getMainLooper()).postDelayed({ count = 0 }, 3000)
            when (count) {
                3 -> {
                    Toast.makeText(context, "Berhasil", Toast.LENGTH_SHORT).show()

                    val manager = AppWidgetManager.getInstance(context)
                    val views = RemoteViews(context.packageName, R.layout.panic_button)
                    views.setImageViewResource(R.id.recordButton, R.drawable.recording)
                    val id = intent.getIntExtra(KEY_ID, -1)
                    manager.updateAppWidget(id, views)

                    Handler(Looper.getMainLooper()).postDelayed({
                        views.setImageViewResource(R.id.recordButton, R.drawable.record)
                        manager.updateAppWidget(id, views)
                    }, 10000)

                    context.startActivity(Intent(context, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        action = HomeActivity.ACTION_RECORD
                    })
                    // TODO: RECORD ACTIVITY MASUKKAN KE DATA
                }
                2 -> Toast.makeText(context, "Tekan sekali lagi", Toast.LENGTH_SHORT).show()
                1 -> Toast.makeText(context, "Tekan dua kali lagi", Toast.LENGTH_SHORT).show()
            }
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.panic_button)
        views.setOnClickPendingIntent(R.id.recordButton, getPending(context, appWidgetId))

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPending(context: Context, id: Int): PendingIntent {
        val intent = Intent(context, PanicButton::class.java)
        intent.action = KEY_ACTION
        intent.putExtra(KEY_ID, id)
        return PendingIntent.getBroadcast(context, id, intent, 0)
    }
}