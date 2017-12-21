package com.example.batru.gamecaro.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.batru.gamecaro.R

class SignUpFragment: Fragment() {
    private lateinit var btnRegister: Button
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater!!.inflate(R.layout.fragment_sign_up, container, false)

        btnRegister = rootView.findViewById(R.id.btnRegister)
        edtName = rootView.findViewById(R.id.edtName)
        edtEmail = rootView.findViewById(R.id.edtPassword)
        edtPassword = rootView.findViewById(R.id.edtPassword)
        edtConfirmPassword = rootView.findViewById(R.id.edtConfirmPassword)

        return rootView
    }
}