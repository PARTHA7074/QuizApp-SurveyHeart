package com.partha.quizappsurveyheart.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MCQViewModel(private val state: SavedStateHandle) : ViewModel() {

    // Using keys for saved state
    companion object {
        private const val SELECTED_OPTION_KEY = "selected_option"
        private const val SHUFFLED_ANSWERS_KEY = "shuffled_answers"
    }

    val selectedOption = state.getLiveData<Int>(SELECTED_OPTION_KEY)
    val shuffledAnswers = state.getLiveData<List<String>>(SHUFFLED_ANSWERS_KEY)

    fun onOptionSelected(optionId: Int) {
        state[SELECTED_OPTION_KEY] = optionId
        selectedOption.postValue(optionId)
    }

    fun setShuffledAnswersIfAbsent(answers: List<String>) {
        if (state.get<List<String>>(SHUFFLED_ANSWERS_KEY) == null) {
            state[SHUFFLED_ANSWERS_KEY] = answers
            shuffledAnswers.postValue(answers)
        }
    }

    fun getShuffledAnswers(): List<String>? {
        return state[SHUFFLED_ANSWERS_KEY]
    }
}
