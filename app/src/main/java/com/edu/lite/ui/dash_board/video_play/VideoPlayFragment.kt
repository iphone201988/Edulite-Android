package com.edu.lite.ui.dash_board.video_play

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
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

    override fun getLayoutResource(): Int {
        return R.layout.fragment_video_play
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        videoUrl = if (args.videoUrl.isNotEmpty()){
            Constants.BASE_URL_IMAGE+args.videoUrl
        }else{
            args.videoPath
        }
        // initialize player
        initializePlayer()
        // click
        initOnClick()
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
     * Initialize ExoPlayer safely
     */
    private fun initializePlayer() {
        val url = videoUrl
        if (url.isNullOrEmpty()) {
            showInfoToast("Video URL not found")
            return
        }

        player = ExoPlayer.Builder(requireContext()).build().apply {
            binding.playerView.player = this
            setMediaItem(MediaItem.fromUri(Uri.parse(url)))
            prepare()
            playWhenReady = true

            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e("ExoPlayer", "Playback error: ${error.message}", error)
                    showInfoToast("Video failed to load.")
                }
            })
        }
    }

    /**
     * Pause playback when fragment is not visible
     */
    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    /**
     * Release resources to prevent leaks
     */
    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }


}