package com.cucumber007.voicenot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class VoiceNotService extends NotificationListenerService implements AudioManager.OnAudioFocusChangeListener  {

    boolean spellAuthor = false;
    boolean isServiceActive = true;

    boolean earphonesPlugged = false;
    boolean wasActive = false;

    Context context = this;
    String message;
    TextToSpeech ttsEngine;
    AudioManager audioManager;
    MusicIntentReceiver musicIntentReceiver;
    List<String> appWhiteList = new ArrayList<String>();

    private BroadcastReceiver updateSettingsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isServiceActive = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(MainActivity.PARAMETER_SERVICE_STATUS, true);
            spellAuthor = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(MainActivity.PARAMETER_SPELL_TITLE, true);
        }
    };


    @Override
    public void onCreate() {
        ttsEngine = new TextToSpeech(this, this::onTtsInit);

        IntentFilter intFilt = new IntentFilter(MainActivity.BROADCAST_SERVICE_UPDATE_SETTINGS);
        registerReceiver(updateSettingsReceiver, intFilt);

        musicIntentReceiver = new MusicIntentReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(musicIntentReceiver, filter);

        audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);

        appWhiteList.add("com.vkontakte.android");
        appWhiteList.add("com.cucumber007.voicenot");

        Log.d("cutag", "Service created");
    }

    @Override
    public void onDestroy() {
        Log.d("cutag", "Sevice destroyed");
    }

    public void onTtsInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            LogUtil.logDebug("TTS succesfully inited");
            int tts_result = ttsEngine.setLanguage(new Locale("en"));

            ttsEngine.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone (String utteranceId) {
                    if(wasActive) {
                        sendMediaButton(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                    }
                }

                @Override
                public void onStart (String utteranceId) {}

                @Override
                public void onError (String utteranceId) {
                    Log.d("cutag", "error");
                }
            }
            );

            if (tts_result == TextToSpeech.LANG_MISSING_DATA
                    || tts_result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("cutag", "Извините, этот язык не поддерживается");
            }
        } else {
            Log.e("cutag", "Ошибка!");
        }
    }



    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        LogUtil.logDebug("Notification got");
        if(isServiceActive) {

            Bundle notification_info = sbn.getNotification().extras;
            String text = notification_info.getString("android.text");
            String author = notification_info.getString("android.title");

            String sourceApp = sbn.getPackageName();

            Log.d("cutag", text);
            if(detectRussian(text+author)) {
                int tts_result = ttsEngine.setLanguage(new Locale("ru"));
            }
            else {
                int tts_result = ttsEngine.setLanguage(new Locale("en"));
            }


            if (text.length() > 250) text = text.substring(0, 250) + ". Достигнуто ограничение длины сообщения.";

            if(spellAuthor) message = "от " + author + "." + text;
            else message = text;

            Log.d("cutag", message);

            /*wasActive = audioManager.isMusicActive();
            wasActive = false;
            if (wasActive) {
                sendMediaButton(context, KeyEvent.KEYCODE_MEDIA_PAUSE);
            }*/

            //ЛИСТАЙ ДАЛЬШЕ, А СЮДА НЕ СМОТРИ
            long time = System.currentTimeMillis();
            long delta = 0;
            while (delta < 1000) {
                delta = System.currentTimeMillis() - time;
            }

            if (earphonesPlugged && appWhiteList.contains(sourceApp)) {
                LogUtil.logDebug("Utterance started");
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "myID");
                ttsEngine.speak(message, TextToSpeech.QUEUE_ADD, params);
                message = "";
            }

        /* int result = audioManager.requestAudioFocus(this,
            AudioManager.STREAM_SYSTEM,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
        */
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        /*Log.d("cutag", "focus: "+focusChange);
        if (earphonesPlugged && focusChange == 1) {

        }*/
    }


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

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    };

    private boolean detectRussian(String text) {
        boolean res = false;
        char[] chars = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray();
        //todo
        for (int i = 0; i < chars.length; i++) {
            res = text.contains(String.valueOf(chars[i]));
            if(res) break;
        }
        return res;
    }

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        //Log.d(TAG, "Headset is unplugged");
                        earphonesPlugged = false;
                        break;
                    case 1:
                        //Log.d(TAG, "Headset is earphonesPlugged");
                        earphonesPlugged = true;
                        break;
                    default:
                        //Log.d(TAG, "I have no idea what the headset state is");
                        earphonesPlugged = false;
                }
            }
        }
    }
}
