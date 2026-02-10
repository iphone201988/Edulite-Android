package com.edu.lite.ui.dash_board.home.video_lessons

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.ApiHelper
import com.edu.lite.data.room_moduel.DownloadVideoData
import com.edu.lite.utils.Resource
import com.edu.lite.utils.event.SingleRequestEvent
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoLessonsFragmentVM @Inject constructor(
    private val apiHelper: ApiHelper, private val repository: VideoRepository
) : BaseViewModel() {
    val observeCommon = SingleRequestEvent<JsonObject>()
    val observeVideo = SingleRequestEvent<List<DownloadVideoData>>()

    fun insertVideo(video: DownloadVideoData) {
        viewModelScope.launch {
            repository.insertVideo(video)
        }
    }

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


    fun getVideoApi(data: HashMap<String, Any>, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiGetWithQuery(data, url)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("getVideoApi", response.body()))
                } else {
                    val errorMsg = handleErrorResponse(response.errorBody(), response.code())
                    observeCommon.postValue(Resource.error(errorMsg, null))
                }
            }.onFailure { e ->
                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                observeCommon.postValue(Resource.error("Something went wrong: ${e.message}", null))
            }
        }
    }
}