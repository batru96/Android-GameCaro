package com.example.batru.gamecaro.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.fragments.SignInFragment
import com.example.batru.gamecaro.fragments.SignUpFragment
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray

class LoginActivity : BaseActivity() {
    companion object {
        var mSocket: Socket = IO.socket(IP_ADDRESS)
    }

    private val TAG = "LoginActivity"
    private val labelSignInSuccess = "SERVER_REPLY_SIGN_IN_SUCCESS"
    private val labelSignInFailed = "SERVER_REPLY_SIGN_IN_FAILED"
    private var isSignInFragment = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mSocket.on(labelSignInSuccess, onSignInSuccessListener)
        mSocket.on(labelSignInFailed, onSignInFailedListener)
        mSocket.connect()
        addDefaultSignIn()
    }

    private val onSignInFailedListener = Emitter.Listener {
        runOnUiThread {
            toast("Sign in failed")
        }
    }

    private val onSignInSuccessListener = Emitter.Listener { args ->
        runOnUiThread {
            val users = args[0] as JSONArray
            val intent = Intent(this, GameScreenActivity::class.java)
            intent.putExtra("USERS", users.toString())
            startActivity(intent)
        }
    }

    fun onButtonClick(view: View) {
        val transaction = fragmentManager.beginTransaction()
        when (view.id) {
            R.id.btnSignInFragment -> {
                if (!isSignInFragment) {
                    transaction.replace(R.id.frame_content, SignInFragment())
                    toggleButton(buttonOn = btnSignUpFragment, buttonOff = btnSignInFragment)
                }
            }
            R.id.btnSignUpFragment -> {
                if (isSignInFragment) {
                    transaction.replace(R.id.frame_content, SignUpFragment())
                    toggleButton(btnSignInFragment, btnSignUpFragment)
                }
            }
        }
        transaction.commit()
    }

    private fun toggleButton(buttonOn: Button, buttonOff: Button) {
        isSignInFragment = !isSignInFragment
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

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
        mSocket.off()
    }
}
