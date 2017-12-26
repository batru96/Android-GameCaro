package com.example.batru.gamecaro.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.fragments.UserFragment
import com.example.batru.gamecaro.models.User
import com.github.nkzawa.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class GameActivity : AppCompatActivity() {
    private val TAG = "GameActivity"
    private val labelOnUserSignIn = "SERVER_SEND_USER_SIGN_IN_SUCCESS"
    private val labelSignOut = "USER_SIGN_OUT"
    private val labelOnUserSignOut = "SERVER_SEND_EMAIL_SIGN_OUT"

    private lateinit var mUserFragment: UserFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_screen)

        LoginActivity.mSocket.on(labelOnUserSignIn, onUserSignInListener)
        LoginActivity.mSocket.on(labelOnUserSignOut, onUserSignOutLister)

        mUserFragment = fragmentManager.findFragmentById(R.id.fragmentUser) as UserFragment
        updateUserFragment()
    }

    private val onUserSignOutLister = Emitter.Listener { args ->
        runOnUiThread {
            val email = args[0].toString()
            mUserFragment.removeUser(email)
        }
    }

    private val onUserSignInListener = Emitter.Listener { args ->
        runOnUiThread {
            val obj = args[0] as JSONObject
            mUserFragment.addNewUser(getUserFromJsonObject(obj))
        }
    }

    private fun updateUserFragment() {
        val users = try {
            JSONArray(intent.getStringExtra("USERS"))
        } catch (e: JSONException) {
            Log.d(TAG, e.message)
            JSONArray("[]")
        }
        if (users.length() > 0) {
            for (i in 0 until users.length()) {
                val obj = users.getJSONObject(i)
                mUserFragment.addNewUser(getUserFromJsonObject(obj))
            }
        }
    }

    private fun getUserFromJsonObject(obj: JSONObject): User {
        try {
            val name = obj.getString("name")
            val email = obj.getString("email")
            val points = obj.getInt("points")
            return User(name = name, email = email, points = points)
        } catch (e: JSONException) {
            Log.d(TAG, e.message.toString())
        }
        return User()
    }

    override fun onBackPressed() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Close the game?")
        dialog.setNegativeButton("NO") { _, _ ->

        }
        dialog.setPositiveButton("YES") { _, _ ->
            super.onBackPressed()
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "DESTROY")
        LoginActivity.mSocket.emit(labelSignOut)
        LoginActivity.mSocket.off(labelOnUserSignOut)
        LoginActivity.mSocket.off(labelOnUserSignIn)
    }
}
