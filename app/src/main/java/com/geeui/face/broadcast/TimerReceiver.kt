package com.geeui.face.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class TimerReceiver : BroadcastReceiver() {
    var calendar: Calendar? = null
    override fun onReceive(context: Context, intent: Intent) {
        calendar = Calendar.getInstance()
        val hour = calendar!!.get(Calendar.HOUR_OF_DAY)
        val min = calendar!!.get(Calendar.MINUTE)

        TimerKeeperCallback.instance.setTimerKeeper(hour, min)
    }
}
