package com.edu.lite.ui.dash_board.profile.rewards

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.model.RewardsModel
import com.edu.lite.databinding.FragmentRewardsBinding
import com.edu.lite.databinding.RvRewardsItemBinding
import com.edu.lite.ui.dash_board.profile.ProfileFragmentVM
import com.edu.lite.utils.DummyList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RewardsFragment : BaseFragment<FragmentRewardsBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private lateinit var rewardsAdapter: SimpleRecyclerViewAdapter<RewardsModel, RvRewardsItemBinding>
    private var rewardsList = ArrayList<RewardsModel>()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_rewards
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        rewardsList = DummyList.rewardsList(requireActivity())
        if (rewardsList.isNullOrEmpty()) {
            binding.rvRewards.visibility = View.GONE
            binding.listEmpty.visibility = View.VISIBLE
        } else {
            binding.rvRewards.visibility = View.VISIBLE
            binding.listEmpty.visibility = View.GONE
        }
        // click
        initOnClick()

        // adapter
        initRewardsAdapter()
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
    private fun initRewardsAdapter() {
        rewardsAdapter = SimpleRecyclerViewAdapter(R.layout.rv_rewards_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.clCreative -> {

                }
            }
        }
        binding.rvRewards.adapter = rewardsAdapter
        rewardsAdapter.list = rewardsList
    }


}