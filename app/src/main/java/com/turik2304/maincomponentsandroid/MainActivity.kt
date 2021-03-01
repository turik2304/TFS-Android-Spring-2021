package com.turik2304.maincomponentsandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.turik2304.maincomponentsandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            binding.tvTitle.text = data?.getStringExtra(EXTRA_TITLE) ?: "none"
            binding.tvStartDate.text = data?.getStringExtra(EXTRA_START_DATE) ?: "none"
            binding.tvEndDate.text =  data?.getStringExtra(EXTRA_END_DATE) ?: "none"
            binding.tvDescription.text = data?.getStringExtra(EXTRA_DESCRIPTION) ?: "none"
        } else {
            binding.tvUpcomingEvent.text = getString(R.string.backPressedText)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSecondActivity.setOnClickListener {
            startForResult.launch(Intent(this, SecondActivity::class.java))
        }

    }


}