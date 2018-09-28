package com.example.m0ksem.imprtest.TestCreate.ChooseTags

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.example.m0ksem.imprtest.R
import com.example.m0ksem.imprtest.TestList.TestAdapter

class ChooseTags : AppCompatActivity() {

    private lateinit var adapter: TagInChooseTagsAdapter
    lateinit var tags_list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_tags)
        tags_list = this.findViewById<RecyclerView>(R.id.tag_list)
        tags_list.layoutManager = LinearLayoutManager(this)
        val tags: ArrayList<String> = intent.getStringArrayListExtra("tags")  ?: ArrayList()
        adapter = TagInChooseTagsAdapter(tags)
        tags_list.adapter = adapter
    }

    fun addTag(view: View) {
        val str: String = findViewById<EditText>(R.id.new_tag_input).text.toString()
        adapter.add(str)
    }

    fun back(view: View){
        save()
        finish()
    }

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
        intent.putExtra("tags", adapter.tags)
        setResult(RESULT_OK, intent)
        finish()
    }
}
