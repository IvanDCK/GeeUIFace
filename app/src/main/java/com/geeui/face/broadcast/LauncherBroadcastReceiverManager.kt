package com.geeui.face.broadcast

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

/**
 * 广播管理器
 *
 * @author liujunbin
 */
class LauncherBroadcastReceiverManager(var mContext: Context) {
    init {
        init(mContext)
    }

    private fun init(context: Context) {
        setNetWorkChangeListener()
        setWifiChangeListener()
        setTimeListener()
    }

    private fun setNetWorkChangeListener() {
        val netChangeReceiver = NetWorkChangeReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        //        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

        mContext.registerReceiver(netChangeReceiver, intentFilter)
    }

    private fun setWifiChangeListener() {
        val wifiReceiver = WifiReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        mContext.registerReceiver(wifiReceiver, intentFilter)
    }

    private fun setTimeListener() {
        val mTimeReceiver = TimerReceiver()
        val timeFilter = IntentFilter()
        timeFilter.addAction(Intent.ACTION_TIME_TICK)
        mContext.registerReceiver(mTimeReceiver, timeFilter)
    }


    companion object {
        private var instance: LauncherBroadcastReceiverManager? = null

        fun getInstance(context: Context): LauncherBroadcastReceiverManager {
            synchronized(LauncherBroadcastReceiverManager::class.java) {
                if (instance == null) {
                    instance = LauncherBroadcastReceiverManager(context.applicationContext)
                }
                return instance!!
            }
        }
    }
}
