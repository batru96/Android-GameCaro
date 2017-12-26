package com.example.batru.gamecaro.fragments

import android.app.Fragment
import android.content.Context
import android.os.Message
import android.widget.Toast

open class BaseFragment: Fragment() {
    fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun isStringsBlank(vararg strings: String): Boolean {
        return strings.any { it.isBlank() }
    }

}