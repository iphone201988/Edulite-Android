package com.edu.lite.ui.auth.dash_board.home.theams

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
import com.edu.lite.data.model.QuestModel
import com.edu.lite.databinding.FragmentTodayThemeBinding
import com.edu.lite.databinding.RvQuestItemBinding
import com.edu.lite.ui.auth.dash_board.home.HomeFragmentVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.DummyList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodayThemeFragment : BaseFragment<FragmentTodayThemeBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()
    private val args: TodayThemeFragmentArgs by navArgs()
    private lateinit var questAdapter: SimpleRecyclerViewAdapter<QuestModel, RvQuestItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_today_theme
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initOnQuestAdapter()
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

                R.id.btnStartMission -> {
                    val action =
                        TodayThemeFragmentDirections.navigateToPickSubjectFragment(subjectId = args.gradeId, grade = args.grade)
                    BindingUtils.navigateWithSlide(findNavController(), action)
                }
            }

        }
    }


    /**
     * quest adapter
     */
    private fun initOnQuestAdapter() {
        questAdapter = SimpleRecyclerViewAdapter(R.layout.rv_quest_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.clQuest -> {

                }

            }
        }

        binding.rvQuest.adapter = questAdapter
        questAdapter.list = DummyList.questList()

    }


}

