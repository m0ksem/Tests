package com.example.m0ksem.imprtest.TestCreate.ChooseTags

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import com.example.m0ksem.imprtest.R
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.animation.AccelerateInterpolator
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.LinearLayout


@Suppress("UNUSED_PARAMETER")
class ChooseTags : AppCompatActivity() {

    private lateinit var adapter: TagInChooseTagsAdapter
    private lateinit var tags_list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_tags)
        tags_list = this.findViewById(R.id.tag_list)
        tags_list.layoutManager = LinearLayoutManager(this)
        val tags: ArrayList<String> = intent.getStringArrayListExtra("tags")  ?: ArrayList()
        adapter = TagInChooseTagsAdapter(tags)
        tags_list.adapter = adapter
        val header: ConstraintLayout = this.findViewById(R.id.header)!!
        val vto = header.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
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

    fun addTag(view: View) {
        val str: String = findViewById<EditText>(R.id.new_tag_input).text.toString()
        findViewById<EditText>(R.id.new_tag_input).setText("")
        adapter.add(str)
    }

    fun back(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        save()
        super.onBackPressed()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_addition_setting_enter, R.anim.close_addition_setting_exit)
    }


    fun deleteTag(view: View) {
        val item: ConstraintLayout = view.parent as ConstraintLayout
        val pos: Int = tags_list.getChildAdapterPosition(item)
        adapter.delete(pos)
    }

    fun save() {
        intent.putExtra("tags", adapter.tags)
        setResult(RESULT_OK, intent)
        finish()
    }
}

