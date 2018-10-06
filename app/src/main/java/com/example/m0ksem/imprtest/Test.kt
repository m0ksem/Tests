package com.example.m0ksem.imprtest

import android.util.Log
import java.io.Serializable

open class Test(val name: String = "Псих тест", val author: String = "m0ksem") : Serializable {

    var questionCount: Int = 0
    lateinit var type: String
    lateinit var questions: ArrayList<Question>
    lateinit var tags: ArrayList<String>
    lateinit var results: ArrayList<Result>

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

    open class Question : Serializable {
        lateinit var text: String
        var answers: ArrayList<Answer> = ArrayList()


        open class Answer(var text: String) : Serializable
    }


    open class Result(var text: String) : Serializable
}

class ScoreTest(name: String, author: String) : Test(name, author) {
    class Question : Test.Question() {
        class Answer(text: String, var score: Int) : Test.Question.Answer(text)
    }

    class Result(result: String, var min: Int, var max: Int) : Test.Result(result)

    fun getResult(position: Int): String {
        for (r in results) {
            val result: ScoreTest.Result = r as Result
            Log.d("Result $r", "text: ${r.text}, min ${r.min}, max ${r.max}")
            if (result.min <= position && result.max > position) return result.text
        }
        return "Пользователь $author не добавил ничего для ваших результатов :("
    }
}


//class AnswerWithTextValue(text: String, val value: String) : Test.Answer(text)

