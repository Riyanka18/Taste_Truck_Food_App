package com.example.spicezilla.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.spicezilla.R
import org.json.JSONException
import org.json.JSONObject
import util.ConnectionManager

class LoginActivity : AppCompatActivity() {
    lateinit var phone: EditText
    lateinit var login: Button
    lateinit var password: EditText
    lateinit var forgot: TextView
    lateinit var register: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        phone = findViewById(R.id.phone)
        password = findViewById(R.id.password)
        login = findViewById(R.id.loginButton)
        forgot = findViewById(R.id.forgot)
        register = findViewById(R.id.signUp)

        sharedPreferences=getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        val isloggedIn=sharedPreferences.getBoolean("isloggedin",false)
        if(isloggedIn)
        {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        forgot.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotActivity::class.java))
        }
        register.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        login.setOnClickListener {
            if((phone.text.toString()).length==10 && (password.text.toString()).length>=4)
            {
                if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
                    val url="http://13.235.250.119/v2/login/fetch_result"
                    val queue = Volley.newRequestQueue(this)
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number",phone.text.toString())
                    jsonParams.put("password",password.text.toString())

                    val jsonObjectRequest = object : JsonObjectRequest(Method.POST,url,jsonParams,Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val obj = data.getJSONObject("data")

                                    sharedPreferences.edit().putString("user_id",obj.getString("user_id")).apply()
                                    sharedPreferences.edit().putString("user_name",obj.getString("name")).apply()
                                    sharedPreferences.edit().putString("user_mobile_number",obj.getString("mobile_number")).apply()
                                    sharedPreferences.edit().putString("user_address",obj.getString("address")).apply()
                                    sharedPreferences.edit().putString("user_email",obj.getString("email")).apply()
                                    sharedPreferences.edit().putBoolean("isloggedin",true).apply()

                                    startActivity(Intent(this@LoginActivity,
                                        MainActivity::class.java))
                                    finish()
                                    }
                                    else
                                    {
                                    Toast.makeText(this@LoginActivity,"Volley error",Toast.LENGTH_SHORT).show()
                                    }
                            } catch (e: JSONException) {
                                Toast.makeText(this@LoginActivity,"Json exception occured",Toast.LENGTH_SHORT).show()
                                }
                        }, Response.ErrorListener {
                            Toast.makeText(this@LoginActivity,"Error listener",Toast.LENGTH_SHORT).show()
                        })
                        {
                            override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"]="application/json"
                            headers["token"]="e59d83ed325595"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                }
                else {
                    Toast.makeText(this@LoginActivity, "No internet Connection", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this@LoginActivity, "Invalid Phone or Password", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    override fun onPause() {
        super.onPause()
        finish()
    }
}