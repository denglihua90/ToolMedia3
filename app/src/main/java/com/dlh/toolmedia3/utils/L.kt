package com.dlh.toolmedia3.utils

import android.util.Log

/**
 * 日志类
 * 
 */
object L {
    private const val TAG = "ToolMedia3"

    private var isDebug = true


    fun d(msg: String) {
        if (isDebug) {
            Log.d(TAG, msg)
        }
    }

    fun e(msg: String) {
        if (isDebug) {
            Log.e(TAG, msg)
        }
    }

    fun i(msg: String) {
        if (isDebug) {
            Log.i(TAG, msg)
        }
    }

    fun w(msg: String) {
        if (isDebug) {
            Log.w(TAG, msg)
        }
    }

    fun setDebug(isDebug: Boolean) {
        L.isDebug = isDebug
    }
}
