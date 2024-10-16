package com.partha.quizappsurveyheart.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.partha.quizappsurveyheart.R
import com.partha.quizappsurveyheart.databinding.ActivityScoreBinding
import com.partha.quizappsurveyheart.utils.PreferencesManager

class ScoreActivity : AppCompatActivity() {
    private val binding by lazy { ActivityScoreBinding.inflate(layoutInflater) }
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferencesManager = PreferencesManager(this)

        val score = intent.getIntExtra("SCORE", 0)
        val total = intent.getIntExtra("TOTAL", 0)

        val highScore = preferencesManager.getHighScore()

        // Update high score if the current score is higher
        if (score > highScore) {
            preferencesManager.saveHighScore(score)
        }

        // Update UI to display score and high score
        binding.score.text = "Score: $score/$total"
        binding.highScore.text = "High Score: ${preferencesManager.getHighScore()}/$total"

        binding.retryButton.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.exitButton.setOnClickListener {
            finish()
        }
    }
}
