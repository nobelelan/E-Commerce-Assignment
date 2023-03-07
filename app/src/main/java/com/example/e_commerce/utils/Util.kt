package com.example.e_commerce.utils

import android.app.Activity
import android.content.Context
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
}