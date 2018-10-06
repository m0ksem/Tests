@file:Suppress("UNUSED_PARAMETER")

package com.example.m0ksem.imprtest.TestList

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.m0ksem.imprtest.Login
import com.example.m0ksem.imprtest.R
import com.example.m0ksem.imprtest.ScoreTest
import com.example.m0ksem.imprtest.Test
import com.example.m0ksem.imprtest.TestCreate.TestCreate
import com.example.m0ksem.imprtest.TestView.TestViewActivity
import kotlinx.android.synthetic.main.activity_tests_list.*
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class TestsList : AppCompatActivity() {
    private var username: String? = null

    private lateinit var accountPreferences: SharedPreferences

    private lateinit var adapter: TestAdapter

    override fun onResume() {
        super.onResume()
        helloUser()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests_list)
        accountPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)
        checkLogin()
        helloUser()
        val list = this.findViewById<RecyclerView>(R.id.tests_list)
        list.layoutManager = LinearLayoutManager(this)
        adapter = TestAdapter(loadData())
        list.adapter = adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.navigationBarColor = resources.getColor(R.color.colorBackground)
            window.statusBarColor = resources.getColor(R.color.colorBackground)
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun logout(view: View) {
        accountPreferences.edit().putString("login", null)
        accountPreferences.edit().apply()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    fun createTest(view: View) {
        val intent = Intent(this, TestCreate::class.java)
        intent.putExtra("username", username)
        // startActivity(intent)
        startActivityForResult(intent, 1)
    }

    private fun checkLogin() {
        username = accountPreferences.getString("login", null)
        if (username == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        user_name.text = username.toString()
    }

    @SuppressLint("SetTextI18n")
    fun helloUser() {
        val time: Int = Date().hours
        val hello: String = when (time) {
            in 0..4 -> resources.getString(R.string.hello_user_night)
            in 5..9 -> resources.getString(R.string.hello_user_morning)
            in 10..15 -> resources.getString(R.string.hello_user_afternoon)
            in 16..21 -> resources.getString(R.string.hello_user_evening)
            else -> resources.getString(R.string.hello_user_night)
        }

        hello_user.text = "$hello, "
    }

    private fun loadData(): ArrayList<Test> {
        // TODO Сделать подгрузку этого всего из бд с сервера и компановку полученных данных в виде массивов
        val array: ArrayList<Test> = ArrayList()
        val t: Test = ScoreTest("Психологический тест", "ImproveTests")
        t.type = "answers_with_score"
        t.tags = arrayListOf("tag", "tag2")
        val q = ScoreTest.Question()
        q.text = "Как вы себя чувствуете?"
        val a1 =  ScoreTest.Question.Answer("Нормально", 1)
        val a2 =  ScoreTest.Question.Answer("Очень плохо", 0)
        q.answers = arrayListOf(a1, a2)
        var r1 = ScoreTest.Result("Всё хорошо, проходите тесты дальше, если их нет - создайте", 1, 2)
        var r2 = ScoreTest.Result("Всё очень плохо, Вам нужно проходить наши тесты! Если их нет - создайте", 0, 1)
        t.results = arrayListOf(r1, r2)
        t.questions = arrayListOf(q)
        array.add(t)
        return array
    }

    fun openTest(view: View) {
        val test: Test = view.tag as Test
        val intent = Intent(this, TestViewActivity::class.java)
        intent.putExtra("test", test)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            return
        }
        if (requestCode == 1) {
            val test: Test = data.getSerializableExtra("test") as Test
            adapter.add(test)
            Toast.makeText(this, "Хорошоая работа, ты сделал свой первый тест!", Toast.LENGTH_LONG).show()
        }
    }
}
