package com.example.m0ksem.imprtest.TestCreate.SetQuestions

import android.annotation.SuppressLint
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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.m0ksem.imprtest.R
import java.io.Serializable

class SetQuestions : AppCompatActivity()  {

    private lateinit var adapter: QuestionsAdapter
    lateinit var list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_questions)
        val questions: ArrayList<ArrayList<String>> = intent.getSerializableExtra("questions") as ArrayList<ArrayList<String>>
        list = this.findViewById<RecyclerView>(R.id.create_test_questions_list)
        list.layoutManager = LinearLayoutManager(this)
        adapter = QuestionsAdapter(questions)
        list.adapter = adapter
    }

    fun back(view: View){
        save()
        finish()
    }

    override fun onBackPressed() {
        save()
        super.onBackPressed()
    }

    fun addQuestion(view: View) {
        val input: EditText = findViewById(R.id.new_tag_input)
        adapter.add(input.text.toString())
        input.setText("")
    }

    fun addAnswer(view: View) {
        val item: LinearLayout = view.parent.parent as LinearLayout
        val input: EditText = item.findViewById(R.id.new_answer_input)
        val pos: Int = list.getChildAdapterPosition(item)
        adapter.addAnswer(pos, input.text.toString())
        input.setText("")
    }

    fun deleteAnswer(view: View) {
        val item: ConstraintLayout = view.parent as ConstraintLayout
        val answers: RecyclerView = view.parent.parent as RecyclerView
        val answer_pos: Int = answers.getChildAdapterPosition(item)
        val question_pos: Int = list.getChildAdapterPosition(answers.parent as LinearLayout)
        adapter.deleteAnswer(question_pos, answer_pos)
    }

    fun save() {
        intent.putExtra("questions", adapter.questions as Serializable)
        setResult(RESULT_OK, intent)
        finish()
    }

    fun deleteQuestion(view: View) {
        val item: LinearLayout = view.parent.parent.parent as LinearLayout
        val pos: Int = list.getChildAdapterPosition(item)
        adapter.delete(pos)
    }

    class QuestionsAdapter(val questions: ArrayList<ArrayList<String>>) : RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_questions, parent, false))
        }

        fun delete(position: Int) {
            questions.removeAt(position)
            notifyDataSetChanged()
        }

        fun add(question: String) {
            val q = ArrayList<String>()
            q.add(question)
            questions.add(q)
            notifyDataSetChanged()
        }

        fun addAnswer(position: Int , answer: String) {
            questions[position].add(answer)
            notifyDataSetChanged()
        }

        fun deleteAnswer(question_position: Int, answer_position: Int) {
            questions[question_position].removeAt(answer_position + 1)
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(view: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
            view.question_text.text = questions[position][0]
            view.answers.layoutManager = LinearLayoutManager(view.ctx)
            view.answers.adapter = AnswersAdapter(questions[position])
        }

        override fun getItemCount(): Int {
            return questions.size
        }

        inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
            val question_text = view.findViewById<TextView>(R.id.qustion_name)
            val answers = view.findViewById<RecyclerView>(R.id.answes)
            val ctx = view.context
            init {
                question_text.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        questions[position][0] = p0.toString()
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })
            }
        }

        class AnswersAdapter(val answers: ArrayList<String>) : RecyclerView.Adapter<AnswersAdapter.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
                return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_answer, parent, false))
            }

            override fun onBindViewHolder(view: ViewHolder, position: Int) {
                view.answer_text.text = answers[position + 1]
                view.answer_text.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        answers[position + 1] = p0.toString()
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })
            }

            override fun getItemCount(): Int {
                return answers.size - 1
            }

            class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
                val answer_text = view.findViewById<TextView>(R.id.answer_text)
            }
        }
    }

}

