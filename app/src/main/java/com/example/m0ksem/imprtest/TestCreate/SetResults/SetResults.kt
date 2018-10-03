@file:Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")

package com.example.m0ksem.imprtest.TestCreate.SetResults

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.m0ksem.imprtest.R
import com.example.m0ksem.imprtest.ScoreTest
import com.example.m0ksem.imprtest.Test

class SetResults : AppCompatActivity() {

    private lateinit var adapter: ResultAdapter
    lateinit var tags_list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_tags)
        tags_list = this.findViewById<RecyclerView>(R.id.tag_list)
        tags_list.layoutManager = LinearLayoutManager(this)
        if (intent.getStringExtra("type") == "answer_with_score") {
            val results: ArrayList<ScoreTest.Result> = intent.getSerializableExtra("results") as ArrayList<ScoreTest.Result>
            adapter = ResultWithScoreAdapter(results as ArrayList<Test.Result>)
        }
        tags_list.adapter = adapter
    }

    fun addResult(view: View) {
        val str: String = findViewById<EditText>(R.id.new_tag_input).text.toString()
        val result: Test.Result = Test.Result(str)
        result.text = str
        adapter.add(result)
    }

//    fun back(view: View){
//        save()
//        finish()
//    }

    override fun onBackPressed() {
        save()
        super.onBackPressed()
    }

    fun deleteTag(view: View) {
        val item: ConstraintLayout = view.parent as ConstraintLayout
        val pos: Int = tags_list.getChildAdapterPosition(item)
        adapter.delete(pos)
    }

    fun save() {
        intent.putExtra("tags", adapter.results)
        setResult(RESULT_OK, intent)
        finish()
    }
}

open class ResultAdapter(open val results: ArrayList<Test.Result>) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_score_result, parent, false))
    }

    open fun delete(position: Int) {
        results.removeAt(position)
        notifyDataSetChanged()
    }

    open fun add(result: Test.Result) {
        results.add(result)
        notifyDataSetChanged()
    }

    open override fun onBindViewHolder(view: ViewHolder, postion: Int) {
        view.text.text = results[postion].text
    }

    override fun getItemCount(): Int {
        return results.size
    }

    open class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val text = view.findViewById<TextView>(R.id.result_text)!!
    }
}


class ResultWithScoreAdapter(override val results: ArrayList<Test.Result>) : ResultAdapter(results as ArrayList<Test.Result>) {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_score_result, parent, false))
    }

    override fun add(result: Test.Result) {
        results.add(result)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(view: ResultAdapter.ViewHolder, postion: Int) {
        view.text.text = results[postion].text
        view as ResultWithScoreAdapter.ViewHolder
        view.min.text = "0"
        view.max.text = "1"
    }

    override fun getItemCount(): Int {
        return results.size
    }

    class ViewHolder(view: View): ResultAdapter.ViewHolder(view) {
        val min = view.findViewById<TextView>(R.id.tag_text)!!
        val max = view.findViewById<TextView>(R.id.tag_text)!!
    }
}

//class ResultAdapter(val results: ArrayList<Test.Result>) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
//        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_tag, parent, false))
//    }
//
//    fun delete(position: Int) {
//        results.removeAt(position)
//        notifyDataSetChanged()
//    }
//
//    fun add(result: Test.Result) {
//        results.add(result)
//        notifyDataSetChanged()
//    }
//
//    override fun onBindViewHolder(view: ViewHolder, postion: Int) {
//        view.text.text = results[postion].text
//    }
//
//    override fun getItemCount(): Int {
//        return results.size
//    }
//
//    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
//        val text = view.findViewById<TextView>(R.id.tag_text)
//    }
//}
