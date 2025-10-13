package com.edu.lite.utils

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.edu.lite.R
import com.edu.lite.data.api.Constants
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Calendar
import java.util.Date

object BindingUtils {

    @BindingAdapter("setImageFromUrl")
    @JvmStatic
    fun setImageFromUrl(image: ShapeableImageView, url: String?) {
        if (url != null) {
            Glide.with(image.context).load(url).placeholder(R.drawable.person_holder).error(R.drawable.person_holder)
                .into(image)
        }
    }


    @BindingAdapter("setIconFromUrl")
    @JvmStatic
    fun setIconFromUrl(image: ShapeableImageView, url: String?) {
        if (url != null) {
            Glide.with(image.context).load(Constants.BASE_URL_IMAGE+url)
                .into(image)
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

    /**
     * navigate with slide animation
     */
    fun navigateWithSlide(navController: NavController, directions: NavDirections) {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()

        navController.navigate(directions, navOptions)
    }

    @BindingAdapter("setQuestionTypeBgLayout")
    @JvmStatic
    fun setQuestionTypeBgLayout(image: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> {
                    image.setBackgroundResource(R.drawable.quests_first_bg)
                }

                2 -> {
                    image.setBackgroundResource(R.drawable.quests_second_bg)
                }

                3 -> {
                    image.setBackgroundResource(R.drawable.quests_third_bg)
                }
            }

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

    @BindingAdapter("setLetsPlaySelection")
    @JvmStatic
    fun setLetsPlaySelection(editText: AppCompatTextView, data: String?) {
        if (!data.isNullOrEmpty()) {
            when {
                data.contains("quiz", ignoreCase = true) -> {
                    editText.text = "Fun Quizzes"
                }

                data.contains("test", ignoreCase = true) -> {
                    editText.text = "Practice Test"
                }

                data.contains("videoLesson", ignoreCase = true) -> {
                    editText.text = "Video Lessons"
                }

                data.contains("creativeProject", ignoreCase = true) -> {
                    editText.text = "Creative Projects"
                }
            }
        }
    }



    @BindingAdapter("setProgress")
    @JvmStatic
    fun setProgress(textView: AppCompatTextView, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> {
                    textView.text = textView.context.getString(R.string.today_s_progress)
                }

                2 -> {
                    textView.text = textView.context.getString(R.string.class_overview)
                }

                3 -> {
                    textView.text = textView.context.getString(R.string.weekly_digest)
                }
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
                0 -> {
                    image.setBackgroundResource(R.drawable.quests_first_bg)
                }

                else -> {
                    image.setBackgroundResource(R.drawable.quests_second_bg)
                }

            }

        }
    }


    @BindingAdapter("changeBackgroundStrokePick")
    @JvmStatic
    fun changeBackgroundStrokePick(image: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                0 -> {
                    image.setBackgroundResource(R.drawable.quests_first_bg)
                }

                1 -> {
                    image.setBackgroundResource(R.drawable.quests_third_bg)
                }

                2 -> {
                    image.setBackgroundResource(R.drawable.quests_second_bg)
                }

                else -> {
                    image.setBackgroundResource(R.drawable.quests_first_bg)
                }

            }

        }
    }

    @BindingAdapter("changeBackgroundStrokePick1")
    @JvmStatic
    fun changeBackgroundStrokePick1(image: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                0 -> {
                    image.setBackgroundResource(R.drawable.quests_first_bg)
                }

                1 -> {
                    image.setBackgroundResource(R.drawable.quests_second_bg)
                }

                2 -> {
                    image.setBackgroundResource(R.drawable.quests_third_bg)
                }

                else -> {
                    image.setBackgroundResource(R.drawable.quests_first_bg)
                }

            }

        }
    }


    @BindingAdapter("changeTextColor")
    @JvmStatic
    fun changeTextColor(textView: AppCompatTextView, type: Int?) {
        if (type != null) {
            when (type) {
                0 -> {
                    textView.setTextColor(textView.context.getColor(R.color.start_color))
                }

                1 -> {
                    textView.setTextColor(textView.context.getColor(R.color.yellow))
                }

                2 -> {
                    textView.setTextColor(textView.context.getColor(R.color.end_color))
                }

                else -> {
                    textView.setTextColor(textView.context.getColor(R.color.start_color))
                }

            }

        }
    }


    @BindingAdapter("changeTextColor1")
    @JvmStatic
    fun changeTextColor1(textView: AppCompatTextView, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> {
                    textView.setTextColor(textView.context.getColor(R.color.start_color))
                }

                2 -> {
                    textView.setTextColor(textView.context.getColor(R.color.yellow))
                }

                3 -> {
                    textView.setTextColor(textView.context.getColor(R.color.end_color))
                }

                else -> {
                    textView.setTextColor(textView.context.getColor(R.color.start_color))
                }

            }

        }
    }


    @BindingAdapter("changeImageColor")
    @JvmStatic
    fun changeImageColor(imageView: AppCompatImageView, type: Int?) {
        if (type != null) {
            when (type) {
                0 -> {
                    imageView.setColorFilter(imageView.context.getColor(R.color.start_color))
                }

                1 -> {
                    imageView.setColorFilter(imageView.context.getColor(R.color.yellow))
                }

                2 -> {
                    imageView.setColorFilter(imageView.context.getColor(R.color.end_color))
                }

                else -> {
                    imageView.setColorFilter(imageView.context.getColor(R.color.start_color))
                }

            }

        }
    }

    @BindingAdapter("changeImageColor1")
    @JvmStatic
    fun changeImageColor1(imageView: AppCompatImageView, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> {
                    imageView.setColorFilter(imageView.context.getColor(R.color.start_color))
                }

                2 -> {
                    imageView.setColorFilter(imageView.context.getColor(R.color.yellow))
                }

                3 -> {
                    imageView.setColorFilter(imageView.context.getColor(R.color.end_color))
                }

                else -> {
                    imageView.setColorFilter(imageView.context.getColor(R.color.start_color))
                }

            }

        }
    }


    @BindingAdapter("changeBgColor")
    @JvmStatic
    fun changeBgColor(layout: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                2 -> {
                    layout.setBackgroundResource(R.drawable.progress_bar_color_1)
                }

                3 -> {
                    layout.setBackgroundResource(R.drawable.progress_bar_color_2)
                }

                else -> {
                    layout.setBackgroundResource(R.drawable.progress_bar_color_1)
                }

            }

        }
    }


    @BindingAdapter("changeBgColor1")
    @JvmStatic
    fun changeBgColor1(layout: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> {
                    layout.setBackgroundResource(R.drawable.quests_first_bg)
                }

                2 -> {
                    layout.setBackgroundResource(R.drawable.quests_second_bg)
                }
                3 -> {
                    layout.setBackgroundResource(R.drawable.quests_third_bg)
                }
                else -> {
                    layout.setBackgroundResource(R.drawable.quests_first_bg)
                }

            }

        }
    }


    @BindingAdapter("changeBgNotification")
    @JvmStatic
    fun changeBgNotification(layout: ConstraintLayout, type: Int?) {
        if (type != null) {
            when (type) {
                1 -> {
                    layout.setBackgroundResource(R.drawable.notification_first_bg)
                }

                2 -> {
                    layout.setBackgroundResource(R.drawable.notifiction_second_bg)
                }
                3 -> {
                    layout.setBackgroundResource(R.drawable.notification_third_bg)
                }
                else -> {
                    layout.setBackgroundResource(R.drawable.notification_first_bg)
                }

            }

        }
    }


    @BindingAdapter("changeViewBgColor")
    @JvmStatic
    fun changeViewBgColor(view: View, type: Int?) {
        if (type != null) {
            when (type) {
                2 -> {
                    view.setBackgroundResource(R.drawable.yellow_bg)
                }

                3 -> {
                    view.setBackgroundResource(R.drawable.green_bg)
                }

                else -> {
                    view.setBackgroundResource(R.drawable.yellow_bg)
                }

            }

        }
    }


    @BindingAdapter("changeImageBgColor")
    @JvmStatic
    fun changeImageBgColor(imageView: AppCompatImageView, type: Int?) {
        if (type != null) {
            when (type) {
                2 -> {
                    imageView.setColorFilter(imageView.context.getColor(R.color.yellow))
                }

                3 -> {
                    imageView.setColorFilter(imageView.context.getColor(R.color.end_color))
                }

                else -> {
                    imageView.setColorFilter(imageView.context.getColor(R.color.yellow))
                }

            }

        }
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



}

