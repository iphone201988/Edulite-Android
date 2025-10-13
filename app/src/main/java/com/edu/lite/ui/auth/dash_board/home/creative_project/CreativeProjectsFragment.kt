package com.edu.lite.ui.auth.dash_board.home.creative_project

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
import com.edu.lite.data.model.CreativeProject
import com.edu.lite.data.model.GetCreativeModelClass
import com.edu.lite.databinding.FragmentCreativeProjectsBinding
import com.edu.lite.databinding.RvCreativeItemBinding
import com.edu.lite.ui.auth.dash_board.home.HomeFragmentVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreativeProjectsFragment : BaseFragment<FragmentCreativeProjectsBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()
    private val args: CreativeProjectsFragmentArgs by navArgs()
    private lateinit var creativeAdapter: SimpleRecyclerViewAdapter<CreativeProject, RvCreativeItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_creative_projects
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initOnCreativeAdapter()
        // api call
        val data = HashMap<String, String>()
        data["grade"] = args.grade
        data["subject"] = args.subjectId
        viewModel.getCreativeApi(data, Constants.CREATIVE_PROJECT)
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
                        "getCreativeApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: GetCreativeModelClass? = BindingUtils.parseJson(jsonData)
                                val creative = model?.creativeProjects
                                if (creative != null) {
                                    creativeAdapter.list = creative
                                } else {
                                    showErrorToast("Something went wrong")
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
     * creative  adapter
     */
    private fun initOnCreativeAdapter() {
        creativeAdapter = SimpleRecyclerViewAdapter(R.layout.rv_creative_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.clCreative -> {
                    val action =
                        CreativeProjectsFragmentDirections.navigateToCreativeProjectsDetailsFragment(creativeProjectId = m._id.toString())
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }

            }
        }

        binding.rvCreative.adapter = creativeAdapter

    }

}
