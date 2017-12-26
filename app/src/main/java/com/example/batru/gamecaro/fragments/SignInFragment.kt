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
import com.example.batru.gamecaro.ui.LoginActivity
import org.json.JSONObject

class SignInFragment : BaseFragment() {
    private lateinit var btnSignIn: Button
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText

    private val labelEmit = "USER_SEND_SIGN_IN"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater!!.inflate(R.layout.fragment_sign_in, container, false)

        btnSignIn = rootView.findViewById(R.id.btnSignIn)
        edtEmail = rootView.findViewById(R.id.edtEmail)
        edtPassword = rootView.findViewById(R.id.edtPassword)

        btnSignIn.setOnClickListener { signIn() }

        return rootView
    }

    private fun signIn() {
        val email = edtEmail.text.toString()
        val password = edtPassword.text.toString()

        if (isStringsBlank(email, password)) {
            toast(activity, resources.getString(R.string.check_input))
            return
        }

        if (!email.contains("@")) {
            toast(activity, "Email is not valid")
            return
        }

        val map = mutableMapOf<String, String>()
        map.put("email", email)
        map.put("password", password)
        val userObj = JSONObject(map)

        LoginActivity.mSocket.emit(labelEmit, userObj)
    }
}