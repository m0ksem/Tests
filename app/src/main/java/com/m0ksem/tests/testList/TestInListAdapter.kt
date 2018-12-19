package com.m0ksem.tests.testList
import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.m0ksem.tests.R
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.m0ksem.tests.Test
import com.m0ksem.tests.testCreate.TestCreateActivity

class TestAdapter(private val tests: ArrayList<Test>, private val activity: TestsListActivity) : RecyclerView.Adapter<TestAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_test, parent, false))
    }

    fun add(test: Test) {
        tests.add(test)
        notifyDataSetChanged()
    }

    fun edit(test: Test, pos: Int) {
        tests[pos] = test
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

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.test_name)!!
        val author = view.findViewById<TextView>(R.id.test_author)!!

        init {
            view.setOnLongClickListener {
                if (activity.username == tests[adapterPosition].author) {
                    val builder = AlertDialog.Builder(activity)

                    val items: Array<String> = if (tests[adapterPosition].id == "offline") {
                        arrayOf(activity.resources.getString(R.string.edit_test_edit), activity.resources.getString(R.string.edit_test_remove), activity.resources.getString(R.string.test_add_request))
                    } else {
                        arrayOf(activity.resources.getString(R.string.edit_test_edit), activity.resources.getString(R.string.edit_test_remove))
                    }

                    builder.setItems(items) { _, which ->
                        when(which) {
                            0 -> {
                                val intent = Intent(activity as Context, TestCreateActivity::class.java)
                                intent.putExtra("test", tests[adapterPosition])
                                intent.putExtra("position", adapterPosition)
                                activity.startActivityForResult(intent, 2)
                            }
                            1 -> {
                                val makeSureQuestion = AlertDialog.Builder(activity)

                                makeSureQuestion.setTitle(activity.resources.getString(R.string.edit_test_make_sure_delete_title))
                                makeSureQuestion.setMessage(activity.resources.getString(R.string.edit_test_make_sure_delete_message))
                                makeSureQuestion.setPositiveButton(activity.resources.getString(R.string.yes)) { _, _ ->
                                    activity.ServerDataBase().deleteTest(tests[adapterPosition].id)
                                    tests.removeAt(adapterPosition)
                                    notifyDataSetChanged()
                                }
                                makeSureQuestion.setNeutralButton(activity.resources.getString(R.string.cancel)) { _,_ -> }

                                makeSureQuestion.create().show()
                            }
                            2 -> {
                                if (!activity.offline) {
                                    activity.ServerDataBase().addTest(tests[adapterPosition])
                                } else {
                                    val warning = AlertDialog.Builder(activity)

                                    warning.setTitle(activity.resources.getString(R.string.please_login))
                                    warning.setMessage(activity.resources.getString(R.string.please_login_message))
                                    warning.setPositiveButton(activity.resources.getString(R.string.ok)) { _, _ -> }
                                    warning.create().show()
                                }
                            }
                        }
                    }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                } else {
                    val builder = AlertDialog.Builder(activity)

                    builder.setTitle(activity.resources.getString(R.string.edit_test_fail_header))
                    builder.setMessage(activity.resources.getString(R.string.edit_test_fail_message))
                    builder.setPositiveButton(activity.resources.getString(R.string.ok)) { _, _ ->
                    }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
                true
            }
        }
    }
}