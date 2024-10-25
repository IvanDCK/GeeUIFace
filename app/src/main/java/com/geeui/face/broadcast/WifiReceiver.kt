package com.geeui.face.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.util.Log

class WifiReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (WifiManager.WIFI_STATE_CHANGED_ACTION == action) {
            // WiFi 状态变化
            val wifiState =
                intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
            when (wifiState) {
                WifiManager.WIFI_STATE_ENABLED -> {}
                WifiManager.WIFI_STATE_DISABLED -> {}
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION == action) {
            // 网络状态变化
            val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
            if (networkInfo != null) {
                if (networkInfo.state == NetworkInfo.State.CONNECTED) {
                    // 已连接到 WiFi 网络
                    Log.e("letianpai_net", "net_Connect")
                    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val wifiInfo = wifiManager.connectionInfo
                    val ssid = wifiInfo.ssid // 获取当前连接的 WiFi 网络的 SSID
                    NetworkChangingUpdateCallback.instance.setNetworkStatus(
                        NetworkChangingUpdateCallback.NETWORK_TYPE_WIFI,
                        3
                    )
                } else if (networkInfo.state == NetworkInfo.State.DISCONNECTED) {
                    Log.e("letianpai_net", "net_Disconnect")
                    // WiFi 网络已断开连接
                    NetworkChangingUpdateCallback.instance.setNetworkStatus(
                        NetworkChangingUpdateCallback.NETWORK_TYPE_DISABLED,
                        3
                    )
                }
            }
        }
    }
}