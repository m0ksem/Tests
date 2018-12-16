package com.example.m0ksem.imprtest.TestView

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.m0ksem.imprtest.*
import com.example.m0ksem.imprtest.Results.ManyStringResult
import com.example.m0ksem.imprtest.Results.OneStringResult


@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
class TestViewActivity : AppCompatActivity() {
    companion object {
        lateinit var test: Test
    }

    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var list: RecyclerView

    lateinit var button: ConstraintLayout
    private var buttonShowed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_view)
        test = intent.getSerializableExtra("test") as Test
        findViewById<TextView>(R.id.test_name).text = test.name
        findViewById<TextView>(R.id.view_test_questions_count).text = test.questions.size.toString()
        findViewById<TextView>(R.id.view_test_author).text = test.author
        adapter = AdapterAnswers(test.questions) as RecyclerView.Adapter<RecyclerView.ViewHolder>
        list = this.findViewById(R.id.questions_list)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        button = this.findViewById(R.id.nav_bar)!!
        button.alpha = 0.0F
        button.isClickable = false
        if (Build.VERSION.SDK_INT >= 21) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        val header = findViewById<LinearLayout>(R.id.header)
        header.setPadding(header.paddingLeft, (50 * resources.displayMetrics.density).toInt(), header.paddingRight, header.paddingBottom)
    }

    fun back(view: View){
        finish()
    }

    fun getResult(view: View) {
        if (test.type == "answers_with_score") {
            val result: String
            val a = getUserAnswersScores() ?: return
            var sum = 0f
            for (i in a) {
                sum += i
            }
            result = (test as ScoreTest).getResult(sum)
            val intent = Intent(this, OneStringResult::class.java)
            intent.putExtra("result", result)
            startActivityForResult(intent, 1)
        } else if (test.type == "answers_with_string") {
            val answers: ArrayList<String> = ArrayList()
            for (i: Int in 0 until adapter.itemCount) {
                val q: AdapterAnswers.ViewHolder = list.findViewHolderForAdapterPosition(i)!! as AdapterAnswers.ViewHolder
                val a: AdapterAnswers.ViewAnswerAdapter = q.answers.adapter as AdapterAnswers.ViewAnswerAdapter
                if (a.selectedAnswer == -1) {
                }
                val ans = test.questions[i].answers[a.selectedAnswer] as StringTest.Question.Answer
                answers.add(ans.explanation)
            }
            val array = arrayOfNulls<String>(answers.size)
            answers.toArray(array)
            val intent = Intent(this, ManyStringResult::class.java)
            intent.putExtra("results", array)
            startActivityForResult(intent, 1)
        } else if (test.type == "answers_with_connection") {
            getUserAnswersNeuro()
            val results: ArrayList<String> = ArrayList()
            for (r in test.results) {
                val result: NeuroTest.Result = r as NeuroTest.Result
                if (result.min <= result.score && result.max > result.score) results.add(result.text)
                result.score = 0f
            }
            val array = arrayOfNulls<String>(results.size)
            results.toArray(array)
            val intent = Intent(this, ManyStringResult::class.java)
            intent.putExtra("results", array)
            startActivityForResult(intent, 1)
        }
    }

    private fun getUserAnswersScores() : ArrayList<Float>? {
        val scores: ArrayList<Float> = ArrayList()
        for (i: Int in 0 until adapter.itemCount) {
            val q: AdapterAnswers.ViewHolder = list.findViewHolderForAdapterPosition(i)!! as AdapterAnswers.ViewHolder
            val a: AdapterAnswers.ViewAnswerAdapter = q.answers.adapter as AdapterAnswers.ViewAnswerAdapter
            if (a.selectedAnswer == -1) {
                return null
            }
            val ans = test.questions[i].answers[a.selectedAnswer] as ScoreTest.Question.Answer
            scores.add(ans.score)
        }
        return scores
    }

    private fun getUserAnswersNeuro() : ArrayList<Float>? {
        val scores: ArrayList<Float> = ArrayList()
        for (i: Int in 0 until adapter.itemCount) {
            val q: AdapterAnswers.ViewHolder = list.findViewHolderForAdapterPosition(i)!! as AdapterAnswers.ViewHolder
            val a: AdapterAnswers.ViewAnswerAdapter = q.answers.adapter as AdapterAnswers.ViewAnswerAdapter
            if (a.selectedAnswer == -1) {
                return null
            }
            val answer = test.questions[i].answers[a.selectedAnswer] as NeuroTest.Question.Answer
            for (connection in answer.connections) {
                (test.results[connection.resultPosition] as NeuroTest.Result).score += connection.weight
            }
        }
        return scores
    }


    private fun checkTestPassed() {
        if (!buttonShowed) {
            for (i: Int in 0 until adapter.itemCount) {
                val q: AdapterAnswers.ViewHolder = list.findViewHolderForAdapterPosition(i)!! as AdapterAnswers.ViewHolder
                val a: AdapterAnswers.ViewAnswerAdapter = q.answers.adapter as AdapterAnswers.ViewAnswerAdapter
                if (a.selectedAnswer == -1) {
                    return
                }
            }
            button.alpha = 1.0f
            button.isClickable = true
            button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.open_activity_button_up))
            buttonShowed = true
        }
    }

    inner class AdapterAnswers(private val questions: ArrayList<Test.Question>) : RecyclerView.Adapter<AdapterAnswers.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_test_question, parent, false))
        }

        override fun onBindViewHolder(view: ViewHolder, position: Int) {
            view.questionText.text = questions[position].text
            view.answers.layoutManager = LinearLayoutManager(view.ctx)
            view.answers.adapter = ViewAnswerAdapter(questions[position].answers)
        }

        override fun getItemCount(): Int {
            return questions.size
        }

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val questionText = view.findViewById<TextView>(R.id.view_test_question)!!
            val answers = view.findViewById<RecyclerView>(R.id.view_test_answer)!!
            val ctx = view.context!!
        }

        inner class ViewAnswerAdapter(private val answers: ArrayList<Test.Question.Answer>) : RecyclerView.Adapter<ViewAnswerAdapter.ViewHolder>() {
            var selectedAnswer: Int = -1

            override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
                return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_test_answer, parent, false))
            }

            override fun onBindViewHolder(view: ViewHolder, position: Int) {
                view.answer.text = answers[position].text
                view.answer.isChecked = false
                if (selectedAnswer == position) {
                    view.answer.isChecked = true
                }
            }

            override fun getItemCount(): Int {
                return answers.size
            }

            inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
                val answer = view.findViewById<CheckBox>(R.id.view_test_answer)!!

                init {
                    val clickListener = View.OnClickListener {
                        selectedAnswer = adapterPosition
                        checkTestPassed()
                        notifyDataSetChanged()
                    }
                    answer.setOnClickListener(clickListener)
                    view.setOnClickListener(clickListener)
                }
            }
        }
    }
}
