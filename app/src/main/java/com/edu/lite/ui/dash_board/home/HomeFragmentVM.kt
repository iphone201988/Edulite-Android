package com.edu.lite.ui.dash_board.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.ApiHelper
import com.edu.lite.data.room_moduel.RoomDataBaseQueryPage
import com.edu.lite.utils.Resource
import com.edu.lite.utils.event.SingleRequestEvent
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFragmentVM @Inject constructor(
    private val apiHelper: ApiHelper, private val dao: RoomDataBaseQueryPage
) : BaseViewModel() {

    val observeCommon = SingleRequestEvent<JsonObject>()

    fun getGradeApi(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))

            runCatching {
                val response = apiHelper.apiGetOnlyAuthToken(url)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("getGradeApi", response.body()))
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

    fun getCreativeApi(data: HashMap<String, Any>, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiGetWithQuery(data, url)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("getCreativeApi", response.body()))
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


    fun getCreativeDetailsApi(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))

            runCatching {
                val response = apiHelper.apiGetOnlyAuthToken(url)
                if (response.isSuccessful) {
                    observeCommon.postValue(
                        Resource.success(
                            "getCreativeDetailsApi",
                            response.body()
                        )
                    )
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
}
