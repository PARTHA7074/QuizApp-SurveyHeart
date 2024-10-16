package com.partha.quizappsurveyheart.room

import com.partha.quizappsurveyheart.pojos.Question

class RoomRepository(private val quizDao: QuizDao) {

    suspend fun getAllQuestions(): List<Question>? {
        return quizDao.getAllQuestion()
    }

    suspend fun insert(task: Question) {
        quizDao.insertTask(task)
    }

    suspend fun clearAllQuestions() {
        quizDao.clearAllQuestions()
    }

    suspend fun insertQuestions(tasks: List<Question>) {
        quizDao.insertQuestions(tasks)
    }

}
