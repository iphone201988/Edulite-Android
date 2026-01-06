package com.edu.lite.ui.dash_board.home.practice

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
import com.edu.lite.data.model.PreviewModel
import com.edu.lite.databinding.FragmentPracticeTestBinding
import com.edu.lite.databinding.RvPracticeCategoryItemBinding
import com.edu.lite.databinding.RvPracticeTextItemBinding
import com.edu.lite.ui.dash_board.home.quiz.FeaturedQuizzesFragmentDirections
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.DummyList
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PracticeTestFragment : BaseFragment<FragmentPracticeTestBinding>() {
    private val viewModel: PracticeTestFragmentVM by viewModels()
    private val args: PracticeTestFragmentArgs by navArgs()
    private lateinit var practiceTestAdapter: SimpleRecyclerViewAdapter<FeaturedQuizze, RvPracticeTextItemBinding>
    private lateinit var practiceCategoryAdapter: SimpleRecyclerViewAdapter<PreviewModel, RvPracticeCategoryItemBinding>
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false

    override fun getLayoutResource(): Int {
        return R.layout.fragment_practice_test
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initOnPracticeCategoryAdapter()
        initOnPracticeTestAdapter()
        // click
        initOnClick()
        // api call
        val data = HashMap<String, Any>()
        data["subject"] = args.subject
        data["type"] = args.practiceTest
        data["page"] = currentPage
        viewModel.practiceTestApi(data, Constants.TEST_QUIZ_DATA)
        // observer
        initObserver()
        // pagination
        pagination()
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

    /** api response observer ***/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "practiceTestApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: FeaturedApiResponse? = BindingUtils.parseJson(jsonData)
                                val quizzes = model?.quizzes.orEmpty()
                                if (currentPage == 1) {
                                    practiceTestAdapter.list = quizzes
                                } else {
                                    practiceTestAdapter.addToList(quizzes)
                                }
                                isLastPage = model?.pagination?.hasNextPage != true

                                binding.tvEmpty.visibility =
                                    if (practiceTestAdapter.list.isEmpty())
                                        View.VISIBLE
                                    else
                                        View.GONE

                                if (model == null) {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }

                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                binding.tvEmpty.visibility = View.VISIBLE
                                showErrorToast(e.localizedMessage ?: getString(R.string.something_went_wrong))
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
     * Practice adapter
     */
    private fun initOnPracticeTestAdapter() {
        practiceTestAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_practice_text_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPractise -> {
                        val action =
                            FeaturedQuizzesFragmentDirections.navigateToQuizQuestionFragment(quizId = m._id.toString())
                        BindingUtils.navigateWithSlide(findNavController(), action)
                    }
                }
            }
        binding.rvPracticeText.adapter = practiceTestAdapter
    }

    /**
     * Practice category adapter
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun initOnPracticeCategoryAdapter() {
        practiceCategoryAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_practice_category_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPracticeCategory -> {
                        for (i in practiceCategoryAdapter.list) {
                            i.isCheck = i.title == m.title
                        }
                        practiceCategoryAdapter.notifyDataSetChanged()
                    }
                }
            }
        binding.rvCategory.adapter = practiceCategoryAdapter
        practiceCategoryAdapter.list = DummyList.practiceCategoryList(requireActivity())
    }

    /**
     * quiz handel pagination
     */
    private fun pagination() {
        binding.rvPracticeText.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        data["type"] = args.practiceTest
        data["page"] = currentPage
        viewModel.practiceTestApi(data, Constants.TEST_QUIZ_DATA)
    }
}
