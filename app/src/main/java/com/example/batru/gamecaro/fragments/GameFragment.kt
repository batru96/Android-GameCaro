package com.example.batru.gamecaro.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.models.PlayerState
import com.example.batru.gamecaro.ui.GameActivity
import com.example.batru.gamecaro.ui.LoginActivity.Companion.mSocket
import com.github.nkzawa.emitter.Emitter
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

    private fun isWinner(button: Button) {
        if (isWinningRow(button) || isWinningColumn(button) || isWinningSlash(button)) {
            toast(activity, "Thang roi ne")
        }
    }

    private fun isWinningSlash(button: Button): Boolean {
        val obj = JSONObject(button.tag.toString())
        var x = obj.getInt("x")
        val xMin = if (x - 4 < 0) 0 else x - 4

        var y = obj.getInt("y")
        val yMax = if (y + 4 > numLayouts - 1) numLayouts - 1 else y + 4

        val lamdaX = x - xMin
        val lamdaY = yMax - y

        if (lamdaX < lamdaY) {
            x = xMin
            y += lamdaX
        } else {
            y = yMax
            x -= lamdaY
        }

        val booleans: ArrayList<Boolean> = arrayListOf()
        while (x < numButtons && y > -1) {
            val layout = mRootView.findViewWithTag<LinearLayout>(y)
            val mButton = layout.getChildAt(x) as Button
            addValueToArray(mButton, booleans)
            x++; y--
        }
        return checkBooleans(booleans)
    }

    private fun isWinningRow(button: Button): Boolean {
        val obj = JSONObject(button.tag.toString())
        val x = obj.getInt("x")
        val y = obj.getInt("y")

        val layout = mRootView.findViewWithTag<LinearLayout>(y)
        val minX = if (x - 4 < 0) 0 else x - 4
        val maxX = if (x + 4 >= numButtons) numButtons - 1 else x + 4

        val booleans: ArrayList<Boolean> = arrayListOf()
        for (index in minX..maxX) {
            val mButton = layout.getChildAt(index) as Button
            addValueToArray(mButton, booleans)
        }
        return checkBooleans(booleans)
    }

    private fun isWinningColumn(button: Button): Boolean {
        val obj = JSONObject(button.tag.toString())
        val x = obj.getInt("x")
        val y = obj.getInt("y")

        val minY = if (y - 4 <= 0) 0 else y - 4
        val maxY = if (y + 4 >= numLayouts) numLayouts - 1 else y + 4

        val booleans: ArrayList<Boolean> = arrayListOf()
        for (index in minY..maxY) {
            val layout = mRootView.findViewWithTag<LinearLayout>(index)
            val mButton = layout.getChildAt(x) as Button
            addValueToArray(mButton, booleans)
        }

        return checkBooleans(booleans)
    }

    private fun addValueToArray(button: Button, booleans: ArrayList<Boolean>) {
        val buttonJSONObj = JSONObject(button.tag.toString())
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

    private fun checkBooleans(booleans: ArrayList<Boolean>): Boolean {
        var sum = 0
        booleans.forEach { it ->
            if (it) {
                if (++sum == 5)
                    return true
            } else {
                sum = 0
            }
        }
        return false
    }

}