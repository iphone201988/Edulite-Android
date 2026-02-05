package com.edu.lite.ui.dash_board.home.quiz.question

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.edu.lite.data.model.QuizAnswerApiResponse
import com.edu.lite.data.model.QuizAnswerUserResponse
import com.edu.lite.data.model.QuizQuestionApiResponse
import com.edu.lite.databinding.FragmentQuizQuestionBinding
import com.edu.lite.databinding.QuizDialogBoxItemBinding
import com.edu.lite.ui.dash_board.home.quiz.FeaturedQuizzesVM
import com.edu.lite.ui.dash_board.home.quiz.question.adapter.QuestionAdapter
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("SetTextI18n")
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
    private var totalMinutes: Double = 0.0
    private var totalQuestions = 0
    private var quizStartTimeMillis: Long = 0L
    private var timeTaking = 0

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
    }
    /**
     * click event handel
     */
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBackButton -> {
                    val answers = buildAnswers()
                    val status = getQuizStatus(answers.size)
                    val timeTakenSeconds =
                        ((System.currentTimeMillis() - quizStartTimeMillis) / 1000).coerceAtLeast(0L)
                    val finalTime = timeTaking + timeTakenSeconds.toInt()
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.apply {
                            set("quizId", args.quizId)
                            set("status", status)
                            set("timeTaken", finalTime)
                            set("answers", ArrayList(answers))
                        }
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                // Next button click
                R.id.btnNext -> {
                    val currentQuestion = questionsList[currentIndex]
                    if (currentQuestion?.userSelectedOptionId == null) {
                        showErrorToast(getString(R.string.please_select_an_answer_before_proceeding))
                    }
                    else {
                        if (currentIndex < questionsList.size - 1) {
                            currentIndex++
                            binding.tvQuestionNumber.text = "Question ${currentIndex + 1} of $totalQuestions"
                            binding.tvChoose.text = "${currentIndex + 1} of $totalQuestions"

                            // Update adapter with new question
                            questionAdapter.updateQuestions(listOf(questionsList[currentIndex]))
                            updateNavigationButtons()
                            BindingUtils.setProgress(binding.progressionGuideline, 50)
                        }
                        else {
                            BindingUtils.setProgress(binding.progressionGuideline, 100)
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
                            // api call
                            postApiCall()

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
        if (currentIndex == questionsList.size - 1)
            binding.btnNext.text = getString(R.string.submit)
         else
             binding.btnNext.text = getString(R.string.next)

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
                                    binding.tvQuestion.text=question.name
                                    totalQuestions = question.numberOfQuestions ?: 0
                                    questionsList = question.questions ?: emptyList()
                                    binding.tvQuestionNumber.text = "Question ${currentIndex + 1} of $totalQuestions"
                                    binding.tvChoose.text = "${currentIndex + 1} of $totalQuestions"
                                    questionAdapter =
                                        QuestionAdapter(listOf(questionsList[currentIndex])) { questionPos, option ->

                                        }
                                    binding.rvQuestion.adapter = questionAdapter

                                    // time show logic
                                    val seconds = model.responseData?.timeTaken ?: 0
                                    timeTaking = model.responseData?.timeTaken ?: 0
                                    val minutesTaken = seconds / 60.0
                                      totalMinutes = model.quiz.time?.toDouble() ?: 0.0
                                    val remainingMinutes = (totalMinutes - minutesTaken).coerceAtLeast(0.0)
                                    val remainingSeconds = (remainingMinutes * 60).toLong()

                                    // start timer
                                    startTimer(binding.tvTimer, remainingSeconds)
                                }

                                else showErrorToast(getString(R.string.something_went_wrong))
                            }.onFailure { e ->
                                showErrorToast(e.message.toString())
                            }.also {
                                hideLoading()
                            }
                        }
                        "postUserResponse" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: QuizAnswerApiResponse? =
                                    BindingUtils.parseJson(jsonData)
                                val question = model?.userResponse
                                if (question != null) {
                                    // Show result dialog
                                    initDialog(question)
                                } else {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
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
    private fun initDialog(selectedAnswers: QuizAnswerUserResponse) {

        quizEndDialog = BaseCustomDialog(requireActivity(), R.layout.quiz_dialog_box_item) {
            when (it?.id) {
                R.id.ivCancel -> quizEndDialog?.dismiss()
            }
        }

        quizEndDialog?.setCancelable(false)
        quizEndDialog?.show()


        quizEndDialog?.binding?.tvQuesValue?.text = totalQuestions.toString()
        quizEndDialog?.binding?.tvXCorrectPoint?.text = selectedAnswers.correctCount.toString()
        quizEndDialog?.binding?.tvWrongPoint?.text = selectedAnswers.incorrectCount.toString()
        quizEndDialog?.binding?.circularProgressBar?.apply {
            setTextView(quizEndDialog?.binding?.tvScoredValue)
            if (selectedAnswers.points!=null){
                setProgress( selectedAnswers.points)
            }
        }
        // Set values in dialog views
        quizEndDialog?.binding?.tvScoredValue?.text = "+${selectedAnswers.points}"
        quizEndDialog?.binding?.circularProgressBar?.apply {
            setTextView(quizEndDialog?.binding?.tvScoredValue)

            val total = totalQuestions
            val correct = selectedAnswers.correctCount ?: 0

            setMaxProgress(total*10)
            setProgress(correct*10)
        }
        // call back
        quizEndDialog?.setOnDismissListener {
            findNavController().popBackStack()
        }
    }
    /*** start timer ***/
    fun startTimer(
        text: AppCompatTextView, remainingSeconds: Long, onTimerFinished: (() -> Unit)? = null
    ) {
        handler.removeCallbacksAndMessages(null)
        quizStartTimeMillis = System.currentTimeMillis()
        remainingTimeMillis = remainingSeconds * 1000L
        timerRunnable = object : Runnable {
            override fun run() {
                if (remainingTimeMillis <= 0L) {
                    text.text = "00:00:00"
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
                    if (isAdded && !isDetached && context != null) {
                      postApiCall()
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
    @SuppressLint("DefaultLocale")
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


    /**
     * api call
     */
    private fun postApiCall() {
        if (questionsList.isEmpty()) return
        val answers = buildAnswers()
        val status = getQuizStatus(answers.size)
        val timeTakenSeconds = ((System.currentTimeMillis() - quizStartTimeMillis) / 1000).coerceAtLeast(0L)
        val finalTime = timeTaking+timeTakenSeconds.toInt()
        val data = HashMap<String, Any>().apply {
            put("quizId", args.quizId)
            put("answers", answers)
            put("status", status)
            put("timeTaken", finalTime)
        }
         viewModel.postUserResponse(Constants.USER_RESPONSE, data)
    }

    /**
     * build answer
     */
    private fun buildAnswers(): List<Map<String, String>> {
        return questionsList.mapNotNull { question ->
            val selectedOptionId = question?.userSelectedOptionId
            if (question?._id != null && selectedOptionId != null) {
                mapOf(
                    "questionId" to question._id, "selectedOptionId" to selectedOptionId
                )
            } else null
        }
    }

    /**
     * get quiz status
     */
    private fun getQuizStatus(answeredCount: Int): String {
        return if (answeredCount == questionsList.size) {
            "completed"
        } else {
            "in-progress"
        }
    }

}

