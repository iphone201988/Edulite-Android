package com.edu.lite.ui.dash_board.download

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.model.PreviewModel
import com.edu.lite.data.room_moduel.DownloadVideoData
import com.edu.lite.databinding.FragmentDownloadBinding
import com.edu.lite.databinding.RvDownloadItemBinding
import com.edu.lite.databinding.RvPracticeCategoryItemBinding
import com.edu.lite.ui.dash_board.video_play.VideoPlayFragmentDirections
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.DummyList
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DownloadFragment : BaseFragment<FragmentDownloadBinding>() {
    private val viewModel: DownloadFragmentVM by viewModels()
    private lateinit var categoryAdapter: SimpleRecyclerViewAdapter<PreviewModel, RvPracticeCategoryItemBinding>
    private lateinit var downloadAdapter: SimpleRecyclerViewAdapter<DownloadVideoData, RvDownloadItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_download
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initOnDownloadAdapter()
        //  initOnCategoryAdapter()

        viewModel.getAllVideos()
        // observer
        initObserver()

    }


    /** api response observer ***/
    private fun initObserver() {
        viewModel.observeVideo.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getDownloadVideo" -> {
                            runCatching {
                                downloadAdapter.list = it.data
                                if (downloadAdapter.list.isNotEmpty()) {
                                    binding.listEmpty.visibility = View.GONE
                                } else {
                                    binding.listEmpty.visibility = View.VISIBLE
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
     * download video category adapter
     */
    private fun initOnCategoryAdapter() {
        categoryAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_practice_category_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPracticeCategory -> {
                        for (i in categoryAdapter.list) {
                            i.isCheck = i.title == m.title
                        }
                        categoryAdapter.notifyDataSetChanged()
                    }

                }
            }

        binding.rvCategory.adapter = categoryAdapter
        categoryAdapter.list = DummyList.practiceCategoryList(requireActivity())

    }


    /**
     * download video adapter
     */
    private fun initOnDownloadAdapter() {
        downloadAdapter = SimpleRecyclerViewAdapter(R.layout.rv_download_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.clLetPlay -> {
                    val action =
                        VideoPlayFragmentDirections.navigateToVideoPlayFragment(videoPath = m.localPath.toString())
                    BindingUtils.navigateWithSlide(findNavController(), action)
                }

            }
        }

        binding.rvDownload.adapter = downloadAdapter
    }
}