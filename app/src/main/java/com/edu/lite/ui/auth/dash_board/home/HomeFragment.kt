package com.edu.lite.ui.auth.dash_board.home


import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.model.PreviewModel
import com.edu.lite.data.model.QuestionModel
import com.edu.lite.data.model.RoastedModel
import com.edu.lite.data.model.UnitModel
import com.edu.lite.databinding.FragmentHomeBinding
import com.edu.lite.databinding.RvBadgesEarnedItemBinding
import com.edu.lite.databinding.RvPreviewItemBinding
import com.edu.lite.databinding.RvQuestionItemBinding
import com.edu.lite.databinding.RvRoasterItemBinding
import com.edu.lite.databinding.RvUnitItemBinding
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.DummyList
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()
    private lateinit var previewAdapter: SimpleRecyclerViewAdapter<PreviewModel, RvPreviewItemBinding>
    private lateinit var questionAdapter: SimpleRecyclerViewAdapter<QuestionModel, RvQuestionItemBinding>
    private lateinit var progressValveAdapter: SimpleRecyclerViewAdapter<UnitModel, RvUnitItemBinding>
    private lateinit var unitAlveAdapter: SimpleRecyclerViewAdapter<RoastedModel, RvRoasterItemBinding>
    private lateinit var budgetEarnedAdapter: SimpleRecyclerViewAdapter<PreviewModel, RvBadgesEarnedItemBinding>


    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // status bar color change
        BindingUtils.setStatusBarGradient(requireActivity())
        // click
        initOnClick()
        // adapter
        binding.type = 1
        initOnPreviewAdapter()
        initOnQuestionAdapter()
        initOnProgressValveAdapter()
        initOnUnitAlveAdapterAdapter()
        initOnBudgetEarnedAdapter()
        // det progress
        BindingUtils.setProgress(binding.progressionGuideline, 20)

        // chart finalize
        initLineChart()

    }


    private fun initLineChart() {

        val entries = listOf(
            Entry(0f, 30f), // Mon
            Entry(1f, 35f), // Tue
            Entry(2f, 45f), // Wed
            Entry(3f, 25f), // Thu
            Entry(4f, 30f), // Fri
            Entry(5f, 50f), // Sat
            Entry(6f, 35f)  // Sun
        )

        val lineDataSet = LineDataSet(entries, "").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = ContextCompat.getColor(requireContext(), R.color.start_color)
            lineWidth = 2f
            setDrawValues(false)
            setDrawCircles(false)

            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient)
        }

        binding.lineChart.apply {
            data = LineData(lineDataSet)

            // X axis (Mon-Sun)
            val days = arrayOf("Mon", "Tues", "Wed", "Thu", "Fri", "Sat", "Sun")
            xAxis.valueFormatter = IndexAxisValueFormatter(days)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)

            // Hide Y axis & grid
            axisLeft.setDrawLabels(false)
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawAxisLine(false)
            axisRight.isEnabled = false

            // Hide legend and description
            legend.isEnabled = false
            description.isEnabled = false

            animateY(1000)
            invalidate()
        }


    }

    /**
     * click event handel
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.btnStart -> {
                    val action = HomeFragmentDirections.navigateToChooseGradeFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)
                }
            }

        }

        // filter button click

        binding.ivFilter.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val totalWidth = v.width
                val sectionWidth = totalWidth / 3
                val x = event.x.toInt()

                when {
                    x < sectionWidth -> {
                        showInfoToast("filter")
                    }

                    x < sectionWidth * 2 -> {
                        showInfoToast("download")
                    }

                    else -> {
                        showInfoToast("settings")
                    }
                }
            }
            true
        }

    }

    /**
     * preview adapter
     */
    private fun initOnPreviewAdapter() {
        previewAdapter = SimpleRecyclerViewAdapter(R.layout.rv_preview_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.clPreview -> {
                    when (m.title) {
                        "Student Preview" -> {
                            binding.type = 1
                        }

                        "Teacher Dashboard" -> {
                            binding.type = 2
                        }

                        "Parent Digest" -> {
                            binding.type = 3
                        }
                    }
                    //if (m.title.)
                    for (i in previewAdapter.list) {
                        i.isCheck = i.title == m.title
                    }
                    previewAdapter.notifyDataSetChanged()
                }

            }
        }
        binding.rvPreview.adapter = previewAdapter
        previewAdapter.list = DummyList.previewList()

    }

    /**
     * question adapter
     */
    private fun initOnQuestionAdapter() {
        questionAdapter = SimpleRecyclerViewAdapter(R.layout.rv_question_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.tvType -> {

                }

            }
        }
        binding.rvQuestion.adapter = questionAdapter
        questionAdapter.list = DummyList.questionList()

    }

    /**
     * progress adapter
     */
    private fun initOnProgressValveAdapter() {
        progressValveAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_unit_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.tvType -> {

                    }

                }
            }
        binding.rvUnit.adapter = progressValveAdapter
        progressValveAdapter.list = DummyList.unitList()

    }

    /**
     * progress adapter
     */
    private fun initOnUnitAlveAdapterAdapter() {
        unitAlveAdapter = SimpleRecyclerViewAdapter(R.layout.rv_roaster_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.tvType -> {

                }

            }
        }
        binding.rvRoaster.adapter = unitAlveAdapter
        unitAlveAdapter.list = DummyList.roastList()

    }

    /**
     * progress adapter
     */
    private fun initOnBudgetEarnedAdapter() {
        budgetEarnedAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_badges_earned_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.tvType -> {

                    }

                }
            }
        binding.rvBadgesEarned.adapter = budgetEarnedAdapter
        budgetEarnedAdapter.list = DummyList.budgetList()

    }


}