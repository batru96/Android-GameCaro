package com.example.batru.gamecaro.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.fragments.SignInFragment
import com.example.batru.gamecaro.fragments.SignUpFragment
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private var isSignIn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        addDefaultSignIn()
    }

    fun onButtonClick(view: View) {
        val transaction = fragmentManager.beginTransaction()
        when (view.id) {
            R.id.btnSignInFragment -> {
                if (!isSignIn) {
                    transaction.replace(R.id.frame_content, SignInFragment())
                    toggleButton(buttonOn = btnSignUpFragment, buttonOff = btnSignInFragment)
                }
            }
            R.id.btnSignUpFragment -> {
                if (isSignIn) {
                    transaction.replace(R.id.frame_content, SignUpFragment())
                    toggleButton(btnSignInFragment, btnSignUpFragment)
                }
            }
        }
        transaction.commit()
    }

    private fun toggleButton(buttonOn: Button, buttonOff: Button) {
        isSignIn = !isSignIn
        setButtonBackground(buttonOn, R.drawable.button_sign_in)
        setButtonBackground(buttonOff, R.drawable.button_un_enable)
        buttonOn.isEnabled = true
        buttonOff.isEnabled = false
    }

    private fun setButtonBackground(button: Button, backgroundResourceId: Int) {
        button.setBackgroundResource(backgroundResourceId)
    }

    private fun addDefaultSignIn() {
        fragmentManager.beginTransaction().add(R.id.frame_content, SignInFragment()).commit()
        setButtonBackground(btnSignInFragment, R.drawable.button_un_enable)
        btnSignInFragment.isEnabled = false
    }
}
