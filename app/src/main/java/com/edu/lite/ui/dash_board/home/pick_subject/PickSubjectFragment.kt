package com.edu.lite.ui.dash_board.home.pick_subject

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.GradeByIdResponse
import com.edu.lite.data.model.SubjectData
import com.edu.lite.databinding.FragmentPickSubjectBinding
import com.edu.lite.databinding.RvPickSubjectItemBinding
import com.edu.lite.ui.dash_board.home.HomeFragmentVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PickSubjectFragment : BaseFragment<FragmentPickSubjectBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()
    private val args: PickSubjectFragmentArgs by navArgs()
    private lateinit var pickSubjectAdapter: SimpleRecyclerViewAdapter<SubjectData, RvPickSubjectItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_pick_subject
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initOnGradeAdapter()
        // click
        initOnClick()

        // api call
        viewModel.getGradeApi(Constants.GRADES + "/${args.subjectId}")

        // observer
        initObserver()
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
                                val model: GradeByIdResponse? = BindingUtils.parseJson(jsonData)
                                val grade = model?.grade
                                if (grade != null) {
                                    pickSubjectAdapter.list = grade.subjects
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
        pickSubjectAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_pick_subject_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPick -> {
                        val subjectData: SubjectData = m
                        val action = PickSubjectFragmentDirections.navigateToLetPlayLearnFragment(
                            subjectId = args.subjectId,
                            subjectData = subjectData,
                            grade = args.grade
                        )
                        BindingUtils.navigateWithSlide(findNavController(), action)

                    }
                }
            }
        binding.rvPickSubject.adapter = pickSubjectAdapter
    }


}

