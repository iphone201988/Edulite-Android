package com.edu.lite.base.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.edu.lite.utils.event.NetworkErrorHandler
import com.edu.lite.data.api.ApiHelper
import com.edu.lite.data.api.ApiHelperImpl
import com.edu.lite.data.api.ApiService
import com.edu.lite.data.api.Constants
import com.edu.lite.data.room_moduel.AppDb
import com.edu.lite.data.room_moduel.RoomDataBaseQueryPage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideBaseUrl() = Constants.BASE_URL
//    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Singleton
    @Provides
    fun networkErrorHandler(context: Application): NetworkErrorHandler {
        return NetworkErrorHandler(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
        .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        BASE_URL: String
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

    @Provides
    @Singleton
    fun provideSharedPref(application: Application): SharedPreferences {
        return application.getSharedPreferences(application.packageName, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(application: Application): AppDb {
        return Room.databaseBuilder(
            application,
            AppDb::class.java,
            "edu_lite_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideVideoDao(db: AppDb): RoomDataBaseQueryPage {
        return db.connectAbstractClassConnection()
    }
}