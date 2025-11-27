package com.google.mediapipe.examples.gesturerecognizer.data.manager

import android.content.Context
import android.content.SharedPreferences

class RoomPreferenceManager(context: Context) {
    companion object {
        private const val PREFS_NAME = "RoomPrefs"
        private const val ROOM_ID_KEY = "preferred_room_id"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var preferredRoomId: Int?
        get() {
            if (!sharedPreferences.contains(ROOM_ID_KEY)) return null
            val value = sharedPreferences.getInt(ROOM_ID_KEY, -1)
            return if (value > 0) value else null
        }
        set(value) {
            sharedPreferences.edit().apply {
                if (value == null) {
                    remove(ROOM_ID_KEY)
                } else {
                    putInt(ROOM_ID_KEY, value)
                }
            }.apply()
        }
}

