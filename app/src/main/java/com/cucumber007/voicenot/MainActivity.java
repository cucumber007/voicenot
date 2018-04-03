package com.cucumber007.voicenot;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;

import com.cucumber007.reusables.utils.logging.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private Context context = this;

    public static final String BROADCAST_SERVICE_UPDATE_SETTINGS = "com.cucumber007.update_settings_broadcast";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.switch_active) Switch switchActive;
    @BindView(R.id.switch_title) Switch switchTitle;
    @BindView(R.id.switch_headphones) Switch switchHeadphones;

    private PreferencesModel preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        preferences = PreferencesModel.getInstance();

        switchActive.setChecked(preferences.isServiceActiveSetting().load());
        switchTitle.setChecked(preferences.spellTitleSetting().load());
        switchHeadphones.setChecked(preferences.headphonesOnlySetting().load());

        startVoiceNotService();
    }

    private void startVoiceNotService() {
        Intent serviceIntent = new Intent(this, VoiceNotService.class);
        startService(serviceIntent);
    }

    private boolean isVoiceNotServiceRunning() {
        Class serviceClass = VoiceNotService.class;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void sendTestNotification(String title, String text) {
        Notification.Builder notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notificationBuilder.build());
    }

    ///////////////////////////////////////////////////////////////////////////
    // OnClick
    ///////////////////////////////////////////////////////////////////////////

    @OnCheckedChanged(R.id.switch_active)
    void onActive(boolean value) {
        preferences.isServiceActiveSetting().save(value);
    }

    @OnCheckedChanged(R.id.switch_headphones)
    void onHeadphones(boolean value) {
        preferences.headphonesOnlySetting().save(value);
    }

    @OnCheckedChanged(R.id.switch_title)
    void onTitle(boolean value) {
        preferences.spellTitleSetting().save(value);
    }

    @OnClick(R.id.permissions_button)
    public void onClick1() {
        final Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }


    @OnClick(R.id.app_whitelist_button)
    public void onClick3() {
        startActivity(new Intent(this, AppWhitelistActivity.class));
    }

    @OnClick({R.id.b_test_en, R.id.b_test_ru, R.id.b_test_ua})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_test_en:
                sendTestNotification("English test notification", "Hey, it's working!");
                break;
            case R.id.b_test_ru:
                sendTestNotification("Russian test notification", "Оно работает!");
                break;
            case R.id.b_test_ua:
                sendTestNotification("Ukrainian test notification", "Воно працює!");
                break;
        }
    }
}
