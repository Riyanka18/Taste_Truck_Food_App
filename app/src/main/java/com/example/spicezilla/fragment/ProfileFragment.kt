package com.example.spicezilla.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.spicezilla.R

class ProfileFragment:Fragment() {

    lateinit var username:TextView
    lateinit var phone:TextView
    lateinit var address:TextView
    lateinit var email:TextView
    lateinit var sharedPreference:SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile,container,false)
        sharedPreference=(activity as FragmentActivity).getSharedPreferences("Food_preference", Context.MODE_PRIVATE)
        username= view.findViewById(R.id.profileuserName)
        phone= view.findViewById(R.id.Profilephone)
        email= view.findViewById(R.id.profileEmail)
        address= view.findViewById(R.id.profileAddress)

        username.text = sharedPreference.getString("user_name", null)
        phone.text=sharedPreference.getString("user_mobile_number", null)
        email.text=sharedPreference.getString("user_email", null)
        address.text=sharedPreference.getString("user_address", null)

        return view
    }

}
