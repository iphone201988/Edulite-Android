package com.edu.lite.utils

import android.app.Activity
import androidx.navigation.fragment.findNavController
import com.edu.lite.R
import com.edu.lite.data.model.BadgesModel
import com.edu.lite.data.model.FeaturedQuizzesModel
import com.edu.lite.data.model.NotificationModel
import com.edu.lite.data.model.PracticeTextModel
import com.edu.lite.data.model.PreviewModel
import com.edu.lite.data.model.ProfileModel
import com.edu.lite.data.model.QuestModel
import com.edu.lite.data.model.QuestionModel
import com.edu.lite.data.model.RewardsModel
import com.edu.lite.data.model.RoadMapModel
import com.edu.lite.data.model.RoastedModel
import com.edu.lite.data.model.UnitModel
import com.edu.lite.data.model.VideoLessonsModel
import com.edu.lite.ui.dash_board.profile.ProfileFragmentDirections
import dagger.hilt.android.internal.Contexts

object DummyList {
    fun previewList(context : Activity): ArrayList<PreviewModel> {
        var list = ArrayList<PreviewModel>()
        list.add(PreviewModel(context.getString(R.string.student_preview), true))
        list.add(PreviewModel(context.getString(R.string.teacher_dashboard)))
        list.add(PreviewModel(context.getString(R.string.parent_digest)))
        return list

    }

    fun budgetList(context : Activity): ArrayList<PreviewModel> {
        var list = ArrayList<PreviewModel>()
        list.add(PreviewModel(context.getString(R.string.photograph_builder), true))
        list.add(PreviewModel(context.getString(R.string.food_chains)))
        list.add(PreviewModel(context.getString(R.string.inquirer)))
        return list

    }

    fun questionList(context : Activity): ArrayList<QuestionModel> {
        var list = ArrayList<QuestionModel>()
        list.add(QuestionModel(context.getString(R.string.solve_5_math_problems), "Start", 1))
        list.add(QuestionModel(context.getString(R.string.read_10_minutes), "Skip", 2))
        list.add(QuestionModel(context.getString(R.string.practice_spelling_3_words), "Later", 3))

        return list

    }

    fun questionTeamList(context : Activity): ArrayList<QuestionModel> {
        var list = ArrayList<QuestionModel>()
        list.add(QuestionModel(context.getString(R.string.solve_5_math_problems), "Start", 1))
        list.add(QuestionModel(context.getString(R.string.read_10_minutes), "Skip", 2))
        list.add(QuestionModel(context.getString(R.string.practice_spelling_3_words), "Later", 3))
        list.add(QuestionModel(context.getString(R.string.solve_5_math_problems), "Start", 1))
        list.add(QuestionModel(context.getString(R.string.read_10_minutes), "Skip", 2))
        list.add(QuestionModel(context.getString(R.string.practice_spelling_3_words), "Later", 3))

        return list

    }


    fun unitList(context : Activity): ArrayList<UnitModel> {
        var list = ArrayList<UnitModel>()
        list.add(UnitModel("Who We Are: Well-be....", 20))
        list.add(UnitModel("Who We Are: Well-be....", 30))
        list.add(UnitModel("Who We Are: Well-be....", 40))
        list.add(UnitModel("Who We Are: Well-be....", 50))
        list.add(UnitModel("Who We Are: Well-be....", 60))


        return list

    }

    fun roastList(context : Activity): ArrayList<RoastedModel> {
        var list = ArrayList<RoastedModel>()
        list.add(RoastedModel("Ava", "XP 100", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 100", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 100", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 100", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 100", "LVL 6", "Streak 9", "80%"))



        return list

    }

    fun snapShotList(context : Activity): ArrayList<RoastedModel> {
        var list = ArrayList<RoastedModel>()
        list.add(RoastedModel("Ava", "XP 100", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 190", "LVL 6", "Streak 9", "80%", true))
        list.add(RoastedModel("Ava", "XP 110", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 130", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 140", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 150", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 120", "LVL 6", "Streak 9", "80%"))



        return list

    }


    fun questList(context : Activity): ArrayList<QuestModel> {
        var list = ArrayList<QuestModel>()
        list.add(
            QuestModel(
                "Quest: Letter-Sound Quest", "Focus: Phonics: CVC Blending", "ATL: Communication"
            )
        )
        list.add(
            QuestModel(
                "Quest: Number Bonds 10", "Focus: Compose/ Decompose to 10", "ATL: Thinking"
            )
        )


        return list

    }


    fun notificationList(context : Activity): ArrayList<NotificationModel> {
        var list = ArrayList<NotificationModel>()
        list.add(
            NotificationModel(
                "Well done! You completed the Math Practice Test. Your score: 85%.", 1, "2min ago"
            )
        )
        list.add(
            NotificationModel(
                "New Video: Algebra Basics Watch now to strengthen your fundamentals.",
                2,
                "22min ago"
            )
        )
        list.add(
            NotificationModel(
                "Today’s Fun Quiz is live! Attempt now and earn bonus points.", 3, "12min ago"
            )
        )
        list.add(
            NotificationModel(
                " Join the Weekly Challenge Compete and climb the leaderboard.", 1, "32min ago"
            )
        )

        return list

    }


    fun featuredQuizzesList(context : Activity): ArrayList<FeaturedQuizzesModel> {
        var list = ArrayList<FeaturedQuizzesModel>()
        list.add(
            FeaturedQuizzesModel(
                R.drawable.problom_solving_first_image,
                "Algebra Word Problem Challenge",
                "Solve real-world problems using your algebra skills. From linear equations to polynomials.",
                "16 Question",
                "5 mins"
            )
        )
        list.add(
            FeaturedQuizzesModel(
                R.drawable.problom_solving_first_image,
                "Algebra Word Problem Challenge",
                "Solve real-world problems using your algebra skills. From linear equations to polynomials.",
                "16 Question",
                "5 mins"
            )
        )
        list.add(
            FeaturedQuizzesModel(
                R.drawable.problom_solving_first_image,
                "Algebra Word Problem Challenge",
                "Solve real-world problems using your algebra skills. From linear equations to polynomials.",
                "16 Question",
                "5 mins"
            )
        )
        list.add(
            FeaturedQuizzesModel(
                R.drawable.problom_solving_first_image,
                "Algebra Word Problem Challenge",
                "Solve real-world problems using your algebra skills. From linear equations to polynomials.",
                "16 Question",
                "5 mins"
            )
        )

        return list

    }


    fun practiceTestList(context : Activity): ArrayList<PracticeTextModel> {
        var list = ArrayList<PracticeTextModel>()
        list.add(
            PracticeTextModel(
                R.drawable.practice_test_image,
                "SAT Math Section Practice",
                "A full-length practice test for the SAT Math section with detailed explanations.",
                "16 Question",
                "50 mins",
                40,
                1
            )
        )
        list.add(
            PracticeTextModel(
                R.drawable.practice_test_image,
                "SAT Math Section Practice",
                "A full-length practice test for the SAT Math section with detailed explanations.",
                "12/ 58 Questions",
                "80 mins",
                40,
                2
            )
        )
        list.add(
            PracticeTextModel(
                R.drawable.practice_test_image,
                "SAT Math Section Practice",
                "A full-length practice test for the SAT Math section with detailed explanations.",
                "58 Questions",
                "80 mins",
                100,
                3
            )
        )
        list.add(
            PracticeTextModel(
                R.drawable.practice_test_image,
                "SAT Math Section Practice",
                "A full-length practice test for the SAT Math section with detailed explanations.",
                "58 Question",
                "80 mins",
                40,
                1
            )
        )

        return list

    }


    fun practiceCategoryList(context : Activity): ArrayList<PreviewModel> {
        var list = ArrayList<PreviewModel>()
        list.add(PreviewModel(context.getString(R.string.all), true))
        list.add(PreviewModel(context.getString(R.string.news)))
        list.add(PreviewModel(context.getString(R.string.in_progress)))
        list.add(PreviewModel(context.getString(R.string.completed)))

        return list

    }
    fun practiceCategoryListSession(context : Activity): ArrayList<PreviewModel> {
        var list = ArrayList<PreviewModel>()
        list.add(PreviewModel(context.getString(R.string.all), true))
        list.add(PreviewModel(context.getString(R.string.news)))
        list.add(PreviewModel(context.getString(R.string.in_progress)))
        list.add(PreviewModel(context.getString(R.string.completed_1)))

        return list

    }


    fun videoCategoryList(context : Activity): ArrayList<PreviewModel> {
        var list = ArrayList<PreviewModel>()
        list.add(PreviewModel(context.getString(R.string.all), true))
        list.add(PreviewModel(context.getString(R.string.news)))
        list.add(PreviewModel(context.getString(R.string.completed)))
        return list
    }


    fun rewardsList(context : Activity): ArrayList<RewardsModel> {
        var list = ArrayList<RewardsModel>()
        list.add(RewardsModel(R.drawable.nebula_theme,
            context.getString(R.string.nebula_theme), context.getString(R.string.theme),1))
        val add = list.add(
            RewardsModel(
                R.drawable.pixel_background,
                context.getString(R.string.pixel_background), context.getString(R.string.theme), 2
            )
        )
        list.add(RewardsModel(R.drawable.gold_avatar,
            context.getString(R.string.gold_avatar_frame), context.getString(R.string.avatar),3))
        list.add(RewardsModel(R.drawable.diamond_profile,
            context.getString(R.string.diamond_profile_icon), context.getString(R.string.avatar),1))

        return list

    }

    fun badgesList(context : Activity): ArrayList<BadgesModel> {
        var list = ArrayList<BadgesModel>()
        list.add(BadgesModel(R.drawable.nebula_theme,
            context.getString(R.string.mastered_fractions),
            context.getString(R.string.completed_all_fractions_units),1))
        list.add(BadgesModel(R.drawable.pixel_background,
            context.getString(R.string.persistence_i_fractions),
            context.getString(R.string.used_3_retry_tokens_in_one_lesson),2))
        list.add(BadgesModel(R.drawable.streak_icon,
            context.getString(R.string._7_day_streak),
            context.getString(R.string.logged_in_for_a_full_week),3))
        list.add(BadgesModel(R.drawable.resilient_learner,
            context.getString(R.string.resilient_learner),
            context.getString(R.string.completed_5_tasks_after_initial_failure),1))
        list.add(BadgesModel(R.drawable.diamond_profile,
            context.getString(R.string.first_try_ace),
            context.getString(R.string.scored_100_on_a_hard_problem),4))
        list.add(BadgesModel(R.drawable.diamond_profile,
            context.getString(R.string.calculus_conqueror),
            context.getString(R.string.completed_all_calculus_lessons),4))

        return list

    }
    


    fun videoLessonsModelList(context : Activity): ArrayList<VideoLessonsModel> {
        var list = ArrayList<VideoLessonsModel>()
        list.add(VideoLessonsModel(context.getString(R.string.sat_math_section_practice),
            context.getString(
                R.string.by_jane_doe
            ), context.getString(R.string._25_min), 1))
        list.add(VideoLessonsModel(context.getString(R.string.sat_math_section_practice), context.getString(
                R.string.by_jane_doe
            ), context.getString(R.string._25_min), 2))
        list.add(VideoLessonsModel(context.getString(R.string.sat_math_section_practice), context.getString(
                R.string.by_jane_doe
            ), context.getString(R.string._25_min), 3))
        list.add(VideoLessonsModel(context.getString(R.string.sat_math_section_practice), context.getString(
                R.string.by_jane_doe
            ), context.getString(R.string._25_min), 1))
        list.add(VideoLessonsModel(context.getString(R.string.sat_math_section_practice), context.getString(
                R.string.by_jane_doe
            ), context.getString(R.string._25_min), 2))
        list.add(VideoLessonsModel(context.getString(R.string.sat_math_section_practice), context.getString(
                R.string.by_jane_doe
            ), context.getString(R.string._25_min), 3))


        return list

    }

    fun profileModelList(context : Activity): ArrayList<ProfileModel> {
        var list = ArrayList<ProfileModel>()
        list.add(ProfileModel(context.getString(R.string.edit_profile), 1))
        list.add(ProfileModel(context.getString(R.string.notification), 1))
//        list.add(ProfileModel(context.getString(R.string.change_theme),1))
//        list.add(ProfileModel(context.getString(R.string.language), 1))
        list.add(ProfileModel(context.getString(R.string.choose_grade), 1))
        list.add(ProfileModel(context.getString(R.string.downloads), 1))
        list.add(ProfileModel(context.getString(R.string.subscription), 1))
        list.add(ProfileModel(context.getString(R.string.change_password), 1))
        list.add(ProfileModel(context.getString(R.string.faq), 1))
        list.add(ProfileModel(context.getString(R.string.terms_conditions), 1))
        list.add(ProfileModel(context.getString(R.string.privacy_policy), 1))
        list.add(ProfileModel(context.getString(R.string.contact_us), 1))
        list.add(ProfileModel(context.getString(R.string.logout), 1))
        list.add(ProfileModel(context.getString(R.string.delete_profile), 2))
        return list

    }
    

}