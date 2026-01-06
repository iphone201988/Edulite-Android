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
import com.edu.lite.data.model.QuizAnswerApiResponse
import com.edu.lite.databinding.FragmentFeaturedQuizzesBinding
import com.edu.lite.databinding.RvFeaturedQuizzesItemBinding
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
    private var isUserBackPosted = false
    private lateinit var featuredQuizzesAdapter: SimpleRecyclerViewAdapter<FeaturedQuizze, RvFeaturedQuizzesItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_featured_quizzes
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initOnFeatureQuizzesAdapter()
        // click
        initOnClick()
        // header text
        binding.tvHeader.text =
            getString(R.string.how_well_do_you_know_geometry) + " " + args.subject + "?"
        // observer
        initObserver()
        // pagination
        pagination()

        // api call
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("quizId")
            ?.observe(viewLifecycleOwner) { quizId ->
                val status =
                    findNavController().currentBackStackEntry?.savedStateHandle?.get<String>("status")
                val finalTime =
                    findNavController().currentBackStackEntry?.savedStateHandle?.get<Int>("timeTaken")
                val answers =
                    findNavController().currentBackStackEntry?.savedStateHandle?.get<ArrayList<Map<String, String>>>(
                            "answers"
                        )
                if (quizId != null && status != null && finalTime != null && answers != null) {
                    isUserBackPosted = true
                    val data = HashMap<String, Any>().apply {
                        put("quizId", quizId)
                        put("answers", answers)
                        put("status", status)
                        put("timeTaken", finalTime)
                    }
                    viewModel.postUserBack(Constants.USER_RESPONSE, data)
                }
            }

    }

    override fun onResume() {
        super.onResume()
        // api call
        if (!isUserBackPosted) {
            val data = HashMap<String, Any>().apply {
                put("subject", args.subject)
                put("page", currentPage)
                put("type", args.featuredType)
            }
            viewModel.getFeaturesApi(data, Constants.TEST_QUIZ_DATA)

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

                                } else {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(e.message.toString())
                            }.also {
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
                               val data = HashMap<String, Any>().apply {
                                    put("subject", args.subject)
                                    put("page", currentPage)
                                    put("type", args.featuredType)
                                }
                                viewModel.getFeaturesApi(data, Constants.TEST_QUIZ_DATA)
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
            SimpleRecyclerViewAdapter(R.layout.rv_featured_quizzes_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clFeatures -> {
                        val action =
                            FeaturedQuizzesFragmentDirections.navigateToQuizQuestionFragment(quizId = m._id.toString())
                        BindingUtils.navigateWithSlide(findNavController(), action)
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
                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
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
        data["subject"] = args.subject
        data["page"] = currentPage
        data["type"] = args.featuredType
        viewModel.getFeaturesApi(data, Constants.TEST_QUIZ_DATA)
    }
}

