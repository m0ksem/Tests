package com.m0ksem.tests.results

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.m0ksem.tests.R

@Suppress("UNUSED_PARAMETER")
class OneStringResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_get_one_result)
        findViewById<TextView>(R.id.result).text = intent.getStringExtra("result")
        if (Build.VERSION.SDK_INT >= 21) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        val header = findViewById<ConstraintLayout>(R.id.header)
        header.setPadding(header.paddingLeft, (40 * resources.displayMetrics.density).toInt(), header.paddingRight, header.paddingBottom)
    }

    fun back(view: View){
        finish()
    }

}
