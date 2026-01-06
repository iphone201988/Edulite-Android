package com.edu.lite.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * common api response
 */
data class CommonApiResponse(
    val message: String?,
    val success: Boolean?
)

data class UploadProfileAPi(
    val fileName: String?,
    val isVideo: Boolean?,
    val mimeType: String?,
    val previewUrl: String?,
    val size: Int?,
    val success: Boolean?,
    val url: String?
)

/**
 * signup and login api response
 */
data class SignupResponse(
    val message: String?,
    val success: Boolean?,
    val user: SignupData?
)

data class SignupData(
    val _id: String?,
    val address: String?,
    val countryCode: String?,
    val createdAt: String?,
    val dob: String?,
    val email: String?,
    val grade: String?,
    val gradeId: String?,
    val isEmailVerified: Boolean?,
    val location: Location?,
    val name: String?,
    val token: String?,
    val phone: String?,
    val preferredLanguage: String?,
    val profilePicture: String?,
    val role: Int?,
    val status: String?

)


class Location(

)







/**
 * grade api response
 */
data class GradeModelResponse(
    val grades: List<GradeData?>?,
    val message: String?,
    val success: Boolean?
)

data class GradeData(
    val __v: Int?,
    val _id: String?,
    val createdAt: String?,
    val grade: String?,
    val icon: String?,
    val subjects: List<Subject?>?,
    val updatedAt: String?
)


data class Subject(
    val _id: String?,
    val icon: String?,
    val name: String?,
    val types: List<Type?>?
)

data class Type(
    val _id: String?,
    val icon: String?,
    val name: String?
)


/**
 * get grade by id api response
 */

data class GradeByIdResponse(
    val grade: GradeByIdData?,
    val message: String?,
    val success: Boolean?
)
@Parcelize
data class GradeByIdData(
    val __v: Int?,
    val _id: String?,
    val createdAt: String?,
    val grade: String?,
    val icon: String?,
    val subjects: List<SubjectData?>?,
    val updatedAt: String?
): Parcelable
@Parcelize
data class SubjectData(
    val _id: String?,
    val icon: String?,
    val name: String?,
    val types: List<QuizType?>?,
    var check : Boolean = false
): Parcelable
@Parcelize
data class QuizType(
    val _id: String?,
    val icon: String?,
    val name: String?
): Parcelable




/***
 * roadmap  api response
 */
data class RoadMapResponse(
    val message: String?,
    val pagination: Pagination?,
    val quizzes: List<RoadMapQuizze?>?,
    val success: Boolean?
)

data class Pagination(
    val currentPage: Int?,
    val hasNextPage: Boolean?,
    val hasPrevPage: Boolean?,
    val pageSize: Int?,
    val totalCount: Int?,
    val totalPages: Int?
)

data class RoadMapQuizze(
    val _id: String?,
    val description: String?,
    val grade: String?,
    val name: String?,
    val questions: List<HomeQuestion?>?,
    val subject: String?,
    val time: Int?,
    val type: String?,
    val userResponse: UserResponse?
)

/**
 * quiz question api response
 */

data class QuizQuestionApiResponse(
    val message: String?,
    val quiz: QuizQuestionData?,
    val responseData: ResponseData?,
    val success: Boolean?
)

data class QuizQuestionData(
    val __v: Int?,
    val _id: String?,
    val createdAt: String?,
    val description: String?,
    val grade: String?,
    val name: String?,
    val numberOfQuestions: Int?,
    val questions: List<QuestionData?>?,
    val subject: String?,
    val time: Int?,
    val type: String?,
    val updatedAt: String?
)

data class ResponseData(
    val __v: Int?,
    val _id: String?,
    val answers: List<OptionAnswer?>?,
    val correctCount: Int?,
    val createdAt: String?,
    val incorrectCount: Int?,
    val points: Int?,
    val quizId: String?,
    val status: String?,
    val timeTaken: Int?,
    val type: String?,
    val updatedAt: String?,
    val userId: String?
)

data class QuestionData(
    val _id: String?,
    val answer: String?,
    val options: List<OptionData?>?,
    val question: String?,
    var userSelectedOptionId: String?
)

data class OptionData(
    val _id: String?,
    val text: String?,
    var selectedAnswer: Boolean = false,
)

data class OptionAnswer(
    val isCorrect: Boolean?,
    val questionId: String?,
    val selectedOptionId: String?
)

/**
 * creative project api response
 */

data class GetCreativeModelClass(
    val creativeProjects: List<CreativeProject?>?,
    val message: String?,
    val success: Boolean?
)

data class CreativeProject(
    val _id: String?,
    val name: String?,
    val time: Int?,
    val userName: String?
)
/**
 * creative project details api response
 */

data class CreativityDetailsApiResponse(
    val message: String?,
    val project: ProjectDetails?,
    val success: Boolean?
)

data class ProjectDetails(
    val __v: Int?,
    val _id: String?,
    val description: String?,
    val grade: String?,
    val name: String?,
    val subject: String?,
    val time: Int?,
    val type: String?,
    val userId: String?,
    val userName: String?
)

/**
 * get home test quest api response
 */
data class GetHomeQuestApi(
    val count: Int?,
    val message: String?,
    val quests: List<GetHomeQuest?>?
)

data class GetHomeQuest(
    val __v: Int?,
    val _id: String?,
    val `class`: String?,
    val createdAt: String?,
    val date: String?,
    val name: String?,
    val questType: String?,
    val testQuizId: TestQuizId?,
    val type: String?,
    val updatedAt: String?,
    val userProgress: UserProgress?
)

data class TestQuizId(
    val _id: String?,
    val name: String?,
    val questions: List<HomeQuestion?>?,
    val type: String?
)

data class UserProgress(
    val completedAt: Any?,
    val progress: Any?,
    val startedAt: Any?,
    val status: String?,
    val userResponseId: Any?
)

data class HomeQuestion(
    val _id: String?,
    val answer: String?,
    val options: List<HomeOption?>?,
    val question: String?
)

data class HomeOption(
    val _id: String?,
    val text: String?
)


/**
 * featured api response
 */

data class FeaturedApiResponse(
    val message: String?,
    val pagination: Pagination?,
    val quizzes: List<FeaturedQuizze?>?,
    val success: Boolean?
)


data class FeaturedQuizze(
    val _id: String?,
    val description: String?,
    val grade: String?,
    val name: String?,
    val questions: List<HomeQuestion?>?,
    val subject: String?,
    val time: Int?,
    val type: String?,
    val userResponse: UserResponse?
)


data class UserResponse(
    val _id: String?,
    val answers: List<Answer?>?,
    val correctCount: Int?,
    val createdAt: String?,
    val incorrectCount: Int?,
    val points: Int?,
    val quizId: String?,
    val status: String?,
    val timeTaken: Int?
)


data class Answer(
    val isCorrect: Boolean?,
    val questionId: String?,
    val selectedOptionId: String?
)

/**
 * get video session  api response
 */
data class GetVideoApiResponse(
    val pagination: Pagination?,
    val success: Boolean?,
    val videos: List<VideoData?>?
)


data class VideoData(
    val _id: String?,
    val createdAt: String?,
    val grade: String?,
    val subject: String?,
    val thumbnailUrl: String?,
    val time: Int?,
    val title: String?,
    val userId: UserId?,
    val videoUrl: String?,
    var videoDownload: Boolean?
)

data class UserId(
    val _id: String?,
    val email: String?,
    val name: String?
)

/**
 * quiz answer api response
 */
data class QuizAnswerApiResponse(
    val message: String?,
    val success: Boolean?,
    val userResponse: QuizAnswerUserResponse?
)

data class QuizAnswerUserResponse(
    val __v: Int?,
    val _id: String?,
    val answers: List<QuizAnswer?>?,
    val correctCount: Int?,
    val createdAt: String?,
    val incorrectCount: Int?,
    val points: Int?,
    val quizId: String?,
    val status: String?,
    val timeTaken: Int?,
    val type: String?,
    val updatedAt: String?,
    val userId: String?
)

data class QuizAnswer(
    val isCorrect: Boolean?,
    val questionId: String?,
    val selectedOptionId: String?
)

/**
 *  notifications api response
 */
data class GetNotificationApiResponse(
    val `data`: List<NotificationData?>?,
    val pagination: Pagination?,
    val success: Boolean?,
    val unreadCount: Int?
)

data class NotificationData(
    val __v: Int?,
    val _id: String?,
    val createdAt: String?,
    val description: String?,
    val isRead: Boolean?,
    val title: String?,
    val type: String?,
    val updatedAt: String?,
    val userId: String?
)

