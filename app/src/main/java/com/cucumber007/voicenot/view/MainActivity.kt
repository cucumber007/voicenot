package com.cucumber007.voicenot.view

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import com.cucumber007.voicenot.R
import com.cucumber007.voicenot.SharedPrefs
import com.cucumber007.voicenot.utils.Logger
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logger.d("Start")


        /*setSupportActionBar(toolbar as Toolbar)

        switch_active.setChecked(SharedPrefs.isServiceActiveSetting.load())
        switch_active.setOnCheckedChangeListener { compoundButton, b ->
            SharedPrefs.isServiceActiveSetting.save(b)
        }

        switch_title.setChecked(SharedPrefs.spellTitleSetting.load())
        switch_headphones.setChecked(SharedPrefs.headphonesOnlySetting.load())
        startVoiceNotService()

        switch_headphones.setOnCheckedChangeListener { compoundButton, b ->
            SharedPrefs.headphonesOnlySetting.save(b)
        }

        switch_title.setOnCheckedChangeListener { compoundButton, b ->
            SharedPrefs.spellTitleSetting.save(b)
        }

        permissions_button.setOnClickListener {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }

        b_tts_settings.setOnClickListener {
            val intent = Intent()
            intent.action = "com.android.settings.TTS_SETTINGS"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        app_whitelist_button.setOnClickListener {
            startActivity(Intent(this, AppWhitelistActivity::class.java))
        }

        listOf(b_test_en, b_test_ru, b_test_ua, b_test_link).forEach {
            it.setOnClickListener {  view ->
                val random = Random().nextInt(1000)
                when (view.id) {
                    R.id.b_test_en -> sendTestNotification("English test notification", "Hey, it's working! $random")
                    R.id.b_test_ru -> sendTestNotification("Russian test notification", "Оно работает! $random")
                    R.id.b_test_ua -> sendTestNotification("Ukrainian test notification", "Воно працює! $random")
                    R.id.b_test_link -> sendTestNotification("Link test notification", "Link : https://google.com/andbdvsyaskjdnasd$random")
                }
            }
        }*/

    }

   /* private fun startVoiceNotService() {
        val serviceIntent = Intent(this, VoiceNotService::class.java)
        startService(serviceIntent)
    }

    private val isVoiceNotServiceRunning: Boolean
        private get() {
            val serviceClass: Class<*> = VoiceNotService::class.java
            val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }

    private fun sendTestNotification(title: String, text: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance)
                notificationManager.createNotificationChannel(channel)
            }
            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            notificationBuilder.setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.drawable.icon)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            notificationManager.notify(0, notificationBuilder.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = "VoiceNot"
        const val BROADCAST_SERVICE_UPDATE_SETTINGS = "com.cucumber007.update_settings_broadcast"
    }*/
}