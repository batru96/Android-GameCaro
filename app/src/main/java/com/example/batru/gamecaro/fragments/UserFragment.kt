package com.example.batru.gamecaro.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.batru.gamecaro.R

class UserFragment: Fragment() {
    private lateinit var revUsers: RecyclerView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_users, container, false)

        revUsers = rootView.findViewById(R.id.revUsers)
        return rootView
    }
}