package com.example.e_commerce.utils

import android.app.Activity
import android.content.Context
import com.example.e_commerce.utils.Constants.GENERAL_SHARED_PREF_CODE
import com.example.e_commerce.utils.Constants.LANGUAGE_CODE
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
        editor?.putString(LANGUAGE_CODE, langCode)
        editor?.apply()
    }

    fun applyGeneralSharedPref(context: Context?, value: String){
        val sharedPref = context?.getSharedPreferences(Constants.GENERAL_SHARED_PREF, Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.putString(GENERAL_SHARED_PREF_CODE, value)
        editor?.apply()
    }
}