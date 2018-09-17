package com.cucumber007.voicenot;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;

import com.cucumber007.reusables.utils.Callback;
import com.cucumber007.reusables.utils.logging.LogUtil;
import com.jakewharton.rxrelay2.BehaviorRelay;

import java.util.HashMap;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class Tts {

    private final static String EN = "en";
    private final static String RU = "ru";
    private final static String UA = "ua";

    private static final char[] RUSSIAN_CHARS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя".toCharArray();
    private static final char[] UKRAINIAN_CHARS = "ґєії".toCharArray();

    public static final int MAX_MESSAGE_LENGTH = 256;

    @Nullable private TextToSpeech englishTtsEngine;
    @Nullable private TextToSpeech russianTtsEngine;
    @Nullable private TextToSpeech ukrainianTtsEngine;
    private Context context;
    private Disposable subscription;
    private TextToSpeech textToSpeech;


    public Tts(Context context) {
        this.context = context;

        subscription = Observable.zip(
                createTtsObservable(EN).doOnNext(it -> englishTtsEngine = it),
                createTtsObservable(RU).doOnNext(it -> russianTtsEngine = it),
                createTtsObservable(UA).doOnNext(it -> ukrainianTtsEngine = it),
                (textToSpeech, textToSpeech2, textToSpeech3) -> "Finish"
        ).subscribe();
    }

    public void speak(String text) {
        String language = detectTextLanguage(text);
        switch (language) {
            case EN: textToSpeech = englishTtsEngine; break;
            case RU: textToSpeech = russianTtsEngine; break;
            case UA: textToSpeech = ukrainianTtsEngine; break;
            default: textToSpeech = englishTtsEngine; break;
        }

        if (text.length() > MAX_MESSAGE_LENGTH) {
            text = text.substring(0, MAX_MESSAGE_LENGTH) + getEndIndicatorText(language);
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "myID");
        if (textToSpeech != null) textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, params);
        else {
            if(englishTtsEngine != null) englishTtsEngine.speak("Text to speech error",
                    TextToSpeech.QUEUE_ADD, params);
        }
    }

    public void stopUtterance() {
        if (textToSpeech != null) textToSpeech.stop();
    }

    private Observable<TextToSpeech> createTtsObservable(String language) {
        BehaviorRelay<TextToSpeech> relay = BehaviorRelay.create();
        new TtsWrapper(context, language, relay::accept);
        return relay;
    }

    class TtsWrapper {

        private TextToSpeech tts;

        public TtsWrapper(Context context, String language, Callback<TextToSpeech> callback) {
            tts = new TextToSpeech(context, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    LogUtil.logDebug("TTS successfully inited");
                    int tts_result = tts.setLanguage(
                            language.equals(UA) ? new Locale("ukr") : new Locale(language)
                    );

                    if (tts_result != TextToSpeech.SUCCESS) {
                        LogUtil.logDebug("This language is not supported, error code = ",
                                tts_result, language);
                    } else {
                        if (tts != null) callback.onReady(tts);
                        else LogUtil.logDebug("TTS = null while init()!");
                    }
                } else {
                    LogUtil.logDebug("Unknown error");
                }
            });
        }
    }


    private String getEndIndicatorText(String language) {
        switch (language) {
            case RU:
                return ". Троеточие.";
            case UA:
                return ". Три крапки.";
            case EN:
                return " ...";
        }
        return "Locale error";
    }

    private String detectTextLanguage(String text) {
        if (detectSymbols(text, UKRAINIAN_CHARS)) return UA;
        if (detectSymbols(text, RUSSIAN_CHARS)) return RU;
        return EN;
    }

    private boolean detectSymbols(String text, char[] symbols) {
        for (int i = 0; i < text.length() / 10 + 1; i++) {
            String part;
            if ((i + 1) * 10 > text.length()) {
                part = text.substring(i * 10).toLowerCase();
            } else part = text.substring(i * 10, (i + 1) * 10).toLowerCase();

            for (int j = 0; j < symbols.length; j++) {
                if (part.contains(String.valueOf(symbols[j]))) return true;
            }
        }
        return false;
    }

    public void destroy() {
        if (subscription != null) subscription.dispose();
    }
}
