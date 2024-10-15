package com.partha.quizappsurveyheart.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.partha.quizappsurveyheart.pojos.Question

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Question)

    @Query("SELECT * FROM Question")
    suspend fun getAllQuestion(): List<Question>?

    @Query("DELETE FROM Question")
    suspend fun clearAllQuestions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)

}
