package com.edu.lite.ui.dash_board.home.video_lessons

import com.edu.lite.data.room_moduel.DownloadVideoData
import com.edu.lite.data.room_moduel.RoomDataBaseQueryPage
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val dao: RoomDataBaseQueryPage
) {

    suspend fun insertVideo(video: DownloadVideoData) {
        dao.insertVideo(video)
    }

    suspend fun getAllVideos(): List<DownloadVideoData> {
        return dao.getAllVideos()
    }

    suspend fun deleteVideoById(id: String?) {
        dao.deleteById(id)
    }

}
