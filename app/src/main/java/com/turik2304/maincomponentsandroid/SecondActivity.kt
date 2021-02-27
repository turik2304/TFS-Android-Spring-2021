package com.turik2304.maincomponentsandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.turik2304.maincomponentsandroid.databinding.ActivityMainBinding
import com.turik2304.maincomponentsandroid.databinding.ActivitySecondBinding

const val MESSAGE = "com.turik2304.maincomponentsandroid.MESSAGE"

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDoSmth.setOnClickListener {
            val intent = Intent().putExtra(MESSAGE, "Some message")
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}