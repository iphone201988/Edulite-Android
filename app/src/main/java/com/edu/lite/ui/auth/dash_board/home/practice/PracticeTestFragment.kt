package com.edu.lite.ui.auth.dash_board.home.practice

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.model.PracticeTextModel
import com.edu.lite.data.model.PreviewModel
import com.edu.lite.databinding.FragmentPracticeTestBinding
import com.edu.lite.databinding.RvPracticeCategoryItemBinding
import com.edu.lite.databinding.RvPracticeTextItemBinding
import com.edu.lite.ui.auth.dash_board.home.HomeFragmentDirections
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.DummyList
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PracticeTestFragment : BaseFragment<FragmentPracticeTestBinding>() {
    private val viewModel: PracticeTestFragmentVM by viewModels()

    private lateinit var practiceTestAdapter: SimpleRecyclerViewAdapter<PracticeTextModel, RvPracticeTextItemBinding>
    private lateinit var practiceCategoryAdapter: SimpleRecyclerViewAdapter<PreviewModel, RvPracticeCategoryItemBinding>


    override fun getLayoutResource(): Int {
        return R.layout.fragment_practice_test
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initOnPracticeCategoryAdapter()
        initOnPracticeTestAdapter()
        // click
        initOnClick()
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
     * Practice adapter
     */
    private fun initOnPracticeTestAdapter() {
        practiceTestAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_practice_text_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPractise -> {
                        val action = PracticeTestFragmentDirections.navigateToQuizQuestionFragment()
                        BindingUtils.navigateWithSlide(findNavController(), action)
                    }

                }
            }

        binding.rvPracticeText.adapter = practiceTestAdapter
        practiceTestAdapter.list = DummyList.practiceTestList()

    }

    /**
     * Practice category adapter
     */
    private fun initOnPracticeCategoryAdapter() {
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
        practiceCategoryAdapter.list = DummyList.practiceCategoryList()

    }

}
