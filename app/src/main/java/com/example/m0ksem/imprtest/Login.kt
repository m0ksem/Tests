package com.example.m0ksem.imprtest

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import android.view.WindowManager
import android.os.Build
import android.view.ViewGroup
import com.example.m0ksem.imprtest.TestList.TestsList


class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.navigationBarColor = resources.getColor(R.color.colorGradientBackgroundBottom)
            window.statusBarColor = resources.getColor(R.color.colorGradientBackgroundTop)
        }
//        findViewById<ViewGroup>(R.id.loginFormLayout).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        login_hello.setText(R.string.hello)
    }

    fun onLoginButtonClick(view: View) {
        when {
            login_input.text.toString() == "" -> {
                LoginErrorMessage.text = "Enter login"
                return Unit
            }
            LoginPasswordInput.text.toString() == "" -> {
                LoginErrorMessage.text = "Enter password"
                return Unit
            }
//            LoginInput.text.toString() != "" ->  {
//                LoginErrorMessage.text = "Incorrect login"
//                return Unit
//            }
            LoginPasswordInput.text.toString() != "123" -> {
                LoginErrorMessage.text = "Incorrect password"
                return Unit
            }
            else -> {
                LoginErrorMessage.text = ""
                Login()
            }
        }
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    fun Login() {
        val message = Toast.makeText(this, "Wellcome", Toast.LENGTH_SHORT)
        val prefs: SharedPreferences.Editor = getSharedPreferences("account", Context.MODE_PRIVATE).edit()
        prefs.putString("login", login_input.text.toString())
        prefs.apply()
        message.show()
        val intent = Intent(this, TestsList::class.java)
        startActivity(intent)
        this.finish()
    }
}
