package com.edu.lite.data.model


data class DummyApiItem(
    val id: Int, val imdbId: String, val posterURL: String, val title: String
)


data class PreviewModel(
    val title: String, var isCheck: Boolean = false
)
data class QuestionModel(
    val title: String,
    var typeTitle: String,
    var type: Int
)

data class UnitModel(
    val title: String,
    var progressValue: Int,

)

data class RoastedModel(
    val Ava: String,
    var xp: String,
    var level: String,
    var streak: String,
    var percetageValue: String,
)


data class GradeModel(
    val title: String,
    var image: Int
)

data class LetPlayModel(
    val title: String,
    var image: Int
)

data class QuestModel(
    val header: String,
    var focus: String,
    var atl: String,
)

data class FeaturedQuizzesModel(
    val image: Int,
    var header: String,
    var desc: String,
    var questionCount: String,
    var time: String,
)


data class PracticeTextModel(
    val image: Int,
    var header: String,
    var desc: String,
    var questionCount: String,
    var time: String,
    var progress:Int,
    var type: Int
)

data class VideoLessonsModel(
    var title: String,
    var desc: String,
    var time: String,
    var type: Int
)

data class ProfileModel(
    var title: String,
    var type: Int

)

data class NotificationModel(
    var title: String,
    var type: Int,
    var time: String

)

data class CountryModel(
    val code: String,
    val emoji: String,
    val image: String,
    val name: String,
    val unicode: String,
    var isSelected: Boolean,
    var countryCode: String
)
