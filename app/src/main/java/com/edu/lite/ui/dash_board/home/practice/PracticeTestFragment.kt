package com.edu.lite.ui.dash_board.home.practice

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.FeaturedApiResponse
import com.edu.lite.data.model.FeaturedQuizze
import com.edu.lite.data.model.PreviewModel
import com.edu.lite.data.model.UserResponse
import com.edu.lite.databinding.DialogQuizCompletedBinding
import com.edu.lite.databinding.FragmentPracticeTestBinding
import com.edu.lite.databinding.RvPracticeCategoryItemBinding
import com.edu.lite.databinding.RvPracticeTextItemBinding
import com.edu.lite.ui.dash_board.home.quiz.FeaturedQuizzesFragmentDirections
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.DummyList
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PracticeTestFragment : BaseFragment<FragmentPracticeTestBinding>() {

    private val viewModel: PracticeTestFragmentVM by viewModels()
    private val args: PracticeTestFragmentArgs by navArgs()

    private lateinit var practiceTestAdapter:
            SimpleRecyclerViewAdapter<FeaturedQuizze, RvPracticeTextItemBinding>

    private lateinit var practiceCategoryAdapter:
            SimpleRecyclerViewAdapter<PreviewModel, RvPracticeCategoryItemBinding>

    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false

    private var selectedStatus: String? = null

    private var quizEndDialog: BaseCustomDialog<DialogQuizCompletedBinding>? = null

    private val clickHandler = Handler(Looper.getMainLooper())
    private var clickRunnable: Runnable? = null

    override fun getLayoutResource() = R.layout.fragment_practice_test
    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(view: View) {
        initOnPracticeCategoryAdapter()
        initOnPracticeTestAdapter()
        initOnClick()
        initObserver()
        pagination()
        resetPaginationAndReload()
    }

    /** ---------------- CLICK EVENTS ---------------- */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBackButton -> findNavController().popBackStack()
            }
        }
    }

    /** ---------------- API OBSERVER ---------------- */
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {

                Status.LOADING -> showLoading()

                Status.SUCCESS -> {
                    when (it.message) {
                        "practiceTestApi" -> {
                            hideLoading()
                            isLoading = false

                            runCatching {
                                val model: FeaturedApiResponse? =
                                    BindingUtils.parseJson(it.data.toString())

                                val rawList: List<FeaturedQuizze> =
                                    model?.quizzes.orEmpty().filterNotNull()

                                val filteredList = filterBySelectedStatus(rawList)

                                if (currentPage == 1) {
                                    practiceTestAdapter.list = filteredList
                                } else {
                                    practiceTestAdapter.addToList(filteredList)
                                }

                                isLastPage = model?.pagination?.hasNextPage != true

                                binding.tvEmpty.visibility =
                                    if (practiceTestAdapter.list.isEmpty())
                                        View.VISIBLE else View.GONE

                            }.onFailure { e ->
                                binding.tvEmpty.visibility = View.VISIBLE
                                showErrorToast(
                                    e.localizedMessage
                                        ?: getString(R.string.something_went_wrong)
                                )
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    isLoading = false
                    showErrorToast(it.message.toString())
                }

                else -> Unit
            }
        }
    }

    /** ---------------- PRACTICE LIST ADAPTER ---------------- */
    private fun initOnPracticeTestAdapter() {
        practiceTestAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_practice_text_item, BR.bean) { v, m, pos ->

                when (v?.id) {

                    R.id.tvShowMore -> {
                        m.isExpanded = !m.isExpanded
                        practiceTestAdapter.notifyItemChanged(pos)
                    }

                    R.id.clPractise -> {
                        if (m.userResponse?.status == "completed") {
                            initDialog(m.userResponse)
                        } else {
                            val action =
                                FeaturedQuizzesFragmentDirections
                                    .navigateToQuizQuestionFragment(m._id.toString())
                            BindingUtils.navigateWithSlide(findNavController(), action)
                        }
                    }
                }
            }

        binding.rvPracticeText.adapter = practiceTestAdapter
    }

    /** ---------------- CATEGORY ADAPTER ---------------- */
    @SuppressLint("NotifyDataSetChanged")
    private fun initOnPracticeCategoryAdapter() {

        practiceCategoryAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_practice_category_item, BR.bean) { v, m, _ ->

                when (v?.id) {
                    R.id.clPracticeCategory -> {
                        // cancel any previous pending click
                        clickRunnable?.let { clickHandler.removeCallbacks(it) }

                        clickRunnable = Runnable {

                            practiceCategoryAdapter.list.forEach {
                                it.isCheck = it.title == m.title
                            }
                            practiceCategoryAdapter.notifyDataSetChanged()

                            selectedStatus = mapCategoryToStatus(m.title)

                            resetPaginationAndReload()
                        }
                        clickHandler.postDelayed(clickRunnable!!, 1000)
                    }
                }
            }

        binding.rvCategory.adapter = practiceCategoryAdapter
        practiceCategoryAdapter.list =
            DummyList.practiceCategoryListSession(requireActivity())
    }

    /** ---------------- PAGINATION ---------------- */
    private fun pagination() {
        binding.rvPracticeText.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lm = rv.layoutManager as LinearLayoutManager
                val visible = lm.childCount
                val total = lm.itemCount
                val first = lm.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if (visible + first >= total && first >= 0) {
                        loadMoreItems()
                    }
                }
            }
        })
    }

    private fun loadMoreItems() {
        isLoading = true
        currentPage++

        val data = HashMap<String, Any>().apply {
            put("subject", args.subject)
            put("type", args.practiceTest)
            put("page", currentPage)
            put("grade", args.grade)
            selectedStatus?.let { put("status", it) }
        }

        viewModel.practiceTestApi(data, Constants.TEST_QUIZ_DATA)
    }

    /** ---------------- RESET & RELOAD ---------------- */
    private fun resetPaginationAndReload() {
        currentPage = 1
        isLoading = false
        isLastPage = false

        practiceTestAdapter.list = emptyList()

        val data = HashMap<String, Any>().apply {
            put("subject", args.subject)
            put("type", args.practiceTest)
            put("page", currentPage)
            put("grade", args.grade)
            selectedStatus?.let { put("status", it) }
        }

        viewModel.practiceTestApi(data, Constants.TEST_QUIZ_DATA)
    }

    /** ---------------- CATEGORY → STATUS MAP ---------------- */
    private fun mapCategoryToStatus(title: String): String? {
        return when (title) {
            "All" -> null
            "New" -> null
            "In Progress" -> "in_progress"
            "Completed" -> "completed"
            else -> null
        }
    }

    /** ---------------- FILTER LOGIC (IMPORTANT) ---------------- */
    private fun filterBySelectedStatus(
        list: List<FeaturedQuizze>
    ): List<FeaturedQuizze> {

        return when (selectedStatus) {

            null -> {
                // All → show everything
                list
            }

            "new" -> {
                // New → ONLY quizzes never attempted
                list.filter { it.userResponse == null }
            }

            "in_progress" -> {
                // In Progress → ONLY started but not completed
                list.filter {
                    it.userResponse != null &&
                            it.userResponse.status == "in_progress"
                }
            }

            "completed" -> {
                // Completed → ONLY completed quizzes
                list.filter {
                    it.userResponse != null &&
                            it.userResponse.status == "completed"
                }
            }

            else -> emptyList()
        }
    }

    /** ---------------- RESULT DIALOG ---------------- */
    private fun initDialog(userResponse: UserResponse) {

        quizEndDialog =
            BaseCustomDialog(requireActivity(), R.layout.dialog_quiz_completed) {
                if (it?.id == R.id.ivCancel) quizEndDialog?.dismiss()
            }

        quizEndDialog?.setCancelable(false)
        quizEndDialog?.show()

        val totalQ =
            (userResponse.correctCount ?: 0) +
                    (userResponse.incorrectCount ?: 0)

        quizEndDialog?.binding?.apply {

            tvQuesValue.text = totalQ.toString()
            tvXCorrectPoint.text = userResponse.correctCount.toString()
            tvWrongPoint.text = userResponse.incorrectCount.toString()
            tvScoredValue.text = "+${userResponse.points}"

            circularProgressBar.apply {
                setTextView(tvScoredValue)
                setMaxProgress(totalQ * 10)
                setProgress((userResponse.correctCount ?: 0) * 10)
            }
        }
    }
}
