@file:Suppress("UNUSED_PARAMETER", "UNCHECKED_CAST")

package com.m0ksem.tests.testList

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.m0ksem.tests.testCreate.TestCreateActivity
import com.m0ksem.tests.testView.TestViewActivity
import kotlinx.android.synthetic.main.activity_tests_list.*
import java.util.*
import kotlin.collections.ArrayList
import android.content.ContentValues
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.m0ksem.tests.*
import com.m0ksem.tests.login.LoginActivity
import org.json.JSONArray
import org.json.JSONObject
import com.m0ksem.tests.Server as server


@Suppress("DEPRECATION")
class TestsListActivity() : AppCompatActivity(), Parcelable {
    var username: String? = null

    private lateinit var accountPreferences: SharedPreferences

    private lateinit var adapter: TestAdapter

    private lateinit var localDataBase: LocalDataBase

    private val serverDataBase: ServerDataBase = ServerDataBase()

    var offline: Boolean = false

    constructor(parcel: Parcel) : this() {
        username = parcel.readString()
    }

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests_list)
        accountPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)
        offline = accountPreferences.getBoolean("offline", false)
        checkLogin()
        helloUser()
        createTestList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.navigationBarColor = resources.getColor(R.color.colorBackground)
            window.statusBarColor = resources.getColor(R.color.colorBackground)
        }
        val button = this.findViewById<ConstraintLayout>(R.id.nav_bar)!!
        button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.open_activity_button_up))
    }

    private fun createTestList() {
        val list:RecyclerView = this.findViewById(R.id.tests_list)
        list.setHasFixedSize(false)
        list.layoutManager = LinearLayoutManager(this)
        adapter = TestAdapter(ServerDataBase().getAllTests(), this)
        list.adapter = adapter
        list.setHasFixedSize(true)
    }

    fun onLogoutClick(view: View) {
        logout()
    }

    fun logout() {
        accountPreferences.edit().remove("login").apply()
        accountPreferences.edit().apply()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun createTest(view: View) {
        val intent = Intent(this, TestCreateActivity::class.java)
        intent.putExtra("username", username)
        startActivityForResult(intent, 1)
    }

    private fun checkLogin() {
        username = accountPreferences.getString("login", null)
        if (username == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
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
            serverDataBase.addTest(test)
            test.id = "offline"
            adapter.add(test)
        }
        if (requestCode == 2) {
            val test: Test = data.getSerializableExtra("test") as Test
            serverDataBase.editTest(test)
            val pos = data.getIntExtra("position", -1)
            adapter.edit(test, pos)
        }
    }

    inner class ServerDataBase {
        fun getAllTests(): ArrayList<Test> {
            val queue = Volley.newRequestQueue(this@TestsListActivity)
            val tests = ArrayList<Test>()
            val request = JsonArrayRequest(Request.Method.GET, server.getAllTestURL, null, Response.Listener<JSONArray> { response ->
                for (i in 0 until response.length()) {
                    val test = parceJSONtoTest(response.getJSONObject(i))
                    if (test != null) tests.add(test)
                }
            }, Response.ErrorListener {
                offline = true
            } )

            request.retryPolicy = DefaultRetryPolicy(3000, 0, 1f)

            queue.add(request)
            return tests
        }

        private fun parceJSONtoTest(json: JSONObject): Test? {
            val testType = json.getString("type")
            when (testType) {
                "answers_with_score" -> {
                    val test = ScoreTest(json.getString("name"), json.getString("author"))
                    test.type = testType
                    test.id = json.getString("_id")
                    val jsonQuestions = json.getJSONArray("questions")
                    val questions: ArrayList<Test.Question> = ArrayList()
                    for (questionIndex in 0 until jsonQuestions.length()) {
                        val jsonQuestion = jsonQuestions.getJSONObject(questionIndex)
                        val question = ScoreTest.Question()
                        question.text = jsonQuestion.getString("text")
                        val jsonAnswers = jsonQuestion.getJSONArray("answers")
                        for (answerIndex in 0 until jsonAnswers.length()) {
                            val jsonAnswer = jsonAnswers.getJSONObject(answerIndex)
                            val answer = ScoreTest.Question.Answer(jsonAnswer.getString("text"), jsonAnswer.getDouble("score").toFloat())
                            question.answers.add(answer)
                        }
                        questions.add(question)
                    }
                    test.questions = questions

                    val jsonResults = json.getJSONArray("results")
                    val results: ArrayList<Test.Result> = ArrayList()
                    for (resultIndex in 0 until jsonResults.length()) {
                        val jsonResult = jsonResults.getJSONObject(resultIndex)
                        val result = ScoreTest.Result(jsonResult.getString("text"), jsonResult.getDouble("min").toFloat(), jsonResult.getDouble("max").toFloat())
                        results.add(result)
                    }
                    test.results = results

                    val jsonTags = json.getJSONArray("tags")
                    val tags: ArrayList<String> = ArrayList()
                    for (resultIndex in 0 until jsonTags.length()) {
                        val tag = jsonTags.getString(resultIndex)
                        tags.add(tag)
                    }
                    test.tags = tags
                    return test
                }
                "answers_with_string" -> {
                    val test = StringTest(json.getString("name"), json.getString("author"))
                    test.type = testType
                    test.id = json.getString("_id")
                    val jsonQuestions = json.getJSONArray("questions")
                    val questions: ArrayList<Test.Question> = ArrayList()
                    for (questionIndex in 0 until jsonQuestions.length()) {
                        val jsonQuestion = jsonQuestions.getJSONObject(questionIndex)
                        val question = StringTest.Question()
                        question.text = jsonQuestion.getString("text")
                        val jsonAnswers = jsonQuestion.getJSONArray("answers")
                        for (answerIndex in 0 until jsonAnswers.length()) {
                            val jsonAnswer = jsonAnswers.getJSONObject(answerIndex)
                            val answer = StringTest.Question.Answer(jsonAnswer.getString("text"), jsonAnswer.getString("explanation"))
                            question.answers.add(answer)
                        }
                        questions.add(question)
                    }
                    test.questions = questions

                    val jsonTags = json.getJSONArray("tags")
                    val tags: ArrayList<String> = ArrayList()
                    for (resultIndex in 0 until jsonTags.length()) {
                        val tag = jsonTags.getString(resultIndex)
                        tags.add(tag)
                    }
                    test.tags = tags
                    return test
                }
                "answers_with_connection" -> {
                    val test = NeuroTest(json.getString("name"), json.getString("author"))
                    test.type = testType
                    test.id = json.getString("_id")
                    val jsonResults = json.getJSONArray("results")
                    val results: ArrayList<Test.Result> = ArrayList()
                    for (resultIndex in 0 until jsonResults.length()) {
                        val jsonResult = jsonResults.getJSONObject(resultIndex)
                        val result = NeuroTest.Result(jsonResult.getString("text"), 0f, jsonResult.getDouble("min").toFloat(), jsonResult.getDouble("max").toFloat())
                        results.add(result)
                    }
                    test.results = results

                    val jsonQuestions = json.getJSONArray("questions")
                    val questions: ArrayList<Test.Question> = ArrayList()
                    for (questionIndex in 0 until jsonQuestions.length()) {
                        val jsonQuestion = jsonQuestions.getJSONObject(questionIndex)
                        val question = NeuroTest.Question()
                        question.text = jsonQuestion.getString("text")
                        val jsonAnswers = jsonQuestion.getJSONArray("answers")
                        for (answerIndex in 0 until jsonAnswers.length()) {
                            val jsonAnswer = jsonAnswers.getJSONObject(answerIndex)
                            val jsonConnections = jsonAnswer.getJSONArray("connections")
                            val connections = ArrayList<NeuroTest.Connection>()
                            for (connectionIndex in 0 until jsonConnections.length()) {
                                connections.add(NeuroTest.Connection(connectionIndex, jsonConnections.getDouble(connectionIndex).toFloat()))
                            }
                            val answer = NeuroTest.Question.Answer(jsonAnswer.getString("text"), connections)
                            question.answers.add(answer)
                        }
                        questions.add(question)
                    }
                    test.questions = questions

                    val jsonTags = json.getJSONArray("tags")
                    val tags: ArrayList<String> = ArrayList()
                    for (resultIndex in 0 until jsonTags.length()) {
                        val tag = jsonTags.getString(resultIndex)
                        tags.add(tag)
                    }
                    test.tags = tags
                    return test
                }
            }
            return null
        }

        fun addTest(test: Test) {
            val queue = Volley.newRequestQueue(this@TestsListActivity)

            val request = JsonObjectRequest(Request.Method.POST, server.addTestURL, parseTestToJSON(test), Response.Listener<JSONObject> {
                localDataBase.deleteElement(test)
            }, Response.ErrorListener {
                Log.d("ERROR", it.toString())
                localDataBase.insert(test)
                Toast.makeText(this@TestsListActivity, resources.getString(R.string.test_add_error), Toast.LENGTH_SHORT).show()
                test.id = "offline"
                offline = true
            })

            request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            queue.add(request)
        }

        fun editTest(test: Test) {
            val queue = Volley.newRequestQueue(this@TestsListActivity)

            val request = JsonObjectRequest(Request.Method.POST, server.editTestURL, parseTestToJSON(test), Response.Listener<JSONObject> {

            }, Response.ErrorListener {
                Toast.makeText(this@TestsListActivity, resources.getString(R.string.test_edit_error), Toast.LENGTH_SHORT).show()
            })

            request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            queue.add(request)
        }

        fun deleteTest(id: String) {
            val queue = Volley.newRequestQueue(this@TestsListActivity)

            val request = JsonObjectRequest(Request.Method.POST, server.deleteTestURL, JSONObject().put("id", id), Response.Listener<JSONObject> {

            }, Response.ErrorListener {
                Toast.makeText(this@TestsListActivity, resources.getString(R.string.test_edit_error), Toast.LENGTH_SHORT).show()
            })

            request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            queue.add(request)
        }


        private fun parseTestToJSON(test: Test): JSONObject {
            when {
                test.type == "answers_with_score" -> {
                    return scoreTestToJSON(test as ScoreTest)
                }
                test.type == "answers_with_string" -> {
                    return stringTestToJSON(test as StringTest)
                }
                test.type == "answers_with_connection" -> {
                    return neuroTestToJSON(test as NeuroTest)
                }
            }
            return JSONObject()
        }

        private fun scoreTestToJSON(test: ScoreTest): JSONObject {
            val jsonObject = JSONObject()
            jsonObject.put("name", test.name)
            jsonObject.put("author", test.author)
            jsonObject.put("type", test.type)
            jsonObject.put("id", test.id)
            val questionsArray = JSONArray()
            for (q in (test.questions)) {
                val question = JSONObject()
                question.put("text", q.text)
                val answersArray = JSONArray()
                for (a in q.answers as ArrayList<ScoreTest.Question.Answer>) {
                    val answer = JSONObject()
                    answer.put("text", a.text)
                    answer.put("score", a.score)
                    answersArray.put(answer)
                }
                question.put("answers", answersArray)
                questionsArray.put(question)
            }
            jsonObject.put("questions",questionsArray)
            val tagsArray = JSONArray()
            for (t in test.tags) {
                tagsArray.put(t)
            }
            jsonObject.put("tags", tagsArray)
            val resultArray = JSONArray()
            for (r in test.results as ArrayList<ScoreTest.Result>) {
                val result = JSONObject()
                result.put("text", r.text)
                result.put("max", r.max)
                result.put("min", r.min)
                resultArray.put(result)
            }
            jsonObject.put("results", resultArray)
            return jsonObject
        }

        private fun stringTestToJSON(test: StringTest): JSONObject {
            val jsonObject = JSONObject()
            jsonObject.put("name", test.name)
            jsonObject.put("author", test.author)
            jsonObject.put("type", test.type)
            jsonObject.put("id", test.id)
            val questionsArray = JSONArray()
            for (q in (test.questions)) {
                val question = JSONObject()
                question.put("text", q.text)
                val answersArray = JSONArray()
                for (a in q.answers as ArrayList<StringTest.Question.Answer>) {
                    val answer = JSONObject()
                    answer.put("text", a.text)
                    answer.put("explanation", a.explanation)
                    answersArray.put(answer)
                }
                question.put("answers", answersArray)
                questionsArray.put(question)
            }
            jsonObject.put("questions",questionsArray)
            val tagsArray = JSONArray()
            for (t in test.tags) {
                tagsArray.put(t)
            }
            jsonObject.put("tags", tagsArray)
            jsonObject.put("results", null)
            return jsonObject
        }

        private fun neuroTestToJSON(test: NeuroTest): JSONObject {
            val jsonObject = JSONObject()
            jsonObject.put("name", test.name)
            jsonObject.put("author", test.author)
            jsonObject.put("type", test.type)
            jsonObject.put("id", test.id)
            val questionsArray = JSONArray()
            for (q in (test.questions)) {
                val question = JSONObject()
                question.put("text", q.text)
                val answersArray = JSONArray()
                for (a in q.answers as ArrayList<NeuroTest.Question.Answer>) {
                    val answer = JSONObject()
                    answer.put("text", a.text)
                    val connections = JSONArray()
                    for (c in a.connections) {
                        connections.put(c.weight)
                    }
                    answer.put("connections", connections)
                    answersArray.put(answer)
                }
                question.put("answers", answersArray)
                questionsArray.put(question)
            }
            jsonObject.put("questions",questionsArray)
            val tagsArray = JSONArray()
            for (t in test.tags) {
                tagsArray.put(t)
            }
            jsonObject.put("tags", tagsArray)
            val resultArray = JSONArray()
            for (r in test.results as ArrayList<NeuroTest.Result>) {
                val result = JSONObject()
                result.put("text", r.text)
                result.put("max", r.max)
                result.put("min", r.min)
                resultArray.put(result)
            }
            jsonObject.put("results", resultArray)
            return jsonObject
        }
    }

    class LocalDataBase(val context: Context?, name: String?) : SQLiteOpenHelper(context, name, null, 1) {

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL("create table Types  ("
                    + "id integer primary key autoincrement,"
                    + "name text"
                    + ");")
            var cv = ContentValues()
            cv.put("name", "answers_with_score")
            db.insert("Types",null, cv)
            cv = ContentValues()
            cv.put("name", "answers_with_strign")
            db.insert("Types",null, cv)
            db.execSQL("create table Tests ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "author text,"
                    + "type integer, FOREIGN KEY (type) REFERENCES Types(id)"
                    + ");")
            db.execSQL("create table Questions ("
                    + "id integer primary key autoincrement,"
                    + "text text,"
                    + "test integer, FOREIGN KEY (test) REFERENCES Tests(id)"
                    + ");")
            db.execSQL("create table ScoreAnswers ("
                    + "id integer primary key autoincrement,"
                    + "text text,"
                    + "score integer,"
                    + "question integer, FOREIGN KEY (question) REFERENCES Questions(id)"
                    + ");")
            db.execSQL("create table StringAnswers ("
                    + "id integer primary key autoincrement,"
                    + "text text,"
                    + "explanation text,"
                    + "question integer, FOREIGN KEY (question) REFERENCES Questions(id)"
                    + ");")
            db.execSQL("create table Results ("
                    + "id integer primary key autoincrement,"
                    + "text text,"
                    + "min integer,"
                    + "max integer,"
                    + "test integer, FOREIGN KEY (test) REFERENCES test(id)"
                    + ");")
        }

        @SuppressLint("Recycle")
        fun insert(test: Test) {
            var db = this.writableDatabase
            var cv = ContentValues()
            cv.put("name",test.name)
            cv.put("author", test.author)
            if (test.type == "answers_with_score") {
                cv.put("type", "answers_with_score")
            }
            else if (test.type == "answers_with_string") {
                cv.put("type", "answers_with_string")
            }
            db.insert("Tests",null, cv)
            val id = db!!.rawQuery("SELECT id from Tests order by id DESC limit 1", null)
            for (question in test.questions) {
                cv = ContentValues()
                cv.put("text", question.text)
                db = this.readableDatabase

                id.moveToFirst()

                cv.put("test", id.getInt(0).toString())
                db = this.writableDatabase
                db.insert("Questions",null, cv)

                val questionId = db.rawQuery("SELECT id from Questions order by id DESC limit 1", null)
                questionId.moveToFirst()
                if (test.type == "answers_with_score") {
                    for (answer: ScoreTest.Question.Answer in (question.answers as ArrayList<ScoreTest.Question.Answer>)) {
                        cv = ContentValues()
                        cv.put("text", answer.text)
                        cv.put("score", answer.score)
                        cv.put("question", questionId.getInt(0))
                        db = this.writableDatabase
                        db.insert("ScoreAnswers",null, cv)
                    }
                }
                else if (test.type == "answers_with_string") {
                    for (answer: StringTest.Question.Answer in question.answers as ArrayList<StringTest.Question.Answer>) {
                        cv = ContentValues()
                        cv.put("text", answer.text)
                        cv.put("explanation", answer.explanation)
                        cv.put("question", questionId.getInt(0))
                        db = this.writableDatabase
                        db.insert("StringAnswers",null, cv)
                    }
                }

            }
            test.results.forEach {
                cv = ContentValues()
                it as ScoreTest.Result
                cv.put("text", it.text)
                cv.put("min", (it).min)
                cv.put("max", (it).max)
                cv.put("test", id.getInt(0))
                db = this.writableDatabase
                db.insert("Results",null, cv)
            }
        }

        @SuppressLint("Recycle")
        fun readData(): ArrayList<Test>{
            val list : ArrayList<Test> = ArrayList()

            val db = this.readableDatabase
            val query = "Select * from " + "Tests"
            val result = db.rawQuery(query,null)

            var answersType = "ScoreAnswers"

            if(result.moveToFirst()){
                do {
                    val test = ScoreTest(result.getString(result.getColumnIndex("name")), result.getString(result.getColumnIndex("author")))
                    test.type = result.getString(result.getColumnIndex("type"))
                    if (test.type == "answers_with_string") {
                        answersType = "StringAnswers"
                    }
                    val questions = ArrayList<Test.Question>()
                    val questionsSQL = db.rawQuery("Select * from Questions where (test = ${ result.getString(result.getColumnIndex("id")) })", null)
                    questionsSQL.moveToFirst()
                    do {
                        val question: Test.Question = Test.Question()
                        question.text = questionsSQL.getString(questionsSQL.getColumnIndex("text"))
                        question.answers = ArrayList()
                        questions.add(question)
                        val answersSQL = db.rawQuery("Select * from $answersType where (question = ${ questionsSQL.getString(questionsSQL.getColumnIndex("id")) })", null)
                        answersSQL.moveToFirst()
                        if (test.type == "answers_with_score") {
                            do {
                                val answer = ScoreTest.Question.Answer(answersSQL.getString(answersSQL.getColumnIndex("text")), answersSQL.getFloat(answersSQL.getColumnIndex("score")))
                                question.answers.add(answer)
                            } while (answersSQL.moveToNext())
                        } else if (test.type == "answers_with_string") {
                            do {
                                val answer = StringTest.Question.Answer(answersSQL.getString(answersSQL.getColumnIndex("text")), answersSQL.getString(answersSQL.getColumnIndex("explanation")))
                                question.answers.add(answer)
                            } while (answersSQL.moveToNext())
                        }
                    }
                    while (questionsSQL.moveToNext())
                    test.questions = questions
                    if (test.type == "answers_with_score") {
                        val resultsSQL = db.rawQuery("Select * from Results where (test = ${result.getString(result.getColumnIndex("id"))})", null)
                        test.results = ArrayList()
                        if (resultsSQL.moveToFirst()) {
                            do {
                                test.results.add(ScoreTest.Result(resultsSQL.getString(resultsSQL.getColumnIndex("text")), resultsSQL.getFloat(resultsSQL.getColumnIndex("min")), resultsSQL.getFloat(resultsSQL.getColumnIndex("max"))))
                            } while (resultsSQL.moveToNext())
                        }
                    }
                    test.tags = ArrayList()
                    test.id = "offline"
                    test.localid = result.getColumnIndex("id")
                    list.add(test)
                }
                while (result.moveToNext())
            }

            result.close()
            db.close()
            return list
        }

        fun deleteElement(test: Test): ArrayList<Test>{
            val list : ArrayList<Test> = ArrayList()

            val db = this.readableDatabase
            val query = "Select * from " + "Tests"
            val result = db.rawQuery(query,null)

            db.delete("Tests","id = ${test.localid}" , null )

            result.close()
            db.close()
            return list
        }

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TestsListActivity> {
        override fun createFromParcel(parcel: Parcel): TestsListActivity {
            return TestsListActivity(parcel)
        }

        override fun newArray(size: Int): Array<TestsListActivity?> {
            return arrayOfNulls(size)
        }
    }
}
