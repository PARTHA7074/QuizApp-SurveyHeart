package com.partha.quizappsurveyheart.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.partha.quizappsurveyheart.pojos.Question
import com.partha.quizappsurveyheart.pojos.Questions
import com.partha.quizappsurveyheart.retrofit.RetrofitRepository
import com.partha.quizappsurveyheart.room.QuizDatabase
import com.partha.quizappsurveyheart.room.RoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizViewModel(application: Application): AndroidViewModel(application) {
    private val roomRepository = RoomRepository(QuizDatabase.getDatabase(application).QuizDao())

    val questionsLiveData: MutableLiveData<List<Question?>?> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()
    val remainingTimeLiveData: MutableLiveData<Long> = MutableLiveData()

    var currentQuestionIndex = 0
    var score: Int = 0
    private var quizEndTime: Long = 0L
    val quizDuration: Long = 5 * 60 * 1000 // 5 minutes in milliseconds

    init {
        if (quizEndTime == 0L) {
            quizEndTime = System.currentTimeMillis() + (quizDuration)
        }
        updateRemainingTime()
    }

    fun updateRemainingTime() {
        val remainingTime = quizEndTime - System.currentTimeMillis()
        remainingTimeLiveData.postValue(remainingTime)
    }


    // Function to get questions from Retrofit Repository and store them in the database
    fun fetchQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            RetrofitRepository.getQuestions().enqueue(object : Callback<Questions> {
                override fun onResponse(call: Call<Questions>, response: Response<Questions>) {
                    if (response.isSuccessful) {
                        val questions = response.body()
                        questions?.let {
                            questionsLiveData.postValue(it.results)
                            saveQuestionsToDb(it.results) // Save questions to the local database
                        }
                    } else {
                        errorMessage.postValue("Failed to fetch data")
                    }
                }

                override fun onFailure(call: Call<Questions>, t: Throwable) {
                    errorMessage.postValue(t.message)
                }
            })
        }
    }

    // Function to save questions to the Room database
    private fun saveQuestionsToDb(questions: List<Question?>?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (questions != null) {
                roomRepository.insertQuestions(questions as List<Question>)
            }
        }
    }

    // Function to load questions from the Room database
    fun loadQuestionsFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val questions = roomRepository.getAllQuestions()
            questionsLiveData.postValue(questions)
        }
    }

}
