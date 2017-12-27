package com.example.batru.gamecaro.models

import java.io.Serializable

class User(username: String = "", emailAddress: String = "", defaultPoints: Int = 0,
           isPlaying: Boolean = false, socketId: String = ""): Serializable {
    private var name: String = username
    private var email: String = emailAddress
    private var points: Int = defaultPoints
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

    var Points: Int
        get() = points
        set(value) {
            points = value
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
        return "$Name -- $Email -- $Points -- $Playing -- $SocketId"
    }
}