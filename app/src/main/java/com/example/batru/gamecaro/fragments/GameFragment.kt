package com.example.batru.gamecaro.fragments

import android.graphics.drawable.Drawable
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
import org.json.JSONObject

class GameFragment : BaseFragment() {
    private val mTagGameFragment = "GameFragment"
    private lateinit var mLinearGame: LinearLayout
    private lateinit var mRootView: View

    private val matchParent = -1
    private val wrapContent = -2
    private val numButtons = 20
    private val numLayouts = 13

    private val idDrawerButtonDefault = R.drawable.button_sign_in
    private val idDrawerButtonA = R.drawable.button_player_a
    private val idDrawerButtonB = R.drawable.button_player_b
    private lateinit var mDrawableA: Drawable
    private lateinit var mDrawableB: Drawable

    private var isPlayerA = true
    private var isYourTurn = true
    private var mRoom = ""

    private val labelSendXAndY = "USER_SEND_X_Y_TO_ROOM"
    private val labelOnListenXAndY = "SERVER_SEND_X_Y_TO_ROOM"

    private val onListenXAndY = Emitter.Listener { args ->
        activity.runOnUiThread {
            val obj = args[0] as JSONObject
            val playerA = obj.getBoolean("isPlayerA")

            obj.remove("room")
            obj.remove("isPlayerA")
            val button = mRootView.findViewWithTag<Button>(obj.toString())
            if (button != null) {
                if (playerA) {
                    obj.put("playerName", "A")
                    button.tag = obj.toString()
                    button.background = mDrawableA
                } else {
                    obj.put("playerName", "B")
                    button.tag = obj.toString()
                    button.background = mDrawableB
                }
                isWinner(button)
                // Nếu người A gửi và người B nhận, đến lượt người B được gửi
                if (playerA != isPlayerA) {
                    isYourTurn = true
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater!!.inflate(R.layout.fragment_game, container, false)

        mDrawableA = activity.getDrawable(R.drawable.button_player_a)
        mDrawableB = activity.getDrawable(R.drawable.button_player_b)

        if (arguments != null) {
            if (!arguments.getBoolean(GameActivity.tagBundlePlayerA)) {
                isPlayerA = false
                isYourTurn = false
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
        layout.layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent, 1.0f)

        val params = LinearLayout.LayoutParams(0, wrapContent, 1.0f)
        for (i in 0 until numButtons) {
            val button = Button(activity)

            val jsonObject = JSONObject()
            jsonObject.put("x", i)
            jsonObject.put("y", position)

            button.tag = jsonObject.toString()

            jsonObject.put("room", mRoom)
            jsonObject.put("isPlayerA", isPlayerA)

            button.setBackgroundResource(idDrawerButtonDefault)
            button.layoutParams = params
            layout.addView(button)

            button.setOnClickListener { handleButtonClicked(jsonObject) }
        }

        return layout
    }

    private fun handleButtonClicked(obj: JSONObject) {
        if (isYourTurn) {
            mSocket.emit(labelSendXAndY, obj)
            isYourTurn = false
        }
    }

    private fun isWinner(button: Button): Boolean {
        val obj = JSONObject(button.tag as String)
        val x = obj.getInt("x")
        val y = obj.getInt("y")

        // region Kiem tra theo hang ngang
        val layout = mRootView.findViewWithTag<LinearLayout>(y)

        var minX = x - 4
        var maxX = x + 4
        if (minX <= 0)
            minX = 0
        if (maxX >= numButtons - 1)
            maxX = numButtons - 1

        val checks = ArrayList<Boolean>()
        for (i in minX..maxX) {
            val mButton = layout.getChildAt(i) as Button
            Log.d(mTagGameFragment, "x: $i -- ${mButton.tag}")
        }


        //endregion

        return false
    }
}