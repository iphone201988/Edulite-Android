package com.edu.lite.ui.dash_board.profile

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.ApiHelper
import com.edu.lite.data.room_moduel.DownloadVideoData
import com.edu.lite.ui.dash_board.home.video_lessons.VideoRepository
import com.edu.lite.utils.Resource
import com.edu.lite.utils.event.SingleRequestEvent
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentVM @Inject constructor(
    private val apiHelper: ApiHelper,
    private val repository: VideoRepository
) : BaseViewModel() {

    val observeCommon = SingleRequestEvent<JsonObject>()

    val observeVideo = SingleRequestEvent<List<DownloadVideoData>>()

    fun getAllVideos() {
        viewModelScope.launch {
            val list = repository.getAllVideos()
            observeVideo.postValue(Resource.success("getDownloadVideo", list))
        }
    }

    fun deleteVideo(id: Int) {
        viewModelScope.launch {
            repository.deleteVideoById(id)
        }
    }

    // change Password  api
    fun changePassword(url: String, request: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiPostForRawBody(url, request)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("changePassword", response.body()))
                } else {
                    val errorMsg = handleErrorResponse(response.errorBody(), response.code())
                    observeCommon.postValue(Resource.error(errorMsg, null))
                }
            }.onFailure { e ->
                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                observeCommon.postValue(Resource.error("${e.message}", null))
            }
        }
    }


    // logout api
    fun logoutApi(url: String, request: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiPostForRawBody(url, request)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("logoutApi", response.body()))
                } else {
                    val errorMsg = handleErrorResponse(response.errorBody(), response.code())
                    observeCommon.postValue(Resource.error(errorMsg, null))
                }
            }.onFailure { e ->
                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                observeCommon.postValue(Resource.error("${e.message}", null))
            }
        }
    }


    // delete api
    fun deleteApi(url: String, request: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))

            runCatching {
                val response = apiHelper.apiPostForRawBody(url, request)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("deleteApi", response.body()))
                } else {
                    val errorMsg = handleErrorResponse(response.errorBody(), response.code())
                    observeCommon.postValue(Resource.error(errorMsg, null))
                }
            }.onFailure { e ->
                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                observeCommon.postValue(Resource.error("${e.message}", null))
            }
        }
    }


    // update profile api
    fun updateProfileApi(
        url: String, request: HashMap<String, Any>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))

            runCatching {
                val response = apiHelper.apiPostForRawBody(url, request)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("updateProfileApi", response.body()))
                } else {
                    val errorMsg = handleErrorResponse(response.errorBody(), response.code())
                    observeCommon.postValue(Resource.error(errorMsg, null))
                }
            }.onFailure { e ->
                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                observeCommon.postValue(Resource.error("${e.message}", null))
            }
        }
    }


    // upload Profile api
    fun uploadProfile(url: String, part: MultipartBody.Part?) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiForPostMultipart(url, part)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("uploadProfile", response.body()))
                } else {
                    val errorMsg = handleErrorResponse(response.errorBody(), response.code())
                    observeCommon.postValue(Resource.error(errorMsg, null))
                }
            }.onFailure { e ->
                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                observeCommon.postValue(Resource.error("${e.message}", null))
            }
        }
    }

    fun getNotificationApi(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))

            runCatching {
                val response = apiHelper.apiGetOnlyAuthToken(url)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("getNotificationApi", response.body()))
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

    fun getHomeApi(data: HashMap<String, Any>, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiGetWithQuery(data, url)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("getHomeApi", response.body()))
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

    fun getProfileApi(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))

            runCatching {
                val response = apiHelper.apiGetOnlyAuthToken(url)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("getProfileApi", response.body()))
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