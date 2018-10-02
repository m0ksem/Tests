@file:Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")

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
import android.widget.Toast
import com.example.m0ksem.imprtest.R
import com.example.m0ksem.imprtest.ScoreTest
import com.example.m0ksem.imprtest.Test
import java.io.Serializable

class SetQuestions : AppCompatActivity()  {

    private lateinit var adapter: QuestionsAdapter
    private lateinit var list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_questions)
        val questions: ArrayList<Test.Question> = intent.getSerializableExtra("questions") as ArrayList<Test.Question>
        list = this.findViewById(R.id.create_test_questions_list)
        list.layoutManager = LinearLayoutManager(this)
        adapter = QuestionsAdapter(questions)
        list.adapter = adapter
    }

//    fun back(view: View){
//        save()
//        finish()
//    }

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
        val answerPos: Int = answers.getChildAdapterPosition(item)
        val questionPos: Int = list.getChildAdapterPosition(answers.parent as LinearLayout)
        adapter.deleteAnswer(questionPos, answerPos)
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

    @Suppress("DEPRECATION")
    class QuestionsAdapter(val questions: ArrayList<Test.Question>) : RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_questions, parent, false))
        }

        fun delete(position: Int) {
            questions.removeAt(position)
            notifyDataSetChanged()
        }

        fun add(question: String) {
            val q = Test.Question()
            q.text = question
            questions.add(q)
            notifyDataSetChanged()
        }

        fun addAnswer(position: Int , answer: String) {
            questions[position].answers.add(ScoreTest.Question.Answer(answer, 0))
            notifyDataSetChanged()
        }

        fun deleteAnswer(question_position: Int, answer_position: Int) {
            questions[question_position].answers.removeAt(answer_position)
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(view: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
            view.questionText.text = questions[position].text
            view.answers.layoutManager = LinearLayoutManager(view.ctx)
            // TODO() Поменять тип адаптера в зависимости от типа теста
            view.answers.adapter = AnswersWithScoreAdapter(questions[position].answers as ArrayList<ScoreTest.Question.Answer>)
        }

        override fun getItemCount(): Int {
            return questions.size
        }

        inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
            val questionText = view.findViewById<TextView>(R.id.qustion_name)!!
            val answers = view.findViewById<RecyclerView>(R.id.answes)!!
            val ctx = view.context!!
            init {
                questionText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        questions[position].text = p0.toString()
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

            override fun onBindViewHolder(view: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
                view.answerText.text = answers[position + 1]
                view.answerText.addTextChangedListener(object : TextWatcher {
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
                val answerText = view.findViewById<TextView>(R.id.answer_text)!!
            }
        }

        class AnswersWithScoreAdapter(val answers: ArrayList<ScoreTest.Question.Answer>) : RecyclerView.Adapter<AnswersWithScoreAdapter.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
                return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_answer_with_value, parent, false))
            }

            override fun onBindViewHolder(view: ViewHolder, position: Int) {
                view.answerText.text = answers[position].text
                view.answerValue.text = answers[position].score.toString()

            }

            override fun getItemCount(): Int {
                return answers.size
            }

            inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
                val answerText = view.findViewById<TextView>(R.id.answer_text)!!
                val answerValue = view.findViewById<TextView>(R.id.answer_value)!!
                init {
                    answerText.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(p0: Editable?) {
                            answers[position].text = p0.toString()
                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        }
                    })
                    answerValue.addTextChangedListener(object : TextWatcher {
                        @SuppressLint("ShowToast")
                        override fun afterTextChanged(p0: Editable?) {
                            // TODO() Сделать проверку что это действительно цифра
                            val inputText: String = p0.toString()
                            val intInput: Int? = inputText.toIntOrNull()
                            when {
                                inputText == "" -> return
                                intInput == null -> {
                                    Toast.makeText(view.context,"Value must be a number!", Toast.LENGTH_SHORT).show()
                                    return
                                }
                                else -> answers[position].score = intInput
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
    }
}

