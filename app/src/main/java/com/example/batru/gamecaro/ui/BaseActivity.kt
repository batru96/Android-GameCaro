package com.example.batru.gamecaro.ui

import android.support.v7.app.AppCompatActivity
import android.widget.Toast

open class BaseActivity : AppCompatActivity() {

    companion object {
        val IP_ADDRESS = "http://192.168.43.180:3000"
    }

    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
