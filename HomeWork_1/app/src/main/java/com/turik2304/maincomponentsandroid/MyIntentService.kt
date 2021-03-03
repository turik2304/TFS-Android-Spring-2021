package com.turik2304.maincomponentsandroid

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager


@Suppress("DEPRECATION")
class MyIntentService() : IntentService(MyIntentService::class.simpleName) {

    private lateinit var toastHandler: Handler
    override fun onHandleIntent(intent: Intent?) {
        val upcomingEvent = CalendarHandler().getUpcomingEvent(applicationContext)
        showToast(this, "Service did the operation")
        val intentForBroadcast = Intent(SecondActivity.ACTION_MY_CUSTOM_ACTION)
            .putExtra(EXTRA_TITLE, upcomingEvent.title)
            .putExtra(EXTRA_START_DATE, upcomingEvent.startDate)
            .putExtra(EXTRA_END_DATE, upcomingEvent.endDate)
            .putExtra(EXTRA_DESCRIPTION, upcomingEvent.description)
        sendBroadcast(this, intentForBroadcast)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        toastHandler = Handler(Looper.getMainLooper())
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showToast(context: Context, message: String) {
        val runnableToast = Runnable {
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
        toastHandler.post {
            runnableToast.run()
        }
    }

    private fun sendBroadcast(context: Context, intent: Intent?) {
        val localBroadcastManager = LocalBroadcastManager.getInstance(context)
        if (intent != null) {
            showToast(this, "Sending a broadCast")
            localBroadcastManager.sendBroadcast(intent)
        } else {
            showToast(this, "Something go wrong")
        }
    }

}