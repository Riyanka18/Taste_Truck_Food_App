package com.example.spicezilla.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.spicezilla.R
import org.json.JSONException
import org.json.JSONObject
import util.ConnectionManager

class ForgotActivity : AppCompatActivity() {
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var phone_no: EditText
    lateinit var email_id: EditText
    lateinit var next: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        progressBar = findViewById(R.id.progressBar)
        progressLayout = findViewById(R.id.progress_layout)
        phone_no= findViewById(R.id.phonef)
        email_id= findViewById(R.id.emailf)
        next=findViewById(R.id.nextf)
        progressLayout=findViewById(R.id.progress_layout)
        progressBar.visibility = View.GONE
        progressLayout.visibility=View.GONE
        next.setOnClickListener {
            val forgotphoneNumber = phone_no.text.toString()
            if (forgotphoneNumber.length == 10) {
                if (ConnectionManager().checkConnectivity(this@ForgotActivity)) {
                    progressBar.visibility = View.GONE
                    funcOtp(phone_no.text.toString(), email_id.text.toString())
                } else {
                    progressLayout.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    Toast.makeText(
                        this@ForgotActivity,
                        "No Internet Connection!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this@ForgotActivity, "   Invalid phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun funcOtp(phone_no: String, email: String) {
        val queue = Volley.newRequestQueue(this)
        val jsonParams = JSONObject()
        jsonParams.put("mobile_number",phone_no)
        jsonParams.put("email",email)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, "http://13.235.250.119/v2/forgot_password/fetch_result", jsonParams, Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val firstTry = data.getBoolean("first_try")
                        if (firstTry) {
                            val builder = AlertDialog.Builder(this@ForgotActivity)
                            builder.setTitle("Information regarding OTP")
                            builder.setMessage("Check your registered Email address for the OTP.")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") {text,listener->
                                val intent = Intent(this@ForgotActivity, ResetActivity::class.java)
                                intent.putExtra("user_mobile",phone_no)
                                startActivity(intent)
                            }
                            builder.create().show()
                        }
                        else {
                            val builder = AlertDialog.Builder(this@ForgotActivity)
                            builder.setTitle("Information")
                            builder.setMessage("OTP has been provided in the previous email")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") {text,listener ->
                                val intent = Intent(this@ForgotActivity,
                                    ResetActivity::class.java)
                                intent.putExtra("user_mobile",phone_no)
                                startActivity(intent)
                            }
                            builder.create().show()
                        }
                    } else {
                        Toast.makeText(this@ForgotActivity, "Contact number not registered!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this@ForgotActivity,"An Error occurred",Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(this@ForgotActivity, "Error occurred", Toast.LENGTH_SHORT).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "e59d83ed325595"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }
    override fun onBackPressed() {
        val i=Intent(this@ForgotActivity,LoginActivity::class.java)
        startActivity(i)
        finish()
    }
}