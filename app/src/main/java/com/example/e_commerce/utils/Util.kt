package com.example.e_commerce.utils

import android.app.Activity
import android.content.Context
import com.example.e_commerce.utils.Constants.PHONE_AUTH_SHARED_PREF_KEY
import com.google.firebase.auth.PhoneAuthProvider
import java.util.*

object Util {

    fun setLocal(activity: Activity, langCode: String){
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val resources = activity.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun applySharedPref(context: Context?,langCode: String){
        val sharedPref = context?.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.putString(Constants.LANGUAGE_CODE, langCode)
        editor?.apply()
    }

    fun applyProfileSharedPref(context: Context?, name: String, phone: String, address: String, role: String){
        val sharedPref = context?.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.putString("name", name)
        editor?.putString("phone", phone)
        editor?.putString("address", address)
        editor?.putString("role", role)
        editor?.apply()
    }
}