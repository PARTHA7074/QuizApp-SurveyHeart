package com.partha.quizappsurveyheart.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.partha.quizappsurveyheart.R
import com.partha.quizappsurveyheart.databinding.ActivityQuizBinding
import com.partha.quizappsurveyheart.fragments.MCQFragment
import com.partha.quizappsurveyheart.pojos.Question
import com.partha.quizappsurveyheart.viewModels.QuizViewModel

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (viewModel.questionsLiveData.value == null) {
            binding.progressBar.isVisible = true
            viewModel.loadQuestionsFromDb()
        }

        // Observe LiveData for questions
        viewModel.questionsLiveData.observe(this) { questions ->
            binding.progressBar.isVisible = false
            if (questions.isNullOrEmpty()) {
                viewModel.fetchQuestions()
            } else {
                // Display the current question, checking if fragment already exists
                if (savedInstanceState == null) {
                    displayQuestion(questions, viewModel.currentQuestionIndex)
                }
            }
        }

        // Observe LiveData for error messages
        viewModel.errorMessage.observe(this) { errorMessage ->
            binding.progressBar.isVisible = false
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
        }

        binding.nextBtn.setOnClickListener {
            viewModel.questionsLiveData.value?.let { questions ->
                if (viewModel.currentQuestionIndex < questions.size - 1) {
                    viewModel.currentQuestionIndex++
                    displayQuestion(questions, viewModel.currentQuestionIndex)
                } else {
                    Toast.makeText(this, "End of Quiz!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.quitBtn.setOnClickListener {
            finish()
        }
    }

    // Updated display method to prevent fragment recreation on config change
    private fun displayQuestion(questions: List<Question?>, index: Int) {
        val fragmentTag = "MCQFragment_$index"

        // Check if fragment is already attached
        val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        if (existingFragment == null) {
            questions.getOrNull(index)?.let { question ->
                val fragment = MCQFragment.newInstance(question)
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                    .replace(R.id.fragmentContainerView, fragment, fragmentTag)
                    .commit()
            }
        }
    }
}
