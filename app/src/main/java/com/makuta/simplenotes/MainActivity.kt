package com.makuta.simplenotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.makuta.simplenotes.databinding.AMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: AMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

}