package com.edu.lite.ui.dash_board.home.video_lessons

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.GetVideoApiResponse
import com.edu.lite.data.model.PreviewModel
import com.edu.lite.data.model.VideoData
import com.edu.lite.data.room_moduel.DownloadVideoData
import com.edu.lite.data.room_moduel.UserId
import com.edu.lite.databinding.FragmentVideoLessonsBinding
import com.edu.lite.databinding.RvPracticeCategoryItemBinding
import com.edu.lite.databinding.RvVideoDownloadItemBinding
import com.edu.lite.databinding.RvVideoLessonsItemBinding
import com.edu.lite.ui.dash_board.video_play.VideoPlayFragmentDirections
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.DummyList
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL


@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class VideoLessonsFragment : BaseFragment<FragmentVideoLessonsBinding>() {
    private val viewModel: VideoLessonsFragmentVM by viewModels()
    private val args: VideoLessonsFragmentArgs by navArgs()
    private lateinit var videoCategoryAdapter: SimpleRecyclerViewAdapter<PreviewModel, RvPracticeCategoryItemBinding>
    private lateinit var videoCategoryAdapter1: SimpleRecyclerViewAdapter<PreviewModel, RvPracticeCategoryItemBinding>
    private lateinit var videoLessonsAdapter: SimpleRecyclerViewAdapter<VideoData, RvVideoLessonsItemBinding>
    private lateinit var downloadAdapter: SimpleRecyclerViewAdapter<DownloadVideoData, RvVideoDownloadItemBinding>
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false

    override fun getLayoutResource(): Int {
        return R.layout.fragment_video_lessons
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initOnVideoLessonsCategoryAdapter()
        initOnVideoLessonsCategoryAdapter1()
        initOnVideoLessonsAdapter()
        initOnDownloadAdapter()
        // nested
        binding.clNested.setOnScrollChangedListener { who, l, t, old, olden ->
            val introBottom = binding.rvVideoCategory.bottom
            if (t >= introBottom) {
                binding.rvVideoCategory1.visibility = View.GONE
            } else {
                binding.rvVideoCategory1.visibility = View.GONE
            }
        }
        viewModel.getAllVideos()
        // observer
        initObserver()
        // pagination
        pagination()
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
                        "getVideoApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: GetVideoApiResponse? = BindingUtils.parseJson(jsonData)
                                val downloadedIds: Set<String> =
                                    downloadAdapter.list.mapNotNull { it._id }.toSet()
                                val videos = model?.videos.orEmpty().map { video ->
                                    video?.copy(
                                        videoDownload = video._id != null && downloadedIds.contains(
                                            video._id
                                        )
                                    )
                                }
                                if (currentPage == 1) {
                                    videoLessonsAdapter.list = videos
                                } else {
                                    videoLessonsAdapter.addToList(videos)
                                }
                                isLastPage = model?.pagination?.hasNextPage != true
                                binding.listEmpty.visibility =
                                    if (videoLessonsAdapter.list.isEmpty()) View.VISIBLE
                                    else View.GONE
                                if (model == null) {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                binding.listEmpty.visibility = View.VISIBLE
                                showErrorToast(
                                    e.localizedMessage ?: getString(R.string.something_went_wrong)
                                )
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

        viewModel.observeVideo.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getDownloadVideo" -> {
                            runCatching {
                                downloadAdapter.list = it.data

                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                binding.listEmpty.visibility = View.VISIBLE
                                showErrorToast(
                                    e.localizedMessage ?: getString(R.string.something_went_wrong)
                                )
                            }.also {
                                // api call
                                val data = HashMap<String, Any>()
                                data["grade"] = args.grade
                                data["subject"] = args.subjectId
                                data["page"] = currentPage
                                viewModel.getVideoApi(data, Constants.VIDEO)
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
     * video lessons category adapter
     */
    private fun initOnVideoLessonsCategoryAdapter() {
        videoCategoryAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_practice_category_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPracticeCategory -> {
                        if (m.title == getString(R.string.completed)) {
                            binding.rvVideoLessons.visibility = View.GONE
                            binding.rvVideoDownload.visibility = View.VISIBLE
                        } else {
                            binding.rvVideoLessons.visibility = View.VISIBLE
                            binding.rvVideoDownload.visibility = View.GONE
                        }
                        for (i in videoCategoryAdapter.list) {
                            i.isCheck = i.title == m.title
                        }
                        videoCategoryAdapter.notifyDataSetChanged()
                    }

                }
            }

        binding.rvVideoCategory.adapter = videoCategoryAdapter
        videoCategoryAdapter.list = DummyList.practiceCategoryList(requireActivity())

    }

    private fun initOnVideoLessonsCategoryAdapter1() {
        videoCategoryAdapter1 =
            SimpleRecyclerViewAdapter(R.layout.rv_practice_category_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clPracticeCategory -> {
                        if (m.title == getString(R.string.completed)) {
                            binding.rvVideoLessons.visibility = View.GONE
                            binding.rvVideoDownload.visibility = View.VISIBLE
                        } else {
                            binding.rvVideoLessons.visibility = View.VISIBLE
                            binding.rvVideoDownload.visibility = View.GONE
                        }
                        for (i in videoCategoryAdapter1.list) {
                            i.isCheck = i.title == m.title
                        }
                        videoCategoryAdapter1.notifyDataSetChanged()
                    }

                }
            }

        binding.rvVideoCategory1.adapter = videoCategoryAdapter1
        videoCategoryAdapter1.list = DummyList.videoCategoryList(requireActivity())

    }


    /**
     * video lessons adapter
     */
    private fun initOnVideoLessonsAdapter() {
        videoLessonsAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_video_lessons_item, BR.bean) { v, m, pos ->
                when (v?.id) {
                    R.id.btnWatchNow, R.id.ivBackButton -> {
                        if (m.videoDownload == true) {
                            val action =
                                VideoPlayFragmentDirections.navigateToVideoPlayFragment(videoUrl = m.videoUrl.toString())
                            BindingUtils.navigateWithSlide(findNavController(), action)
                        } else {
                            if (checkPermission()) {
                                downloadAndSaveVideo(m, pos)
                                showLoading()
                            }
                        }

                    }
                }
            }

        binding.rvVideoLessons.adapter = videoLessonsAdapter
    }

    private fun initOnDownloadAdapter() {
        downloadAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_video_download_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clLetPlay -> {
                        val action =
                            VideoPlayFragmentDirections.navigateToVideoPlayFragment(videoPath = m.localPath.toString())
                        BindingUtils.navigateWithSlide(findNavController(), action)
                    }

                }
            }

        binding.rvVideoDownload.adapter = downloadAdapter
    }


    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val permission = ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001
                )
                false
            } else true
        } else true
    }


    private fun downloadAndSaveVideo(video: VideoData, posInt: Int) {
        showLoading()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val videoUrl = Constants.BASE_URL_IMAGE + video.videoUrl
                Log.d("VideoDownload", "Downloading from: $videoUrl")

                val localPath = requireContext().downloadVideo(
                    url = videoUrl, fileName = "${video._id}.mp4"
                )

                if (localPath == null) {
                    throw Exception("Download failed, localPath is null")
                }

                val downloadVideo = DownloadVideoData(
                    _id = video._id,
                    createdAt = video._id,
                    grade = video.grade,
                    subject = video.subject,
                    thumbnailUrl = video.thumbnailUrl,
                    time = video.time,
                    title = video.title,
                    userId = UserId(
                        _id = video.userId?._id,
                        email = video.userId?.email,
                        name = video.userId?.name,
                    ),
                    videoDownload = true,
                    localPath = localPath
                )

                viewModel.insertVideo(downloadVideo)

                launch(Dispatchers.Main) {
                    videoLessonsAdapter.list[posInt].videoDownload = true
                    videoLessonsAdapter.notifyItemChanged(posInt)
                    hideLoading()
                    showSuccessToast("Video downloaded successfully")
                }

            } catch (e: Exception) {
                Log.e("VideoDownloadError", e.message ?: "Unknown error", e)

                launch(Dispatchers.Main) {
                    hideLoading()
                    showErrorToast("Video download failed. Please try again.")
                }
            }
        }
    }


    fun Context.downloadVideo(url: String, fileName: String): String? {
        return try {
            val input = URL(url).openStream()
            val file = File(filesDir, fileName)
            val output = FileOutputStream(file)
            input.use { inp ->
                output.use { outp ->
                    inp.copyTo(outp)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * quiz handel pagination
     */

    private fun pagination() {
        binding.rvVideoLessons.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        loadMoreItems()
                    }
                }
            }


        })
    }

    /**
     * load more api  call
     */
    private fun loadMoreItems() {
        isLoading = true
        currentPage++
        val data = HashMap<String, Any>()
        data["grade"] = args.grade
        data["subject"] = args.subjectId
        data["page"] = currentPage
        viewModel.getVideoApi(data, Constants.VIDEO)
    }

}
