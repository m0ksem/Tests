package com.example.m0ksem.imprtest.TestList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.m0ksem.imprtest.R
import android.support.v7.widget.RecyclerView
import com.example.m0ksem.imprtest.Test

class TestAdapter(val tests: ArrayList<Test>) : RecyclerView.Adapter<TestAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_test, parent, false))
    }

    fun add(test: Test) {
        tests.add(test)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(view: ViewHolder, postion: Int) {
        view.name.text = tests[postion].name
        view.author.text = tests[postion].author
        view.itemView.tag = tests[postion]
    }

    override fun getItemCount(): Int {
        return tests.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.test_name)
        val author = view.findViewById<TextView>(R.id.test_author)
        val tag_list = view.findViewById<RecyclerView>(R.id.test_tag_list)
    }
}