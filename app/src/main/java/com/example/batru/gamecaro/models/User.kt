package com.example.batru.gamecaro.models

import com.example.batru.gamecaro.R.string.points
import java.io.Serializable

class User(username: String = "", emailAddress: String = "", isPlaying: Boolean = false, socketId: String = "") : Serializable {
    private var name: String = username
    private var email: String = emailAddress
    private var playing = isPlaying
    private var id = socketId

    var Name: String
        get() = name
        set(value) {
            name = value
        }

    var Email: String
        get() = email
        set(value) {
            email = value
        }

    var Playing: Boolean
        get() = this.playing
        set(value) {
            playing = value
        }

    var SocketId: String
        get() = this.id
        set(value) {
            id = value
        }

    override fun toString(): String {
        return "$Name -- $Email -- $Playing -- $SocketId"
    }
}