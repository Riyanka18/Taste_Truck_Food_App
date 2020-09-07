package com.example.spicezilla.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.spicezilla.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(
            {
                val s= Intent(this@SplashActivity,
                    LoginActivity::class.java)
                startActivity(s)
                finish()
            },1000)
    }
    override fun onPause() {
        super.onPause()
        finish()
    }
}