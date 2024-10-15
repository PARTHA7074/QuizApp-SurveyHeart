package com.partha.quizappsurveyheart.retrofit

import com.partha.quizappsurveyheart.pojos.Questions
import retrofit2.Call

object RetrofitRepository {
    private val apiService = RetrofitClient.apiService

    fun getQuestions(): Call<Questions> {
        return apiService.getQuestions()
    }

}