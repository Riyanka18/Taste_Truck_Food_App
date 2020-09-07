package com.example.spicezilla.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.spicezilla.R
import org.json.JSONObject
import util.ConnectionManager

class ResetActivity : AppCompatActivity() {
    lateinit var OTP:EditText
    lateinit var NewPassword:EditText
    lateinit var ConfirmNewPassword: EditText
    lateinit var Submit: Button
    lateinit var progressBar: ProgressBar
    lateinit var mobileNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        OTP = findViewById(R.id.otp)
        NewPassword = findViewById(R.id.npassword)
        ConfirmNewPassword = findViewById(R.id.cpassword)
        Submit= findViewById(R.id.submit)
        progressBar = findViewById(R.id.resetprogressBar)

        progressBar.visibility= View.GONE
        if (intent != null) {
            mobileNumber = intent.getStringExtra("user_mobile") as String
        }
        Submit.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            if (ConnectionManager().checkConnectivity(this@ResetActivity)) {
                if (OTP.text.length == 4) {
                    if ((NewPassword.text.toString()).length>=4) {
                        if (NewPassword.text.toString()==ConfirmNewPassword.text.toString())
                         {
                            resetPassword(mobileNumber,OTP.text.toString(),NewPassword.text.toString())
                        } else {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@ResetActivity, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ResetActivity,
                            "Invalid Password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ResetActivity, "Incorrect OTP", Toast.LENGTH_SHORT).show()
                }
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@ResetActivity,
                    "No Internet Connection!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun resetPassword(mobileNumber: String, otp: String, password: String) {
        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("password", password)
        jsonParams.put("otp", otp)

        val jsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                "http://13.235.250.119/v2/reset_password/fetch_result",
                jsonParams,
                Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            progressBar.visibility = View.INVISIBLE
                            val builder = AlertDialog.Builder(this@ResetActivity)
                            builder.setTitle("Confirmation")
                            builder.setMessage("Your password has been successfully changed")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") { _, _ ->
                                startActivity(
                                    Intent(
                                        this@ResetActivity,
                                        LoginActivity::class.java
                                    )
                                )
                                ActivityCompat.finishAffinity(this@ResetActivity)
                            }
                            builder.create().show()
                        } else {
                            progressBar.visibility = View.GONE
                            val error = data.getString("errorMessage")
                            Toast.makeText(
                                this@ResetActivity,
                                error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ResetActivity,
                            "Incorrect Response!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener {
                    progressBar.visibility = View.GONE
                    VolleyLog.e("An error has occurred")
                    Toast.makeText(this@ResetActivity, it.message, Toast.LENGTH_SHORT).show()
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
        val i=Intent(this@ResetActivity,LoginActivity::class.java)
        startActivity(i)
        finish()
    }
}
