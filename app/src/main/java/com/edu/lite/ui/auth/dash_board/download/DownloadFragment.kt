package com.edu.lite.ui.auth.dash_board.download

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.model.PreviewModel
import com.edu.lite.data.model.VideoLessonsModel
import com.edu.lite.databinding.FragmentDownloadBinding
import com.edu.lite.databinding.RvDownloadItemBinding
import com.edu.lite.databinding.RvPracticeCategoryItemBinding
import com.edu.lite.databinding.RvVideoLessonsItemBinding
import com.edu.lite.utils.DummyList
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DownloadFragment : BaseFragment<FragmentDownloadBinding>() {
    private val viewModel: DownloadFragmentVM by viewModels()
    private lateinit var categoryAdapter: SimpleRecyclerViewAdapter<PreviewModel, RvPracticeCategoryItemBinding>
    private lateinit var downloadAdapter: SimpleRecyclerViewAdapter<VideoLessonsModel, RvDownloadItemBinding>
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
        initOnCategoryAdapter()
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
        categoryAdapter.list = DummyList.practiceCategoryList()

    }



    /**
     * download video adapter
     */
    private fun initOnDownloadAdapter() {
        downloadAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_download_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPracticeCategory -> {

                    }

                }
            }

        binding.rvDownload.adapter = downloadAdapter
        downloadAdapter.list = DummyList.videoLessonsModelList()

    }
}