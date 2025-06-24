package com.example.fido_demo

import android.util.Log

import com.google.gson.Gson


fun logAsJson(tag: String, obj: Any?) {
    val json = Gson().toJson(obj)
    Log.d(tag, json)
}

