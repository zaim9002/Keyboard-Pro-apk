package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.pref.KeyboardPreferences

class KeyboardProApp : Application() {

    @Volatile
    private var _database: AppDatabase? = null
    val database: AppDatabase
        get() = _database ?: synchronized(this) {
            _database ?: AppDatabase.getDatabase(this).also { _database = it }
        }

    @Volatile
    private var _preferences: KeyboardPreferences? = null
    val preferences: KeyboardPreferences
        get() = _preferences ?: synchronized(this) {
            _preferences ?: KeyboardPreferences(this).also { _preferences = it }
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
        _database = AppDatabase.getDatabase(this)
        _preferences = KeyboardPreferences(this)
    }

    companion object {
        @Volatile
        lateinit var instance: KeyboardProApp
            private set
    }
}
