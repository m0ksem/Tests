@file:Suppress("UNUSED_PARAMETER")

package com.example.m0ksem.imprtest.TestCreate

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.m0ksem.imprtest.*
import com.example.m0ksem.imprtest.TestCreate.ChooseTags.ChooseTags
import com.example.m0ksem.imprtest.TestCreate.ChooseType.ChooseType
import com.example.m0ksem.imprtest.TestCreate.SetQuestions.SetQuestions
import com.example.m0ksem.imprtest.TestCreate.SetResults.SetResults
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
class TestCreate : AppCompatActivity() {

    private var tags: ArrayList<String>? = null
    var type: String? = null
    private lateinit var tipsView: TextView
    private lateinit var tips: Array<String>
    private var questions: ArrayList<Test.Question>? = null
    private var results: ArrayList<Test.Result>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_create)
        tags = ArrayList()
        questions = ArrayList()
        tipsView = findViewById(R.id.create_test_tips)
        tips = resources.getStringArray(R.array.create_test_under)
        tipsView.text = tips[0]
    }

    fun nameEnter(view: View) {
        tipsView.text = tips[1]
    }

    fun back(view: View){
        finish()
    }

    fun chooseType(view: View) {
        val intent = Intent(this, ChooseType::class.java)
        intent.putExtra("header_height", this.findViewById<LinearLayout>(R.id.header)!!.height)
        startActivityForResult(intent, 1)
        overridePendingTransition(R.anim.open_addition_setting_enter, R.anim.open_addition_setting_exit)
    }

    fun setQuestions(view: View) {
        if (type != null) {
            val intent = Intent(this, SetQuestions::class.java)
            intent.putExtra("header_height", this.findViewById<LinearLayout>(R.id.header)!!.height)
            if (type == "answers_with_connection" && results != null && questions != null) {
                val defaultConnections: ArrayList<NeuroTest.Connection> = ArrayList()
                for (i in 0 until results!!.size) {
                    defaultConnections.add(NeuroTest.Connection(results!![i], 0f))
                }
                // TODO() Тут как бы результаты совсем другие обьекты
                intent.putExtra("default_connections", defaultConnections as Serializable)
                for (question in questions!!) {
                    for (answer in question.answers) {
                        for (connection in (answer as NeuroTest.Question.Answer).connections) {
                            if (results!!.indexOf(connection.result) != -1) {
                                answer.connections.remove(connection)
                            }
                        }
                        if (results!!.size > answer.connections.size) {
                            val newElementsCount = results!!.size - answer.connections.size
                            for (i in 0 until newElementsCount) {
                                answer.connections.add(NeuroTest.Connection(results!![results!!.size - 1 - i], 0f))
                            }
                        }
                    }
                }
            }
            intent.putExtra("questions", questions as Serializable)
            intent.putExtra("type", type)
            startActivityForResult(intent, 2)
            overridePendingTransition(R.anim.open_addition_setting_enter, R.anim.open_addition_setting_exit)
        }
        else {
            Toast.makeText(this, R.string.create_test_at_first_select_type, Toast.LENGTH_LONG).show()
        }
    }

    fun chooseTags(view: View) {
        val intent = Intent(this, ChooseTags::class.java)
        intent.putExtra("header_height", this.findViewById<LinearLayout>(R.id.header)!!.height)
        intent.putExtra("tags", tags)
        startActivityForResult(intent, 3)
        overridePendingTransition(R.anim.open_addition_setting_enter, R.anim.open_addition_setting_exit)
    }

    fun setResults(view: View) {
        when (type) {
            "answers_with_score" -> {
                val intent = Intent(this, SetResults::class.java)
                intent.putExtra("type", type)
                intent.putExtra("results", results)
                intent.putExtra("header_height", this.findViewById<LinearLayout>(R.id.header)!!.height)
                startActivityForResult(intent, 4)
            }
            "answers_with_connection" -> {
                val intent = Intent(this, SetResults::class.java)
                intent.putExtra("type", type)
                intent.putExtra("results", results)
                intent.putExtra("header_height", this.findViewById<LinearLayout>(R.id.header)!!.height)
                startActivityForResult(intent, 4)
            }
            "answers_with_string" ->
                Toast.makeText(this, R.string.create_test_you_no_need_results, Toast.LENGTH_LONG).show()
            else -> Toast.makeText(this, R.string.create_test_at_first_select_type, Toast.LENGTH_LONG).show()
        }
        overridePendingTransition(R.anim.open_addition_setting_enter, R.anim.open_addition_setting_exit)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            return
        }
        if (requestCode == 1){
            type = data.getStringExtra("type")
            when (type) {
                "answers_with_score" -> findViewById<TextView>(R.id.selected_type).text = resources.getString(R.string.create_test_selected_type)+ " " + resources.getString(R.string.create_test_type_score)
                "answers_with_string" -> findViewById<TextView>(R.id.selected_type).text = resources.getString(R.string.create_test_selected_type) + " " + resources.getString(R.string.create_test_type_string)
                "answers_with_connection" -> findViewById<TextView>(R.id.selected_type).text = resources.getString(R.string.create_test_selected_type) + " " + resources.getString(R.string.create_test_type_neuro)
            }
            tipsView.text = tips[2]
        }
        if (requestCode == 2) {
            questions = data.getSerializableExtra("questions") as ArrayList<Test.Question>
            tipsView.text = tips[3]
        }
        if (requestCode == 3) {
            tags = data.getStringArrayListExtra("tags")
            tipsView.text = tips[4]
        }
        if (requestCode == 4) {
            results = data.getStringArrayListExtra("results") as ArrayList<Test.Result>
            Log.d("Result imported", results.toString())
            tipsView.text = tips[4]
        }
    }

    fun save(view: View) {
        if (type == null || questions == null || tags == null) {
            Toast.makeText(view.context, "End your test creation", Toast.LENGTH_SHORT).show()
            return
        }
        else {
            val testName: String = (findViewById<View>(R.id.create_test_name) as EditText).text.toString()
            val userName: String = intent.getStringExtra("username")
            val test = when (type) {
                "answers_with_score" -> ScoreTest(testName, userName)
                "answers_with_string" -> StringTest(testName, userName)
                "answers_with_connection" -> StringTest(testName, userName)
                else -> null
            }
            when (test) {
                is ScoreTest -> {
                    test.questions = questions!!
                    test.tags = tags!!
                    test.type = type!!
                    test.results = results!!
                }
                is StringTest -> {
                    test.questions = questions!!
                    test.tags = tags!!
                    test.type = type!!
                    test.results = ArrayList()
                }
                is NeuroTest -> {
                    test.questions = questions!!
                    test.tags = tags!!
                    test.type = type!!
                    test.results = results!!
                }
            }
            intent.putExtra("test", test as Serializable)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
