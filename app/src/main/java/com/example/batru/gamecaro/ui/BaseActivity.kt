package com.example.batru.gamecaro.ui

import android.support.v7.app.AppCompatActivity
import android.widget.Toast

/**
 * Created by batru on 12/20/17.
 */
open class BaseActivity: AppCompatActivity() {

    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}