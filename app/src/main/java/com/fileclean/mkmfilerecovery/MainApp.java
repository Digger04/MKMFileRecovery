package com.fileclean.mkmfilerecovery;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;
import com.zeugmasolutions.localehelper.LocaleHelper;
import com.zeugmasolutions.localehelper.LocaleHelperApplicationDelegate;

import org.json.JSONObject;

public class MainApp extends Application {
    public static Context context;
    public static boolean is_open_app_from_noti;

    private static MainApp instance;

    public static MainApp getInstance() {
        return instance;
    }

    public static MMKV mmkv;

//    private FirebaseAnalytics mFirebaseAnalytics;
    private LocaleHelperApplicationDelegate localeAppDelegate = new LocaleHelperApplicationDelegate();

//    public static ApplovinAppOpenManager appOpenManager;


    @Override
    public void onCreate() {
        super.onCreate();

        MMKV.initialize(this);
        mmkv = MMKV.defaultMMKV();
        context = this;

//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        Admob.getInstance().setFan(true);
//        Admob.getInstance().setAppLovin(true);
//        Admob.getInstance().setColony(true);
//
//        OneSignal.setNotificationOpenedHandler(new OneSignal.OSNotificationOpenedHandler() {
//            @Override
//            public void notificationOpened(OSNotificationOpenedResult osNotificationOpenedResult) {
//                JSONObject data = osNotificationOpenedResult.getNotification().getAdditionalData();
//                Log.i("OneSignalExample", "Notification Data: " + data);
//                String notification_topic;
//                if (data != null) {
//                    is_open_app_from_noti = true;
//                    Log.d("ewwefwe", "data: " + data);
//                }
//            }
//        });

//        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
//                .setMinimumFetchIntervalInSeconds(3600)
//                .build();
//        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
//        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

//        Bundle bundle = new Bundle();
//        mFirebaseAnalytics.logEvent("fetch_config", bundle);
//        mFirebaseRemoteConfig.fetchAndActivate() // fetch config from server and activate
//                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Boolean> task) {
//                        if (task.isSuccessful()) {
//
//                            Log.d("MKMLOG", "FirebaseRemoteConfig fetched");
//                            mFirebaseAnalytics.logEvent("fetch_config_fetched", bundle);
//
//                            AdsUtils.time_to_show_inter = mFirebaseRemoteConfig.getDouble("time_to_show_inter");
//                            AdsUtils.count_to_show_inter = mFirebaseRemoteConfig.getDouble("count_to_show_inter");
//
//                            AdsUtils.AOA_bid_high = mFirebaseRemoteConfig.getString("AOA_bid_high");
//                            AdsUtils.AOA_bid_medium = mFirebaseRemoteConfig.getString("AOA_bid_medium");
//                            AdsUtils.AOA_bid_low = mFirebaseRemoteConfig.getString("AOA_bid_low");
//                            AdsUtils.AOA_bid_auto = mFirebaseRemoteConfig.getString("AOA_bid_auto");
//
//                        } else {
//                            mFirebaseAnalytics.logEvent("fetch_config_error", bundle);
////                            Log.d(TAG, "FirebaseRemoteConfig error:" + task.getResult());
//                        }
//                    }
//                });
//
//        appOpenManager = new ApplovinAppOpenManager(this);

        MMKV.initialize(this);
        mmkv = MMKV.defaultMMKV();
        context = this;

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(localeAppDelegate.attachBaseContext(base));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeAppDelegate.onConfigurationChanged(this);
    }

    @Override
    public Context getApplicationContext() {
        return LocaleHelper.INSTANCE.onAttach(super.getApplicationContext());
    }

}
