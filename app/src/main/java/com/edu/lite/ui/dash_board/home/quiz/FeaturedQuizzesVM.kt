package com.edu.lite.ui.dash_board.home.quiz

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.ApiHelper
import com.edu.lite.utils.Resource
import com.edu.lite.utils.event.SingleRequestEvent
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeaturedQuizzesVM @Inject constructor(private val apiHelper: ApiHelper): BaseViewModel(){

    val observeCommon = SingleRequestEvent<JsonObject>()
    fun getFeaturesApi(data: HashMap<String, Any>,url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiGetWithQuery(data,url)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("getFeaturesApi", response.body()))
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


    fun quizQuestionApi(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))

            runCatching {
                val response = apiHelper.apiGetOnlyAuthToken(url)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("quizQuestionApi", response.body()))
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

    fun postUserResponse(url: String, request:HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))

            runCatching {
                val response = apiHelper.apiPostForRawBody(url,request)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("postUserResponse", response.body()))
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

    fun postUserBack(url: String, request:HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))

            runCatching {
                val response = apiHelper.apiPostForRawBody(url,request)
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("postUserResponse", response.body()))
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