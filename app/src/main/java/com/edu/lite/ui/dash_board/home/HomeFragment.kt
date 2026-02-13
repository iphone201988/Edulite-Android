package com.edu.lite.ui.dash_board.home


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.GetHomeQuest
import com.edu.lite.data.model.GetHomeQuestApi
import com.edu.lite.data.model.Quiz
import com.edu.lite.data.model.SignupResponse
import com.edu.lite.databinding.DialogDeleteLogoutBinding
import com.edu.lite.databinding.DialogQuizCompletedBinding
import com.edu.lite.databinding.FragmentHomeBinding
import com.edu.lite.databinding.RvQuestionItemBinding
import com.edu.lite.ui.dash_board.home.quiz.FeaturedQuizzesFragmentDirections
import com.edu.lite.ui.dash_board.profile.ProfileFragmentDirections
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()
    private lateinit var questionAdapter: SimpleRecyclerViewAdapter<GetHomeQuest, RvQuestionItemBinding>
    private var quizEndDialog: BaseCustomDialog<DialogQuizCompletedBinding>? = null

    private var chooseGradeDialog :BaseCustomDialog<DialogDeleteLogoutBinding>? = null
    private var PERMISSION_REQUEST_CODE = 16
    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // check permission
        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                getNotificationPermission()
            }
        }
        // status bar color change
        binding.type = 1
        BindingUtils.setStatusBarGradient(requireActivity())
        // click
        initOnClick()
        // adapter
        initOnQuestionAdapter()

        // api call
        viewModel.getProfileApi(Constants.GET_PROFILE)
        selectGradeDialog()

        // observer
        initObserver()

    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }


    /**
     * click event handel
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.btnStart -> {
//                    val subjectId = sharedPrefManager.getLoginData()?.gradeId
//                    val grade = sharedPrefManager.getLoginData()?.grade
//                    if (!subjectId.isNullOrEmpty() && !grade.isNullOrEmpty()) {
//                        val action = HomeFragmentDirections.navigateToPickSubjectFragment(
//                            subjectId = subjectId, grade = grade
//                        )
//                        BindingUtils.navigateWithSlide(findNavController(), action)
//                    }

                    val action = HomeFragmentDirections.navigateToChooseGradeFragment(from = "home")
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


    /** api response observer ***/
    @SuppressLint("SetTextI18n")
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getHomeApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: GetHomeQuestApi? = BindingUtils.parseJson(jsonData)
                                val quests = model?.quests.orEmpty()
                                questionAdapter.list = quests
                                binding.tvEmpty.visibility =
                                    if (quests.isEmpty()) View.VISIBLE else View.GONE
                                val totalCount = quests.size
                                val completeCount = quests.sumOf { quest ->
                                    listOf(
                                        quest?.userProgress?.quiz?.status,
                                        quest?.userProgress?.reading?.status
                                    ).count { it == "completed" }
                                }
                                val remainingCount = totalCount - completeCount

                                binding.tvProblems.text = when {
                                    totalCount == 0 -> "No quest found"
                                    remainingCount <= 0 -> "All problems completed"
                                    remainingCount == 1 -> "Finish 1 problem"
                                    else -> "Finish $remainingCount problems"
                                }
                                binding.tvCompletedPoint.text = "$completeCount/${quests.size} "
                                setProgress(completed = completeCount, total = quests.size)
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                binding.tvEmpty.visibility = View.VISIBLE
                                showErrorToast(getString(R.string.something_went_wrong))
                            }.also {
                                hideLoading()
//                                viewModel.getProfileApi(Constants.GET_PROFILE)
                            }
                        }

                        "getProfileApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: SignupResponse? = BindingUtils.parseJson(jsonData)
                                val loginData = model?.stats
                                if (loginData != null) {
                                    binding.bean = loginData
                                } else {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(getString(R.string.something_went_wrong))
                            }.also {
                                val data = HashMap<String, Any>()
                                val grade = sharedPrefManager.getLoginData()?.grade
                                val todayDate = getCurrentDate()
                                if (!grade.isNullOrEmpty()) {
                                    data["class"] = grade
                                    data["date"] = todayDate
                                    viewModel.getHomeApi(data, Constants.DAILY_QUEST)
                                }
                                else{
                                    hideLoading()
                                    chooseGradeDialog?.show()
                                    binding.tvEmpty.visibility = View.VISIBLE
                                }
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
     * question adapter
     */
    private fun initOnQuestionAdapter() {
        questionAdapter = SimpleRecyclerViewAdapter(R.layout.rv_question_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.clPreview -> {
                    if (m.type == "questReading") {
                        if (m.userProgress?.reading?.status == "completed") {
                            showInfoToast("You have completed this reading session.")
                        } else {
                            val action =
                                FeaturedQuizzesFragmentDirections.navigateToReadingSessionFragment(
                                    quizId = m.readingId?._id.toString(),
                                    content = m.readingId?.content.toString()
                                )
                            BindingUtils.navigateWithSlide(findNavController(), action)
                        }
                    } else {
                        if (m.userProgress?.quiz?.status == "completed") {
                            initDialog(m.userProgress.quiz)
                        } else {
                            val action =
                                FeaturedQuizzesFragmentDirections.navigateToQuizQuestionFragment(
                                    quizId = m.testQuizId?._id.toString()
                                )
                            BindingUtils.navigateWithSlide(findNavController(), action)
                        }


                    }
                }
            }
        }
        binding.rvQuestion.adapter = questionAdapter
    }


    private fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
            Log.e("fsds", "setUpObserver: $e")
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    showErrorToast("Notification Permission Denied")
                }
            }
        }
    }

    private fun calculateProgressPercent(completed: Int, total: Int): Float {
        if (total <= 0) return 0f
        return completed.toFloat() / total.toFloat()
    }

    private fun setProgress(completed: Int, total: Int) {
        val percent = calculateProgressPercent(completed, total)

        val params = binding.progressionGuideline.layoutParams as ConstraintLayout.LayoutParams
        params.guidePercent = percent.coerceIn(0f, 1f)
        binding.progressionGuideline.layoutParams = params
    }


    /**
     *  handel results dialog
     **/
    private fun initDialog(selectedAnswers: Quiz) {

        quizEndDialog = BaseCustomDialog(requireActivity(), R.layout.dialog_quiz_completed) {
            when (it?.id) {
                R.id.ivCancel -> quizEndDialog?.dismiss()
            }
        }

        quizEndDialog?.setCancelable(false)
        quizEndDialog?.show()


        val totalQuestions =
            (selectedAnswers.correctCount ?: 0) + (selectedAnswers.incorrectCount ?: 0)

        quizEndDialog?.binding?.tvQuesValue?.text = totalQuestions.toString()
        quizEndDialog?.binding?.tvXCorrectPoint?.text = selectedAnswers.correctCount.toString()
        quizEndDialog?.binding?.tvWrongPoint?.text = selectedAnswers.incorrectCount.toString()
        quizEndDialog?.binding?.circularProgressBar?.apply {
            setTextView(quizEndDialog?.binding?.tvScoredValue)
            if (selectedAnswers.points != null) {
                setProgress(selectedAnswers.points)
            }
        }
        // Set values in dialog views
        quizEndDialog?.binding?.tvScoredValue?.text = "+${selectedAnswers.points}"
        quizEndDialog?.binding?.circularProgressBar?.apply {
            setTextView(quizEndDialog?.binding?.tvScoredValue)

            val total = totalQuestions
            val correct = selectedAnswers.correctCount ?: 0

            setMaxProgress(total * 10)
            setProgress(correct * 10)
        }
        // call back
        quizEndDialog?.setOnDismissListener {
            quizEndDialog?.dismiss()
        }
    }

    private fun selectGradeDialog(){
        chooseGradeDialog = BaseCustomDialog(requireActivity(), R.layout.dialog_delete_logout) {
            when (it?.id) {
                R.id.tvCancel -> chooseGradeDialog?.dismiss()
                R.id.tvLogout -> {
                    chooseGradeDialog?.dismiss()
                    val action =
                        ProfileFragmentDirections.navigateToChooseGradeFragment( from = "settings")
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }
            }
        }
        chooseGradeDialog?.binding?.tvTitle?.text = "Select your Grade"
        chooseGradeDialog?.binding?.tvSubHeading?.text = "Select your grade to track your XP progress and badge rewards."
        chooseGradeDialog?.binding?.tvLogout?.text = "Continue"
        chooseGradeDialog?.setCancelable(false)
    }

    /*

         // chart finalize
     initOnPreviewAdapter()
        initOnSnapshotAdapter()
        initOnQuestionTeamAdapter()
        initOnProgressValveAdapter()
        initOnUnitAlveAdapterAdapter()
        initOnBudgetEarnedAdapter()
        initLineChart()
    private lateinit var previewAdapter: SimpleRecyclerViewAdapter<PreviewModel, RvPreviewItemBinding>
    private lateinit var questionTeamAdapter: SimpleRecyclerViewAdapter<QuestionModel, RvTeamQuestionItemBinding>
    private lateinit var progressValveAdapter: SimpleRecyclerViewAdapter<UnitModel, RvUnitItemBinding>
    private lateinit var unitAlveAdapter: SimpleRecyclerViewAdapter<RoastedModel, RvRoasterItemBinding>
    private lateinit var snapshotAdapter: SimpleRecyclerViewAdapter<RoastedModel, RvSnapShotItemBinding>
    private lateinit var budgetEarnedAdapter: SimpleRecyclerViewAdapter<PreviewModel, RvBadgesEarnedItemBinding>
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
    private fun initOnQuestionTeamAdapter() {
        questionTeamAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_team_question_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.tvType -> {

                    }

                }
            }
        binding.rvTeamQuestion.adapter = questionTeamAdapter
        questionTeamAdapter.list = DummyList.questionTeamList()

    }
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
    private fun initOnSnapshotAdapter() {
        snapshotAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_snap_shot_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clSnapShot -> {
                        for (i in snapshotAdapter.list) {
                            i.check = i.xp == m.xp
                        }
                        snapshotAdapter.notifyDataSetChanged()
                    }

                }
            }
        binding.rvRoasterSnap.adapter = snapshotAdapter
        snapshotAdapter.list = DummyList.snapShotList()

    }
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
    */

}