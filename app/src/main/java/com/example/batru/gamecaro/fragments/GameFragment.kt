package com.example.batru.gamecaro.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.batru.gamecaro.R

class GameFragment : Fragment() {
    private lateinit var mLinearGame: LinearLayout

    private val MATCH_PARENT = -1
    private val WRAP_CONTENT = -2
    private val NUMS_BUTTON = 20
    private val NUMS_LAYOUT = 13

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_game, container, false)

        drawLayouts(rootView)
        return rootView
    }

    private fun drawLayouts(rootView: View) {
        for (i in 0 until NUMS_LAYOUT) {
            mLinearGame = rootView.findViewById(R.id.linearGame)
            mLinearGame.addView(drawButtons())
        }
    }

    private fun drawButtons(): LinearLayout {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1.0f)
        layout.layoutParams = layoutParams

        val params = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f)
        for (i in 0 until NUMS_BUTTON) {
            val button = Button(context)
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