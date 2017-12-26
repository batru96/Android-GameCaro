package com.example.batru.gamecaro.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro._global.VolleySingleton
import com.example.batru.gamecaro.ui.BaseActivity
import org.json.JSONObject
import java.io.UnsupportedEncodingException

class SignUpFragment : BaseFragment() {
    private val TAG = "SignUpFragment"
    private var URL = "${BaseActivity.IP_ADDRESS}/sign_up"

    private lateinit var btnRegister: Button
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater!!.inflate(R.layout.fragment_sign_up, container, false)

        btnRegister = rootView.findViewById(R.id.btnRegister)
        edtName = rootView.findViewById(R.id.edtName)
        edtEmail = rootView.findViewById(R.id.edtEmail)
        edtPassword = rootView.findViewById(R.id.edtPassword)
        edtConfirmPassword = rootView.findViewById(R.id.edtConfirmPassword)

        btnRegister.setOnClickListener { register() }

        return rootView
    }

    private fun register() {
        val email = edtEmail.text.toString()
        val name = edtName.text.toString()
        val password = edtPassword.text.toString()
        val cPassword = edtConfirmPassword.text.toString()

        if (isStringsBlank(email, name, password, cPassword)) {
            toast(activity, resources.getString(R.string.check_input))
            return
        }

        if (!email.contains("@")) {
            toast(activity, "Email is not valid")
            return
        }

        if (password != cPassword) {
            toast(activity, resources.getString(R.string.password_incorrect))
        } else {
            val values = JSONObject()
            values.put("name", name)
            values.put("email", email)
            values.put("password", password)
            val body = values.toString()

            val mRequest = object : StringRequest(Request.Method.POST, URL, Response.Listener<String> {
                if (it == "THANH_CONG") {
                    toast(activity, "Success")
                } else {
                    toast(activity, "Email is exist")
                }
            }, Response.ErrorListener {
                Log.d(TAG, it.message.toString())
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    try {
                        if (body.isNotEmpty()) {
                            return body.toByteArray()
                        }
                    } catch (e: UnsupportedEncodingException) {
                        Log.d(TAG, e.message.toString())
                    }
                    return super.getBody()
                }
            }
            VolleySingleton.getInstance(activity).addToRequestQueue(mRequest)
        }
    }


}