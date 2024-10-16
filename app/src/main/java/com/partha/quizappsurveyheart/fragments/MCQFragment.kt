package com.partha.quizappsurveyheart.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.graphics.Color
import com.partha.quizappsurveyheart.R
import com.partha.quizappsurveyheart.databinding.FragmentMCQBinding
import com.partha.quizappsurveyheart.pojos.Question

class MCQFragment : Fragment() {

    private var _binding: FragmentMCQBinding? = null
    private val binding get() = _binding!!
    private var question: Question? = null
    private val viewModel: MCQViewModel by viewModels()

    companion object {
        private const val QUESTION_KEY = "question_key"

        fun newInstance(question: Question): MCQFragment {
            val fragment = MCQFragment()
            val args = Bundle()
            args.putParcelable(QUESTION_KEY, question)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMCQBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        question = arguments?.getParcelable(QUESTION_KEY)

        question?.let {
            binding.question.text = it.question

            if (viewModel.getShuffledAnswers().isNullOrEmpty()) {
                setupOptions()
            }
        }

        viewModel.shuffledAnswers.observe(viewLifecycleOwner) {
            it?.let { answers ->
                assignOptions(answers)
                setupOptionListeners()
            }
        }

        // Observe the selected option and update UI accordingly
        viewModel.selectedOption.observe(viewLifecycleOwner) {
            it?.let { selectedOption -> highlightSelectedOption(selectedOption) }
        }
    }

    private fun setupOptions() {
        // Combine correct and incorrect answers, then shuffle
        val answers = mutableListOf<String>().apply {
            add(question?.correctAnswer ?: "")
            question?.incorrectAnswers?.let { addAll(it) }
        }
        answers.shuffle()
        viewModel.setShuffledAnswersIfAbsent(answers)
    }

    private fun assignOptions(answers: List<String>) {
        binding.option1.text = answers.getOrNull(0) ?: ""
        binding.option2.text = answers.getOrNull(1) ?: ""
        binding.option3.text = answers.getOrNull(2) ?: ""
        binding.option4.text = answers.getOrNull(3) ?: ""
    }

    private fun setupOptionListeners() {
        binding.option1.setOnClickListener { viewModel.onOptionSelected(0) }
        binding.option2.setOnClickListener { viewModel.onOptionSelected(1) }
        binding.option3.setOnClickListener { viewModel.onOptionSelected(2) }
        binding.option4.setOnClickListener { viewModel.onOptionSelected(3) }
    }

    private fun highlightSelectedOption(index: Int) {
        when (index) {
            0 -> checkAnswer(binding.option1)
            1 -> checkAnswer(binding.option2)
            2 -> checkAnswer(binding.option3)
            3 -> checkAnswer(binding.option4)
        }
    }

    private fun checkAnswer(selectedOptionView: TextView) {
        val selectedAnswer = selectedOptionView.text.toString()
        val isCorrect = selectedAnswer == question?.correctAnswer

        // Change text color and set icon based on correctness
        selectedOptionView.apply {
            setTextColor(Color.parseColor(if (isCorrect) "#51F67F" else "#BF0414"))
            setCompoundDrawablesWithIntrinsicBounds(
                0, 0,
                if (isCorrect) R.drawable.ic_baseline_check_circle_24_green else R.drawable.ic_baseline_highlight_off_24_red,
                0
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
