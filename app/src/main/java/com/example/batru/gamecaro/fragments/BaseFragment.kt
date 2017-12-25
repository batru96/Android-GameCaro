package com.example.batru.gamecaro.fragments

import android.app.Fragment
import android.content.Context
import android.os.Message
import android.widget.Toast

/**
 * Created by batru on 12/25/17.
 */
open class BaseFragment: Fragment() {
    fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}