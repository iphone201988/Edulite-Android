package com.edu.lite.ui.dash_board.home.creative_project

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.CreativityDetailsApiResponse
import com.edu.lite.databinding.FragmentCreativeProjectsDetailsBinding
import com.edu.lite.ui.dash_board.home.HomeFragmentVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreativeProjectsDetailsFragment : BaseFragment<FragmentCreativeProjectsDetailsBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()
    private val args: CreativeProjectsDetailsFragmentArgs by navArgs()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_creative_projects_details
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // api call
        viewModel.getCreativeDetailsApi(Constants.CREATIVE_PROJECT + "/${args.creativeProjectId}")
        // observer
        initObserver()
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
                        "getCreativeDetailsApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: CreativityDetailsApiResponse? =
                                    BindingUtils.parseJson(jsonData)
                                val project = model?.project
                                if (project != null) {
                                    binding.bean = project
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

}
