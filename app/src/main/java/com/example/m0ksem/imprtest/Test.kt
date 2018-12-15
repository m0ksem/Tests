package com.example.m0ksem.imprtest

import android.content.Context
import android.util.Log
import java.io.File
import java.io.Serializable

abstract class Test(val name: String = "Псих тест", val author: String = "m0ksem") : Serializable {
    var id: Int = 0
    lateinit var type: String
    lateinit var questions: ArrayList<Question>
    lateinit var tags: ArrayList<String>
    open lateinit var results: ArrayList<Result>

    open class Question : Serializable {
        lateinit var text: String
        var answers: ArrayList<Answer> = ArrayList()

        open class Answer(var text: String) : Serializable
    }

    open class Result(var text: String) : Serializable {
        var id: Int = 0
    }
}

class ScoreTest(name: String, author: String) : Test(name, author) {
    override lateinit var results: ArrayList<Test.Result>

    class Question : Test.Question() {
        class Answer(text: String, var score: Float) : Test.Question.Answer(text)
    }

    class Result(result: String, var min: Float, var max: Float) : Test.Result(result)

    fun getResult(position: Float): String {
        for (r in results) {
            val result: ScoreTest.Result = r as Result
            if (result.min <= position && result.max > position) return result.text
        }
        return "Пользователь $author не добавил ничего для ваших результатов :("
    }
}

class StringTest(name: String, author: String) : Test(name, author) {
    class Question: Test.Question() {
        class Answer(text: String, var explanation: String) : Test.Question.Answer(text)
    }
}

class NeuroTest(name: String, author: String) : Test(name, author) {
    override lateinit var results: ArrayList<Test.Result>

    class Question : Test.Question() {
        class Answer(text: String, val connections: ArrayList<Connection> = ArrayList()) : Test.Question.Answer(text)
    }

    class Connection(var result: Test.Result, var weight: Float) : Serializable {
        fun copy(): Connection {
            return Connection(result, weight)
        }
    }

    class Result(result: String, var score: Float, var min: Float, var max: Float) : Test.Result(result)

//    fun getResult(): ArrayList<String> {
//        val return_results: ArrayList<Test.Result> = ArrayList()
//        for (r in results) {
//            val result: NeuroTest.Result = r as Result
//            if (result.min <= result.score && result.max > result.score) return_results.add(result)
//        }
//        return_results
//    }
}