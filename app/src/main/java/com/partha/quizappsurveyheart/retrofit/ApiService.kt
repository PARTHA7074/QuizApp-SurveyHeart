package com.partha.quizappsurveyheart.retrofit

import com.partha.quizappsurveyheart.pojos.Questions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("api.php")
    fun getQuestions(
        @Query("amount") amount: Int = 10,
        @Query("category") category: Int = 18,
        @Query("difficulty") difficulty: String = "medium",
        @Query("type") type: String = "multiple"
    ): Call<Questions>
}
