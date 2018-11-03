@file:Suppress("UNUSED_PARAMETER", "UNCHECKED_CAST")

package com.example.m0ksem.imprtest.TestCreate.SetQuestions

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.*
import com.example.m0ksem.imprtest.*
import java.io.Serializable

class SetQuestions : AppCompatActivity()  {

    private lateinit var adapter: QuestionsAdapter
    private lateinit var list: RecyclerView
    lateinit var type: String

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_questions)
        val questions: ArrayList<Test.Question> = intent.getSerializableExtra("questions") as ArrayList<Test.Question>
        type = intent.getStringExtra("type")
        list = this.findViewById(R.id.create_test_questions_list)
        list.layoutManager = LinearLayoutManager(this)
        adapter = QuestionsAdapter(questions)
        list.adapter = adapter

        val header: ConstraintLayout = this.findViewById(R.id.header)!!
        val vto = header.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                header.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val startHeight = header.measuredHeight
                val animation = SlideAnimation(header, intent.getIntExtra("header_height", 400), startHeight)
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

    fun back(view: View){
        save()
        finish()
    }

    override fun onBackPressed() {
        save()
        super.onBackPressed()
    }

    fun addQuestion(view: View) {
        adapter.add("")
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
        val removed = ArrayList<Test.Question>()
        for (q in adapter.questions) {
            val removedAnswers = ArrayList<Test.Question.Answer>()
            for (a in q.answers) {
                if (a.text == "") {
                    removedAnswers.add(a)
                }
            }
            q.answers.removeAll(removedAnswers)
            if (q.text == "" && q.answers.size != 0) {
                Toast.makeText(this, "У вас есть пустыее вопросы. Введите их", Toast.LENGTH_LONG).show()
            }
            else if (q.answers.size == 0 && q.text == "") {
                removed.add(q)
            }
        }
        adapter.questions.removeAll(removed)
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
    inner class QuestionsAdapter(val questions: ArrayList<Test.Question>) : RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {
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
            if (type == "answers_with_score") {
                questions[position].answers.add(ScoreTest.Question.Answer(answer, 0f))
            } else if (type == "answers_with_string") {
                questions[position].answers.add(StringTest.Question.Answer(answer, ""))
            } else if (type == "answers_with_connection") {
                if (intent.getSerializableExtra("default_connections") != null) {
                    val defaultConnections: ArrayList<NeuroTest.Connection> = intent.getSerializableExtra("default_connections") as ArrayList<NeuroTest.Connection>
                    val newConnections: ArrayList<NeuroTest.Connection> = ArrayList()
                    for (el: NeuroTest.Connection in defaultConnections) {
                        newConnections.add(el.copy())
                    }
                    questions[position].answers.add(NeuroTest.Question.Answer(answer, newConnections))
                } else  questions[position].answers.add(NeuroTest.Question.Answer(answer, ArrayList()))

            }
            notifyDataSetChanged()
        }

        fun deleteAnswer(question_position: Int, answer_position: Int) {
            questions[question_position].answers.removeAt(answer_position)
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(view: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
            view.questionText.text = questions[position].text
            view.answers.layoutManager = LinearLayoutManager(view.ctx)
            when (type) {
                "answers_with_score" -> view.answers.adapter = AnswersWithScoreAdapter(questions[position].answers as ArrayList<ScoreTest.Question.Answer>)
                "answers_with_string" -> view.answers.adapter = AnswersWithStringAdapter(questions[position].answers as ArrayList<StringTest.Question.Answer>)
                "answers_with_connection" -> view.answers.adapter = AnswersWithConnectionAdapter(questions[position].answers as ArrayList<NeuroTest.Question.Answer>)
            }
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

        inner class AnswersWithStringAdapter(val answers: ArrayList<StringTest.Question.Answer>) : RecyclerView.Adapter<AnswersWithStringAdapter.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
                return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_answer_with_string, parent, false))
            }

            override fun onBindViewHolder(view: ViewHolder, position: Int) {
                view.answerText.text = answers[position].text
                view.answerValue.text = answers[position].explanation
                view.answerText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        answers[view.adapterPosition].text = p0.toString()
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })
                view.answerValue.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        answers[view.adapterPosition].explanation = p0.toString()
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })
            }

            override fun getItemCount(): Int {
                return answers.size
            }

            inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
                val answerText = view.findViewById<TextView>(R.id.answer_text)!!
                val answerValue = view.findViewById<TextView>(R.id.answer_value)!!
            }
        }

        inner class AnswersWithScoreAdapter(val answers: ArrayList<ScoreTest.Question.Answer>) : RecyclerView.Adapter<AnswersWithScoreAdapter.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
                return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_answer_with_value, parent, false))
            }

            override fun onBindViewHolder(view: ViewHolder, position: Int) {
                view.answerText.text = answers[position].text
                view.answerValue.text = answers[position].score.toString()
                view.answerText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        answers[view.adapterPosition].text = p0.toString()
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })
                view.answerValue.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        val inputText: String = p0.toString()
                        val intInput: Float? = inputText.toFloatOrNull()
                        when {
                            inputText == "" -> return
                            intInput == null -> {
                                Toast.makeText(view.context,"Value must be a number!", Toast.LENGTH_SHORT).show()
                                return
                            }
                            else -> answers[view.adapterPosition].score = intInput
                        }
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })
            }

            override fun getItemCount(): Int {
                return answers.size
            }

            inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
                val answerText = view.findViewById<TextView>(R.id.answer_text)!!
                val answerValue = view.findViewById<TextView>(R.id.answer_value)!!
                val context = view.context!!
            }
        }

        inner class AnswersWithConnectionAdapter(val answers: ArrayList<NeuroTest.Question.Answer>) : RecyclerView.Adapter<AnswersWithConnectionAdapter.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
                return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_answer_with_connection, parent, false))
            }

            override fun onBindViewHolder(view: ViewHolder, position: Int) {
                view.answerText.text = answers[position].text
                view.connectionList.layoutManager = LinearLayoutManager(view.ctx)
                view.connectionList.adapter = ConnectionAdapter(answers[position].connections)
                view.answerText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        answers[view.adapterPosition].text = p0.toString()
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })
            }

            override fun getItemCount(): Int {
                return answers.size
            }

            inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
                val answerText = view.findViewById<TextView>(R.id.answer_text)!!
                val connectionList = view.findViewById<RecyclerView>(R.id.connection_list)!!
                val ctx = view.context!!
            }

            inner class ConnectionAdapter(val connections: ArrayList<NeuroTest.Connection>) : RecyclerView.Adapter<ConnectionAdapter.ViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
                    return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_answer_with_connection_connection, parent, false))
                }


                override fun onBindViewHolder(view: ViewHolder, position: Int) {
                    view.resultText.text = connections[position].result.text
                    view.answerWeight.text = connections[position].weight.toString()
                    view.answerWeight.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(p0: Editable?) {
                            val inputText: String = p0.toString()
                            val intInput: Float? = inputText.toFloatOrNull()
                            when {
                                inputText == "" -> return
                                intInput == null -> {
                                    Toast.makeText(view.context,"Value must be a number!", Toast.LENGTH_SHORT).show()
                                    return
                                }
                                else -> connections[view.adapterPosition].weight = intInput
                            }
                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        }
                    })
                }

                override fun getItemCount(): Int {
                    return connections.size
                }

                inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
                    val resultText = view.findViewById<TextView>(R.id.result_text)!!
                    val answerWeight = view.findViewById<TextView>(R.id.weight)!!
                    val context = view.context!!
                }
            }
        }
    }
}

