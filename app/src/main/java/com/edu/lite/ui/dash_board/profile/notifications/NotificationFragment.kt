package com.edu.lite.ui.dash_board.profile.notifications

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.GetNotificationApiResponse
import com.edu.lite.data.model.NotificationData
import com.edu.lite.databinding.FragmentNotificationBinding
import com.edu.lite.databinding.RvNotificationItemBinding
import com.edu.lite.ui.dash_board.profile.ProfileFragmentVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()

    private lateinit var notificationAdapter: SimpleRecyclerViewAdapter<NotificationData, RvNotificationItemBinding>

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
        // observer
        initObserver()
        // api call
        viewModel.getNotificationApi(Constants.NOTIFICATIONS)
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


    /** api response observer ***/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getNotificationApi" -> {

                            runCatching {
                                val jsonData = it.data?.toString()
                                if (jsonData.isNullOrBlank()) {
                                    binding.listEmpty.visibility = View.VISIBLE
                                    return@runCatching
                                }

                                val model: GetNotificationApiResponse? =
                                    try {
                                        BindingUtils.parseJson(jsonData)
                                    } catch (e: Exception) {
                                        null
                                    }

                                val notifications = model?.data ?: emptyList()

                                notificationAdapter.list = notifications
                                binding.listEmpty.visibility =
                                    if (notifications.isEmpty()) View.VISIBLE else View.GONE

                            }.onFailure {
                                binding.listEmpty.visibility = View.VISIBLE
                            }.also {
                                hideLoading()
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

    }

}