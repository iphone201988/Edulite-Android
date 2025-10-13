package com.edu.lite.ui.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.edu.lite.base.BaseViewModel
import com.edu.lite.utils.Resource
import com.edu.lite.utils.event.SingleRequestEvent
import com.edu.lite.data.api.ApiHelper
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthCommonVM @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {

    val observeCommon = SingleRequestEvent<JsonObject>()
    // create account api
    fun createAccount(url: String, request: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiForRawBody(request,url )
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("createAccount", response.body()))
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

    // login api
    fun loginApi(url: String, request: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiForRawBody(request,url )
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("loginApi", response.body()))
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



    // code verification api
    fun codeVerificationApi(url: String, request: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiForRawBody(request,url )
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("codeVerificationApi", response.body()))
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


    // forgot email api
    fun forgotEmailApi(url: String, request: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiForRawBody(request,url )
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("forgotEmailApi", response.body()))
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



    // reset Password  api
    fun resetPassword(url: String, request: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            observeCommon.postValue(Resource.loading(null))
            runCatching {
                val response = apiHelper.apiForRawBody(request,url )
                if (response.isSuccessful) {
                    observeCommon.postValue(Resource.success("resetPassword", response.body()))
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


}

