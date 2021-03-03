package com.turik2304.maincomponentsandroid

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.turik2304.maincomponentsandroid.databinding.ActivitySecondBinding

//const val ACTION_MY_CUSTOM_ACTION = "com.turik2304.maincomponentsandroid.ACTION"

const val EXTRA_TITLE = "com.turik2304.maincomponentsandroid.TITLE"
const val EXTRA_START_DATE = "com.turik2304.maincomponentsandroid.START_DATE"
const val EXTRA_END_DATE = "com.turik2304.maincomponentsandroid.END_DATE"
const val EXTRA_DESCRIPTION = "com.turik2304.maincomponentsandroid.DESCRIPTION"
private const val REQUEST_PERMISSION_CODE: Int = 1

class SecondActivity : AppCompatActivity() {

    companion object {
       const val ACTION_MY_CUSTOM_ACTION = "com.turik2304.maincomponentsandroid.ACTION"
    }

    private lateinit var binding: ActivitySecondBinding
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var permissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions(REQUEST_PERMISSION_CODE, Manifest.permission.READ_CALENDAR)

        binding.btnGetUpcomingEvent.setOnClickListener {
            if (permissionGranted) {
                startService(this)
            } else {
                checkPermissions(REQUEST_PERMISSION_CODE, Manifest.permission.READ_CALENDAR)
            }
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Toast.makeText(
                    context,
                    "BroadcastReciever did the operation",
                    Toast.LENGTH_SHORT
                ).show()
                val titleOfEvent = intent?.getStringExtra(EXTRA_TITLE)
                val startDateOfEvent = intent?.getStringExtra(EXTRA_START_DATE)
                val endDateOfEvent = intent?.getStringExtra(EXTRA_END_DATE)
                val descriptionOfEvent = intent?.getStringExtra(EXTRA_DESCRIPTION)

                val intentForMainActivity = Intent()
                    .putExtra(EXTRA_TITLE, titleOfEvent)
                    .putExtra(EXTRA_START_DATE, startDateOfEvent)
                    .putExtra(EXTRA_END_DATE, endDateOfEvent)
                    .putExtra(EXTRA_DESCRIPTION, descriptionOfEvent)
                setResult(RESULT_OK, intentForMainActivity)
                finish()
            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true
        } else {
            Toast.makeText(this, "You must allow reading contacts!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(ACTION_MY_CUSTOM_ACTION)
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter)

    }

    override fun onStop() {
        super.onStop()
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.unregisterReceiver(broadcastReceiver)
    }

    private fun startService(context: Context) {
        val intent = Intent(this, MyIntentService::class.java)
        context.startService(intent)
    }

    private fun checkPermissions(requestCode: Int, vararg permissionsId: String) {
        for (p in permissionsId) {
            if (ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true
            } else {
                ActivityCompat.requestPermissions(this, permissionsId, requestCode)
            }
        }
    }

}