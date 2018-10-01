package com.example.m0ksem.imprtest

import android.os.Parcelable
import java.io.Serializable

class Test(val name: String = "Псих тест", val author: String = "m0ksem") : Serializable {

    var question_count: Int = 0
    lateinit var type: String
//    lateinit var questions: ArrayList<ArrayList<String>>
    lateinit var questions: ArrayList<Question>
    lateinit var tags: ArrayList<String>

    init {
        type = "list"
//        questions = GetQuestions()
        tags = GetCategories()
    }

    fun GetQuestions(): ArrayList<ArrayList<String>> {
        // TODO Получение вопросов по ID и преобразование их в такой масив, где первым элементом массива есть вопрос, а остальными ответы
        val array: ArrayList<ArrayList<String>> = ArrayList()
        array.add(arrayListOf("Название вопроса", "Ответ1", "Ответ2"))
        array.add(arrayListOf("Название вопроса #2", "Ответ1", "Ответ2"))
        array.add(arrayListOf("Название вопроса #3", "Ответ1", "Ответ2"))
        array.add(arrayListOf("Название вопроса #4", "Ответ1", "Ответ2"))
        return array
    }

    fun GetCategories(): ArrayList<String> {
        val array: ArrayList<String> = ArrayList()
        array.add("Развитие")
        array.add("Улучшение")
        return array
    }

    fun getQuestionsCount(): Int {
        // TODO Выбор количества вопросов из бд COUNT
        return 0
    }
}

class Question : Serializable {
    lateinit var text: String
    var answers: ArrayList<Answer> = ArrayList()
}

abstract class Answer(var text: String) : Serializable
class AnswerWithScore(text: String, var score: Int) : Answer(text)
class AnswerWithTextValue(text: String, val value: String) : Answer(text)