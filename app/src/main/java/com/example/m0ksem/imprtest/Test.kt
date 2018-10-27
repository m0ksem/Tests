package com.example.m0ksem.imprtest

import android.content.Context
import android.util.Log
import java.io.File
import java.io.Serializable

open class Test(val name: String = "Псих тест", val author: String = "m0ksem") : Serializable {
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

    open class Result(var text: String) : Serializable
}

class ScoreTest(name: String, author: String) : Test(name, author) {
    override lateinit var results: ArrayList<Test.Result>

    class Question : Test.Question() {
        class Answer(text: String, var score: Int) : Test.Question.Answer(text)
    }

    class Result(result: String, var min: Int, var max: Int) : Test.Result(result)

    fun getResult(position: Int): String {
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
        class Answer(text: String, var connections: ArrayList<Connection>) : Test.Question.Answer(text) {
            data class Connection(var weight: Int, var result: Test.Result)
        }
    }

    class Result(result: String, var score: Int, var min: Int, var max: Int) : Test.Result(result)

    fun getResults(): String {
        val return_results: ArrayList<Test.Result> = ArrayList()
        for (r in results) {
            val result: NeuroTest.Result = r as Result
            if (result.min <= result.score && result.max > result.score) return_results.add(result)
        }
        return "Пользователь $author не добавил ничего для ваших результатов :("
    }
}