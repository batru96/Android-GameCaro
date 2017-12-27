package com.example.batru.gamecaro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.batru.gamecaro.R

class GameFragment : BaseFragment() {
    private lateinit var mLinearGame: LinearLayout

    private val matchParent = -1
    private val wrapContent = -2
    private val numButtons = 20
    private val numLayouts = 13

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_game, container, false)

        drawLayouts(rootView)
        return rootView
    }

    private fun drawLayouts(rootView: View) {
        for (i in 0 until numLayouts) {
            mLinearGame = rootView.findViewById(R.id.linearGame)
            mLinearGame.addView(drawButtons())
        }
    }

    private fun drawButtons(): LinearLayout {
        val layout = LinearLayout(activity)
        layout.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent, 1.0f)
        layout.layoutParams = layoutParams

        val params = LinearLayout.LayoutParams(0, wrapContent, 1.0f)
        for (i in 0 until numButtons) {
            val button = Button(activity)
            button.setBackgroundResource(R.drawable.button_sign_in)
            button.layoutParams = params
            layout.addView(button)

            button.setOnClickListener { onButtonClicked(button) }
        }

        return layout
    }

    private fun onButtonClicked(button: Button) {
        button.setBackgroundResource(R.drawable.button_player_a)
    }
}