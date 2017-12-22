package com.example.batru.gamecaro.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button

class ImageAdapter(private var context: Context) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val button: Button
        if (convertView == null) {
            button = Button(context)
            button.text = position.toString()
        } else {
            button = convertView as Button
        }
        return button
    }

    override fun getItem(position: Int): Any = Any()

    override fun getItemId(position: Int): Long = 0

    override fun getCount(): Int = 20

}