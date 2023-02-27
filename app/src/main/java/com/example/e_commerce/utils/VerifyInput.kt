package com.example.e_commerce.utils

object VerifyInput {

    fun verifyCode(code1: String, code2: String, code3:String, code4: String, code5: String, code6:String): Boolean{
        return (code1.isNotEmpty() && code2.isNotEmpty() && code3.isNotEmpty() && code4.isNotEmpty()
                && code5.isNotEmpty() && code6.isNotEmpty())
    }
}