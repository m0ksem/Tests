@file:Suppress("UNUSED_PARAMETER")

package com.example.m0ksem.imprtest.TestCreate.ChooseType

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import com.example.m0ksem.imprtest.R
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.LinearLayout

class ChooseType : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_type)
        val header: ConstraintLayout = this.findViewById(R.id.header)!!
        val vto = header.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                header.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val animation = SlideAnimation(header, intent.getIntExtra("header_height", 400), header.measuredHeight)
                animation.interpolator = AccelerateInterpolator()
                animation.duration = 300
                header.animation = animation
                header.startAnimation(animation)
            }
        })
        if (Build.VERSION.SDK_INT >= 21) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.navigationBarColor = resources.getColor(R.color.colorGradientBackgroundBottom)
//            getWindow().setFlags(
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//            )
//            header.setPadding(header.paddingLeft, (50 * resources.displayMetrics.density).toInt(), header.paddingRight, header.paddingBottom)
        }
    }

    inner class SlideAnimation(private var mView: View, private var mFromHeight: Int, private var mToHeight: Int) : Animation() {
        override fun applyTransformation(interpolatedTime: Float, transformation: Transformation) {
            val newHeight: Int

            if (mView.height + 1 != mToHeight) {
                newHeight = (mFromHeight + (mToHeight - mFromHeight) * interpolatedTime).toInt()
                mView.layoutParams.height = newHeight
                mView.requestLayout()
            } else {
                mView.layoutParams.height = mView.height - 1
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    fun back(view: View){
        finish()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.create_test_choose_type_score -> {
                intent.putExtra("type", "answers_with_score")
                setResult(RESULT_OK, intent);
            }
            R.id.create_test_choose_type_string -> {
                intent.putExtra("type", "answers_with_string")
                setResult(RESULT_OK, intent);
            }
            R.id.create_test_choose_type_neuro -> {
                intent.putExtra("type", "answers_with_connection")
                setResult(RESULT_OK, intent);
            }
        }
        finish()
    }
}
