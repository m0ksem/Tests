package com.example.m0ksem.imprtest.Results

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.m0ksem.imprtest.R

@Suppress("UNUSED_PARAMETER")
class ManyStringResult : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_get_many_result)
        val results = this.findViewById<RecyclerView>(R.id.test_results_list)
        results.layoutManager = LinearLayoutManager(this)
        results.adapter = ResultAdapter(intent.getStringArrayExtra("results"))
    }

    fun back(view: View){
        finish()
    }
}

open class ResultAdapter(open val results: Array<String>) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.result_string, parent, false))
    }

    override fun onBindViewHolder(view: ViewHolder, postion: Int) {
        view.text.text = results[postion]
    }

    override fun getItemCount(): Int {
        return results.size
    }

    open inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val text = view.findViewById<TextView>(R.id.result_string_text)!!
    }
}
