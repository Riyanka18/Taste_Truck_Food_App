package com.example.spicezilla.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.spicezilla.R
import org.json.JSONObject
import util.ConnectionManager

class RegisterActivity : AppCompatActivity() {

    lateinit var Register:Button
    lateinit var name:EditText
    lateinit var phone:EditText
    lateinit var password:EditText
    lateinit var email:EditText
    lateinit var address:EditText
    lateinit var confirmPassword:EditText
    lateinit var progressBar:ProgressBar
    lateinit var sharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        name = findViewById(R.id.namereg)
        phone= findViewById(R.id.phonereg)
        email = findViewById(R.id.emailreg)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.cpass)
        address = findViewById(R.id.addressreg)
        Register = findViewById(R.id.register)
        progressBar = findViewById(R.id.regprogressBar)

        progressBar.visibility = View.INVISIBLE

        val isloggedIn=sharedPreferences.getBoolean("isloggedin",false)
        if(isloggedIn)
        {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        Register.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            if (password.text.toString() == confirmPassword.text.toString()) {
                if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {
                    RegisterRequest(
                        name.text.toString(),
                        phone.text.toString(),
                        address.text.toString(),
                        password.text.toString(),
                        email.text.toString()
                    )
                } else {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@RegisterActivity,
                        "No Internet Connection",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this@RegisterActivity, "Passwords don't match", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun RegisterRequest(name: String, phone: String, address: String, password: String, email: String) {
        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("name", name)
        jsonParams.put("mobile_number", phone)
        jsonParams.put("password", password)
        jsonParams.put("address", address)
        jsonParams.put("email", email)

        val jsonObjectRequest = object : JsonObjectRequest(Method.POST,"http://13.235.250.119/v2/register/fetch_result",jsonParams, Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val response = data.getJSONObject("data")
                        sharedPreferences.edit().putString("user_id", response.getString("user_id")).apply()
                        sharedPreferences.edit().putString("user_name", response.getString("name")).apply()
                        sharedPreferences.edit().putString("user_mobile_number", response.getString("mobile_number")).apply()
                        sharedPreferences.edit().putString("user_address", response.getString("address")).apply()
                        sharedPreferences.edit().putString("user_email", response.getString("email")).apply()
                        sharedPreferences.edit().putBoolean("isloggedin",true).apply()
                        startActivity(Intent(this@RegisterActivity,
                            MainActivity::class.java))
                        finish()
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@RegisterActivity,
                            "An error has occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception){
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@RegisterActivity,"JSON Exception",Toast.LENGTH_SHORT).show()
                }
            },Response.ErrorListener {
                Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "e59d83ed325595"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
    override fun onSupportNavigateUp(): Boolean {
        Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        val i=Intent(this@RegisterActivity,LoginActivity::class.java)
        startActivity(i)
        finish()
    }
}
