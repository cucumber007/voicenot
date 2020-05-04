package com.cucumber007.voicenot.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.view.KeyEvent;

import com.cucumber007.voicenot.SharedPrefs;
import com.cucumber007.voicenot.view.AppWhitelistActivity;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class VoiceNotService extends NotificationListenerService {

    public static final int MIN_NOTIFICATION_TIMEOUT = 2000;

    private boolean headphonesPlugged;

    private Context context = this;

    private Tts tts;
    private Set<String> appWhiteList = new HashSet<>();
    private String lastMessage;
    private long lastMessageTimeMillis;

    private Disposable timerSubscription;
    private BroadcastReceiver appWhitelistReceiver;
    private BroadcastReceiver musicIntentReceiver;

    private String linkRegex = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,4}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";


    @Override
    public void onCreate() {
//        LogUtil.logDebug("Service created");
        tts = new Tts(this);

        appWhiteList = new HashSet(SharedPrefs.INSTANCE.getAppWhitelistSetting().load());

        appWhitelistReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                appWhiteList = new HashSet(SharedPrefs.INSTANCE.getAppWhitelistSetting().load());
            }
        };
        registerReceiver(appWhitelistReceiver, new IntentFilter(AppWhitelistActivity.BROADCAST_SERVICE_UPDATE_APP_WHITELIST));

        musicIntentReceiver = new MusicIntentReceiver();
        registerReceiver(musicIntentReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //todo handle static notifications
        //todo performance
//        LogUtil.logDebug("Notification got");

        if (!SharedPrefs.INSTANCE.isServiceActiveSetting().load()) return;
        if ((System.currentTimeMillis() - lastMessageTimeMillis)
                < MIN_NOTIFICATION_TIMEOUT) return;
        lastMessageTimeMillis = System.currentTimeMillis();

        String text = sbn.getNotification().extras.getString("android.text");
        String title = sbn.getNotification().extras.getString("android.title");
        if (text == null) return;
        if (title == null) title = "";

        String sourceAppPackage = sbn.getPackageName();

        String message;
        if (SharedPrefs.INSTANCE.getSpellTitleSetting().load())
            message = title + "." + text;
        else message = text;

        String finalMessage = message.replaceAll(linkRegex, " LINK ");

        if (message.equals(lastMessage)) return;

        timerSubscription = Observable.timer(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> {
//                    LogUtil.logError(throwable);
                    return Observable.never();
                })
                .subscribe(it -> {
            if ((headphonesPlugged || !SharedPrefs.INSTANCE.getHeadphonesOnlySetting().load())
                    && appWhiteList.contains(sourceAppPackage)) {
                //LogUtil.logDebug("Utterance started");
                lastMessage = finalMessage;
                tts.speak(finalMessage);
            }
        });

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {}

    @Override
    public void onDestroy() {
        unregisterReceiver(appWhitelistReceiver);
        unregisterReceiver(musicIntentReceiver);
        tts.destroy();
        if (timerSubscription != null) timerSubscription.dispose();
//        LogUtil.logDebug("Service destroyed");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Utils
    ///////////////////////////////////////////////////////////////////////////

    private static void sendMediaButton(Context context, int keyCode) {
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendBroadcast(intent);

        keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendBroadcast(intent);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Receivers
    ///////////////////////////////////////////////////////////////////////////

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 1:
                        //Log.d(TAG, "Headset is headphonesPlugged");
                        headphonesPlugged = true;
                        break;
                    case 0:
                    default:
                        //Log.d(TAG, "Headset is unplugged");
                        headphonesPlugged = false;
                        tts.stopUtterance();
                        break;
                }
            }
        }
    }
}
