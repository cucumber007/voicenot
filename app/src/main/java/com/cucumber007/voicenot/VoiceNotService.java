package com.cucumber007.voicenot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.cucumber007.reusables.utils.Callback;
import com.cucumber007.reusables.utils.logging.LogUtil;
import com.jakewharton.rxrelay2.BehaviorRelay;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import rx.Subscription;


public class VoiceNotService extends NotificationListenerService {

    public static final int MIN_NOTIFICATION_TIMEOUT = 2000;

    private boolean headphonesPlugged;

    private Context context = this;

    private Tts tts;
    private Set<String> appWhiteList = new HashSet<>();
    private PreferencesModel preferences;
    private String lastMessage;
    private long lastMessageTimeMillis;

    @Override
    public void onCreate() {
        LogUtil.logDebug("Service created");
        preferences = PreferencesModel.getInstance();
        tts = new Tts(this);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                appWhiteList = settings.getStringSet(AppWhitelistActivity.PARAMETER_APP_WHITELIST, new HashSet<>());
                appWhiteList.add(getPackageName());
            }
        }, new IntentFilter(AppWhitelistActivity.BROADCAST_SERVICE_UPDATE_APP_WHITELIST));

        registerReceiver(new MusicIntentReceiver(), new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        appWhiteList.add(getPackageName());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        LogUtil.logDebug("Notification got");

        if (!preferences.isServiceActiveSetting().load()) return;
        if ((System.currentTimeMillis() - lastMessageTimeMillis)
                < MIN_NOTIFICATION_TIMEOUT) return;
        lastMessageTimeMillis = System.currentTimeMillis();

        String text = sbn.getNotification().extras.getString("android.text");
        String title = sbn.getNotification().extras.getString("android.title");
        if (text == null) return;
        if (title == null) title = "";

        String sourceAppPackage = sbn.getPackageName();

        String message;
        if (preferences.spellTitleSetting().load())
            message = title + "." + text;
        else message = text;

        if (message.equals(lastMessage)) return;

        if ((headphonesPlugged || preferences.headphonesOnlySetting().load())
                && appWhiteList.contains(sourceAppPackage)) {
            //LogUtil.logDebug("Utterance started");
            lastMessage = message;
            tts.speak(message);
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {}

    @Override
    public void onDestroy() {
        tts.destroy();
        LogUtil.logDebug("Service destroyed");
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
    // Recievers
    ///////////////////////////////////////////////////////////////////////////

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        //Log.d(TAG, "Headset is unplugged");
                        headphonesPlugged = false;
                        break;
                    case 1:
                        //Log.d(TAG, "Headset is headphonesPlugged");
                        headphonesPlugged = true;
                        break;
                    default:
                        //Log.d(TAG, "I have no idea what the headset state is");
                        headphonesPlugged = false;
                }
            }
        }
    }
}
