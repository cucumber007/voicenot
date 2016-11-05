package com.cucumber007.voicenot;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

public class AppWhitelistActivity extends AppCompatActivity {

    private Context context = this;
    private CheckableRecyclerViewAdapter<AppData> adapter;

    public static final String PARAMETER_APP_WHITELIST = "app_whitelist";
    public static final String BROADCAST_SERVICE_UPDATE_APP_WHITELIST = "com.cucumber007.update_app_whitelist";

    @BindView(R.id.app_recycler_view) RecyclerView appRecyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_whitelist);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Observable.from(packages).map(appinfo -> {
            try {
                return new AppData(pm.getApplicationLabel(appinfo).toString(), appinfo.packageName, pm.getApplicationIcon(appinfo.packageName));
            } catch (PackageManager.NameNotFoundException|NullPointerException e) {
                throw Exceptions.propagate(e);
            }
        })
                //.filter(item->item.getTitle().toLowerCase().contains("v") || item.getTitle().toLowerCase().contains("в"))
                .filter(item-> !item.getTitle().contains("com") && !item.getTitle().contains("org") && !item.getTitle().contains("net"))
                .toSortedList().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(data -> {
            adapter = new CheckableRecyclerViewAdapter<AppData>(context, data, (position, state) -> {
                getSupportActionBar().setTitle(adapter.getCheckedCount()+" items checked");
                Observable.from(adapter.getCheckedItems()).map(AppData::getPackageName).toList()
                        .flatMap(list->Observable.just(new HashSet<>(list))).subscribe(set-> {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(PARAMETER_APP_WHITELIST, set).commit();
                    Intent intent = new Intent(BROADCAST_SERVICE_UPDATE_APP_WHITELIST);
                    sendBroadcast(intent);
                });
            });
            appRecyclerView.setAdapter(adapter);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    class AppData implements CheckableRecyclerViewAdapter.CheckableAdapterItem, Comparable<AppData> {
        private String appName;
        private String packageName;
        private Drawable appIcon;

        public AppData(String appName, String packageName, Drawable appIcon) {
            this.appName = appName;
            this.packageName = packageName;
            this.appIcon = appIcon;
        }

        @Override
        public Drawable getImage() {
            return appIcon;
        }

        @Override
        public String getTitle() {
            return appName;
        }

        public String getPackageName() {
            return packageName;
        }

        @Override
        public int compareTo(AppData o) {
            return appName.compareTo(o.getTitle());
        }
    }
}
