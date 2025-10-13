package com.edu.lite.ui.auth.dash_board.home.quiz.question

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.QuestionData
import com.edu.lite.data.model.QuizQuestionApiResponse
import com.edu.lite.databinding.FragmentQuizQuestionBinding
import com.edu.lite.databinding.QuizDialogBoxItemBinding
import com.edu.lite.ui.auth.dash_board.home.quiz.FeaturedQuizzesVM
import com.edu.lite.ui.auth.dash_board.home.quiz.question.adapter.QuestionAdapter
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuizQuestionFragment : BaseFragment<FragmentQuizQuestionBinding>() {
    private val viewModel: FeaturedQuizzesVM by viewModels()
    private val args: QuizQuestionFragmentArgs by navArgs()
    private var quizEndDialog: BaseCustomDialog<QuizDialogBoxItemBinding>? = null
    private var currentIndex = 0
    private var questionsList: List<QuestionData?> = emptyList()
    private lateinit var questionAdapter: QuestionAdapter
    private var handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var remainingTimeMillis: Long = 0L
    private var totalQuestions = 0

    override fun getLayoutResource(): Int {
        return R.layout.fragment_quiz_question
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // api call
        viewModel.quizQuestionApi(Constants.TEST_QUIZ + "/${args.quizId}")
        // click
        initOnClick()
        // observer
        initObserver()
        // start timer
        startTimer(binding.tvTimer, 1)
    }

    /**
     * click event handel
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBackButton -> {
                    findNavController().popBackStack()
                }

                // Next button click
                R.id.btnNext -> {
                    val currentQuestion = questionsList[currentIndex]
                    if (currentQuestion?.userSelectedOptionId == null) {
                        showErrorToast("Please select an answer before proceeding")
                    } else {
                        if (currentIndex < questionsList.size - 1) {
                            currentIndex++
                            binding.tvQuestionNumber.text = "Question ${currentIndex + 1} of $totalQuestions"
                            binding.tvChoose.text = "${currentIndex + 1} of $totalQuestions"

                            // Update adapter with new question
                            questionAdapter.updateQuestions(listOf(questionsList[currentIndex]))
                            updateNavigationButtons()
                            BindingUtils.setProgress(binding.progressionGuideline,50)
                        } else {
                            BindingUtils.setProgress(binding.progressionGuideline,100)
                            // stop timer
                            stopTimer()
                            // Last question
                            val selectedAnswers = questionsList.mapNotNull { question ->
                                question?.userSelectedOptionId?.let { selectedOptionId ->
                                    val selectedOption =
                                        question.options?.find { it?._id == selectedOptionId }
                                    mapOf(
                                        "question_id" to question._id,
                                        "selected_option_id" to selectedOptionId,
                                        "selected_answer_text" to selectedOption?.text
                                    )
                                }
                            }

                            if (selectedAnswers.size < questionsList.size) {
                                showErrorToast("Please answer all questions before submitting")
                                return@observe
                            }

                            // Show result dialog
                            initDialog(selectedAnswers)
                        }

                    }
                }

                // Previous button click
                R.id.btnPrevious -> {
                    if (currentIndex > 0) {
                        currentIndex--
                        binding.tvQuestionNumber.text =
                            "Question ${currentIndex + 1} of $totalQuestions"
                        binding.tvChoose.text = "${currentIndex + 1} of $totalQuestions"

                        // Update adapter with previous question
                        questionAdapter.updateQuestions(listOf(questionsList[currentIndex]))
                        updateNavigationButtons()
                    }
                }

            }
        }
    }

    /**
     * check current index
     */
    private fun updateNavigationButtons() {
        // Previous button visibility
        binding.btnPrevious.visibility = if (currentIndex == 0) View.GONE else View.VISIBLE
        // Next button text
        if (currentIndex == questionsList.size - 1) {
            binding.btnNext.text = "Submit"
        } else {
            binding.btnNext.text = "Next"
        }
    }


    /** api response observer ***/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "quizQuestionApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: QuizQuestionApiResponse? =
                                    BindingUtils.parseJson(jsonData)
                                val question = model?.quiz
                                if (question != null) {

                                    totalQuestions = question.numberOfQuestions ?: 0
                                    questionsList = question.questions ?: emptyList()

                                    binding.tvQuestionNumber.text =
                                        "Question ${currentIndex + 1} of $totalQuestions"
                                    binding.tvChoose.text = "${currentIndex + 1} of $totalQuestions"


                                    questionAdapter =
                                        QuestionAdapter(listOf(questionsList[currentIndex])) { questionPos, option ->
                                            Log.d(
                                                "Quiz",
                                                "Selected option ${option?.text} for question $questionPos"
                                            )
                                        }
                                    binding.rvQuestion.adapter = questionAdapter


                                } else {
                                    showErrorToast("Something went wrong")
                                }
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(e.message.toString())
                            }.also {
                                hideLoading()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
    }

    /**
     *  handel results dialog
     **/
    private fun initDialog(selectedAnswers: List<Map<String, String?>>) {
        val (totalQuestions, correctAnswers, wrongAnswers, score) = getQuizResult(
            selectedAnswers, questionsList
        )

        quizEndDialog = BaseCustomDialog(requireActivity(), R.layout.quiz_dialog_box_item) {
            when (it?.id) {
                R.id.ivCancel -> quizEndDialog?.dismiss()
            }
        }

        quizEndDialog?.setCancelable(false)
        quizEndDialog?.show()

        // Set values in dialog views
        quizEndDialog?.binding?.tvScoredValue?.text = "+$score"
        quizEndDialog?.binding?.tvQuesValue?.text = totalQuestions.toString()
        quizEndDialog?.binding?.tvXCorrectPoint?.text = correctAnswers.toString()
        quizEndDialog?.binding?.tvWrongPoint?.text = wrongAnswers.toString()
        quizEndDialog?.binding?.circularProgressBar?.apply {
            setTextView(quizEndDialog?.binding?.tvScoredValue)
            setProgress(score)
        }

        // call back
        quizEndDialog?.setOnDismissListener {
            findNavController().popBackStack()
        }

    }

    /**
     * get quiz result methode
     */
    private fun getQuizResult(
        selectedAnswers: List<Map<String, String?>>, questionsList: List<QuestionData?>
    ): Quadruple<Int, Int, Int, Int> {
        // total, correct, wrong, score
        var correct = 0
        var wrong = 0
        val total = questionsList.size

        selectedAnswers.forEach { answerMap ->
            val questionId = answerMap["question_id"]
            val selectedText = answerMap["selected_answer_text"]

            val question = questionsList.find { it?._id == questionId }
            if (question != null) {
                if (selectedText == question.answer) {
                    correct++
                } else {
                    wrong++
                }
            }
        }

        // Calculate score: max 100 points
        val pointsPerQuestion = if (total > 0) 100 / total else 0
        val score = correct * pointsPerQuestion

        return Quadruple(total, correct, wrong, score)
    }

    // Helper class for returning 4 values
    data class Quadruple<A, B, C, D>(
        val first: A, val second: B, val third: C, val fourth: D
    )

    /*** start timer ***/
    fun startTimer(
        text: AppCompatTextView, minutes: Int, onTimerFinished: (() -> Unit)? = null
    ) {
        remainingTimeMillis = minutes * 60 * 1000L
        timerRunnable = object : Runnable {
            override fun run() {
                if (remainingTimeMillis <= 0L) {
                    text.text = "00:00:00"

                    // Timer finished, prepare selected answers
                    val selectedAnswers = questionsList.mapNotNull { question ->
                        question?.userSelectedOptionId?.let { selectedOptionId ->
                            val selectedOption =
                                question.options?.find { it?._id == selectedOptionId }
                            mapOf(
                                "question_id" to question._id,
                                "selected_option_id" to selectedOptionId,
                                "selected_answer_text" to selectedOption?.text
                            )
                        }
                    }

                    // Check fragment is still attached before showing dialog
                    if (isAdded && !isDetached && context != null) {
                        initDialog(selectedAnswers)
                    } else {
                        Log.w("QuizTimer", "Fragment not attached — skipping dialog display")
                    }

                    onTimerFinished?.invoke()
                } else {
                    updateCountdown(text)
                    remainingTimeMillis -= 1000L
                    handler.postDelayed(this, 1000L)
                }
            }
        }
        handler.post(timerRunnable!!)
    }


    /*** update count down ****/
    private fun updateCountdown(text: AppCompatTextView) {
        val hours = (remainingTimeMillis / 3600000).toInt()
        val minutes = ((remainingTimeMillis % 3600000) / 60000).toInt()
        val seconds = ((remainingTimeMillis % 60000) / 1000).toInt()

        text.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    /*** stop timer if needed ***/
    fun stopTimer() {
        timerRunnable?.let { handler.removeCallbacks(it) }
    }


}

