package com.example.m0ksem.imprtest.TestCreate

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.example.m0ksem.imprtest.R
import com.example.m0ksem.imprtest.Test
import com.example.m0ksem.imprtest.TestCreate.ChooseTags.ChooseTags
import com.example.m0ksem.imprtest.TestCreate.ChooseType.ChooseType
import com.example.m0ksem.imprtest.TestCreate.SetQuestions.SetQuestions
import kotlinx.android.synthetic.main.activity_tests_list.*
import org.w3c.dom.Text
import java.io.Serializable

class TestCreate : AppCompatActivity() {

    var tags: ArrayList<String>? = null
    var type: String? = null
    lateinit var tips_view: TextView
    lateinit var tips: Array<String>
    var questions: ArrayList<ArrayList<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_create)
        tags = ArrayList()
        questions = ArrayList()
        tips_view = findViewById<TextView>(R.id.create_test_tips)
        tips = resources.getStringArray(R.array.create_test_under)
        tips_view.text = tips[0]
    }

    fun nameEnter(view: View) {
        tips_view.text = tips[1]
    }

    fun back(view: View){
        finish()
    }

    fun chooseType(view: View) {
        val intent = Intent(this, ChooseType::class.java)
        startActivityForResult(intent, 1)

    }

    fun setQuestions(view: View) {
        val intent = Intent(this, SetQuestions::class.java)
        intent.putExtra("questions", questions as Serializable)
        startActivityForResult(intent, 2)
    }

    fun chooseTags(view: View) {
        val intent = Intent(this, ChooseTags::class.java)
        intent.putExtra("tags", tags)
        startActivityForResult(intent, 3)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            return
        }
        if (requestCode == 1){
            type = data.getStringExtra("type")
            tips_view.text = tips[2]
        }
        if (requestCode == 2) {
            questions = data.getSerializableExtra("questions") as ArrayList<ArrayList<String>>
            tips_view.text = tips[3]
        }
        if (requestCode == 3) {
            tags = data.getStringArrayListExtra("tags")
            tips_view.text = tips[4]
            Log.d("Debug", "Tags returned")
        }
    }

    fun save(view: View) {
        if (type == null) {
            return
        }
        else if (questions == null) {
            return
        }
        else if (tags == null) {
            return
        }
        else {
            val test_name: String = findViewById<EditText>(R.id.create_test_name).text.toString()
            val user_name: String = intent.getStringExtra("username")
            val test: Test = Test(test_name, user_name)
            test.questions = questions!!
            test.tags = tags!!
            test.type = type!!
            intent.putExtra("test", test as Serializable)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
