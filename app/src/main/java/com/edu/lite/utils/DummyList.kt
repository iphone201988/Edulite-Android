package com.edu.lite.utils

import com.edu.lite.R
import com.edu.lite.data.model.FeaturedQuizzesModel
import com.edu.lite.data.model.GradeModel
import com.edu.lite.data.model.LetPlayModel
import com.edu.lite.data.model.NotificationModel
import com.edu.lite.data.model.PracticeTextModel
import com.edu.lite.data.model.PreviewModel
import com.edu.lite.data.model.ProfileModel
import com.edu.lite.data.model.QuestModel
import com.edu.lite.data.model.QuestionModel
import com.edu.lite.data.model.RoastedModel
import com.edu.lite.data.model.UnitModel
import com.edu.lite.data.model.VideoLessonsModel
import com.edu.lite.ui.auth.dash_board.home.practice.PracticeTestFragment

object DummyList {
    fun previewList(): ArrayList<PreviewModel> {
        var list = ArrayList<PreviewModel>()
        list.add(PreviewModel("Student Preview", true))
        list.add(PreviewModel("Teacher Dashboard"))
        list.add(PreviewModel("Parent Digest"))
        return list

    }

    fun budgetList(): ArrayList<PreviewModel> {
        var list = ArrayList<PreviewModel>()
        list.add(PreviewModel("Photograph Builder", true))
        list.add(PreviewModel("Food Chains"))
        list.add(PreviewModel("Inquirer"))
        return list

    }

    fun questionList(): ArrayList<QuestionModel> {
        var list = ArrayList<QuestionModel>()
        list.add(QuestionModel("Solve 5 math problems", "Start", 1))
        list.add(QuestionModel("Read 10 minutes", "Skip", 2))
        list.add(QuestionModel("Practice spelling (3 words)", "Later", 3))

        return list

    }

    fun unitList(): ArrayList<UnitModel> {
        var list = ArrayList<UnitModel>()
        list.add(UnitModel("Who We Are: Well-be....", 20))
        list.add(UnitModel("Who We Are: Well-be....", 30))
        list.add(UnitModel("Who We Are: Well-be....", 40))
        list.add(UnitModel("Who We Are: Well-be....", 50))
        list.add(UnitModel("Who We Are: Well-be....", 60))


        return list

    }

    fun roastList(): ArrayList<RoastedModel> {
        var list = ArrayList<RoastedModel>()
        list.add(RoastedModel("Ava", "XP 100", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 100", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 100", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 100", "LVL 6", "Streak 9", "80%"))
        list.add(RoastedModel("Ava", "XP 100", "LVL 6", "Streak 9", "80%"))



        return list

    }


    fun gradeList(): ArrayList<GradeModel> {
        var list = ArrayList<GradeModel>()
        list.add(GradeModel("Early Grades", R.drawable.early_grades_icon))
        list.add(GradeModel("Grade K", R.drawable.grade_k_icon))
        list.add(GradeModel("Grade 1", R.drawable.grade_1))
        list.add(GradeModel("Grade 2", R.drawable.grade_2))
        list.add(GradeModel("Grade 3", R.drawable.grade_3))
        list.add(GradeModel("Grade 4", R.drawable.grade_4))
        list.add(GradeModel("Grade 5", R.drawable.grade_5))

        return list

    }


    fun questList(): ArrayList<QuestModel> {
        var list = ArrayList<QuestModel>()
        list.add(
            QuestModel(
                "Quest: Letter-Sound Quest",
                "Focus: Phonics: CVC Blending",
                "ATL: Communication"
            )
        )
        list.add(
            QuestModel(
                "Quest: Number Bonds 10",
                "Focus: Compose/ Decompose to 10",
                "ATL: Thinking"
            )
        )


        return list

    }

    fun pickASubjectList(): ArrayList<GradeModel> {
        var list = ArrayList<GradeModel>()
        list.add(GradeModel("Maths", R.drawable.mathes_image))
        list.add(GradeModel("English", R.drawable.english_image))
        list.add(GradeModel("Science", R.drawable.science_image))


        return list

    }



    fun letPlayList(): ArrayList<LetPlayModel> {
        var list = ArrayList<LetPlayModel>()
        list.add(LetPlayModel("Fun Quizzes", R.drawable.fun_quizzes_image))
        list.add(LetPlayModel("Practice Test", R.drawable.practice_test_image))
        list.add(LetPlayModel("Video Lessons", R.drawable.video_lessons_image))
        list.add(LetPlayModel("Creative Projects", R.drawable.creative_projects_image))

        return list

    }


    fun notificationList(): ArrayList<NotificationModel> {
        var list = ArrayList<NotificationModel>()
        list.add(NotificationModel("Well done! You completed the Math Practice Test. Your score: 85%.", 1,"2min ago"))
        list.add(NotificationModel("New Video: Algebra Basics Watch now to strengthen your fundamentals.", 2,"22min ago"))
        list.add(NotificationModel("Today’s Fun Quiz is live! Attempt now and earn bonus points.", 3,"12min ago"))
        list.add(NotificationModel(" Join the Weekly Challenge Compete and climb the leaderboard.", 1,"32min ago"))

        return list

    }


    fun featuredQuizzesList(): ArrayList<FeaturedQuizzesModel> {
        var list = ArrayList<FeaturedQuizzesModel>()
        list.add(FeaturedQuizzesModel( R.drawable.problom_solving_first_image,"Algebra Word Problem Challenge","Solve real-world problems using your algebra skills. From linear equations to polynomials.","16 Question","5 mins"))
        list.add(FeaturedQuizzesModel( R.drawable.problom_solving_first_image,"Algebra Word Problem Challenge","Solve real-world problems using your algebra skills. From linear equations to polynomials.","16 Question","5 mins"))
        list.add(FeaturedQuizzesModel( R.drawable.problom_solving_first_image,"Algebra Word Problem Challenge","Solve real-world problems using your algebra skills. From linear equations to polynomials.","16 Question","5 mins"))
        list.add(FeaturedQuizzesModel( R.drawable.problom_solving_first_image,"Algebra Word Problem Challenge","Solve real-world problems using your algebra skills. From linear equations to polynomials.","16 Question","5 mins"))

        return list

    }


    fun practiceTestList(): ArrayList<PracticeTextModel> {
        var list = ArrayList<PracticeTextModel>()
        list.add(PracticeTextModel( R.drawable.practice_test_image,"SAT Math Section Practice","A full-length practice test for the SAT Math section with detailed explanations.","16 Question","50 mins",40,1))
        list.add(PracticeTextModel( R.drawable.practice_test_image,"SAT Math Section Practice","A full-length practice test for the SAT Math section with detailed explanations.","12/ 58 Questions","80 mins",40,2))
        list.add(PracticeTextModel( R.drawable.practice_test_image,"SAT Math Section Practice","A full-length practice test for the SAT Math section with detailed explanations.","58 Questions","80 mins",100,3))
        list.add(PracticeTextModel( R.drawable.practice_test_image,"SAT Math Section Practice","A full-length practice test for the SAT Math section with detailed explanations.","58 Question","80 mins",40,1))

        return list

    }


    fun practiceCategoryList(): ArrayList<PreviewModel> {
        var list = ArrayList<PreviewModel>()
        list.add(PreviewModel("All",true))
        list.add(PreviewModel("New"))
        list.add(PreviewModel("In Progress"))
        list.add(PreviewModel("Completed"))


        return list

    }


    fun quizCategoryList(): ArrayList<PreviewModel> {
        var list = ArrayList<PreviewModel>()
        list.add(PreviewModel("All",true))
        list.add(PreviewModel("Maths"))
        list.add(PreviewModel("English"))
        list.add(PreviewModel("Science"))
        list.add(PreviewModel("History"))
        list.add(PreviewModel("Geography"))



        return list

    }


    fun videoLessonsModelList(): ArrayList<VideoLessonsModel> {
        var list = ArrayList<VideoLessonsModel>()
        list.add(VideoLessonsModel("SAT Math Section Practice","By Jane Doe","25 min",1))
        list.add(VideoLessonsModel("SAT Math Section Practice","By Jane Doe","25 min",2))
        list.add(VideoLessonsModel("SAT Math Section Practice","By Jane Doe","25 min",3))
        list.add(VideoLessonsModel("SAT Math Section Practice","By Jane Doe","25 min",1))
        list.add(VideoLessonsModel("SAT Math Section Practice","By Jane Doe","25 min",2))
        list.add(VideoLessonsModel("SAT Math Section Practice","By Jane Doe","25 min",3))


        return list

    }

    fun profileModelList(): ArrayList<ProfileModel> {
        var list = ArrayList<ProfileModel>()
        list.add(ProfileModel("Edit Profile",1))
        list.add(ProfileModel("Notification",1))
        list.add(ProfileModel("Language",1))
        list.add(ProfileModel("Downloads",1))
        list.add(ProfileModel("Subscription",1))
        list.add(ProfileModel("Change Password",1))
        list.add(ProfileModel("FAQ",1))
        list.add(ProfileModel("Terms & Conditions",1))
        list.add(ProfileModel("Privacy Policy",1))
        list.add(ProfileModel("Contact Us",1))
        list.add(ProfileModel("Logout",1))
        list.add(ProfileModel("Delete Profile",2))
        return list

    }

}