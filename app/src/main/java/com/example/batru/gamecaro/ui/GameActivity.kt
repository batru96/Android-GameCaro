package com.example.batru.gamecaro.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.`interface`.IUserFragment
import com.example.batru.gamecaro.fragments.GameFragment
import com.example.batru.gamecaro.fragments.UserFragment
import com.example.batru.gamecaro.models.User
import com.example.batru.gamecaro.ui.LoginActivity.Companion.mSocket
import com.github.nkzawa.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.example.batru.gamecaro.fragments.GameFragment.IWinTheGame

class GameActivity : BaseActivity(), IUserFragment, IWinTheGame {
    companion object {
        val tagBundlePlayerA = "BUNDLE_PLAYER_A"
        val tagBundleRoom = "BUNDLE_ROOM"
    }

    private val tag = "GameActivity"
    private val gameFragmentTag = "GameFragment"
    private lateinit var mUserFragment: UserFragment

    private val labelOnUserSignIn = "SERVER_SEND_USER_SIGN_IN_SUCCESS"
    private val labelOnUserSignOut = "SERVER_SEND_EMAIL_SIGN_OUT"
    private val labelOnListenRequest = "SERVER_SEND_REQUEST_FROM_A_TO_B"
    private val labelOnUserLeavesGameRoom = "SERVER_SEND_USER_LEAVES_GAME_ROOM"
    private val labelOnWinningTheGame = "SERVER_SEND_WINNING_THE_GAME"
    private val labelOnReceiveResultRequest = "SERVER_SEND_REPLY_FROM_B_TO_A"

    private val labelSignOut = "USER_SIGN_OUT"
    private val labelReplyRequest = "USER_B_REPLY_REQUEST"
    private val labelUserALeaveRoom = "USER_A_LEAVE_ROOM"
    private val labelUserLeavesGameRoom = "USER_LEAVES_GAME_ROOM"
    private val labelWinTheGame = "USER_SEND_WIN_THE_GAME"
    private val labelSendRequest = "USER_A_SEND_REQUEST"

    private var isPlaying = false
    private var mEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_screen)

        mSocket.on(labelOnUserSignIn, onUserSignInListener)
        mSocket.on(labelOnUserSignOut, onUserSignOutLister)
        mSocket.on(labelOnListenRequest, onListenRequest)
        mSocket.on(labelOnReceiveResultRequest, onReceiveResultRequest)
        mSocket.on(labelOnUserLeavesGameRoom, onUserLeaveGameRoom)
        mSocket.on(labelOnWinningTheGame, onWinningTheGame)

        mUserFragment = fragmentManager.findFragmentById(R.id.fragmentUser) as UserFragment
        updateUserFragment()
    }

    private val onUserLeaveGameRoom = Emitter.Listener {
        runOnUiThread {
            if (isPlaying) {
                toast(resources.getString(R.string.toast_you_win_after_leave_room), Toast.LENGTH_LONG)
                leavesRoom()
            }
        }
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
            val serverObj = args[0] as JSONObject
            val socketId = serverObj.getString("socketId")
            val room = serverObj.getString("room")

            val clientObj = JSONObject()
            clientObj.put("targetId", socketId)
            clientObj.put("room", room)

            if (isPlaying) {
                replyRequest(clientObj, false)
            } else {
                val dialog = AlertDialog.Builder(this)
                        .setTitle(resources.getString(R.string.dialog_waiting_player))
                        .setNegativeButton(resources.getString(R.string.no)) { which, _ ->
                            replyRequest(clientObj, false)
                            which.dismiss()
                        }
                        .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            replyRequest(clientObj, true)
                            toggleGameFragment(isShow = true, isPlayerA = true, room = room)
                        }
                dialog.show()
            }
        }
    }

    private val onReceiveResultRequest = Emitter.Listener { args ->
        runOnUiThread {
            val obj = args[0] as JSONObject
            val isAccept = obj.getBoolean("isAccept")
            val room = obj.getString("room")

            if (isAccept) {
                toggleGameFragment(isShow = true, isPlayerA = false, room = room)
                val dialog = AlertDialog.Builder(this)
                        .setTitle(resources.getString(R.string.fight_now))
                        .setPositiveButton("OK", null)

                dialog.show()
            } else {
                val dialog = AlertDialog.Builder(this)
                        .setTitle(resources.getString(R.string.denied_request))
                        .setPositiveButton("OK") { _, _ ->
                            mSocket.emit(labelUserALeaveRoom, room)
                        }
                dialog.show()
            }
        }
    }

    private val onWinningTheGame = Emitter.Listener { args ->
        runOnUiThread {
            val email = args[0].toString()
            val dialog = AlertDialog.Builder(this@GameActivity)
                    .setTitle("The game is over")
                    .setPositiveButton("OK") { _, _ ->
                        toggleGameFragment(false)
                    }
            if (email == mEmail) {
                dialog.setMessage("You win!")
            } else {
                dialog.setMessage("You're idiot, LOSER!")
            }
            dialog.show()
        }
    }

    private fun replyRequest(jsonObject: JSONObject, isAccept: Boolean) {
        jsonObject.put("isAccept", isAccept)
        mSocket.emit(labelReplyRequest, jsonObject)
    }

    private fun toggleGameFragment(isShow: Boolean, isPlayerA: Boolean = true, room: String = "") {
        val transaction = fragmentManager.beginTransaction()
        if (isShow) {
            val gameFragment = GameFragment()
            val bundle = Bundle()
            bundle.putBoolean(tagBundlePlayerA, isPlayerA)
            bundle.putString(tagBundleRoom, room)
            gameFragment.arguments = bundle
            transaction.add(R.id.frameGameFragment, gameFragment, gameFragmentTag)
            isPlaying = true
            mUserFragment.mCancelButton.visibility = View.VISIBLE
        } else {
            transaction.remove(fragmentManager.findFragmentByTag(gameFragmentTag))
            isPlaying = false
            mUserFragment.mCancelButton.visibility = View.INVISIBLE
        }
        transaction.commit()
    }

    private fun updateUserFragment() {
        val users = try {
            JSONArray(intent.getStringExtra("USERS"))
        } catch (e: JSONException) {
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
        }
    }

    private fun getUserFromJsonObject(obj: JSONObject): User {
        try {
            val name = obj.getString("name")
            val email = obj.getString("email")
            val socketId = obj.getString("socketId")
            val isPlaying = obj.getBoolean("isPlaying")
            return User(name, email, isPlaying, socketId)
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
        mSocket.off(labelOnReceiveResultRequest)
        mSocket.off(labelOnListenRequest)
        mSocket.off(labelOnUserLeavesGameRoom)
        mSocket.off(labelOnWinningTheGame)
    }

    override fun cancelGame() {
        val dialog = android.app.AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.exit))
                .setMessage(resources.getString(R.string.dialog_cancel_message))
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    leavesRoom()
                }
                .setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                }
        dialog.show()
    }

    private fun leavesRoom() {
        val gameFragment = fragmentManager.findFragmentByTag(gameFragmentTag) as GameFragment
        toggleGameFragment(isShow = false)
        mSocket.emit(labelUserLeavesGameRoom, gameFragment.mRoom)
    }

    override fun winningTheGame(room: String) {
        mSocket.emit(labelWinTheGame, room)
    }

    override fun itemClick(user: User) {
        if (isPlaying || user.Email == mEmail) {
            toast(resources.getString(R.string.cannot_send_a_request))
            return
        }
        val dialog = AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.send_a_request))
                .setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    mSocket.emit(labelSendRequest, user.SocketId)
                }
        dialog.show()
    }
}
