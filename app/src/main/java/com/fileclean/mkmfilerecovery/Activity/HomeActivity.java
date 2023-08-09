package com.fileclean.mkmfilerecovery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHide.InfoFileHide;
import com.fileclean.mkmfilerecovery.Database.DataTimeOpenAgo.DataTimeOpen;
import com.fileclean.mkmfilerecovery.Database.DataTimeOpenAgo.InfoTimeOpen;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageView img_settings;
    private ImageView img_exit;
    private RelativeLayout layout_search;

    private LinearLayout layout_photo, layout_video, layout_audio,
            layout_document, layout_zip, layout_recyclebin, layout_filevault, layout_recent;

    private TextView time_open_photo, time_open_audio, time_open_video,
            time_open_document, time_open_zip, time_open_recyclebin;

    private TextView txt_photo, txt_recyclerbin, txt_audio, txt_video,
            txt_zip, txt_document, txt_desinstall, des_filevault;

    private Window window;

    public static List<InfoFileHide> listHideLocal;
    private boolean ispermission = false;
    private File dir;
    private String type;

    private TextView nameapp;


    private TextView titleInstall;

    public static boolean isback = false;

    private List<InfoTimeOpen> listtime;
    private long TimePhoto;
    private long TimeAudio;
    private long TimeVideo;
    private long TimeDocument;
    private long TimeZip;
    private long TimeRecyclebin;

    private long PresentTime;

//    private FirebaseAnalytics mFirebaseAnalytics;
//    private AdsUtils adsUtils;
//
//    private ShimmerFrameLayout shimmerFrameLayout;
//    private FrameLayout frAds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initview();
        SetViewTimeAgo();
        setStatusbar();
        ClickSettings();
        Search();
        Exit();
        SelectFun();
    }

    private void SelectFun() {
        layout_recent.setOnClickListener(view -> {
//
//            Bundle bundle = new Bundle();
//            mFirebaseAnalytics.logEvent("recent", bundle);
//            mFirebaseAnalytics.logEvent("SelectFun", bundle);

            startActivity(new Intent(this, RecentActivity.class));
        });

        layout_photo.setOnClickListener(view -> {

//            Bundle bundle = new Bundle();
//            mFirebaseAnalytics.logEvent("photo", bundle);
//            mFirebaseAnalytics.logEvent("SelectFun", bundle);

            ViewFileActivity.Type = Key.Photo;
            InsertTime(Key.Photo);

            Intent intent = new Intent(this, ViewFileGroupActivity.class);
            intent.putExtra(Key.KeyIntent, Key.Photo);
            startActivity(intent);
        });

        layout_audio.setOnClickListener(view -> {

            InsertTime(Key.Audio);

//            Bundle bundle = new Bundle();
//            mFirebaseAnalytics.logEvent("audio", bundle);
//            mFirebaseAnalytics.logEvent("SelectFun", bundle);

            Intent intent = new Intent(this, ViewFileActivity.class);
            intent.putExtra(Key.KeyIntent, Key.Audio);
            startActivity(intent);
        });

        layout_video.setOnClickListener(view -> {

            InsertTime(Key.Video);

//            Bundle bundle = new Bundle();
//            mFirebaseAnalytics.logEvent("video", bundle);
//            mFirebaseAnalytics.logEvent("SelectFun", bundle);

            Intent intent = new Intent(this, ViewFileActivity.class);
            intent.putExtra(Key.KeyIntent, Key.Video);
            startActivity(intent);
        });

        layout_document.setOnClickListener(view -> {

            InsertTime(Key.Document);

//            Bundle bundle = new Bundle();
//            mFirebaseAnalytics.logEvent("document", bundle);
//            mFirebaseAnalytics.logEvent("SelectFun", bundle);

            Intent intent = new Intent(this, ViewFileActivity.class);
            intent.putExtra(Key.KeyIntent, Key.Document);
            startActivity(intent);
        });

        layout_zip.setOnClickListener(view -> {

            InsertTime(Key.Zip);

//            Bundle bundle = new Bundle();
//            mFirebaseAnalytics.logEvent("zip", bundle);
//            mFirebaseAnalytics.logEvent("SelectFun", bundle);

            Intent intent = new Intent(this, ViewFileActivity.class);
            intent.putExtra(Key.KeyIntent, Key.Zip);
            startActivity(intent);
        });

        layout_recyclebin.setOnClickListener(view -> {

            ViewFileActivity.Type = Key.Recyclebin;
            InsertTime(Key.Recyclebin);

//            Bundle bundle = new Bundle();
//            mFirebaseAnalytics.logEvent("recyclebin", bundle);
//            mFirebaseAnalytics.logEvent("SelectFun", bundle);

            Intent intent = new Intent(this, ViewFileGroupActivity.class);
            intent.putExtra(Key.KeyIntent, Key.Recyclebin);
            startActivity(intent);
        });

        layout_filevault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Bundle bundle = new Bundle();
//                mFirebaseAnalytics.logEvent("filevault", bundle);
//                mFirebaseAnalytics.logEvent("SelectFun", bundle);

                //   startActivity(new Intent(MainActivity.this, FileVault.class));
                startActivity(new Intent(HomeActivity.this, OTPActivity.class));
            }
        });

    }

    private void InsertTime(String SelectType) {

        if (listtime.size() <=0 ) {
            for (int i = 0; i < listtime.size(); i++) {
                if (listtime.get(i).getType().equals(SelectType)) {
                    listtime.remove(i);
                }
            }
        }

        long time = System.currentTimeMillis();
        DataTimeOpen.getInstance(getApplicationContext()).daoSql()
                .insert(new InfoTimeOpen(SelectType, time));
    }
    private void DialogExit() {

//        Bundle bundle = new Bundle();
//        mFirebaseAnalytics.logEvent("activity_main_onBack", bundle);

//        if (unifiedNativeAd_Admob == null){
//
//            mFirebaseAnalytics.logEvent("activity_main_onBack_Null", bundle);
//
//            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.sureexit);
//            builder.setCancelable(false);
//            builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
//
//            });
//
//            builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
//
//                if( AdsUtils.enable_aoa == true){
//                    Intent intent = new Intent(MainActivity.this, BeforeExitAppActivity.class);
//                    intent.putExtra("type", "BeforeExitActivity");
//                    startActivity(intent);
//                    finish();
//                }else{
//                    System.exit(0);
//                }
//
////                System.exit(0);
//            });
//            builder.show();
//
//
//        }else{
//
//            mFirebaseAnalytics.logEvent("activity_main_onBack_Popup", bundle);
//
//            DialogExitApp1 dialogExitApp1 = new DialogExitApp1(this, unifiedNativeAd_Admob, 1);
//            dialogExitApp1.setDialogExitListener(new DialogExitListener() {
//                @Override
//                public void onExit(boolean exit) {
//
//                    mFirebaseAnalytics.logEvent("activity_main_onBack_Popup_onExit", bundle);
//
//                    if( AdsUtils.enable_aoa == true){
//
//                        Intent intent = new Intent(MainActivity.this, BeforeExitAppActivity.class);
//                        intent.putExtra("type", "BeforeExitActivity");
//                        startActivity(intent);
//                        finish();
//
//                    }else{
//
//                        System.exit(0);
//
//                    }
//
////                    System.exit(0);
//                }
//            });
//            dialogExitApp1.setCancelable(false);
//            dialogExitApp1.show();
//        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.sureexit));

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                System.exit(0);
            }
        });

        builder.show();
    }
    private void Exit() {
        img_exit.setOnClickListener(view -> {
            DialogExit();
        });
    }
    private void Search() {
        layout_search.setOnClickListener(view -> {

//            Bundle bundle = new Bundle();
//            mFirebaseAnalytics.logEvent("search", bundle);

            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
        });
    }

    private void ClickSettings() {
        img_settings.setOnClickListener(view -> {

//            Bundle bundle = new Bundle();
//            mFirebaseAnalytics.logEvent("settings", bundle);

            startActivity(new Intent(HomeActivity.this, SettingActivity.class));
        });
    }
    private void setStatusbar() {
        window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue017));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    public void onBackPressed() {
        DialogExit();
        // DialogExit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SetViewTimeAgo();
        //loadNativeExit();
    }

    private void initview() {
        img_settings = findViewById(R.id.img_settings);
        img_exit = findViewById(R.id.img_exit);
        layout_search = findViewById(R.id.layout_search);
        titleInstall = findViewById(R.id.titleInstall);
//        titleInstall.setSelected(true);

        nameapp = findViewById(R.id.nameapp);
        nameapp.setSelected(true);

        time_open_photo = findViewById(R.id.time_open_photo);
        txt_photo = findViewById(R.id.txt_photo);
        time_open_photo.setSelected(true);
        txt_photo.setSelected(true);

        time_open_audio = findViewById(R.id.time_open_audio);
        txt_audio = findViewById(R.id.txt_audio);
        txt_audio.setSelected(true);
        time_open_audio.setSelected(true);

        time_open_video = findViewById(R.id.time_open_video);
        txt_video = findViewById(R.id.txt_video);
        txt_video.setSelected(true);
        time_open_video.setSelected(true);

        time_open_document = findViewById(R.id.time_open_document);
        txt_document = findViewById(R.id.txt_document);
        txt_document.setSelected(true);
        time_open_document.setSelected(true);

        time_open_zip = findViewById(R.id.time_open_zip);
        txt_zip = findViewById(R.id.txt_zip);
        txt_zip.setSelected(true);
        time_open_zip.setSelected(true);

        txt_recyclerbin = findViewById(R.id.txt_recyclebin);
        time_open_recyclebin = findViewById(R.id.time_open_recyclebin);
        txt_recyclerbin.setSelected(true);
        time_open_recyclebin.setSelected(true);

        layout_photo = findViewById(R.id.layout_photo);
        layout_audio = findViewById(R.id.layout_audio);
        layout_video = findViewById(R.id.layout_video);
        layout_recent = findViewById(R.id.layout_recent);

        layout_document = findViewById(R.id.layout_document);
        layout_zip = findViewById(R.id.layout_zip);
        layout_recyclebin = findViewById(R.id.layout_recyclebin);
        layout_filevault = findViewById(R.id.layout_filevault);

        txt_desinstall = findViewById(R.id.txt_desinstall);
//        txt_desinstall.setSelected(true);

        des_filevault = findViewById(R.id.des_filevault);
        des_filevault.setSelected(true);

        listHideLocal = new ArrayList<>();
        listtime = new ArrayList<>();
    }

    private void SetViewTimeAgo() {

        listtime.clear();

        listtime = DataTimeOpen.getInstance(getApplicationContext()).daoSql().getall();

        if (listtime.size() <=0) {
            return;
        }

        for (int i = 0; i < listtime.size(); i++) {
            if (listtime.get(i).getType().equals(Key.Photo)) {
                TimePhoto = listtime.get(i).getTime();
            }else if (listtime.get(i).getType().equals(Key.Audio)) {
                TimeAudio = listtime.get(i).getTime();
            }else if (listtime.get(i).getType().equals(Key.Video)) {
                TimeVideo = listtime.get(i).getTime();
            }else if (listtime.get(i).getType().equals(Key.Document)) {
                TimeDocument = listtime.get(i).getTime();
            }else if (listtime.get(i).getType().equals(Key.Zip)) {
                TimeZip = listtime.get(i).getTime();
            }else if (listtime.get(i).getType().equals(Key.Recyclebin)) {
                TimeRecyclebin = listtime.get(i).getTime();
            }
        }

        PresentTime = System.currentTimeMillis();

        if (TimePhoto > 0) {
            int sTime = (int) ((PresentTime - TimePhoto) /1000);
            int Mtime = sTime / 60;
            int dTime = sTime / 60 / 60 / 24;
            int hTime = (sTime - (dTime * 24 * 60 * 60)) / 60 / 60;
            int mTime = (Mtime % 60);

            if (dTime > 0) {
                time_open_photo.setText(getString(R.string.scan) + " " + dTime + " " + getString(R.string.day) + " " + hTime + " " + getString(R.string.hour) + " "  + mTime + " " + getString(R.string.minute) + " "+ getString(R.string.ago));
            }else if (hTime > 0) {
                time_open_photo.setText(getString(R.string.scan) + " " + hTime + " " + getString(R.string.hour) + " " + mTime + " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }else {
                time_open_photo.setText(getString(R.string.scan) + " " + mTime+ " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }

        }
        if (TimeAudio > 0) {
            int sTime = (int) ((PresentTime - TimeAudio) /1000);
            int Mtime = sTime / 60;
            int dTime = sTime / 60 / 60 / 24;
            int hTime = (sTime - (dTime * 24 * 60 * 60)) / 60 / 60;
            int mTime = (Mtime % 60);

            if (dTime > 0) {
                time_open_audio.setText(getString(R.string.scan) + " " + dTime + " " + getString(R.string.day) + " " + hTime + " " + getString(R.string.hour) + " "  + mTime + " " + getString(R.string.minute) + " "+ getString(R.string.ago));
            }else if (hTime > 0) {
                time_open_audio.setText(getString(R.string.scan) + " " + hTime + " " + getString(R.string.hour) + " " + mTime + " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }else {
                time_open_audio.setText(getString(R.string.scan) + " " + mTime+ " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }
        }
        if (TimeVideo > 0) {
            int sTime = (int) ((PresentTime - TimeVideo) /1000);
            int Mtime = sTime / 60;
            int dTime = sTime / 60 / 60 / 24;
            int hTime = (sTime - (dTime * 24 * 60 * 60)) / 60 / 60;
            int mTime = (Mtime % 60);

            if (dTime > 0) {
                time_open_video.setText(getString(R.string.scan) + " " + dTime + " " + getString(R.string.day) + " " + hTime + " " + getString(R.string.hour) + " "  + mTime + " " + getString(R.string.minute) + " "+ getString(R.string.ago));
            }else if (hTime > 0) {
                time_open_video.setText(getString(R.string.scan) + " " + hTime + " " + getString(R.string.hour) + " " + mTime + " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }else {
                time_open_video.setText(getString(R.string.scan) + " " + mTime+ " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }
        }
        if (TimeDocument > 0) {
            int sTime = (int) ((PresentTime - TimeDocument) /1000);
            int Mtime = sTime / 60;
            int dTime = sTime / 60 / 60 / 24;
            int hTime = (sTime - (dTime * 24 * 60 * 60)) / 60 / 60;
            int mTime = (Mtime % 60);

            if (dTime > 0) {
                time_open_document.setText(getString(R.string.scan) + " " + dTime + " " + getString(R.string.day) + " " + hTime + " " + getString(R.string.hour) + " "  + mTime + " " + getString(R.string.minute) + " "+ getString(R.string.ago));
            }else if (hTime > 0) {
                time_open_document.setText(getString(R.string.scan) + " " + hTime + " " + getString(R.string.hour) + " " + mTime + " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }else {
                time_open_document.setText(getString(R.string.scan) + " " + mTime+ " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }
        }
        if (TimeZip > 0) {
            int sTime = (int) ((PresentTime - TimeZip) /1000);
            int Mtime = sTime / 60;
            int dTime = sTime / 60 / 60 / 24;
            int hTime = (sTime - (dTime * 24 * 60 * 60)) / 60 / 60;
            int mTime = (Mtime % 60);

            if (dTime > 0) {
                time_open_zip.setText(getString(R.string.scan) + " " + dTime + " " + getString(R.string.day) + " " + hTime + " " + getString(R.string.hour) + " "  + mTime + " " + getString(R.string.minute) + " "+ getString(R.string.ago));
            }else if (hTime > 0) {
                time_open_zip.setText(getString(R.string.scan) + " " + hTime + " " + getString(R.string.hour) + " " + mTime + " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }else {
                time_open_zip.setText(getString(R.string.scan) + " " + mTime+ " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }
        }
        if (TimeRecyclebin > 0) {
            int sTime = (int) ((PresentTime - TimeRecyclebin) /1000);
            int Mtime = sTime / 60;
            int dTime = sTime / 60 / 60 / 24;
            int hTime = (sTime - (dTime * 24 * 60 * 60)) / 60 / 60;
            int mTime = (Mtime % 60);

            if (dTime > 0) {
                time_open_recyclebin.setText(getString(R.string.scan) + " " + dTime + " " + getString(R.string.day) + " " + hTime + " " + getString(R.string.hour) + " "  + mTime + " " + getString(R.string.minute) + " "+ getString(R.string.ago));
            }else if (hTime > 0) {
                time_open_recyclebin.setText(getString(R.string.scan) + " " + hTime + " " + getString(R.string.hour) + " " + mTime + " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }else {
                time_open_recyclebin.setText(getString(R.string.scan) + " " + mTime+ " " + getString(R.string.minute) + " " + getString(R.string.ago));
            }
        }
    }

}