package com.edu.lite.ui.dash_board.profile.theme

import android.content.Intent
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
import com.edu.lite.databinding.DialogChangeThemeBinding
import com.edu.lite.databinding.FragmentChangeThemeBinding
import com.edu.lite.databinding.RewardsDialogItemBinding
import com.edu.lite.databinding.RvRewardsItemBinding
import com.edu.lite.ui.auth.WelcomeActivity
import com.edu.lite.ui.dash_board.profile.ProfileFragmentVM
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.DummyList
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChangeThemeFragment : BaseFragment<FragmentChangeThemeBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private lateinit var rewardsAdapter: SimpleRecyclerViewAdapter<RewardsModel, RvRewardsItemBinding>
    private var getRewardsDialog: BaseCustomDialog<RewardsDialogItemBinding>? = null

    private var themeChangeDialog: BaseCustomDialog<DialogChangeThemeBinding>? = null
    private var selectedThemeIndex: Int? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_change_theme
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
        //  initDialog()
        // theme dialog
        initThemeDialog()

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
            when (v.id) {
                R.id.clBadges -> {

                    val currentTheme = ThemeHelper.getSavedThemeName(requireActivity())

                    val selectedTheme = when (pos) {
                        0 -> "green"
                        1 -> "purple"
                        2 -> "orange"
                        3 -> "blue"
                        else -> return@SimpleRecyclerViewAdapter
                    }
                    if (currentTheme.equals(selectedTheme, true)) {
                        showErrorToast("Theme is already in use.")
                        return@SimpleRecyclerViewAdapter
                    }
                    selectedThemeIndex = pos
                    themeChangeDialog?.show()
                }

            }
        }

        binding.rvRewards.adapter = rewardsAdapter
        rewardsAdapter.list = DummyList.rewardsList(requireActivity())
    }


    private fun changeTheme(themeName: String) {
        ThemeHelper.saveThemeName(requireActivity(), themeName)
        restartApp()
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

    private fun initThemeDialog() {
        themeChangeDialog = BaseCustomDialog(requireContext(), R.layout.dialog_change_theme) {
            when (it?.id) {
                R.id.tvCancel -> {
                    themeChangeDialog?.dismiss()
                }

                R.id.tvLogout -> {
                    when (selectedThemeIndex) {
                        0 -> {
                            changeTheme("green")
                        }

                        1 -> {
                            changeTheme("purple")
                        }

                        2 -> {
                            changeTheme("orange")
                        }

                        3 -> {
                            changeTheme("blue")
                        }
                    }
                    themeChangeDialog?.dismiss()

                }
            }

        }
        themeChangeDialog?.setCancelable(false)
    }

    private fun restartApp() {
        val intent = Intent(requireContext(), WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}