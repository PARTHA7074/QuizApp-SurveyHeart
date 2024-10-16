package com.partha.quizappsurveyheart.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import com.partha.quizappsurveyheart.fragments.OnAnswerSelectedListener
import com.partha.quizappsurveyheart.pojos.Question
import com.partha.quizappsurveyheart.viewModels.QuizViewModel

class QuizActivity : AppCompatActivity(), OnAnswerSelectedListener {
    private lateinit var binding: ActivityQuizBinding
    private val viewModel: QuizViewModel by viewModels()
    private var countDownTimer: CountDownTimer? = null
    private var isNavigatedOnce = false

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
                if (savedInstanceState == null) {
                    displayQuestion(questions, viewModel.currentQuestionIndex)
                }
            }
            if (viewModel.currentQuestionIndex == questions?.size?.minus(1)) binding.nextBtn.text = "Submit"
        }

        // Observe LiveData for error messages
        viewModel.errorMessage.observe(this) { errorMessage ->
            binding.progressBar.isVisible = false
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Set up button listeners
        setupButtonListeners()

        setupTimerObserver()

    }

    private fun setupButtonListeners() {
        binding.nextBtn.setOnClickListener {
            viewModel.questionsLiveData.value?.let { questions ->
                if (viewModel.currentQuestionIndex < questions.size-1) {
                    viewModel.currentQuestionIndex++
                    displayQuestion(questions, viewModel.currentQuestionIndex)
                    if (viewModel.currentQuestionIndex == questions.size - 1) binding.nextBtn.text = "Submit"
                } else {
                    finishQuiz()
                }
            }
        }

        binding.quitBtn.setOnClickListener {
            finish()
        }
    }

    private fun displayQuestion(questions: List<Question?>, index: Int) {
        val fragmentTag = "MCQFragment_$index"

        // Check if fragment is already attached
        val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        // Load the fragment only if it does not exist already
        if (existingFragment == null) {
            questions.getOrNull(index)?.let { question ->
                val fragment = MCQFragment.newInstance(question)
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                    .replace(R.id.fragmentContainerView, fragment, fragmentTag)
                    .commit()
            } ?: run {
                Toast.makeText(this, "Failed to load question.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTimerObserver() {
        viewModel.remainingTimeLiveData.observe(this) { remainingTime ->
            if (remainingTime > 0) {
                startCountDownTimer(remainingTime)
            } else {
                if (!isNavigatedOnce) finishQuiz()
            }
        }
    }

    private fun startCountDownTimer(remainingTime: Long) {
        countDownTimer?.cancel() // Cancel any previous timer

        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60

                // Update UI
                binding.remainingTime.text = String.format("%02d:%02d", minutes, seconds)
                val progress = ((viewModel.quizDuration - millisUntilFinished).toFloat() / viewModel.quizDuration * 100).toInt()
                binding.timeProgressBar.progress = progress
            }

            override fun onFinish() {
                finishQuiz()
            }
        }.start()
    }

    private fun finishQuiz() {
        isNavigatedOnce = true
        countDownTimer?.cancel()
        navigateToScoreActivity()
    }

    private fun navigateToScoreActivity() {
        val intent = Intent(this, ScoreActivity::class.java).apply {
            putExtra("SCORE", viewModel.score)
            putExtra("TOTAL", viewModel.questionsLiveData.value?.size ?: 0)
        }
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateRemainingTime()
    }

    override fun onAnswerSelected(isCorrect: Boolean) {
        if (isCorrect) {
            viewModel.score++
        }
    }
}
