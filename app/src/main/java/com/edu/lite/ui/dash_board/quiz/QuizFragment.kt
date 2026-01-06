package com.edu.lite.ui.dash_board.quiz

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
import com.edu.lite.databinding.FragmentQuizBinding
import com.edu.lite.databinding.RvQuizFilterItemBinding
import com.edu.lite.databinding.RvQuizItemBinding
import com.edu.lite.ui.dash_board.home.quiz.FeaturedQuizzesFragmentDirections
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuizFragment : BaseFragment<FragmentQuizBinding>() {
    private val viewModel: QuizFragmentVM by viewModels()
    private lateinit var quizzesAdapter: SimpleRecyclerViewAdapter<RoadMapQuizze, RvQuizItemBinding>
    private lateinit var quizFilterAdapter: SimpleRecyclerViewAdapter<SubjectData, RvQuizFilterItemBinding>
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private var subject: String? = null
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
        // observer
        initObserver()
        val subjectId = sharedPrefManager.getLoginData()?.gradeId
        if (!subjectId.isNullOrEmpty()) {
            // api call
            viewModel.getGradeApi(Constants.GRADES + "/${subjectId}")
        } else {
            // api call
            val data = HashMap<String, Any>()
            data["type"] = "quiz"
            data["page"] = currentPage
            viewModel.getQuizApi(data, Constants.TEST_QUIZ_DATA)
        }
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
                                val quizzes = model?.quizzes.orEmpty()
                                if (currentPage == 1) {
                                    quizzesAdapter.list = quizzes
                                } else {
                                    quizzesAdapter.addToList(quizzes)
                                }

                                isLastPage = model?.pagination?.hasNextPage != true

                                binding.listEmpty.visibility =
                                    if (quizzesAdapter.list.isEmpty()) View.VISIBLE
                                    else View.GONE

                                if (model == null) {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }

                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                binding.listEmpty.visibility = View.VISIBLE
                                showErrorToast(
                                    e.localizedMessage ?: getString(R.string.something_went_wrong)
                                )
                            }.also {
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
                                val data = HashMap<String, Any>()
                                data["type"] = "quiz"
                                data["page"] = currentPage
                                viewModel.getQuizApi(data, Constants.TEST_QUIZ_DATA)
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
     *  Quiz adapter
     */
    private fun initOnFeatureQuizzesAdapter() {
        quizzesAdapter = SimpleRecyclerViewAdapter(R.layout.rv_quiz_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.clLetPlay -> {
                    Log.d("dgdffgfdfgdg", "quiz adapter click: ")
                    val action =
                        FeaturedQuizzesFragmentDirections.navigateToQuizQuestionFragment(quizId = m._id.toString())
                    BindingUtils.navigateWithSlide(findNavController(), action)
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
                currentPage == 1
                // update selection
                quizFilterAdapter.list.forEach {
                    it.check = it.name == m.name
                }
                quizFilterAdapter.notifyDataSetChanged()

                val data = HashMap<String, Any>()
                data["type"] = "quiz"
                data["page"] = currentPage
                subject = m.name

                when (m.name) {
                    "All" -> {
                        // no subject needed
                    }

                    else -> {
                        data["subject"] = m.name!!
                    }
                }

                viewModel.getQuizApi(data, Constants.TEST_QUIZ_DATA)
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
        data["type"] = "quiz"
        data["page"] = currentPage
        if (!subject.isNullOrEmpty()) {
            data["subject"] = subject!!
        }
        viewModel.getQuizApi(data, Constants.TEST_QUIZ_DATA)
    }


}