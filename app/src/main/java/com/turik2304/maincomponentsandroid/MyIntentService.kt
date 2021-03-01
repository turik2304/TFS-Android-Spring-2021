package com.turik2304.maincomponentsandroid

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager




@Suppress("DEPRECATION")
class MyIntentService() : IntentService(MyIntentService::class.simpleName) {
    override fun onHandleIntent(intent: Intent?) {
        Toast.makeText(this, "Service did the operation", Toast.LENGTH_SHORT).show()
        val upcomingEvent = CalendarHandler().getUpcomingEvent(applicationContext)

        val intentForBroadcast = Intent(ACTION_MY_CUSTOM_ACTION)
            .putExtra(EXTRA_TITLE, upcomingEvent.title)
            .putExtra(EXTRA_START_DATE, upcomingEvent.startDate)
            .putExtra(EXTRA_END_DATE, upcomingEvent.endDate)
            .putExtra(EXTRA_DESCRIPTION, upcomingEvent.description)
        sendBroadcast(this, intentForBroadcast)


    }



    private fun sendBroadcast(context: Context, intent: Intent?) {
        val localBroadcastManager = LocalBroadcastManager.getInstance(context)
        if (intent != null) {
            Toast.makeText(context, "Sending a broadCast", Toast.LENGTH_SHORT).show()
            localBroadcastManager.sendBroadcast(intent)
        } else {
            Toast.makeText(context, "Something go wrong", Toast.LENGTH_SHORT).show()
        }
    }

}