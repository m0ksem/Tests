package com.m0ksem.tests.testCreate

import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.EditText
import android.widget.TextView
import com.m0ksem.tests.R


@Suppress("UNUSED_PARAMETER")
class SetTagsActivity : AppCompatActivity() {

    private lateinit var adapter: TagInChooseTagsAdapter
    private lateinit var tagsList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_tags)
        tagsList = this.findViewById(R.id.tag_list)
        tagsList.layoutManager = LinearLayoutManager(this)
        val tags: ArrayList<String> = intent.getStringArrayListExtra("tags")  ?: ArrayList()
        adapter = TagInChooseTagsAdapter(tags)
        tagsList.adapter = adapter
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
            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorGradientBackgroundBottom)
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
        val pos: Int = tagsList.getChildAdapterPosition(item)
        adapter.delete(pos)
    }

    fun save() {
        intent.putExtra("tags", adapter.tags)
        setResult(RESULT_OK, intent)
        finish()
    }
}


class TagInChooseTagsAdapter(val tags: ArrayList<String>) : RecyclerView.Adapter<TagInChooseTagsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_tag, parent, false))
    }

    fun delete(position: Int) {
        tags.removeAt(position)
        notifyDataSetChanged()
    }

    fun add(tag: String) {
        tags.add(tag)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(view: ViewHolder, postion: Int) {
        view.text.text = tags[postion]
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val text = view.findViewById<TextView>(R.id.tag_text)!!
    }
}

