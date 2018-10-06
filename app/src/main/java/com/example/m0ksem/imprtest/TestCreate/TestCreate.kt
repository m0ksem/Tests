@file:Suppress("UNUSED_PARAMETER")

package com.example.m0ksem.imprtest.TestCreate

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.m0ksem.imprtest.R
import com.example.m0ksem.imprtest.ScoreTest
import com.example.m0ksem.imprtest.Test
import com.example.m0ksem.imprtest.TestCreate.ChooseTags.ChooseTags
import com.example.m0ksem.imprtest.TestCreate.ChooseType.ChooseType
import com.example.m0ksem.imprtest.TestCreate.SetQuestions.SetQuestions
import com.example.m0ksem.imprtest.TestCreate.SetResults.SetResults
import java.io.Serializable

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

//    fun back(view: View){
//        finish()
//    }

    fun chooseType(view: View) {
        val intent = Intent(this, ChooseType::class.java)
        startActivityForResult(intent, 1)

    }

    fun setQuestions(view: View) {
        // TODO() Сделать так чтобы активити нельзя было вызвать если тип не выбран.
        val intent = Intent(this, SetQuestions::class.java)
        intent.putExtra("questions", questions as Serializable)
        startActivityForResult(intent, 2)
    }

    fun chooseTags(view: View) {
        val intent = Intent(this, ChooseTags::class.java)
        intent.putExtra("tags", tags)
        startActivityForResult(intent, 3)
    }

    fun setResults(view: View) {
        if (type == "answers_with_score") {
            val intent = Intent(this, SetResults::class.java)
            intent.putExtra("type", "answer_with_score")
            intent.putExtra("results", results)
            startActivityForResult(intent, 4)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            return
        }
        if (requestCode == 1){
            type = data.getStringExtra("type")
            tipsView.text = tips[2]
        }
        if (requestCode == 2) {
            @Suppress("UNCHECKED_CAST")
            questions = data.getSerializableExtra("questions") as ArrayList<Test.Question>
            tipsView.text = tips[3]
        }
        if (requestCode == 3) {
            tags = data.getStringArrayListExtra("tags")
            tipsView.text = tips[4]
            Log.d("Debug", "Tags returned")
        }
        if (requestCode == 4) {
            results = data.getStringArrayListExtra("results") as ArrayList<Test.Result>
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
                else -> Test(testName, userName)
            }
            if (test is ScoreTest) {
                test.questions = questions!!
                test.tags = tags!!
                test.type = type!!
                test.results = results!!
            }
            intent.putExtra("test", test as Serializable)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
