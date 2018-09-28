package com.example.m0ksem.imprtest.TestCreate.ChooseTags
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.m0ksem.imprtest.R


class TagInChooseTagsAdapter(val tags: ArrayList<String>) : RecyclerView.Adapter<TagInChooseTagsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.create_test_tag, parent, false))
    }

    fun delete(position: Int) {
        tags.removeAt(position)
        notifyDataSetChanged()
    }

    fun add(tag: String) {
        tags.add(tag)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(view: ViewHolder, postion: Int) {
        view.text.text = tags[postion]
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val text = view.findViewById<TextView>(R.id.tag_text)
    }
}