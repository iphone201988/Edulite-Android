package com.edu.lite.ui.dash_board.download

import androidx.lifecycle.viewModelScope
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.room_moduel.DownloadVideoData
import com.edu.lite.ui.dash_board.home.video_lessons.VideoRepository
import com.edu.lite.utils.Resource
import com.edu.lite.utils.event.SingleRequestEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadFragmentVM @Inject constructor(private val repository: VideoRepository): BaseViewModel(){
    val observeVideo = SingleRequestEvent<List<DownloadVideoData>>()

    fun getAllVideos() {
        viewModelScope.launch {
            val list = repository.getAllVideos()
            observeVideo.postValue(Resource.success("getDownloadVideo", list))
        }
    }

    fun deleteVideo(id: String?) {
        viewModelScope.launch {
            repository.deleteVideoById(id)
        }
    }
}