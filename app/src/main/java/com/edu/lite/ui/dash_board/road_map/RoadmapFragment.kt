package com.edu.lite.ui.dash_board.road_map

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.RoadMapQuizze
import com.edu.lite.data.model.RoadMapResponse
import com.edu.lite.databinding.FragmentRoadmapBinding
import com.edu.lite.databinding.RvRoadMapItemBinding
import com.edu.lite.ui.dash_board.home.quiz.FeaturedQuizzesFragmentDirections
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.BindingUtils.CommonModel
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoadmapFragment : BaseFragment<FragmentRoadmapBinding>() {
    private val viewModel: RoadmapFragmentVM by viewModels()
    private lateinit var roadAdapterAdapter: SimpleRecyclerViewAdapter<RoadMapQuizze, RvRoadMapItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_roadmap
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initOnRoadMapAdapter()
        // api call
        val data = HashMap<String, Any>()
        data["subject"] = "English"
        data["type"] = "quiz"
        viewModel.getRoadMapApi(data, Constants.TEST_QUIZ_DATA)
        // click
        initOnClick()
        // observer
        initObserver()
    }

    /**
     * click event handel
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.clFilter -> {
                    BindingUtils.showDropdownModel(it, addPriceTypeList()) { selected ->
                        binding.tvFilter.text = selected.name
                        // api call
                        val data = HashMap<String, Any>()
                        data["subject"] = selected.name
                        data["type"] = "quiz"
                        viewModel.getRoadMapApi(data, Constants.TEST_QUIZ_DATA)
                    }
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
                        "getRoadMapApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: RoadMapResponse? = BindingUtils.parseJson(jsonData)
                                val grade = model?.quizzes
                                if (grade != null) {
                                    roadAdapterAdapter.list = grade
                                    binding.ivView.visibility = View.VISIBLE
                                } else {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(e.message.toString())
                                binding.ivView.visibility = View.GONE
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
     * road map adapter
     */
    private fun initOnRoadMapAdapter() {
        roadAdapterAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_road_map_item, BR.bean) { v, m, pos ->
                when (v?.id) {
                    R.id.leftSide, R.id.rightSide -> {
                        Log.d("dgdffgfdfgdg", "leftSideClick: ")
                        val action =
                            FeaturedQuizzesFragmentDirections.navigateToQuizQuestionFragment(quizId = m._id.toString())
                        BindingUtils.navigateWithSlide(findNavController(), action)
                    }
                }
            }
        binding.rvRoadmap.adapter = roadAdapterAdapter


    }


    /**
     * Method to add price type list
     */
    private fun addPriceTypeList(): ArrayList<CommonModel> {
        return arrayListOf(
            CommonModel("Mathematics", 1),
            CommonModel("English", 2),
            CommonModel("Science", 3),
        )
    }


}