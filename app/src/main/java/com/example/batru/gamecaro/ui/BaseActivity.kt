package com.example.batru.gamecaro.ui

import android.support.v7.app.AppCompatActivity
import android.widget.Toast

open class BaseActivity : AppCompatActivity() {

    companion object {
        val IP_ADDRESS = "http://192.168.20.122:3000"
    }

    fun toast(message: String, type: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, type).show()
    }
}
