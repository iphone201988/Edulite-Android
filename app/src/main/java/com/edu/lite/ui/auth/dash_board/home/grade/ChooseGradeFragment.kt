package com.edu.lite.ui.auth.dash_board.home.grade

import android.annotation.SuppressLint
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
import com.edu.lite.data.model.GradeData
import com.edu.lite.data.model.GradeModelResponse
import com.edu.lite.databinding.FragmentChooseGradeBinding
import com.edu.lite.databinding.RvGradeItemBinding
import com.edu.lite.ui.auth.dash_board.home.HomeFragmentVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseGradeFragment : BaseFragment<FragmentChooseGradeBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()

    private lateinit var gradeAdapter: SimpleRecyclerViewAdapter<GradeData, RvGradeItemBinding>


    override fun getLayoutResource(): Int {
        return R.layout.fragment_choose_grade
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initOnGradeAdapter()
        // click
        initOnClick()

        // observer
        initObserver()
        // api call
        viewModel.getGradeApi(Constants.GRADES)
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
                        "getGradeApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: GradeModelResponse? = BindingUtils.parseJson(jsonData)
                                val grades = model?.grades
                                if (!grades.isNullOrEmpty()) {
                                    gradeAdapter.list = grades
                                } else {
                                    showErrorToast("No grades found.")
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
     * grade adapter
     */
    private fun initOnGradeAdapter() {
        gradeAdapter = SimpleRecyclerViewAdapter(R.layout.rv_grade_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.clGrade -> {
                    val action =
                        ChooseGradeFragmentDirections.navigateToTodayThemeFragment(gradeId = m._id.toString(), grade = m.grade.toString())
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }

            }
        }
        binding.rvGrade.adapter = gradeAdapter

    }

}
