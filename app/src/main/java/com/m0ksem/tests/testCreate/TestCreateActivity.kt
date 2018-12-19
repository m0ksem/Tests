@file:Suppress("UNUSED_PARAMETER")

package com.m0ksem.tests.testCreate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.m0ksem.tests.*
import java.io.Serializable
import android.support.v7.app.AlertDialog
import android.view.WindowManager

@Suppress("UNCHECKED_CAST")
class TestCreateActivity : AppCompatActivity() {

    private var tags: ArrayList<String>? = null
    var type: String? = null
    private lateinit var tipsView: TextView
    private lateinit var tips: Array<String>
    private var questions: ArrayList<Test.Question>? = null
    private var results: ArrayList<Test.Result>? = null
    private lateinit var userName: String
    private var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_create)
        tipsView = findViewById(R.id.create_test_tips)
        tips = resources.getStringArray(R.array.create_test_under)
        tipsView.text = tips[0]
        if (Build.VERSION.SDK_INT >= 21) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorGradientBackgroundBottom)
        }
        val test: Test? = intent.getSerializableExtra("test") as Test?
        if (test != null) {
            (findViewById<View>(R.id.create_test_name) as EditText).setText(test.name)
            type = test.type
            tags = test.tags
            questions = test.questions
            results = test.results
            userName = test.author
            id = test.id
        } else {
            userName = intent.getStringExtra("username")
        }
    }

    fun nameEnter(view: View) {
        tipsView.text = tips[1]
    }

    fun back(view: View){
        onBackPressed()
    }

    override fun onBackPressed() {
        val testName: String = (findViewById<View>(R.id.create_test_name) as EditText).text.toString()
        if (testName == "" && type == null && questions == null && tags == null) {
            super.onBackPressed()
            return
        }

        val builder = AlertDialog.Builder(this@TestCreateActivity)

        builder.setTitle(resources.getString(R.string.create_test_exit_creation_question))
        builder.setMessage(resources.getString(R.string.create_test_exit_creation_warning))
        builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            super.onBackPressed()
        }
        builder.setNeutralButton(resources.getString(R.string.cancel)) { _,_ -> }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun chooseType(view: View) {
        if (type == null) {
            val intent = Intent(this, SetTypeActivity::class.java)
            intent.putExtra("header_height", this.findViewById<LinearLayout>(R.id.header)!!.height)
            startActivityForResult(intent, 1)
            overridePendingTransition(R.anim.open_addition_setting_enter, R.anim.open_addition_setting_exit)
        } else {
            val builder = AlertDialog.Builder(this@TestCreateActivity)

            builder.setTitle(resources.getString(R.string.create_test_change_type_question))
            builder.setMessage(resources.getString(R.string.create_test_change_type_warning))
            builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                questions = null
                results = null
                val intent = Intent(this, SetTypeActivity::class.java)
                intent.putExtra("header_height", this.findViewById<LinearLayout>(R.id.header)!!.height)
                startActivityForResult(intent, 1)
                overridePendingTransition(R.anim.open_addition_setting_enter, R.anim.open_addition_setting_exit)
            }
            builder.setNeutralButton(resources.getString(R.string.cancel)) { _,_ -> }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    fun setQuestions(view: View) {
        if (questions == null) {
            questions = ArrayList()
        }
        if (type != null) {
            val intent = Intent(this, SetQuestionsActivity::class.java)
            intent.putExtra("header_height", this.findViewById<LinearLayout>(R.id.header)!!.height)
            if (type == "answers_with_connection" && results != null && questions != null) {
                intent.putExtra("results", results as Serializable)
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
        if (tags == null) {
            tags = ArrayList()
        }
        val intent = Intent(this, SetTagsActivity::class.java)
        intent.putExtra("header_height", this.findViewById<LinearLayout>(R.id.header)!!.height)
        intent.putExtra("tags", tags)
        startActivityForResult(intent, 3)
        overridePendingTransition(R.anim.open_addition_setting_enter, R.anim.open_addition_setting_exit)
    }

    private fun updateResultsId() {
        results!!.forEachIndexed {index, el ->
            el.id = index
        }
    }

    fun setResults(view: View) {
        if (results == null) {
            results = ArrayList()
        }
        when (type) {
            "answers_with_score" -> {
                val intent = Intent(this, SetResultsActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("results", results)
                intent.putExtra("header_height", this.findViewById<LinearLayout>(R.id.header)!!.height)
                startActivityForResult(intent, 4)
            }
            "answers_with_connection" -> {
                val intent = Intent(this, SetResultsActivity::class.java)
                intent.putExtra("type", type)
                updateResultsId()
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
            findViewById<TextView>(R.id.select_type).text = resources.getString(R.string.create_test_selected_type)
            when (type) {
                "answers_with_score" -> {
                    findViewById<TextView>(R.id.selected_test_type).text = resources.getString(R.string.create_test_type_score).toLowerCase()
                }
                "answers_with_string" -> {
                    findViewById<TextView>(R.id.selected_test_type).text = resources.getString(R.string.create_test_type_string).toLowerCase()
                }
                "answers_with_connection" -> {
                    findViewById<TextView>(R.id.selected_test_type).text = resources.getString(R.string.create_test_type_neuro).toLowerCase()
                }
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
            if (type == "answers_with_connection") {
                updateResultsInAnswers(data.getStringArrayListExtra("deleted") as ArrayList<Int>)
            }
            tipsView.text = tips[4]
        }
    }

    private fun updateResultsInAnswers(deletedAnswers: ArrayList<Int>) {
        if (questions != null) {
            for (question in questions!!) {
                for (answer in question.answers as ArrayList<NeuroTest.Question.Answer>) {
                    for (k in deletedAnswers) {
                        answer.connections.removeAt(k)
                    }
                }
            }
            for (i in 0 until results!!.size) {
                for (question in questions!!) {
                    for (answer in question.answers as ArrayList<NeuroTest.Question.Answer>) {
                        for (j in answer.connections.size until results!!.size) {
                            answer.connections.add(NeuroTest.Connection(0, 0f))
                        }
                        answer.connections[i].resultPosition = i
                    }
                }
            }
        }
    }

    fun save(view: View) {
        val testName: String = (findViewById<View>(R.id.create_test_name) as EditText).text.toString()
        if (testName == "" || type == null || questions == null || tags == null) {
            Toast.makeText(view.context, "End your test creation", Toast.LENGTH_SHORT).show()
            return
        }
        else {

            val test = when (type) {
                "answers_with_score" -> ScoreTest(testName, userName)
                "answers_with_string" -> StringTest(testName, userName)
                "answers_with_connection" -> NeuroTest(testName, userName)
                else -> null
            }
            when (test) {
                is ScoreTest -> {
                    test.questions = questions!!
                    test.tags = tags!!
                    test.type = type!!
                    test.results = results!!
                    test.id = id
                }
                is StringTest -> {
                    test.questions = questions!!
                    test.tags = tags!!
                    test.type = type!!
                    test.results = ArrayList()
                    test.id = id
                }
                is NeuroTest -> {
                    test.questions = questions!!
                    test.tags = tags!!
                    test.type = type!!
                    test.results = results!!
                    test.id = id
                }
            }

            intent.putExtra("test", test as Serializable)
            intent.putExtra("position", intent.getIntExtra("position", -1))
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
