package com.edu.lite.ui.dash_board.home.play_learn

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.model.QuizType
import com.edu.lite.databinding.FragmentLetPlayLearnBinding
import com.edu.lite.databinding.RvLetPlayItemBinding
import com.edu.lite.ui.dash_board.home.HomeFragmentVM
import com.edu.lite.utils.BindingUtils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LetPlayLearnFragment : BaseFragment<FragmentLetPlayLearnBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()
    private val args: LetPlayLearnFragmentArgs by navArgs()
    private lateinit var letPlayAdapter: SimpleRecyclerViewAdapter<QuizType, RvLetPlayItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_let_play_learn
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initOnLetsPlayAdapter()
        // click
        initOnClick()
        // data
        letPlayAdapter.list = args.subjectData.types
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
     * lets play adapter
     */
    private fun initOnLetsPlayAdapter() {
        letPlayAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_let_play_item, BR.bean) { v, m, pos ->
                when (v?.id) {
                    R.id.clLetPlay -> {
                        when (pos) {
                            0 -> {
                                val action =
                                    LetPlayLearnFragmentDirections.navigateToFeaturedQuizzesFragment(
                                        subject = args.subjectData.name.toString(),
                                        m.name.toString()
                                    )
                                BindingUtils.navigateWithSlide(findNavController(), action)
                            }

                            1 -> {
                                val action =
                                    LetPlayLearnFragmentDirections.navigateToPracticeTestFragment(
                                        subject = args.subjectData.name.toString(),
                                        m.name.toString()
                                    )
                                BindingUtils.navigateWithSlide(findNavController(), action)
                            }

                            2 -> {
                                val action =
                                    LetPlayLearnFragmentDirections.navigateToVideoLessonsFragment(
                                        subjectId = args.subjectData.name.toString(),
                                        args.grade
                                    )
                                BindingUtils.navigateWithSlide(findNavController(), action)
                            }

                            3 -> {
                                val action =
                                    LetPlayLearnFragmentDirections.navigateToCreativeProjectsFragment(
                                        subjectId = args.subjectData.name.toString(),
                                        grade = args.grade
                                    )
                                BindingUtils.navigateWithSlide(findNavController(), action)
                            }
                        }
                    }
                }
            }
        binding.rvLetPlay.adapter = letPlayAdapter
        //  letPlayAdapter.list = DummyList.letPlayList()
    }
}

