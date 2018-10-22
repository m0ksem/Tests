package com.example.m0ksem.imprtest.Results

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.m0ksem.imprtest.R

@Suppress("UNUSED_PARAMETER")
class OneStringResult : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_get_one_result)
        findViewById<TextView>(R.id.result).text = intent.getStringExtra("result")
    }

    fun back(view: View){
        finish()
    }

}
