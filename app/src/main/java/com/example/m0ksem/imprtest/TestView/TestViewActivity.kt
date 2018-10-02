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
import android.widget.Toast
import com.example.m0ksem.imprtest.*


@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
class TestViewActivity : AppCompatActivity() {
    companion object {
        lateinit var test: Test
    }

    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_view)
        test = intent.getSerializableExtra("test") as Test
        findViewById<TextView>(R.id.test_name).text = test.name
        findViewById<TextView>(R.id.view_test_questions_count).text = test.questions.size.toString()
        findViewById<TextView>(R.id.view_test_author).text = test.author
        if (test is ScoreTest) {
            adapter = AdapterAnswersWithScore(test.questions) as RecyclerView.Adapter<RecyclerView.ViewHolder>
        }

        list = this.findViewById(R.id.questions_list)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
    }

//    fun back(view: View){
//        finish()
//    }

    fun getResult(view: View) {
        var result = ""
        if (test is ScoreTest) {
            val a = getUserAnswersScores()
            var sum = 0
            for (i in a) {
                sum += i
            }
            result = (test as ScoreTest).getResult(sum)
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show()
    }

    private fun getUserAnswersScores() : ArrayList<Int> {
        val scores: ArrayList<Int> = ArrayList()
        for (i: Int in 0 until adapter.itemCount) {
            val q: AdapterAnswersWithScore.ViewHolder = list.findViewHolderForAdapterPosition(i)!! as AdapterAnswersWithScore.ViewHolder
            val a: AdapterAnswersWithScore.ViewAnswerAdapter = q.answers.adapter as AdapterAnswersWithScore.ViewAnswerAdapter
            val ans = test.questions[i].answers[a.selectedAnswer] as ScoreTest.Question.Answer
            scores.add(ans.score)
        }
        return scores
    }


    class AdapterAnswersWithScore(private val questions: ArrayList<Test.Question>) : RecyclerView.Adapter<AdapterAnswersWithScore.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_test_question, parent, false))
        }

        override fun onBindViewHolder(view: ViewHolder, position: Int) {
            view.questionText.text = questions[position].text
            view.answers.layoutManager = LinearLayoutManager(view.ctx)
            view.answers.adapter = ViewAnswerAdapter(questions[position].answers as ArrayList<ScoreTest.Question.Answer>)
        }

        override fun getItemCount(): Int {
            return questions.size
        }

        class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val questionText = view.findViewById<TextView>(R.id.view_test_question)!!
            val answers = view.findViewById<RecyclerView>(R.id.view_test_answer)!!
            val ctx = view.context!!
        }

        class ViewAnswerAdapter(private val answers: ArrayList<ScoreTest.Question.Answer>) : RecyclerView.Adapter<ViewAnswerAdapter.ViewHolder>() {
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
                        notifyDataSetChanged()
                    }
                    answer.setOnClickListener(clickListener)
                    view.setOnClickListener(clickListener)
                }
            }
        }
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
