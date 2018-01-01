package com.example.batru.gamecaro.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.ui.GameActivity
import com.example.batru.gamecaro.ui.LoginActivity.Companion.mSocket
import com.github.nkzawa.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

class GameFragment : BaseFragment() {
    private val mTagGameFragment = "GameFragment"
    private lateinit var mLinearGame: LinearLayout
    private lateinit var mRootView: View

    private val mMatchParent = -1
    private val mWrapContent = -2
    private val numButtons = 20
    private val numLayouts = 13

    private val idDrawerButtonDefault = R.drawable.button_sign_in
    private val mButtonResourceMe = R.drawable.button_player_a
    private val mButtonResourceThem = R.drawable.button_player_b

    private var isFirst = true
    private var mPlayerState = PlayerState.NOTHING
    private var mRoom = ""

    private enum class PlayerState {
        WAITING, PLAYING, NOTHING
    }

    private val labelSendXAndY = "USER_SEND_X_Y_TO_ROOM"
    private val labelOnListenXAndY = "SERVER_SEND_X_Y_TO_ROOM"

    private val onListenXAndY = Emitter.Listener { args ->
        activity.runOnUiThread {
            if (mPlayerState == PlayerState.WAITING) {
                val obj = args[0] as JSONObject

                obj.remove("room")
                val button = mRootView.findViewWithTag<Button>(obj.toString())
                if (button != null) {
                    if (isFirst) {
                        obj.put("name", "B")
                        button.setBackgroundResource(mButtonResourceThem)
                    } else {
                        obj.put("name", "A")
                        button.setBackgroundResource(mButtonResourceMe)
                    }
                    button.tag = obj.toString()
                }
                mPlayerState = PlayerState.PLAYING
            } else if (mPlayerState == PlayerState.NOTHING) {
                mPlayerState = PlayerState.WAITING
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater!!.inflate(R.layout.fragment_game, container, false)

        if (arguments != null) {
            if (!arguments.getBoolean(GameActivity.tagBundlePlayerA)) {
                isFirst = false
                mPlayerState = PlayerState.WAITING
            } else {
                mPlayerState = PlayerState.PLAYING
            }
            mRoom = arguments.getString(GameActivity.tagBundleRoom)
        }
        mSocket.on(labelOnListenXAndY, onListenXAndY)

        drawLayouts(mRootView)
        return mRootView
    }

    private fun drawLayouts(rootView: View) {
        for (i in 0 until numLayouts) {
            mLinearGame = rootView.findViewById(R.id.linearGame)
            mLinearGame.addView(drawButtons(i))
        }
    }

    private fun drawButtons(position: Int): LinearLayout {
        val layout = LinearLayout(activity)
        layout.tag = position
        layout.orientation = LinearLayout.HORIZONTAL
        layout.layoutParams = LinearLayout.LayoutParams(mMatchParent, mWrapContent, 1.0f)

        val params = LinearLayout.LayoutParams(0, mWrapContent, 1.0f)
        for (i in 0 until numButtons) {
            val button = Button(activity)

            val jsonObject = JSONObject()
            jsonObject.put("x", i)
            jsonObject.put("y", position)

            button.tag = jsonObject.toString()

            button.setBackgroundResource(idDrawerButtonDefault)
            button.layoutParams = params
            layout.addView(button)

            button.setOnClickListener { handleButtonClicked(button) }
        }

        return layout
    }

    private fun handleButtonClicked(button: Button) {
        // ServerObject: {x, y, room}
        val buttonObject = JSONObject(button.tag.toString())
        try {
            buttonObject.getString("name")
            return
        } catch (e: Exception) {
            if (mPlayerState == PlayerState.PLAYING) {
                val serverObject = JSONObject(button.tag.toString())
                if (isFirst) {
                    button.setBackgroundResource(mButtonResourceMe)
                    buttonObject.put("name", "A")
                } else {
                    button.setBackgroundResource(mButtonResourceThem)
                    buttonObject.put("name", "B")
                }
                button.tag = buttonObject.toString()

                isWinner(button)

                serverObject.put("room", mRoom)
                mSocket.emit(labelSendXAndY, serverObject)
                mPlayerState = PlayerState.NOTHING
            }
        }
    }

    private fun isWinner(button: Button): Boolean {
        if (isWinningRow(button) && isWinningColumn(button))
            return true
        return false
    }

    private fun isWinningRow(button: Button): Boolean {
        val obj = JSONObject(button.tag.toString())
        val x = obj.getInt("x")
        val y = obj.getInt("y")

        val layout = mRootView.findViewWithTag<LinearLayout>(y)
        var minX = x - 4
        var maxX = x + 4
        if (minX <= 0)
            minX = 0
        if (maxX >= numButtons - 1)
            maxX = numButtons - 1

        val booleans: ArrayList<Boolean> = arrayListOf()
        for (index in minX..maxX) {
            val mButton = layout.getChildAt(index) as Button
            val buttonJSONObj = JSONObject(mButton.tag.toString())
            try {
                val name = buttonJSONObj.getString("name")
                if (isFirst) {
                    if (name == "A") {
                        booleans.add(true)
                    } else {
                        booleans.add(false)
                    }
                } else {
                    if (name == "B") {
                        booleans.add(true)
                    } else {
                        booleans.add(false)
                    }
                }
            } catch (e: Exception) {
                booleans.add(false)
            }
        }

        return checkBooleans(booleans)
    }

    private fun isWinningColumn(button: Button): Boolean {
        return false
    }

    private fun checkBooleans(booleans: ArrayList<Boolean>): Boolean {
        var sum = 0
        booleans.forEach { it ->
            if (it) {
                if (++sum == 5) return true
            } else {
                sum = 0
            }
        }
        return false
    }

}