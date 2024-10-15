package com.partha.quizappsurveyheart.room

import com.partha.quizappsurveyheart.pojos.Question

class RoomRepository(private val dao: Dao) {

    suspend fun getAllTasks(): List<Question>? {
        return dao.getAllQuestion()
    }

    suspend fun insert(task: Question) {
        dao.insertTask(task)
    }

    suspend fun clearAllQuestions() {
        dao.clearAllQuestions()
    }

    suspend fun insertQuestions(tasks: List<Question>) {
        dao.insertQuestions(tasks)
    }

}