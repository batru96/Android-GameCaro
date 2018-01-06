package com.example.batru.gamecaro.`interface`

import com.example.batru.gamecaro.models.User

interface IUserFragment {
    fun cancelGame()

    fun itemClick(user: User)
}
