package com.geeui.face.gesture


import android.util.Log
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class GestureDataThreadExecutor private constructor() : Executor {
    var currentTask: Future<*>? = null
    private val mExecutorService: ExecutorService =
        Executors.newSingleThreadExecutor()

    override fun execute(task: Runnable) {
        if (currentTask != null && !currentTask!!.isDone) {
            Log.d("GestureDataThreadExecutor", "execute: cancel thread")
            currentTask!!.cancel(true)
        } else {
            Log.d("GestureDataThreadExecutor", "execute: thread empty")
        }
        currentTask = mExecutorService.submit(task)
    }

    companion object {
        private var gestureDataThreadExecutor: GestureDataThreadExecutor? = null

        val instance: GestureDataThreadExecutor
            get() {
                if (gestureDataThreadExecutor == null) {
                    gestureDataThreadExecutor =
                        GestureDataThreadExecutor()
                }
                return gestureDataThreadExecutor!!
            }
    }
}
