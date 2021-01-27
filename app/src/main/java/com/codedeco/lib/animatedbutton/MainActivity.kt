package com.codedeco.lib.animatedbutton

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.codedeco.lib.animatedbutton.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            button1.setOnClickListener {
                button1.setExpanded(!button1.isExpanded)
            }

            button2.setOnClickListener {
                button2.setExpanded(!button2.isExpanded)
            }

            button3.setOnClickListener {
                button3.setExpanded(!button3.isExpanded)
            }

            button4.setOnClickListener {
                button4.setExpanded(!button4.isExpanded)
            }

            button5.setOnClickListener {
                button5.setExpanded(!button5.isExpanded)
            }

            button6.setOnClickListener {
                button6.setExpanded(!button6.isExpanded)
            }
        }

    }
}