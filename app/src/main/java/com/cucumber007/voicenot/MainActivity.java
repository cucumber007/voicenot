package com.cucumber007.voicenot;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences settings;
    private Editor editor;
    private Context context = this;

    public static final String PARAMETER_SERVICE_STATUS = "service_status";
    public static final String PARAMETER_SPELL_TITLE = "spell_title";
    public static final String PARAMETER_SPELL_HEADPHONES_ONLY = "headphones_only";

    public static final String BROADCAST_SERVICE_UPDATE_SETTINGS = "com.cucumber007.update_settings_broadcast";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.switch_active) Switch switchActive;
    @BindView(R.id.switch_title) Switch switchTitle;
    @BindView(R.id.switch_headphones) Switch switchHeadphones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        switchActive.setChecked(settings.getBoolean(PARAMETER_SERVICE_STATUS, false));
        switchTitle.setChecked(settings.getBoolean(PARAMETER_SPELL_TITLE, false));
        switchHeadphones.setChecked(settings.getBoolean(PARAMETER_SPELL_HEADPHONES_ONLY, false));

        startVoiceNotService();
    }

    @Override
    protected void onStop() {
        commitSettings();
        super.onStop();
    }

    private void commitSettings() {
        editor = settings.edit();
        editor.putBoolean(PARAMETER_SERVICE_STATUS, switchActive.isChecked());
        editor.putBoolean(PARAMETER_SPELL_TITLE, switchTitle.isChecked());
        editor.putBoolean(PARAMETER_SPELL_HEADPHONES_ONLY, switchHeadphones.isChecked());
        editor.commit();
    }

    private void startVoiceNotService() {
        Intent serviceIntent = new Intent(this, VoiceNotService.class);
        startService(serviceIntent);
    }

    private void sendUpdateSettingsBroadcast() {
        commitSettings();
        Intent intent = new Intent(BROADCAST_SERVICE_UPDATE_SETTINGS);
        sendBroadcast(intent);
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

    private void sendTestNotification() {
        Notification.Builder notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setContentTitle("Hey")
                .setContentText("It's working!")
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

    @OnClick({R.id.switch_active, R.id.switch_title, R.id.switch_headphones})
    public void onClick() {
        sendUpdateSettingsBroadcast();
    }

    @OnClick(R.id.permissions_button)
    public void onClick1() {
        final Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }

    @OnClick(R.id.notification_button)
    public void onClick2() {
        LogUtil.logDebug("Service active = ", isVoiceNotServiceRunning());
        sendTestNotification();
    }


}
