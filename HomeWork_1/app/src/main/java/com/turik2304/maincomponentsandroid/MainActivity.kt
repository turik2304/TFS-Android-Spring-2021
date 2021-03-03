package com.turik2304.maincomponentsandroid

import android.app.ActivityManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.turik2304.maincomponentsandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var title: String
    private lateinit var startDate: String
    private lateinit var endDate: String
    private lateinit var description: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSecondActivity.setOnClickListener {
            startForResult.launch(Intent(this, SecondActivity::class.java))
        }

    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            title = data?.getStringExtra(EXTRA_TITLE) ?: "none"
            startDate = data?.getStringExtra(EXTRA_START_DATE) ?: "none"
            endDate = data?.getStringExtra(EXTRA_END_DATE) ?: "none"
            description = data?.getStringExtra(EXTRA_DESCRIPTION) ?: "none"
            setTextForTextViews()
        } else {
            binding.tvUpcomingEvent.text = getString(R.string.backPressedText)
        }

    }

    private fun setTextForTextViews() {
        binding.tvTitle.text = title
        binding.tvStartDate.text = startDate
        binding.tvEndDate.text = endDate
        binding.tvDescription.text = description
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        title = binding.tvTitle.text.toString()
        startDate = binding.tvStartDate.text.toString()
        endDate = binding.tvEndDate.text.toString()
        description = binding.tvDescription.text.toString()
        outState.putString(EXTRA_TITLE, title)
        outState.putString(EXTRA_START_DATE, startDate)
        outState.putString(EXTRA_END_DATE, endDate)
        outState.putString(EXTRA_DESCRIPTION, description)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        title = savedInstanceState?.getString(EXTRA_TITLE).toString()
        startDate = savedInstanceState?.getString(EXTRA_START_DATE).toString()
        endDate = savedInstanceState?.getString(EXTRA_END_DATE).toString()
        description = savedInstanceState?.getString(EXTRA_DESCRIPTION).toString()
        setTextForTextViews()
    }


}