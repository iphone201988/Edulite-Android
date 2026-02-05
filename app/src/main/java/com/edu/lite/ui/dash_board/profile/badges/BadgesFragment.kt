package com.edu.lite.ui.dash_board.profile.badges

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.model.Badge
import com.edu.lite.data.model.BadgesModel
import com.edu.lite.databinding.CorrectAnswerDialogItemBinding
import com.edu.lite.databinding.FragmentBadgesBinding
import com.edu.lite.databinding.RvBadgesItemBinding
import com.edu.lite.databinding.WrongAnswerDialogItemBinding
import com.edu.lite.ui.dash_board.home.quiz.question.QuizQuestionFragmentArgs
import com.edu.lite.ui.dash_board.profile.ProfileFragmentVM
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.DummyList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.builtins.ArraySerializer
import kotlin.getValue


@AndroidEntryPoint
class BadgesFragment : BaseFragment<FragmentBadgesBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private lateinit var badgesAdapter: SimpleRecyclerViewAdapter<Badge, RvBadgesItemBinding>

    private val args: BadgesFragmentArgs by navArgs()
    private var badgesList = ArrayList<Badge>()
    private var correctAnswerDialog: BaseCustomDialog<CorrectAnswerDialogItemBinding>? = null
    private var wrongAnswerDialog: BaseCustomDialog<WrongAnswerDialogItemBinding>? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_badges
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        badgesList = args.statsData?.badges as ArrayList<Badge>
        if (badgesList.isNullOrEmpty()){
            binding.rvBadges.visibility = View.GONE
            binding.listEmpty.visibility = View.VISIBLE
        }
        else{
            binding.rvBadges.visibility = View.VISIBLE
            binding.listEmpty.visibility = View.GONE
        }
        // click
        initOnClick()

        // adapter
        initOnBadgesAdapter()
        // dialog
      //  initCorrectAnswerDialog()
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
     * badges adapter
     */
    private fun initOnBadgesAdapter() {
        badgesAdapter = SimpleRecyclerViewAdapter(R.layout.rv_badges_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.clCreative -> {

                }
            }
        }
        binding.rvBadges.adapter = badgesAdapter
        badgesAdapter.list = badgesList
    }


    /**
     *  correct Answer dialog
     **/
    private fun initCorrectAnswerDialog() {
        correctAnswerDialog =
            BaseCustomDialog(requireActivity(), R.layout.correct_answer_dialog_item) {
                when (it?.id) {
                    R.id.btnNext -> {
                        correctAnswerDialog?.dismiss()
                        initWrongAnswerDialog()
                    }

                    R.id.ivCancel -> {
                        correctAnswerDialog?.dismiss()
                    }
                }
            }

        correctAnswerDialog?.setCancelable(false)
        correctAnswerDialog?.show()


    }

    /**
     *  wrong Answer dialog
     **/
    private fun initWrongAnswerDialog() {
        wrongAnswerDialog = BaseCustomDialog(requireActivity(), R.layout.wrong_answer_dialog_item) {
            when (it?.id) {
                R.id.btnTryAgain -> {
                    wrongAnswerDialog?.dismiss()
                }

                R.id.btnGetHint -> {
                    wrongAnswerDialog?.dismiss()
                }

                R.id.ivCancel -> {
                    wrongAnswerDialog?.dismiss()
                }
            }
        }

        wrongAnswerDialog?.setCancelable(false)
        wrongAnswerDialog?.show()


    }

}