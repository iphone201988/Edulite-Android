package com.edu.lite.ui.dash_board.home.quiz

import android.annotation.SuppressLint
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
import com.edu.lite.data.model.Quiz
import com.edu.lite.data.model.QuizAnswerApiResponse
import com.edu.lite.data.model.UserResponse
import com.edu.lite.databinding.DialogQuizCompletedBinding
import com.edu.lite.databinding.FragmentFeaturedQuizzesBinding
import com.edu.lite.databinding.QuizDialogBoxItemBinding
import com.edu.lite.databinding.RvFeaturedQuizzesItemBinding
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeaturedQuizzesFragment : BaseFragment<FragmentFeaturedQuizzesBinding>() {
    private val viewModel: FeaturedQuizzesVM by viewModels()
    private val args: FeaturedQuizzesFragmentArgs by navArgs()
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private lateinit var featuredQuizzesAdapter: SimpleRecyclerViewAdapter<FeaturedQuizze, RvFeaturedQuizzesItemBinding>

    private var quizEndDialog: BaseCustomDialog<DialogQuizCompletedBinding>? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_featured_quizzes
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initOnFeatureQuizzesAdapter()
        resetPagination()
        // click
        initOnClick()
        // header text
        binding.tvHeader.text =
            getString(R.string.how_well_do_you_know_geometry) + " " + args.subject + "?"

        callFeatureApi()
        // observer
        initObserver()
        // pagination
        pagination()

//        // api call
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("quizId")
//            ?.observe(viewLifecycleOwner) { quizId ->
//                val status =
//                    findNavController().currentBackStackEntry?.savedStateHandle?.get<String>("status")
//                val finalTime =
//                    findNavController().currentBackStackEntry?.savedStateHandle?.get<Int>("timeTaken")
//                val answers =
//                    findNavController().currentBackStackEntry?.savedStateHandle?.get<ArrayList<Map<String, String>>>(
//                            "answers"
//                        )
//                if (quizId != null && status != null && finalTime != null && answers != null) {
//                    isUserBackPosted = true
//                    val data = HashMap<String, Any>().apply {
//                        put("quizId", quizId)
//                        put("answers", answers)
//                        put("status", status)
//                        put("timeTaken", finalTime)
//                    }
//                    viewModel.postUserBack(Constants.USER_RESPONSE, data)
//                }
//            }

    }

    private fun callFeatureApi() {
        val data = HashMap<String, Any>().apply {
            put("subject", args.subject)
            put("page", currentPage)
            put("type", args.featuredType)
            put("grade", args.grade)
        }
        viewModel.getFeaturesApi(data, Constants.TEST_QUIZ_DATA)
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
                        "getFeaturesApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: FeaturedApiResponse? = BindingUtils.parseJson(jsonData)
                                val grade = model?.quizzes
                                if (grade != null) {
                                    if (currentPage == 1) {
                                        featuredQuizzesAdapter.list = grade
                                    } else {
                                        featuredQuizzesAdapter.addToList(grade)
                                    }
                                    isLastPage = model.pagination?.hasNextPage != true
                                    binding.listEmpty.visibility =
                                        if (featuredQuizzesAdapter.list.isEmpty()) View.VISIBLE
                                        else View.GONE

                                } else {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                binding.listEmpty.visibility = View.VISIBLE
                                showErrorToast(e.message.toString())
                            }.also {
                                isLoading = false
                                hideLoading()
                            }
                        }

                        "postUserBack" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: QuizAnswerApiResponse? = BindingUtils.parseJson(jsonData)
                                val question = model?.userResponse
                                if (question != null) {
                                    // Show result dialog
                                } else {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
                                showErrorToast(e.message.toString())
                            }.also {
                                isLoading= false
                                resetPagination()
                                callFeatureApi()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    isLoading= false
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
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
            }

        }
    }

    /**
     * featured Quizzes adapter
     */
    private fun initOnFeatureQuizzesAdapter() {
        featuredQuizzesAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_featured_quizzes_item, BR.bean) { v, m, pos ->
                when (v?.id) {
                    R.id.tvShowMore->{
                        m.isExpanded = !m.isExpanded
                        featuredQuizzesAdapter.notifyItemChanged(pos)
                    }
                    R.id.clFeatures -> {
                        if (m.userResponse?.status=="completed"){
                            initDialog(m.userResponse)
                        }
                        else{
                            val action =
                                FeaturedQuizzesFragmentDirections.navigateToQuizQuestionFragment(quizId = m._id.toString())
                            BindingUtils.navigateWithSlide(findNavController(), action)
                        }

                    }

                }
            }

        binding.rvFeaturedQuizzes.adapter = featuredQuizzesAdapter

    }

    /**
     * quiz handel pagination
     */

    private fun pagination() {
        binding.rvFeaturedQuizzes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        val data = HashMap<String, Any>().apply {
            put("subject", args.subject)
            put("page", currentPage)
            put("type", args.featuredType)
            put("grade", args.grade)
        }

        viewModel.getFeaturesApi(data, Constants.TEST_QUIZ_DATA)
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
            (selectedAnswers.correctCount ?: 0) +
                    (selectedAnswers.incorrectCount ?: 0)

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
            quizEndDialog?.dismiss()
        }
    }

    private fun resetPagination() {
        currentPage = 1
        isLoading = false
        isLastPage = false
        featuredQuizzesAdapter.list = emptyList()
    }
}

