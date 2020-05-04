package com.cucumber007.voicenot

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.cucumber007.voicenot.utils.Toaster

open class CustomApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        appConfig = createAppConfig()

        if(appConfig.mockMode) {
            Toaster.show("Mock mode!")
        }
    }

    open fun createAppConfig() = AppConfig()

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        lateinit var appConfig: AppConfig

    }

    data class AppConfig(
        val debugMode: Boolean = false,
        val mockMode: Boolean = false
    ) {
        val releaseMode = !debugMode
        val notMockMode = releaseMode || (debugMode && !mockMode)
    }
}