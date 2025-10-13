package com.edu.lite.ui.auth.dash_board.profile.notifications

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.model.NotificationModel
import com.edu.lite.databinding.FragmentNotificationBinding
import com.edu.lite.databinding.RvNotificationItemBinding
import com.edu.lite.ui.auth.dash_board.profile.ProfileFragmentVM
import com.edu.lite.utils.DummyList
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()

    private lateinit var notificationAdapter: SimpleRecyclerViewAdapter<NotificationModel, RvNotificationItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_notification
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initOnNotificationAdapter()
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
     * notification Adapter
     */
    private fun initOnNotificationAdapter() {
        notificationAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_notification_item, BR.bean) { v, m, pos ->
                when (v?.id) {
                    R.id.clLetPlay -> {


                    }

                }
            }

        binding.rvNotification.adapter = notificationAdapter
        notificationAdapter.list = DummyList.notificationList()

    }

}