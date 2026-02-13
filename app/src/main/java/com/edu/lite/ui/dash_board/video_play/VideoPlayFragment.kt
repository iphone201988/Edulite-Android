package com.edu.lite.ui.dash_board.video_play

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.databinding.FragmentVideoPlayBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayFragment : BaseFragment<FragmentVideoPlayBinding>() {

    private val viewModel: VideoPlayFragmentVM by viewModels()
    private val args: VideoPlayFragmentArgs by navArgs()

    private var player: ExoPlayer? = null
    private var videoUrl: String? = null

    private var isFullscreen = false
    private var isMuted = false

    private var playWhenReady = true
    private var playbackPosition = 0L

    override fun getLayoutResource(): Int = R.layout.fragment_video_play
    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(view: View) {
        videoUrl = if (args.videoUrl.isNotEmpty()) {
            Constants.BASE_URL_IMAGE + args.videoUrl
        } else {
            args.videoPath
        }

        initializePlayer()
        initOnClick()
        handleBackPress()
    }

    /**
     * Header click handling
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBackButton -> findNavController().popBackStack()
                R.id.ivFullscreen -> { toggleFullscreen()
                     }
                R.id.ivMute -> toggleMute()
            }
        }
    }

    /**
     * Initialize ExoPlayer
     */
    private fun initializePlayer() {
        val url = videoUrl ?: run {
            showInfoToast("Video URL not found")
            return
        }

        player = ExoPlayer.Builder(requireContext()).build().apply {
            binding.playerView.player = this
            setMediaItem(MediaItem.fromUri(Uri.parse(url)))
            prepare()
            seekTo(playbackPosition)
            playWhenReady = this@VideoPlayFragment.playWhenReady
            setupControllerVisibility()

            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e("ExoPlayer", "Playback error: ${error.message}", error)
                    showInfoToast("Video failed to load")
                }
            })
        }
    }

    private fun setupControllerVisibility() {
        binding.playerView.setControllerVisibilityListener(
            PlayerView.ControllerVisibilityListener { visibility ->
                val show = visibility == View.VISIBLE

                if (show) {
                    binding.ivMute.fadeInSlideUp()
                    binding.ivFullscreen.fadeInSlideUp()
                } else {
                    binding.ivMute.fadeOutSlideDown()
                    binding.ivFullscreen.fadeOutSlideDown()
                }
            })
    }

    /**
     * Fullscreen + rotation
     */
    private fun toggleFullscreen() {

        isFullscreen = !isFullscreen

        if (isFullscreen) {
            enterFullscreen()
        } else {
            exitFullscreen()
        }
    }

    private fun enterFullscreen() {

        isFullscreen = true

        requireActivity().requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

//        hideHeader(true)
        hideSystemUI()

        val params = binding.videoContainer.layoutParams as ConstraintLayout.LayoutParams

        // Clear previous constraints
        params.topToBottom = ConstraintLayout.LayoutParams.UNSET
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

        binding.videoContainer.layoutParams = params

        binding.ivFullscreen.setImageResource(R.drawable.ic_minimise)
    }


    private fun exitFullscreen() {

        isFullscreen = false

        requireActivity().requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

//        hideHeader(false)
        showSystemUI()

        val params = binding.videoContainer.layoutParams as ConstraintLayout.LayoutParams

        // Clear fullscreen constraints
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
//        params.topToBottom = R.id.ivBackButton
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

        binding.videoContainer.layoutParams = params

        binding.ivFullscreen.setImageResource(R.drawable.ic_maximise)
    }
    /**
     * Mute / Unmute
     */
    private fun toggleMute() {
        isMuted = !isMuted
        player?.volume = if (isMuted) 0f else 1f

        binding.ivMute.setImageResource(
            if (isMuted) R.drawable.ic_sound_off
            else R.drawable.ic_sound_on
        )
    }

    /**
     * Back press → exit fullscreen first
     */
    private fun handleBackPress() {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner
        ) {
            if (isFullscreen) {
                exitFullscreen()
                isFullscreen = false
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun hideHeader(hide: Boolean) {
        binding.clHeader.root.visibility = if (hide) View.GONE else View.VISIBLE
        binding.tvChoose.visibility = if (hide) View.GONE else View.VISIBLE
        binding.ivBackButton.visibility = if (hide) View.GONE else View.VISIBLE
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        WindowInsetsControllerCompat(
            requireActivity().window,
            requireActivity().window.decorView
        ).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
        WindowInsetsControllerCompat(
            requireActivity().window,
            requireActivity().window.decorView
        ).show(WindowInsetsCompat.Type.systemBars())
    }

    override fun onPause() {
        super.onPause()
        savePlayerState()
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Reset system UI
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        requireActivity().window.statusBarColor = Color.TRANSPARENT

        savePlayerState()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun savePlayerState() {
        player?.let {
            playWhenReady = it.playWhenReady
            playbackPosition = it.currentPosition
        }
    }

    private fun View.fadeInSlideUp() {
        if (visibility == View.VISIBLE) return

        // start slightly below
        alpha = 0f
        translationY = height * 0.25f
        visibility = View.VISIBLE

        animate().alpha(1f).translationY(0f).setDuration(1)
            .setInterpolator(android.view.animation.DecelerateInterpolator()).start()
    }

    private fun View.fadeOutSlideDown() {
        if (visibility != View.VISIBLE) return

        animate().alpha(0f).translationY(height * 0.25f).setDuration(1)
            .setInterpolator(android.view.animation.AccelerateInterpolator()).withEndAction {
                visibility = View.GONE
                alpha = 1f
                translationY = 0f
            }.start()
    }

//    override fun onResume() {
//        super.onResume()
//
//        requireActivity().window.decorView.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//
//        requireActivity().window.statusBarColor = Color.TRANSPARENT
//    }
}