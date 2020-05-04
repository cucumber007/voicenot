package com.cucumber007.voicenot

class VoicenotApplication: CustomApplication() {

    override fun createAppConfig() = if(!BuildConfig.DEBUG) {
        AppConfig()
    } else {
        AppConfig(
                debugMode = true
        )
    }
}