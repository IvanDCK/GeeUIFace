package com.geeui.face.gesture

import com.renhejia.robot.gesturefactory.parser.GestureData

/**
 * @author liujunbin
 */
class GestureCallback private constructor() {
    private var mGestureResponseListener: GestureResponseListener? = null
    private var mGestureCompleteListener: GestureCompleteListener? = null

    private object GestureCallbackHolder {
        val instance: GestureCallback = GestureCallback()
    }

    interface GestureResponseListener {
        fun onGestureReceived(gesture: String?)

        fun onGestureReceived(gesture: String?, gestureId: Int)

        fun onGesturesReceived(list: ArrayList<GestureData?>?, taskId: Int)

        fun onGesturesReceived(gestureData: GestureData?)
    }

    fun setGestureListener(listener: GestureResponseListener?) {
        this.mGestureResponseListener = listener
    }

    fun setGestureCompleteListener(listener: GestureCompleteListener?) {
        this.mGestureCompleteListener = listener
    }

    fun setGesture(gesture: String?) {
        if (mGestureResponseListener != null) {
            mGestureResponseListener!!.onGestureReceived(gesture)
        }
    }

    fun setGesture(gesture: String?, geTaskId: Int) {
        if (mGestureResponseListener != null) {
            mGestureResponseListener!!.onGestureReceived(gesture, geTaskId)
        }
    }

    fun setGestures(list: ArrayList<GestureData?>?, taskId: Int) {
        if (mGestureResponseListener != null) {
            mGestureResponseListener!!.onGesturesReceived(list, taskId)
        }
    }

    fun setGesture(gestureData: GestureData?) {
        if (mGestureResponseListener != null) {
            mGestureResponseListener!!.onGesturesReceived(gestureData)
        }
    }

    interface GestureCompleteListener {
        fun onGestureCompleted(gesture: String?, geTaskId: Int)
    }

    fun setGesturesComplete(gesture: String?, geTaskId: Int) {
        if (mGestureCompleteListener != null) {
            mGestureCompleteListener!!.onGestureCompleted(gesture, geTaskId)
        }
    }

    companion object {
        val instance: GestureCallback
            get() = GestureCallbackHolder.instance
    }
}
