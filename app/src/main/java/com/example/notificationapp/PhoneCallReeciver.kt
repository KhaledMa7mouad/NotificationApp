package com.example.notificationapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.telephony.TelephonyManager
import android.widget.Toast

class PhoneCallReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {    // with the Intent filter you want it to work with
        if (intent!!.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                Toast.makeText(context, "NewCall", Toast.LENGTH_SHORT).show()
            }


        }

    }

}