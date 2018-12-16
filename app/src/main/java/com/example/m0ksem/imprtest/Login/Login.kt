@file:Suppress("UNUSED_PARAMETER", "DEPRECATION")

package com.example.m0ksem.imprtest.Login

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
import android.support.v7.app.AlertDialog
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.m0ksem.imprtest.R
import com.example.m0ksem.imprtest.TestList.TestsList
import kotlinx.android.synthetic.main.activity_tests_list.*
import java.net.URL


class Login : AppCompatActivity() {

    val loginURL = "http://192.168.0.100:3000/login"
    val registerULR = "http://192.168.0.100:3000/register"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.navigationBarColor = resources.getColor(R.color.colorGradientBackgroundBottom)
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        login_hello.setText(R.string.hello)
    }

    fun onLoginButtonClick(view: View) {
        when {
            login_input.text.toString() == "" -> {
                LoginErrorMessage.setText(R.string.error_enter_login)
                login_password.setText("")
            }
            login_password.text.toString() == "" -> {
                LoginErrorMessage.setText(R.string.error_enter_password)
                login_password.setText("")
            }
            login_input.text.toString() == "" && login_input.text.toString().length > 20 ->  {
                LoginErrorMessage.setText(R.string.error_invalid_login)
                login_input.setText("")
                login_password.setText("")
            }
            login_password.text.toString() == "" -> {
                LoginErrorMessage.setText(R.string.error_invalid_password)
                login_password.setText("")
            }
            else -> {
                LoginErrorMessage.text = ""
                Login()
            }
        }
    }

    private fun serverNotAvailable() {
        val message = AlertDialog.Builder(this)

        message.setTitle(resources.getString(R.string.server_not_available_header))
        message.setMessage(resources.getString(R.string.server_not_available_message))
        message.setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
            val prefs: SharedPreferences.Editor = getSharedPreferences("account", Context.MODE_PRIVATE).edit()
            prefs.putString("login", login_input.text.toString())
            prefs.putBoolean("offline", true)
            prefs.apply()
            val intent = Intent(this, TestsList::class.java)
            startActivity(intent)
            this.finish()
        }
        message.setNeutralButton(resources.getString(R.string.cancel)) { _,_ -> }

        message.create().show()
    }

    fun onRegisterButtonClick(view: View) {
        val makeSureQuestion = AlertDialog.Builder(this)

        makeSureQuestion.setTitle(resources.getString(R.string.register_warning_header))
        makeSureQuestion.setMessage(resources.getString(R.string.register_warning_message))
        makeSureQuestion.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            registerRequest()
        }
        makeSureQuestion.setNeutralButton(resources.getString(R.string.cancel)) { _,_ -> }

        makeSureQuestion.create().show()
    }

    private fun registerRequest() {
        val login = login_input.text.toString()
        val password = login_password.text.toString()

        val queue = Volley.newRequestQueue(this)

        val postRequest = object : StringRequest(Request.Method.POST, registerULR, Response.Listener<String>
        {
            response ->
            Log.d("Response", response)
            when (response) {
                "1" -> {
                    Log.d("Response", "User registered")
                }
                "0" -> {
                    Log.d("Response", "User already exist")
                }
            }
        },
                Response.ErrorListener {
                    Log.d("ErrorResponse", it.message)
                    serverNotAvailable()
                }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()

                params["login"] = login
                params["password"] = password

                return params
            }
        }

        postRequest.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        queue.add(postRequest)
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    private fun Login() {
        val login = login_input.text.toString()
        val password = login_password.text.toString()

        val queue = Volley.newRequestQueue(this)

        val response: String? = null

        val postRequest = object : StringRequest(Request.Method.POST, loginURL, Response.Listener<String>
        {
            response ->
            Log.d("Response", response)
            when (response.toString()) {
                "0" -> {
                    LoginErrorMessage.setText(R.string.error_invalid_login)
                    login_input.setText("")
                    login_password.setText("")
                }
                "1" -> {
                    val prefs: SharedPreferences.Editor = getSharedPreferences("account", Context.MODE_PRIVATE).edit()
                    prefs.putString("login", login_input.text.toString())
                    prefs.apply()
                    val intent = Intent(this, TestsList::class.java)
                    startActivity(intent)
                    this.finish()
                }
                "2" -> {
                    LoginErrorMessage.setText(R.string.error_invalid_password)
                    login_password.setText("")
                }
            }
        },
        Response.ErrorListener {
            Log.d("ErrorResponse", it.message)
            serverNotAvailable()
        }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()

                params["login"] = login
                params["password"] = password

                return params
            }
        }

        postRequest.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        queue.add(postRequest)
    }
}
