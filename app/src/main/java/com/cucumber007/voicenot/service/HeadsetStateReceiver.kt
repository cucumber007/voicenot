package com.cucumber007.voicenot.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cucumber007.voicenot.utils.Toaster

class HeadsetStateReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_HEADSET_PLUG) {
            val state = intent.getIntExtra("state", -1)
            when (state) {
                1 ->
                    onPlugged()
                else -> {
                    onUnplugged()
                }
            }
        }
    }

    private fun onPlugged() {
        Toaster.show("lol")
    }

    private fun onUnplugged() {
        Toaster.show("lol1")
    }
}