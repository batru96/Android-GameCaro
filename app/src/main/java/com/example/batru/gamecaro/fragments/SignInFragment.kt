package com.example.batru.gamecaro.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.batru.gamecaro.R

/**
 * Created by batru on 12/20/17.
 */
class SignInFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater!!.inflate(R.layout.fragment_sign_in, container, false)

        return rootView
    }
}