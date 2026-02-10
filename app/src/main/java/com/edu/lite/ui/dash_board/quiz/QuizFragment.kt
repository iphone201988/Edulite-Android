package com.edu.lite.ui.dash_board.quiz

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.GradeByIdResponse
import com.edu.lite.data.model.RoadMapQuizze
import com.edu.lite.data.model.RoadMapResponse
import com.edu.lite.data.model.SubjectData
import com.edu.lite.data.model.UserResponse
import com.edu.lite.databinding.DialogQuizCompletedBinding
import com.edu.lite.databinding.FragmentQuizBinding
import com.edu.lite.databinding.RvQuizFilterItemBinding
import com.edu.lite.databinding.RvQuizItemBinding
import com.edu.lite.ui.dash_board.home.quiz.FeaturedQuizzesFragmentDirections
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@AndroidEntryPoint
class QuizFragment : BaseFragment<FragmentQuizBinding>() {
    private val viewModel: QuizFragmentVM by viewModels()
    private lateinit var quizzesAdapter: SimpleRecyclerViewAdapter<RoadMapQuizze, RvQuizItemBinding>
    private lateinit var quizFilterAdapter: SimpleRecyclerViewAdapter<SubjectData, RvQuizFilterItemBinding>
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private var subject: String? = null
    private var quizEndDialog: BaseCustomDialog<DialogQuizCompletedBinding>? = null

    private val clickHandler = Handler(Looper.getMainLooper())
    private var clickRunnable: Runnable? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_quiz
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initOnFeatureQuizzesAdapter()
        initOnFeatureCategoryAdapter()
        val subjectId = sharedPrefManager.getLoginData()?.gradeId?._id
        if (!subjectId.isNullOrEmpty()) {
            // api call
            viewModel.getGradeApi(Constants.GRADES + "/${subjectId}")
        } else {
            // api call
            val data = HashMap<String, Any>()
            data["type"] = "quiz"
            data["page"] = currentPage
            sharedPrefManager.getLoginData()?.grade?.let {
                data["grade"] = it
            }
            viewModel.getQuizApi(data, Constants.TEST_QUIZ_DATA)
        }
        // observer
        initObserver()
        // pagination
        pagination()
    }

    /**
     * click event handel
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
            }

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
                        "getQuizApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: RoadMapResponse? = BindingUtils.parseJson(jsonData)

                                // ✅ SAFETY CHECK
                                if (model == null) {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                    return@runCatching
                                }

                                val quizzes = model.quizzes.orEmpty()
                                if (currentPage == 1) {
                                    quizzesAdapter.list = quizzes
                                } else {
                                    quizzesAdapter.addToList(quizzes)
                                }

                                isLastPage = model.pagination?.hasNextPage != true

                                binding.listEmpty.visibility =
                                    if (quizzesAdapter.list.isEmpty()) View.VISIBLE else View.GONE

                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                binding.listEmpty.visibility = View.VISIBLE
                                showErrorToast(
                                    e.localizedMessage ?: getString(R.string.something_went_wrong)
                                )
                            }.also {
                                // ✅ ALWAYS HIDE LOADER
                                hideLoading()
                            }
                        }

                        "getGradeApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: GradeByIdResponse? = BindingUtils.parseJson(jsonData)
                                val grade = model?.grade
                                if (grade != null) {
                                    val list = grade.subjects.orEmpty().toMutableList()
                                    list.removeAll { it?.name.equals("All", true) }
                                    val allItem = SubjectData(
                                        _id = null,
                                        icon = null,
                                        name = "All",
                                        types = null,
                                        check = true
                                    )
                                    list.add(0, allItem)
                                    quizFilterAdapter.list = list
                                } else {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(e.message.orEmpty())
                            }.also {
                                isLoading = false
                                val data = HashMap<String, Any>()
                                data["type"] = "quiz"
                                data["page"] = currentPage
                                viewModel.getQuizApi(data, Constants.TEST_QUIZ_DATA)
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    isLoading = false
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
    }

    /**
     *  Quiz adapter
     */
    private fun initOnFeatureQuizzesAdapter() {
        quizzesAdapter = SimpleRecyclerViewAdapter(R.layout.rv_quiz_item, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.tvShowMore -> {
                    m.isExpanded = !m.isExpanded
                    quizzesAdapter.notifyItemChanged(pos)
                }

                R.id.clLetPlay -> {
                    if (m.userResponse?.status == "completed") {
                        initDialog(m.userResponse)
                    } else {
                        val action =
                            FeaturedQuizzesFragmentDirections.navigateToQuizQuestionFragment(quizId = m._id.toString())
                        BindingUtils.navigateWithSlide(findNavController(), action)
                    }

                }

            }
        }

        binding.rvQuizzes.adapter = quizzesAdapter

    }


    /**
     * Feature category adapter
     */
    private fun initOnFeatureCategoryAdapter() {
        quizFilterAdapter = SimpleRecyclerViewAdapter(
            R.layout.rv_quiz_filter_item, BR.bean
        ) { v, m, _ ->

            if (v?.id == R.id.clPracticeCategory) {

                // cancel any previous pending click
                clickRunnable?.let { clickHandler.removeCallbacks(it) }

                clickRunnable = Runnable {

                    currentPage = 1
                    isLastPage = false
                    isLoading = false

                    quizzesAdapter.list = emptyList()

                    quizFilterAdapter.list.forEach {
                        it.check = it.name == m.name
                    }
                    quizFilterAdapter.notifyDataSetChanged()

                    val data = HashMap<String, Any>().apply {
                        put("type", "quiz")
                        put("page", currentPage)

                        if (!m.name.equals("All", ignoreCase = true)) {
                            put("subject", m.name.orEmpty())
                        }
                    }

                    subject = m.name
                    viewModel.getQuizApi(data, Constants.TEST_QUIZ_DATA)
                }

                clickHandler.postDelayed(clickRunnable!!, 1000)
            }
        }

        binding.rvCategory.adapter = quizFilterAdapter
    }

    /**
     * quiz handel pagination
     */

    private fun pagination() {
        binding.rvQuizzes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (!isLoading && !isLastPage && dy > 0) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 2) {
                        loadMoreItems()
                    }
                }
            }


        })
    }


    /**
     * load more api  call
     */
    private fun loadMoreItems() {
        isLoading = true
        currentPage++

        val data = HashMap<String, Any>()
        data["type"] = "quiz"
        data["page"] = currentPage
        subject
            ?.takeIf { !it.equals("All", ignoreCase = true) }
            ?.let { data["subject"] = it }

        viewModel.getQuizApi(data, Constants.TEST_QUIZ_DATA)
    }

    /**
     *  handel results dialog
     **/
    private fun initDialog(selectedAnswers: UserResponse) {

        quizEndDialog = BaseCustomDialog(requireActivity(), R.layout.dialog_quiz_completed) {
            when (it?.id) {
                R.id.ivCancel -> quizEndDialog?.dismiss()
            }
        }

        quizEndDialog?.setCancelable(false)
        quizEndDialog?.show()


        val totalQuestions =
            (selectedAnswers.correctCount ?: 0) + (selectedAnswers.incorrectCount ?: 0)

        quizEndDialog?.binding?.tvQuesValue?.text = totalQuestions.toString()
        quizEndDialog?.binding?.tvXCorrectPoint?.text = selectedAnswers.correctCount.toString()
        quizEndDialog?.binding?.tvWrongPoint?.text = selectedAnswers.incorrectCount.toString()
        quizEndDialog?.binding?.circularProgressBar?.apply {
            setTextView(quizEndDialog?.binding?.tvScoredValue)
            if (selectedAnswers.points != null) {
                setProgress(selectedAnswers.points)
            }
        }
        // Set values in dialog views
        quizEndDialog?.binding?.tvScoredValue?.text = "+${selectedAnswers.points}"
        quizEndDialog?.binding?.circularProgressBar?.apply {
            setTextView(quizEndDialog?.binding?.tvScoredValue)

            val total = totalQuestions
            val correct = selectedAnswers.correctCount ?: 0

            setMaxProgress(total * 10)
            setProgress(correct * 10)
        }
        // call back
        quizEndDialog?.setOnDismissListener {
            quizEndDialog?.dismiss()
        }
    }


}