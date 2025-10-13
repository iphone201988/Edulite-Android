package com.edu.lite.ui.auth.dash_board.home.video_lessons

import android.annotation.SuppressLint
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
import com.edu.lite.databinding.FragmentVideoLessonsBinding
import com.edu.lite.databinding.RvPracticeCategoryItemBinding
import com.edu.lite.databinding.RvVideoLessonsItemBinding
import com.edu.lite.ui.auth.dash_board.home.HomeFragmentVM
import com.edu.lite.utils.DummyList
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VideoLessonsFragment : BaseFragment<FragmentVideoLessonsBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()

    private lateinit var videoCategoryAdapter: SimpleRecyclerViewAdapter<PreviewModel, RvPracticeCategoryItemBinding>
    private lateinit var videoCategoryAdapter1: SimpleRecyclerViewAdapter<PreviewModel, RvPracticeCategoryItemBinding>
    private lateinit var videoLessonsAdapter: SimpleRecyclerViewAdapter<VideoLessonsModel, RvVideoLessonsItemBinding>


    override fun getLayoutResource(): Int {
        return R.layout.fragment_video_lessons
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()

        // adapter
        initOnVideoLessonsCategoryAdapter()
        initOnVideoLessonsCategoryAdapter1()
        initOnVideoLessonsAdapter()


        binding.clNested.setOnScrollChangedListener { who, l, t, oldl, oldt ->
            val introBottom = binding.rvVideoCategory.bottom
            if (t >= introBottom) {
                binding.rvVideoCategory1.visibility = View.VISIBLE
            } else {
                binding.rvVideoCategory1.visibility = View.GONE
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
     * video lessons category adapter
     */
    private fun initOnVideoLessonsCategoryAdapter() {
        videoCategoryAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_practice_category_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPracticeCategory -> {
                        for (i in videoCategoryAdapter.list) {
                            i.isCheck = i.title == m.title
                        }
                        videoCategoryAdapter.notifyDataSetChanged()
                    }

                }
            }

        binding.rvVideoCategory.adapter = videoCategoryAdapter
        videoCategoryAdapter.list = DummyList.practiceCategoryList()

    }

    private fun initOnVideoLessonsCategoryAdapter1() {
        videoCategoryAdapter1 =
            SimpleRecyclerViewAdapter(R.layout.rv_practice_category_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPracticeCategory -> {
                        for (i in videoCategoryAdapter1.list) {
                            i.isCheck = i.title == m.title
                        }
                        videoCategoryAdapter1.notifyDataSetChanged()
                    }

                }
            }

        binding.rvVideoCategory1.adapter = videoCategoryAdapter1
        videoCategoryAdapter1.list = DummyList.practiceCategoryList()

    }


    /**
     * video lessons adapter
     */
    private fun initOnVideoLessonsAdapter() {
        videoLessonsAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_video_lessons_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPracticeCategory -> {

                    }

                }
            }

        binding.rvVideoLessons.adapter = videoLessonsAdapter
        videoLessonsAdapter.list = DummyList.videoLessonsModelList()

    }
}
