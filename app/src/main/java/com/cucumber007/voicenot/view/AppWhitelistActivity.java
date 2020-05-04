package com.cucumber007.voicenot.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.cucumber007.voicenot.R;
import com.cucumber007.voicenot.SharedPrefs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;

public class AppWhitelistActivity extends AppCompatActivity {

    private Context context = this;
    private AppsAdapter adapter;

    public static final String PARAMETER_APP_WHITELIST = "app_whitelist";
    public static final String BROADCAST_SERVICE_UPDATE_APP_WHITELIST = "com.cucumber007.update_app_whitelist";

    RecyclerView appRecyclerView;
    Toolbar toolbar;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_whitelist);

        appRecyclerView = findViewById(R.id.app_recycler_view);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar2);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Set<String> selectedAppPackages = new HashSet(SharedPrefs.INSTANCE.getAppWhitelistSetting().load());

        appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new AppsAdapter();
        adapter.setOnItemSelectedListener((AppItem item, boolean isSelected, Set<Integer> selectedPositions, List<? extends AppItem> items) -> {
            getSupportActionBar().setTitle(selectedPositions.size() + " items checked");
            SharedPrefs.INSTANCE.getAppWhitelistSetting().save(new ArrayList(new HashSet<String>(
                    Observable.fromIterable(selectedPositions)
                            .map(it -> ((AppItem) items.get(it)).getPackageName())
                            .toList().blockingGet()
            )));

        });/*, data, (position, state) -> {
            getSupportActionBar().setTitle(adapter.getCheckedCount() + " items checked");
            Observable.from(adapter.getCheckedItems()).map(AppItem::getPackageName).toList()
                    .flatMap(list -> Observable.just(new HashSet<>(list))).subscribe(set -> {
                PreferenceManager.getDefaultSharedPreferences(context).edit()
                        .putStringSet(PARAMETER_APP_WHITELIST, set).apply();
                Intent intent = new Intent(BROADCAST_SERVICE_UPDATE_APP_WHITELIST);
                sendBroadcast(intent);
            });
        });*/
        appRecyclerView.setAdapter(adapter);

        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        Disposable disposable = Observable.fromIterable(packages)
                .map(applicationInfo -> {
                    try {
                        return new AppItem(
                                applicationInfo,
                                pm.getApplicationLabel(applicationInfo).toString(),
                                applicationInfo.packageName,
                                pm.getApplicationIcon(applicationInfo.packageName),
                                selectedAppPackages.contains(applicationInfo.packageName)
                        );
                    } catch (PackageManager.NameNotFoundException | NullPointerException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .filter(item -> !item.getTitle().contains("com") && !item.getTitle().contains("org") && !item.getTitle().contains("net"))
                .filter(item -> !isSystemPackage(item.getApplicationInfo()))
                .toSortedList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(data -> {
                    progressBar.setVisibility(GONE);
                    adapter.setSelectedPositions(
                            new HashSet<>(Observable.range(0, selectedAppPackages.size()).toList().blockingGet())
                    );
                    adapter.updateItems(data);
                });
    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else return super.onOptionsItemSelected(item);
    }


}
