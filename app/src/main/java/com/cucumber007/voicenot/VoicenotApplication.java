package com.cucumber007.voicenot;

import com.cucumber007.reusables.ContextApplication;
import com.cucumber007.reusables.utils.logging.LogUtil;

public class VoicenotApplication extends ContextApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.setDebugMode(true);
        LogUtil.setContext(getContext());
        LogUtil.setTag("voicenot_tag");
    }
}
