package com.example.batru.gamecaro.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.fragments.SignInFragment
import com.example.batru.gamecaro.fragments.SignUpFragment
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        onButtonClick(btnSignInFragment)
    }

    fun onButtonClick(view: View) {
        val transaction = fragmentManager.beginTransaction()
        when (view.id) {
            R.id.btnSignInFragment -> {
                transaction.replace(R.id.frame_content, SignInFragment())
            }
            R.id.btnSignUpFragment -> {
                transaction.replace(R.id.frame_content, SignUpFragment())
            }
        }
        transaction.commit()
    }
}
