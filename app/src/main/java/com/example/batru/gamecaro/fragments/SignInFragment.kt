package com.example.batru.gamecaro.fragments

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.ui.GameScreenActivity

class SignInFragment: Fragment() {
    private lateinit var btnSignIn: Button
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater!!.inflate(R.layout.fragment_sign_in, container, false)

        btnSignIn = rootView.findViewById(R.id.btnSignIn)
        edtEmail = rootView.findViewById(R.id.edtEmail)
        edtPassword = rootView.findViewById(R.id.edtPassword)

        btnSignIn.setOnClickListener {
            activity.startActivity(Intent(activity, GameScreenActivity::class.java))
        }

        return rootView
    }
}