package com.example.batru.gamecaro.models

class User(name: String = "", email: String = "", points: Int = 0) {
    private var name: String = name
    private var email: String = email
    private var points: Int = points
    private var isPlaying = false

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

    var IsPlaying: Boolean
        get() = this.isPlaying
        set(value) {
            isPlaying = value
        }
}