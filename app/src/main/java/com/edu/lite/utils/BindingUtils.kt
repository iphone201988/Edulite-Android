package com.edu.lite.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.PictureDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.edu.lite.R
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.GetHomeQuest
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object BindingUtils {

    @BindingAdapter("setImageFromUrl")
    @JvmStatic
    fun setImageFromUrl(image: ShapeableImageView, url: String?) {
        if (url != null) {
            Glide.with(image.context).load(url).placeholder(R.drawable.person_holder)
                .error(R.drawable.person_holder).into(image)
        }
    }


    @BindingAdapter("setImageFromUrlViewBadge")
    @JvmStatic
    fun setImageFromUrlViewBadge(image: AppCompatImageView, url: String?) {
        if (url != null) {
            Glide.with(image.context).load(Constants.BASE_URL_IMAGE +url).placeholder(R.drawable.progress_drawable)
                .error(R.drawable.batch_icon).into(image)
        }
    }


    @BindingAdapter("setIconFromUrl")
    @JvmStatic
    fun setIconFromUrl(image: ShapeableImageView, url: String?) {
        if (url != null) {
            Glide.with(image.context).load(Constants.BASE_URL_IMAGE + url).into(image)
        }
    }

    @BindingAdapter("setTime")
    @JvmStatic
    fun setTime(textView: AppCompatTextView, time: String?) {
        if (!time.isNullOrEmpty()) {
            val result = getTimeAgo(time)
            textView.text = result
        } else {
            textView.text = "-"
        }
    }


    fun getTimeAgo(isoTime: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            val past = sdf.parse(isoTime) ?: return ""
            val now = Date()

            val diffMillis = now.time - past.time

            when {
                diffMillis < TimeUnit.MINUTES.toMillis(1) -> "just now"

                diffMillis < TimeUnit.HOURS.toMillis(1) -> "${
                    TimeUnit.MILLISECONDS.toMinutes(
                        diffMillis
                    )
                } min ago"

                diffMillis < TimeUnit.DAYS.toMillis(1) -> "${
                    TimeUnit.MILLISECONDS.toHours(
                        diffMillis
                    )
                } hour ago"

                diffMillis < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diffMillis)} day ago"

                diffMillis < TimeUnit.DAYS.toMillis(30) -> "${
                    TimeUnit.MILLISECONDS.toDays(
                        diffMillis
                    ) / 7
                } week ago"

                diffMillis < TimeUnit.DAYS.toMillis(365) -> "${
                    TimeUnit.MILLISECONDS.toDays(
                        diffMillis
                    ) / 30
                } month ago"

                else -> "${TimeUnit.MILLISECONDS.toDays(diffMillis) / 365} year ago"
            }
        } catch (e: Exception) {
            ""
        }
    }


    @BindingAdapter("setImageInt")
    @JvmStatic
    fun setImageInt(imageView: AppCompatImageView, image: Int?) {
        if (image != null) {
            imageView.setImageResource(image)
        }
    }

    inline fun <reified T> parseJson(json: String): T? {
        return try {
            val gson = Gson()
            gson.fromJson(json, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * set status bar color
     */
    fun setStatusBarGradient(activity: Activity) {
        activity.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        activity.window.statusBarColor = Color.TRANSPARENT
    }


    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }


    fun hasPermissions(context: Context?, permissions: Array<String>?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context, permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * navigate with slide animation
     */
    fun navigateWithSlide(navController: NavController, directions: NavDirections) {
        val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        navController.navigate(directions, navOptions)
    }

    fun navigateWithSlide(navController: NavController, destinationId: Int, bundle: Bundle?) {
        val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        navController.navigate(destinationId, bundle, navOptions)
    }


    @BindingAdapter("setQuestionTypeBgLayout")
    @JvmStatic
    fun setQuestionTypeBgLayout(image: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> image.setBackgroundResource(R.drawable.quests_first_bg)
                2 -> image.setBackgroundResource(R.drawable.quests_second_bg)
                3 -> image.setBackgroundResource(R.drawable.quests_third_bg)
            }
        }
    }

    @BindingAdapter("setHomeBg")
    @JvmStatic
    fun setHomeBg(layout: ConstraintLayout, position: Int) {

        if (position == RecyclerView.NO_POSITION) {
            layout.setBackgroundResource(R.drawable.quests_first_bg)
            return
        }

        val bgRes = when (position % 3) {
            0 -> R.drawable.quests_first_bg
            1 -> R.drawable.quests_second_bg_home
            2 -> R.drawable.quest_third_bg_home
            else -> R.drawable.quests_first_bg
        }

        layout.setBackgroundResource(bgRes)
    }
    @BindingAdapter("setRoadMapImage")
    @JvmStatic
    fun setRoadMapImage(image: AppCompatImageView, type: String?) {
        if (type != null) {
            when (type) {
                "completed" -> image.setImageResource(R.drawable.green_right)
                "in-progress" -> image.setImageResource(R.drawable.iv_pending)
                "pending" -> image.setImageResource(R.drawable.iv_progress)
                else -> image.setImageResource(R.drawable.iv_progress)
            }
        } else image.setImageResource(R.drawable.iv_progress)
    }

    @BindingAdapter("setRoadMapRadius")
    @JvmStatic
    fun setRoadMapRadius(image: ConstraintLayout, type: String?) {
        if (type != null) {
            when (type) {
                "completed" -> image.setBackgroundResource(R.drawable.green_radius_12)
                "in-progress" -> image.setBackgroundResource(R.drawable.blue_radius_12)
                "pending" -> image.setBackgroundResource(R.drawable.blue_40_radius_12)
                else -> image.setBackgroundResource(R.drawable.blue_40_radius_12)
            }
        } else image.setBackgroundResource(R.drawable.blue_40_radius_12)
    }

    @BindingAdapter("handelCLick")
    @JvmStatic
    fun handelCLick(image: ConstraintLayout, type: String?) {
        if (type != null) {
            when (type) {
                "completed" -> {
                    image.isFocusable = false
                    image.isClickable = false
                    image.isEnabled = false
                }

                else -> {
                    image.isFocusable = true
                    image.isClickable = true
                    image.isEnabled = true
                }
            }
        } else {
            image.isFocusable = true
            image.isClickable = true
            image.isEnabled = true
        }
    }


    @BindingAdapter("setQuestionTypeBgText")
    @JvmStatic
    fun setQuestionTypeBgText(textView: AppCompatTextView, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> {
                    textView.text = textView.context.getString(R.string.start)
                    textView.setBackgroundResource(R.drawable.start_bg_btn)
                }

                2 -> {
                    textView.text = textView.context.getString(R.string.skip)
                    textView.setBackgroundResource(R.drawable.skip_btn_bg)
                }

                3 -> {
                    textView.text = textView.context.getString(R.string.later)
                    textView.setBackgroundResource(R.drawable.later_bg_btn)
                }
            }
        }
    }

    @BindingAdapter("setQuestionTypeBg")
    @JvmStatic
    fun setQuestionTypeBg(textView: AppCompatTextView, type: String?) {
        if (type != null) {
            when (type) {
                "pending" -> textView.setText("Start")
                "in-progress" -> textView.setText("Start")
                "completed" -> textView.setText("Done")
                else -> textView.setText("Start")
            }
        }
        else{
            textView.setText("Start")
        }
    }

    @BindingAdapter("setLetsPlaySelection")
    @JvmStatic
    fun setLetsPlaySelection(editText: AppCompatTextView, data: String?) {
        if (!data.isNullOrEmpty()) {
            when {
                data.contains("quiz", ignoreCase = true) -> editText.text =
                    editText.context.getString(R.string.fun_quizzes)

                data.contains("test", ignoreCase = true) -> editText.text =
                    editText.context.getString(R.string.practice_test)

                data.contains("videoLesson", ignoreCase = true) -> editText.text =
                    editText.context.getString(R.string.video_lessons)

                data.contains("creativeProject", ignoreCase = true) -> editText.text =
                    editText.context.getString(R.string.creative_projects)
            }
        }
    }


    @BindingAdapter("setProgress")
    @JvmStatic
    fun setProgress(textView: AppCompatTextView, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> textView.text = textView.context.getString(R.string.today_s_progress)
                2 -> textView.text = textView.context.getString(R.string.class_overview)
                3 -> textView.text = textView.context.getString(R.string.weekly_digest)
            }

        }
    }

    @BindingAdapter("setProgressValue")
    @JvmStatic
    fun setProgressValue(guideline: Guideline, type: Int?) {
        if (type != null) {
            setProgress(guideline, type)
        }
    }

    /** set password progress bar ***/
    fun setProgress(guideline: Guideline, percentage: Int?) {
        if (percentage != null && percentage in 0..100) {
            val layoutParams = guideline.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.guidePercent = percentage / 100f
            guideline.layoutParams = layoutParams
        }
    }


    @BindingAdapter("changeBackgroundStroke")
    @JvmStatic
    fun changeBackgroundStroke(image: ConstraintLayout, type: String?) {
        type?.toIntOrNull()?.let { number ->
            val resId = when (number % 3) {
                1 -> R.drawable.grade_green_stroke
                2 -> R.drawable.grade_yellow_stroke
                else -> R.drawable.quests_first_bg
            }
            image.setBackgroundResource(resId)
        } ?: image.setBackgroundResource(R.drawable.quests_first_bg)
    }


    @BindingAdapter("changeBackgroundStroke1")
    @JvmStatic
    fun changeBackgroundStroke1(image: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                0 -> image.setBackgroundResource(R.drawable.quests_first_bg)
                else -> image.setBackgroundResource(R.drawable.quests_second_bg)
            }

        }
    }


    @BindingAdapter("changeBackgroundStrokePick")
    @JvmStatic
    fun changeBackgroundStrokePick(image: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                0 -> image.setBackgroundResource(R.drawable.quests_first_bg)
                1 -> image.setBackgroundResource(R.drawable.quests_third_bg)
                2 -> image.setBackgroundResource(R.drawable.quests_second_bg)
                else -> image.setBackgroundResource(R.drawable.quests_first_bg)
            }
        }
    }

    @BindingAdapter("quizBackground")
    @JvmStatic
    fun quizBackground(image: ConstraintLayout, type: String?) {
        if (type != null) {
            when (type) {
                "completed" -> image.setBackgroundResource(R.drawable.quests_third_bg)
                "in-progress" -> image.setBackgroundResource(R.drawable.quests_second_bg)
                "pending" -> image.setBackgroundResource(R.drawable.quests_first_bg)
                else -> image.setBackgroundResource(R.drawable.quests_first_bg)
            }
        } else image.setBackgroundResource(R.drawable.quests_first_bg)
    }

    @BindingAdapter("setStatusVisibility")
    @JvmStatic
    fun setStatusVisibility(view: View, status: String?) {
        view.visibility = if (status == "completed" || status == "in-progress") View.VISIBLE
        else View.GONE
    }

    @BindingAdapter("quizImageBg")
    @JvmStatic
    fun quizImageBg(imageView: AppCompatImageView, type: String?) {
        if (type != null) {
            when (type) {
                "completed" -> imageView.setColorFilter(imageView.context.getColor(R.color.theme1_start_color))
                "in-progress" -> imageView.setColorFilter(imageView.context.getColor(R.color.yellow))
                "pending" -> imageView.setColorFilter(imageView.context.getColor(R.color.theme1_start_color))
                else -> imageView.setColorFilter(imageView.context.getColor(R.color.theme1_start_color))
            }
        } else imageView.setColorFilter(imageView.context.getColor(R.color.theme1_start_color))
    }



    @BindingAdapter("changeTextColor")
    @JvmStatic
    fun changeTextColor(textView: AppCompatTextView, type: String?) {
        val context = textView.context
        val color = when(type) {
            "completed" -> context.resolveAttrColor(R.attr.endColor)
            "in-progress" -> context.getColor(R.color.yellow)
            "pending" -> context.resolveAttrColor(R.attr.startColor)
            else -> context.resolveAttrColor(R.attr.startColor)
        }
        textView.setTextColor(color)
    }


    @BindingAdapter("changeTextColor12")
    @JvmStatic
    fun changeTextColor12(textView: AppCompatTextView, type: Boolean?) {
        if (type == true) textView.setTextColor(textView.context.resolveAttrColor(R.attr.endColor))
        else textView.setTextColor(textView.context.resolveAttrColor(R.attr.startColor))
    }

    @BindingAdapter("changeTextColor1")
    @JvmStatic
    fun changeTextColor1(textView: AppCompatTextView, type: Int?) {
        val context = textView.context
        val color = when(type) {
            1 -> context.resolveAttrColor(R.attr.startColor)
            2 -> context.getColor(R.color.yellow)
            3 -> context.resolveAttrColor(R.attr.endColor)
            else -> context.resolveAttrColor(R.attr.startColor)
        }
        textView.setTextColor(color)
    }


    @BindingAdapter("changeImageColor1")
    @JvmStatic
    fun changeImageColor1(imageView: AppCompatImageView, type: Int?) {
        val context = imageView.context
        val color = when (type) {
            1 -> context.resolveAttrColor(R.attr.startColor)
            2 -> context.getColor(R.color.yellow)
            3 -> context.resolveAttrColor(R.attr.endColor)
            else -> context.resolveAttrColor(R.attr.startColor)
        }
        imageView.setColorFilter(color)
    }

    @BindingAdapter("changeImageTint")
    @JvmStatic
    fun changeImageTint(imageView: AppCompatImageView, type: Boolean?) {
        if (type == true) imageView.setColorFilter(imageView.context.resolveAttrColor(R.attr.endColor))
        else imageView.setColorFilter(imageView.context.resolveAttrColor(R.attr.startColor))
    }


    @BindingAdapter("changeBgColor")
    @JvmStatic
    fun changeBgColor(layout: ConstraintLayout, type: String?) {
        if (type != null) {
            when (type) {
                "in-progress" -> layout.setBackgroundResource(R.drawable.progress_bar_color_1)
                "completed" -> layout.setBackgroundResource(R.drawable.progress_bar_color_2)
                else -> layout.setBackgroundResource(R.drawable.progress_bar_color_1)
            }
        } else layout.setBackgroundResource(R.drawable.progress_bar_color_1)
    }

    @BindingAdapter("changeBgCl")
    @JvmStatic
    fun changeBgCl(layout: ConstraintLayout, type: Boolean?) {
        if (type == true) layout.setBackgroundResource(R.drawable.quests_third_bg)
        else layout.setBackgroundResource(R.drawable.quests_first_bg)
    }

    @BindingAdapter("changeBgColor1")
    @JvmStatic
    fun changeBgColor1(layout: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> layout.setBackgroundResource(R.drawable.quests_first_bg)
                2 -> layout.setBackgroundResource(R.drawable.quests_second_bg)
                3 -> layout.setBackgroundResource(R.drawable.quests_third_bg)
                else -> layout.setBackgroundResource(R.drawable.quests_first_bg)
            }

        }
    }

    @BindingAdapter("setColorBadges")
    @JvmStatic
    fun setColorBadges(layout: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> layout.setBackgroundResource(R.drawable.rewards_green_bg)
                2 -> layout.setBackgroundResource(R.drawable.badges_blue_40_bg)
                3 -> layout.setBackgroundResource(R.drawable.rewards_yellow_bg)
                4 -> layout.setBackgroundResource(R.drawable.rewards_blur_bg_40)
                else -> layout.setBackgroundResource(R.drawable.rewards_green_bg)
            }

        }
    }

    @BindingAdapter("setTintByType")
    @JvmStatic
    fun ImageView.setTintByType(type: Int?) {
        imageTintList = if (type == 4) {
            ContextCompat.getColorStateList(context, R.color.gray)
        } else {
            null
        }
    }

    @BindingAdapter("changeBgNotification")
    @JvmStatic
    fun changeBgNotification(layout: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> layout.setBackgroundResource(R.drawable.notification_first_bg)
                2 -> layout.setBackgroundResource(R.drawable.notifiction_second_bg)
                3 -> layout.setBackgroundResource(R.drawable.notification_third_bg)
                else -> layout.setBackgroundResource(R.drawable.notification_first_bg)
            }
        }
    }


    @BindingAdapter("changeViewBgColor")
    @JvmStatic
    fun changeViewBgColor(view: View, type: String?) {
        if (type != null) {
            when (type) {
                "in-progress" -> view.setBackgroundResource(R.drawable.yellow_bg)
                "completed" -> view.setBackgroundResource(R.drawable.green_bg)
                else -> view.setBackgroundResource(R.drawable.yellow_bg)
            }
        } else view.setBackgroundResource(R.drawable.yellow_bg)
    }


    @BindingAdapter("loadSvgImage")
    @JvmStatic
    fun loadSvgImage(image: ImageView, url: String) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val svgString = response.body?.string()
                    svgString?.let {
                        val svg = SVG.getFromString(it)
                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            try {
                                val drawable = PictureDrawable(svg.renderToPicture())
                                image.setImageDrawable(drawable)
                            } catch (e: SVGParseException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        })
    }

    data class CommonModel(
        val name: String, val id: Int, var isSelected: Boolean = false
    )

    fun showDropdownModel(
        anchor: View, items: List<CommonModel>, onItemSelected: (CommonModel) -> Unit
    ) {
        val context = anchor.context
        val inflater = LayoutInflater.from(context)

        val popupView = inflater.inflate(R.layout.popup_menu_view, null)
        val listView = popupView.findViewById<ListView>(R.id.listView)

        val popupWindow = PopupWindow(
            popupView,
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._180sdp),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val adapter =
            ArrayAdapter(context, android.R.layout.simple_list_item_1, items.map { it.name })
        listView.adapter = adapter


        listView.setOnItemClickListener { _, _, position, _ ->
            onItemSelected(items[position])
            popupWindow.dismiss()
        }

        popupWindow.elevation = 12f
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                anchor.context, R.drawable.popup_bg
            )
        )
        popupWindow.showAsDropDown(anchor, 0, 0, Gravity.END)

    }

    fun Context.resolveAttrColor(attr: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    @BindingAdapter("setThemeHeaderImage")
    @JvmStatic
    fun setThemeHeaderImage(appCompatImageView: AppCompatImageView,enabled: Boolean = true) {
        if (!enabled) return

        val typedValue = TypedValue()
        val theme = appCompatImageView.context.theme

        if (theme.resolveAttribute(R.attr.headerImage, typedValue, true)) {
            if (typedValue.resourceId != 0) {
                appCompatImageView.setImageResource(typedValue.resourceId)
            }
        }
    }

    @BindingAdapter("setSplashBackground")
    @JvmStatic
    fun setSplashBackground(layout: ConstraintLayout, enabled: Boolean = true) {
        if (!enabled) return

        val typedValue = TypedValue()
        val theme = layout.context.theme

        if (theme.resolveAttribute(R.attr.splashImage, typedValue, true)) {
            when {
                typedValue.resourceId != 0 -> {
                    // drawable resource
                    layout.setBackgroundResource(typedValue.resourceId)
                }
                typedValue.data != 0 -> {
                    // color value
                    layout.setBackgroundColor(typedValue.data)
                }
            }
        }
    }

    @BindingAdapter("setThemeProfileImage")
    @JvmStatic
    fun setThemeProfileImage(appCompatImageView: AppCompatImageView,enabled: Boolean = true) {
        if (!enabled) return

        val typedValue = TypedValue()
        val theme = appCompatImageView.context.theme

        if (theme.resolveAttribute(R.attr.profileImage, typedValue, true)) {
            if (typedValue.resourceId != 0) {
                appCompatImageView.setImageResource(typedValue.resourceId)
            }
        }
    }

    @BindingAdapter("setDownloadTint")
    @JvmStatic
    fun setDownloadTint(view: TextView, isDownloaded: Boolean?) {
        val colorAttr = if (isDownloaded == true)
            R.attr.endColor
        else
            R.attr.startColor

        val typedValue = TypedValue()
        view.context.theme.resolveAttribute(colorAttr, typedValue, true)

        val color = if (typedValue.resourceId != 0)
            ContextCompat.getColor(view.context, typedValue.resourceId)
        else
            typedValue.data

        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color))
    }

    @BindingAdapter("setQuestHeading")
    @JvmStatic
    fun setQuestHeading(
        textView: AppCompatTextView,
        bean: GetHomeQuest?
    ) {
        if (bean == null) {
            textView.text = ""
            return
        }

        textView.text = when (bean.type) {
            "questReading" -> bean.name.orEmpty()
            "questQuiz" -> bean.testQuizId?.name.orEmpty()
            else -> ""
        }
    }


}

