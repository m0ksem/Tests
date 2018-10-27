package com.example.m0ksem.imprtest.TestCreate.SetResults

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.m0ksem.imprtest.R
import com.example.m0ksem.imprtest.ScoreTest
import com.example.m0ksem.imprtest.Test

class SetResults : AppCompatActivity() {

    private lateinit var adapter: ResultAdapter
    lateinit var result_list: RecyclerView
    var type: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_results)
        result_list = this.findViewById<RecyclerView>(R.id.create_test_results_list)
        result_list.layoutManager = LinearLayoutManager(this)
        if (intent.getStringExtra("type") == "answer_with_score") {
            type = 0
            val results: ArrayList<ScoreTest.Result>
            if ( intent.getSerializableExtra("results") != null) {
                results = intent.getSerializableExtra("results") as ArrayList<ScoreTest.Result>
            }
            else {
                results = ArrayList()
            }
            adapter = ResultWithScoreAdapter(results as ArrayList<Test.Result>)
        }
        result_list.adapter = adapter
    }

    fun addResult(view: View) {
        val str: String = findViewById<EditText>(R.id.new_tag_input).text.toString()
        if (type == 0) {
            val result: ScoreTest.Result = ScoreTest.Result(str, 0, 0)
            result.text = str
            adapter.add(result)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            val header: ConstraintLayout = this.findViewById(R.id.header)!!
            val startHeight: Int = header.height + 20
            val animation = SlideAnimation(header, intent.getIntExtra("header_height", 400), startHeight)
            animation.interpolator = AccelerateInterpolator()
            animation.duration = 300
            header.animation = animation
            header.startAnimation(animation)
        }
    }

    inner class SlideAnimation(private var mView: View, private var mFromHeight: Int, private var mToHeight: Int) : Animation() {
        override fun applyTransformation(interpolatedTime: Float, transformation: Transformation) {
            val newHeight: Int

            if (mView.height != mToHeight) {
                newHeight = (mFromHeight + (mToHeight - mFromHeight) * interpolatedTime).toInt()
                mView.layoutParams.height = newHeight
                mView.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    fun back(view: View){
        save()
        finish()
    }

    override fun onBackPressed() {
        save()
        super.onBackPressed()
    }

    fun deleteResult(view: View) {
        val item: ConstraintLayout = view.parent.parent as ConstraintLayout
        val pos: Int = result_list.getChildAdapterPosition(item)
        adapter.delete(pos)
    }

    fun save() {
        intent.putExtra("results", adapter.results)
        setResult(RESULT_OK, intent)
        finish()
    }
}

open class ResultAdapter(open val results: ArrayList<Test.Result>) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_score_result, parent, false))
    }

    open fun delete(position: Int) {
        results.removeAt(position)
        notifyDataSetChanged()
    }

    open fun add(result: Test.Result) {
        results.add(result)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(view: ViewHolder, postion: Int) {
        view.text.text = results[postion].text
    }

    override fun getItemCount(): Int {
        return results.size
    }

    open inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val text = view.findViewById<TextView>(R.id.result_text)!!
        init {
            text.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    results[position].text = p0.toString()
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            })
        }
    }
}


class ResultWithScoreAdapter(override val results: ArrayList<Test.Result>) : ResultAdapter(results as ArrayList<Test.Result>) {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_score_result, parent, false))
    }

    override fun add(result: Test.Result) {
        results.add(result)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(view: ResultAdapter.ViewHolder, postion: Int) {
        view.text.text = results[postion].text
        view as ResultWithScoreAdapter.ViewHolder
        view.min.text = (results[postion] as ScoreTest.Result).min.toString()
        view.max.text = (results[postion] as ScoreTest.Result).max.toString()
    }

    override fun getItemCount(): Int {
        return results.size
    }

    inner class ViewHolder(view: View): ResultAdapter.ViewHolder(view) {
        val min = view.findViewById<TextView>(R.id.min)!!
        val max = view.findViewById<TextView>(R.id.max)!!
        init {
            min.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    val res: ScoreTest.Result = results[position] as ScoreTest.Result
                    if (p0.toString().toIntOrNull() != null) {
                        (res).min = p0.toString().toInt()
                    }
                    else {
                        Toast.makeText(view.context,"Invalid input", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            })
            max.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    val res: ScoreTest.Result = results[position] as ScoreTest.Result
                    if (p0.toString().toIntOrNull() != null) {
                        (res).max = p0.toString().toInt()
                    }
                    else {
                        Toast.makeText(view.context,"Invalid input", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            })
        }
    }
}
