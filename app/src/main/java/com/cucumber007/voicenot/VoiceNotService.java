package com.cucumber007.voicenot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
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


public class VoiceNotService extends NotificationListenerService implements
        TextToSpeech.OnInitListener, AudioManager.OnAudioFocusChangeListener  {

    boolean active = true;
    boolean plugged = false;
    boolean wasActive = false;
    boolean spellAuthor = false;
    Context context = this;
    String message;
    TextToSpeech tts;
    AudioManager audioManager;
    MusicIntentReceiver myReceiver;
    List<String> appWhiteList = new ArrayList<String>();

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)  {
           active = intent.getBooleanExtra("status", true);
           spellAuthor = intent.getBooleanExtra("spell_author", true);
           //Log.d("cutag", ""+active+spellAuthor);
        }
    };

    @Override
    public void onCreate() {
        tts = new TextToSpeech(this, this);

        IntentFilter intFilt = new IntentFilter("com.cucumber007.service_status_broadcast");
        registerReceiver(mMessageReceiver, intFilt);

        myReceiver = new MusicIntentReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);

        audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);

        appWhiteList.add("com.vkontakte.android");

        Log.d("cutag", "Service created");
    }

    @Override
    public void onDestroy() {
        //super.onDestroy();
        Log.d("cutag", "Sevice destroyed");
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int tts_result = tts.setLanguage(new Locale("en"));

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
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
                Log.d("TTS", "Извините, этот язык не поддерживается");
            }
        } else {
            Log.e("TTS", "Ошибка!");
        }

    }



    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if(active) {
            //Log.d("cutag", "Posted");
            Bundle notification_info = sbn.getNotification().extras;
            String text = notification_info.getString("android.text");
            String author = notification_info.getString("android.title");

            String sourceApp = sbn.getPackageName();
            //com.vkontakte.android

            //Log.d("cutag", text);
            if(detectRussian(text+author)) {
                int tts_result = tts.setLanguage(new Locale("ru"));
            }
            else {
                int tts_result = tts.setLanguage(new Locale("en"));
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
            while (delta < 4000) {
                delta = System.currentTimeMillis() - time;
            }

            if (plugged && appWhiteList.contains(sourceApp)) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "myID");
                tts.speak(message, TextToSpeech.QUEUE_ADD, params);
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
        if (plugged && focusChange == 1) {

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
                        plugged = false;
                        break;
                    case 1:
                        //Log.d(TAG, "Headset is plugged");
                        plugged = true;
                        break;
                    default:
                        //Log.d(TAG, "I have no idea what the headset state is");
                        plugged = false;
                }
            }
        }
    }
}
