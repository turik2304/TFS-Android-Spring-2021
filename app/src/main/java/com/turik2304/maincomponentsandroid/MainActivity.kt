package com.turik2304.maincomponentsandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.turik2304.maincomponentsandroid.databinding.ActivityMainBinding

const val REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.textView.text = result.data?.getStringExtra(MESSAGE) ?: "fuck"
        } else {
            binding.textView.text = "Back pressed"
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