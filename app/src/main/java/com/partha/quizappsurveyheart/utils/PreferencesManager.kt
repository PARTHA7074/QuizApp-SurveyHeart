package com.partha.quizappsurveyheart.utils

import android.content.Context

class PreferencesManager(context: Context) {
    private val PREFS_NAME = "QuizAppPrefs"
    private val HIGH_SCORE_KEY = "HighScore"
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getHighScore(): Int {
        return sharedPreferences.getInt(HIGH_SCORE_KEY, 0)
    }

    fun saveHighScore(score: Int) {
        sharedPreferences.edit().putInt(HIGH_SCORE_KEY, score).apply()
    }
}
