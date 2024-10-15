package com.partha.quizappsurveyheart.pojos

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Questions(

	@field:SerializedName("response_code")
	val responseCode: Int? = null,

	@field:SerializedName("results")
	val results: List<Question?>? = null
)

@Entity(tableName = "Question")
data class Question(

	@PrimaryKey(autoGenerate = true)
	var id: Int = 0,

	@field:SerializedName("difficulty")
	val difficulty: String? = null,

	@field:SerializedName("question")
	val question: String? = null,

	@field:SerializedName("correct_answer")
	val correctAnswer: String? = null,

	@field:SerializedName("incorrect_answers")
	val incorrectAnswers: List<String?>? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("category")
	val category: String? = null
)
