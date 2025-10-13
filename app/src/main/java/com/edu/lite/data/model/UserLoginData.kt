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
    val email: String?,
    val isEmailVerified: Boolean?,
    val location: Location?,
    val name: String?,
    val profilePicture: String?,
    val status: String?,
    val token: String?
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
    val _id: String?,
    val grade: String?,
    val icon: String?
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
    val types: List<QuizType?>?
): Parcelable
@Parcelize
data class QuizType(
    val _id: String?,
    val icon: String?,
    val name: String?
): Parcelable

/**
 * featured api response
 */
data class FeaturedApiResponse(
    val message: String?,
    val quizzes: List<FeaturedQuizze?>?,
    val success: Boolean?
)

data class FeaturedQuizze(
    val _id: String?,
    val name: String?,
    val time: Int?
)

/**
 * quiz question api response
 */

data class QuizQuestionApiResponse(
    val message: String?,
    val quiz: QuizQuestionData?,
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

data class QuestionData(
    val _id: String?,
    val answer: String?,
    val options: List<OptionData?>?,
    val question: String?,
    var userSelectedOptionId: String?
)

data class OptionData(
    val _id: String?,
    var selectedAnswer: Boolean = false,
    val text: String?
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