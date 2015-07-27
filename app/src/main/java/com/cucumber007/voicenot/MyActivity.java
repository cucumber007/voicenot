package com.cucumber007.voicenot;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;


public class MyActivity extends Activity {

    Intent serviceIntent;
    SharedPreferences settings;
    Editor editor;
    //Intent intent = new Intent("com.cucumber007.service_status_broadcast");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = this.getSharedPreferences("settings", Context.MODE_PRIVATE);

        setContentView(R.layout.activity_my);
        serviceIntent = new Intent(this, VoiceNotService.class);
        startService(serviceIntent);

        ((Switch) findViewById(R.id.switch1)).setChecked(settings.getBoolean("status", false));
        ((Switch) findViewById(R.id.switch2)).setChecked(settings.getBoolean("author", false));

        Intent intent = new Intent("com.cucumber007.service_status_broadcast");
        intent.putExtra("status", ((Switch) findViewById(R.id.switch1)).isChecked());
        intent.putExtra("spell_author", ((Switch) findViewById(R.id.switch2)).isChecked());
        sendBroadcast(intent);

    }

    @Override
    protected void onStop() {
        editor = settings.edit();
        editor.putBoolean("status", ((Switch) findViewById(R.id.switch1)).isChecked());
        editor.putBoolean("author", ((Switch) findViewById(R.id.switch2)).isChecked());
        editor.commit();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void startPermissionActivityButton(View v) {
        final Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }


    public void sendInfoToService (View v) {
        Intent intent = new Intent("com.cucumber007.service_status_broadcast");
        intent.putExtra("status", ((Switch) findViewById(R.id.switch1)).isChecked());
        intent.putExtra("spell_author", ((Switch) findViewById(R.id.switch2)).isChecked());
        sendBroadcast(intent);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
