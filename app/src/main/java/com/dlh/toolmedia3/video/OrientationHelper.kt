package com.dlh.toolmedia3.video

import android.content.Context
import android.view.OrientationEventListener

/**
 * 设备方向监听
 */
open class OrientationHelper(context: Context) : OrientationEventListener(context) {
    private var mLastTime: Long = 0

    private var mOnOrientationChangeListener: OnOrientationChangeListener? = null

    override fun onOrientationChanged(orientation: Int) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - mLastTime < 300) return  //300毫秒检测一次

        if (mOnOrientationChangeListener != null) {
            mOnOrientationChangeListener!!.onOrientationChanged(orientation)
        }
        mLastTime = currentTime
    }


    interface OnOrientationChangeListener {
        fun onOrientationChanged(orientation: Int)
    }

    fun setOnOrientationChangeListener(onOrientationChangeListener: OnOrientationChangeListener?) {
        mOnOrientationChangeListener = onOrientationChangeListener
    }
}
