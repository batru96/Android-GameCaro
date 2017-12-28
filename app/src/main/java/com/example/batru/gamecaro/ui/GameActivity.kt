package com.example.batru.gamecaro.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.fragments.GameFragment
import com.example.batru.gamecaro.fragments.UserFragment
import com.example.batru.gamecaro.models.User
import com.example.batru.gamecaro.ui.LoginActivity.Companion.mSocket
import com.github.nkzawa.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class GameActivity : BaseActivity() {
    private val tag = "GameActivity"
    private val gameFragmentTag = "GameFragment"

    private val labelOnUserSignIn = "SERVER_SEND_USER_SIGN_IN_SUCCESS"
    private val labelSignOut = "USER_SIGN_OUT"
    private val labelOnUserSignOut = "SERVER_SEND_EMAIL_SIGN_OUT"
    private val labelOnListenRequest = "SERVER_SEND_REQUEST_FROM_A_TO_B"
    private val labelReplyRequest = "USER_B_REPLY_REQUEST"
    private val labelReceiveResultRequest = "SERVER_SEND_REPLY_FROM_B_TO_A"
    private val labelUserALeaveRoom = "USER_A_LEAVE_ROOM"

    private lateinit var mUserFragment: UserFragment

    companion object {
        var mEmail: String = ""
        val tagBundlePlayerA = "BUNDLE_PLAYER_A"
        val tagBundleRoom = "BUNDLE_ROOM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_screen)

        mSocket.on(labelOnUserSignIn, onUserSignInListener)
        mSocket.on(labelOnUserSignOut, onUserSignOutLister)
        mSocket.on(labelOnListenRequest, onListenRequest)
        mSocket.on(labelReceiveResultRequest, onReceiveResultRequest)

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

    private val onListenRequest = Emitter.Listener { args ->
        runOnUiThread {
            val obj = args[0] as JSONObject
            val socketId = obj.getString("socketId")
            val room = obj.getString("room")

            val map = hashMapOf<String, Any>()
            map.put("targetId", socketId)
            map.put("room", room)

            val dialog = AlertDialog.Builder(this)
                    .setTitle(resources.getString(R.string.dialog_waiting_player))
                    .setNegativeButton(resources.getString(R.string.no)) { which, _ ->
                        map.put("isAccept", false)
                        mSocket.emit(labelReplyRequest, JSONObject(map))
                        which.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                        map.put("isAccept", true)
                        mUserFragment.enableRecyclerView(false)
                        mSocket.emit(labelReplyRequest, JSONObject(map))
                        toggleGameFragment(isShow = true, isPlayerA = true, room = room)
                    }
            dialog.show()
        }
    }

    private val onReceiveResultRequest = Emitter.Listener { args ->
        runOnUiThread {
            val obj = args[0] as JSONObject
            val isAccept = obj.getBoolean("isAccept")
            val room = obj.getString("room")

            if (isAccept) {
                val dialog = AlertDialog.Builder(this)
                        .setTitle(resources.getString(R.string.fight_now))
                        .setPositiveButton("OK") { _, _ ->
                            toggleGameFragment(isShow = true, isPlayerA = false, room = room)
                            mUserFragment.enableRecyclerView(false)
                        }
                dialog.show()
            } else {
                val dialog = AlertDialog.Builder(this)
                        .setTitle(resources.getString(R.string.denied_request))
                        .setPositiveButton("OK") { _, _ ->
                            mSocket.emit(labelUserALeaveRoom, room)
                            mUserFragment.enableRecyclerView(true)
                        }
                dialog.show()
            }
        }
    }

    private fun toggleGameFragment(isShow: Boolean, isPlayerA: Boolean, room: String = "") {
        val transaction = fragmentManager.beginTransaction()
        if (isShow) {
            val gameFragment = GameFragment()
            val bundle = Bundle()
            bundle.putBoolean(tagBundlePlayerA, isPlayerA)
            bundle.putString(tagBundleRoom, room)
            gameFragment.arguments = bundle
            transaction.add(R.id.frameGameFragment, gameFragment, gameFragmentTag)
        } else {
            transaction.remove(fragmentManager.findFragmentByTag(gameFragmentTag))
        }
        transaction.commit()
    }

    private fun updateUserFragment() {
        val users = try {
            JSONArray(intent.getStringExtra("USERS"))
        } catch (e: JSONException) {
            Log.d(tag, e.message)
            JSONArray("[]")
        }
        if (users.length() > 0) {
            for (i in 0 until users.length()) {
                val obj = users.getJSONObject(i)
                mUserFragment.addNewUser(getUserFromJsonObject(obj))
                if (i == users.length() - 1) {
                    mEmail = obj.getString("email")
                }
            }
//            (0 until users.length())
//                    .map { users.getJSONObject(it) }
//                    .forEach { mUserFragment.addNewUser(getUserFromJsonObject(it)) }
        }
    }

    private fun getUserFromJsonObject(obj: JSONObject): User {
        try {
            val name = obj.getString("name")
            val email = obj.getString("email")
            val points = obj.getInt("points")
            val socketId = obj.getString("socketId")
            val isPlaying = obj.getBoolean("isPlaying")
            return User(username = name, emailAddress = email, defaultPoints = points,
                    isPlaying = isPlaying, socketId = socketId)
        } catch (e: JSONException) {
            Log.d(tag, e.message.toString())
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
        mSocket.emit(labelSignOut)
        mSocket.off(labelOnUserSignOut)
        mSocket.off(labelOnUserSignIn)
        mSocket.off(labelReceiveResultRequest)
        mSocket.off(labelOnListenRequest)
    }
}
