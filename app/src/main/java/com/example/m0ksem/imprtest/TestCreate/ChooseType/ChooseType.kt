package com.example.m0ksem.imprtest.TestCreate.ChooseType

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.m0ksem.imprtest.R

class ChooseType : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_type)
    }

    fun back(view: View){
        finish()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.create_test_choose_type_list -> {
                intent.putExtra("type", "answers_with_score");
                setResult(RESULT_OK, intent);
            }
        }
        finish()
    }
}
