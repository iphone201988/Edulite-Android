package com.edu.lite.ui.dash_board.profile.rewards

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.ThemeHelper
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.model.RewardsModel
import com.edu.lite.databinding.FragmentRewardsBinding
import com.edu.lite.databinding.RewardsDialogItemBinding
import com.edu.lite.databinding.RvRewardsItemBinding
import com.edu.lite.ui.dash_board.profile.ProfileFragmentVM
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.DummyList
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RewardsFragment : BaseFragment<FragmentRewardsBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private lateinit var rewardsAdapter: SimpleRecyclerViewAdapter<RewardsModel, RvRewardsItemBinding>
    private var getRewardsDialog: BaseCustomDialog<RewardsDialogItemBinding>? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_rewards
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initOnRewardsAdapter()
        // open dialog
        initDialog()

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
     * rewards adapter
     */
    private fun initOnRewardsAdapter() {
        rewardsAdapter = SimpleRecyclerViewAdapter(R.layout.rv_rewards_item, BR.bean) { v, m, pos ->
            when (pos) {
                0 -> {
                    changeTheme("green")

                }

                1 -> {
                    changeTheme("purple")
                }
                2 -> {
                    changeTheme("blue")
                }
                3-> {
                    changeTheme("orange")
                }

            }
        }

        binding.rvRewards.adapter = rewardsAdapter
        rewardsAdapter.list = DummyList.rewardsList(requireActivity())
    }


    private fun changeTheme(themeName: String) {
        ThemeHelper.saveThemeName(requireActivity(), themeName)
        requireActivity().recreate() // restart Activity with new theme
    }

    /**
     *  rewards dialog
     **/
    private fun initDialog() {
        getRewardsDialog = BaseCustomDialog(requireActivity(), R.layout.rewards_dialog_item) {
            when (it?.id) {
                R.id.btnClaim -> getRewardsDialog?.dismiss()
            }
        }

        getRewardsDialog?.setCancelable(false)
        getRewardsDialog?.show()

        Glide.with(requireContext()).load(R.drawable.reward)
            .into(getRewardsDialog?.binding?.imageAnimate!!)
        Glide.with(requireContext()).load(R.drawable.congre)
            .into(getRewardsDialog?.binding?.congrateAnimate!!)

    }
}