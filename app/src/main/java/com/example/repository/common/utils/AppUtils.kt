package com.example.repository.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.Html
import android.text.Spanned
import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


object AppUtils {

    @SuppressLint("MissingPermission")
    fun isConnectingToInternet(context: Context): Boolean {
        val connectivity = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo

        return info?.isConnected ?: false
    }


    fun getNetworkClass(context: Context): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        if (info == null || !info.isConnected)
            return "NOT_CONNECTED" //not connected
        if (info.type == ConnectivityManager.TYPE_WIFI)
            return "WIFI"
        if (info.type == ConnectivityManager.TYPE_MOBILE) {
            return when (info.subtype) {
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN //api<8 : replace by 11
                -> "2G"
                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B //api<9 : replace by 14
                    , TelephonyManager.NETWORK_TYPE_EHRPD  //api<11 : replace by 12
                    , TelephonyManager.NETWORK_TYPE_HSPAP  //api<13 : replace by 15
                -> "3G"
                TelephonyManager.NETWORK_TYPE_LTE    //api<11 : replace by 13
                -> "4G"
                else -> "OTHERS"
            }
        }
        return "OTHERS"
    }

    fun String.capitalize(): String {
        return if (isNotEmpty() && this[0].isLowerCase()) substring(0, 1).toUpperCase() + substring(
            1
        ) else this
    }


    fun Any.fromHtml(source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(source)
        }
    }

     fun showSnackBar(view: View) {
        val snackBar = Snackbar
            .make(
                view,
                "please check your internet connection",
                Snackbar.LENGTH_LONG
            )
        snackBar.show()
    }



}