package com.example.m0ksem.imprtest.TestView

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.example.m0ksem.imprtest.R
import com.example.m0ksem.imprtest.Test


class TestViewActivity : AppCompatActivity() {
    companion object {
        lateinit var test: Test
    }

    private lateinit var adapter: ViewQuestionAdapter
    private lateinit var list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_view)
        test = intent.getSerializableExtra("test") as Test
        findViewById<TextView>(R.id.test_name).text = test.name
        findViewById<TextView>(R.id.view_test_questions_count).text = test.questions.size.toString()
        findViewById<TextView>(R.id.view_test_author).text = test.author
        adapter = ViewQuestionAdapter(test.questions)
        list = this.findViewById(R.id.questions_list)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
    }

    fun back(view: View){
        finish()
    }

    fun save(view: View) {
        val a = getUserAnswers()
    }

    private fun getUserAnswers() : ArrayList<Int> {
        val array: ArrayList<Int> = ArrayList()
        for (i: Int in 0 until adapter.itemCount) {
            val q:ViewQuestionAdapter.ViewHolder = list.findViewHolderForAdapterPosition(i)!! as ViewQuestionAdapter.ViewHolder
            val a: ViewQuestionAdapter.ViewAnswerAdapter = q.answers.adapter as ViewQuestionAdapter.ViewAnswerAdapter
            array.add(a.selectedAnswer)
        }
        return array
    }
}

class ViewQuestionAdapter(private val questions: ArrayList<ArrayList<String>>) : RecyclerView.Adapter<ViewQuestionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_test_question, parent, false))
    }

    override fun onBindViewHolder(view: ViewHolder, position: Int) {
        view.questionText.text = questions[position][0]
        view.answers.layoutManager = LinearLayoutManager(view.ctx)
        view.answers.adapter = ViewAnswerAdapter(questions[position])
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val questionText = view.findViewById<TextView>(R.id.view_test_question)!!
        val answers = view.findViewById<RecyclerView>(R.id.view_test_answer)!!
        val ctx = view.context!!
    }

    class ViewAnswerAdapter(private val answers: ArrayList<String>) : RecyclerView.Adapter<ViewAnswerAdapter.ViewHolder>() {
        var selectedAnswer: Int = -1

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_test_answer, parent, false))
        }

        override fun onBindViewHolder(view: ViewHolder, position: Int) {
            view.answer.text = answers[position + 1]
            view.answer.isChecked = false
            if (selectedAnswer == position) {
                view.answer.isChecked = true
            }
        }

        override fun getItemCount(): Int {
            return answers.size - 1
        }

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val answer = view.findViewById<CheckBox>(R.id.view_test_answer)!!

            init {
                val clickListener = View.OnClickListener {
                    selectedAnswer = adapterPosition
                    notifyDataSetChanged()
                }
                answer.setOnClickListener(clickListener)
                view.setOnClickListener(clickListener)
            }
        }
    }
}
