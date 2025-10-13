package com.edu.lite.ui.auth.dash_board.quiz

import android.view.View
import androidx.fragment.app.viewModels
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.model.FeaturedQuizzesModel
import com.edu.lite.data.model.PreviewModel
import com.edu.lite.databinding.FragmentQuizBinding
import com.edu.lite.databinding.RvFeaturedQuizzesItemBinding
import com.edu.lite.databinding.RvPracticeCategoryItemBinding
import com.edu.lite.databinding.RvQuizItemBinding
import com.edu.lite.utils.DummyList
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuizFragment : BaseFragment<FragmentQuizBinding>() {
    private val viewModel: QuizFragmentVM by viewModels()
    private lateinit var quizzesAdapter: SimpleRecyclerViewAdapter<FeaturedQuizzesModel, RvQuizItemBinding>
    private lateinit var practiceCategoryAdapter: SimpleRecyclerViewAdapter<PreviewModel, RvPracticeCategoryItemBinding>
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

    /**
     *  Quiz adapter
     */
    private fun initOnFeatureQuizzesAdapter() {
        quizzesAdapter = SimpleRecyclerViewAdapter(R.layout.rv_quiz_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.clLetPlay -> {

                }

            }
        }

        binding.rvQuizzes.adapter = quizzesAdapter
        quizzesAdapter.list = DummyList.featuredQuizzesList()

    }


    /**
     * Feature category adapter
     */
    private fun initOnFeatureCategoryAdapter() {
        practiceCategoryAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_practice_category_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPracticeCategory -> {
                        for (i in practiceCategoryAdapter.list) {
                            i.isCheck = i.title == m.title
                        }
                        practiceCategoryAdapter.notifyDataSetChanged()
                    }

                }
            }

        binding.rvCategory.adapter = practiceCategoryAdapter
        practiceCategoryAdapter.list = DummyList.quizCategoryList()

    }
}