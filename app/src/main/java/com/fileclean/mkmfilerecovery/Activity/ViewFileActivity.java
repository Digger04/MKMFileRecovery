package com.fileclean.mkmfilerecovery.Activity;

import static android.os.Build.VERSION.SDK_INT;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fileclean.mkmfilerecovery.Adapter.AdapterFile;
import com.fileclean.mkmfilerecovery.BuildConfig;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHistory.DataHistory;
import com.fileclean.mkmfilerecovery.Database.DataHistory.InfofileHistory;
import com.fileclean.mkmfilerecovery.Database.DataRecent.DataRecent;
import com.fileclean.mkmfilerecovery.Database.DataRecent.InforRecent;
import com.fileclean.mkmfilerecovery.Model.InfoFile;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class ViewFileActivity extends AppCompatActivity {

    private ImageView img_back;
    private TextView titleviewfile;
    private RecyclerView recyclerview;
    private TextView txt_scanning;
    public static String Type;
    private ProgressBar progrssbar;
    private ImageView img_filter;

    private AdapterFile adapterFile;
    private List<InfoFile> list;
    private File dir;

    private SectionedRecyclerViewAdapter sectionAdapter;

    private TextView txt_stop;

    private int TIME_OUT = 10;

    private boolean ispermission = false;

    private ImageView img_history;

    private Window window;

    private CardView cardView;
    public static TextView bt_recover;

    private LinearLayout layout_nullfile;

    private boolean is6month = true;
    private boolean is12month = true;
    private boolean is1years = true;
    private boolean is2years = true;

    private List<InfoFile> list6Month;
    private List<InfoFile> list12Month;
    private List<InfoFile> list1Year;
    private List<InfoFile> list2Year;

    public static List<InfoFile> listRecover;

    public static boolean ispause = false;
    public static boolean isBack3 = false;

//    private FirebaseAnalytics mFirebaseAnalytics;

    private boolean askPermission = false;

//    private AdsUtils adsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);

        initview();
        ClickBack();
        ClickFilter();
        getDataIntent();
        setStatusbar();
        StopLoad();
        setNumberRecover();
        Recover();
        History();
    }

    private void History() {
        img_history.setOnClickListener(view -> {

            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra(Key.KeyIntent, Type);
            startActivity(intent);
            ispause = true;
            isBack3 = true;
        });
    }
    private void RecoverPhoto(String type) {
        if (listRecover.size() > 0) {
            List<InfofileHistory> list_history = new ArrayList<>();
            for (int i = 0; i < listRecover.size(); i++) {
                try {

                    Bitmap bm = BitmapFactory.decodeFile(listRecover.get(i).getPath());
                    String path = "/storage/emulated/0/Pictures/" + listRecover.get(i).getName();
                    File file1 = new File(path);
                    file1.createNewFile();
                    FileOutputStream outputStream = new FileOutputStream(file1);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                    switch (type) {
                        case "photo":
                            list_history.add(new InfofileHistory(path, listRecover.get(i).getName(),
                                    listRecover.get(i).getDate(), listRecover.get(i).getSize(), "photo"));
                            DataHistory.getInstance(getApplicationContext()).daoSql().insert(list_history.get(i));
                            break;
                        case "recyclebin":
                            list_history.add(new InfofileHistory(listRecover.get(i).getPath(), listRecover.get(i).getName(),
                                    listRecover.get(i).getDate(), listRecover.get(i).getSize(), "recyclebin"));
                            DataHistory.getInstance(getApplicationContext()).daoSql().insert(list_history.get(i));
                            break;
                    }

                } catch (Exception e) {

                }
                File file = new File(listRecover.get(i).getPath());
                file.delete();
                String path = "/storage/emulated/0/Pictures/" + listRecover.get(i).getName();
                File file1 = new File(path);
                refreshGallery(this, file1);

            }
            listRecover.clear();

            Intent intent = new Intent(this, PendingMoveActivity.class);
            intent.putExtra(Key.KeyIntent, type);
            intent.putExtra(Key.Key_Type_Screen, Key.Viewfile);
            startActivity(intent);

        }else {
            Toast.makeText(this, R.string.pleaseselect, Toast.LENGTH_SHORT).show();
        }
    }


    public void refreshGallery(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(mediaScanIntent);
    }

    private void RecoverAudio(String typefile) {

        if (listRecover.size() > 0) {
            List<InfofileHistory> list_history = new ArrayList<>();
            for (int i = 0; i < listRecover.size(); i++) {
                String Pmove = Key.Path_Recover_Audio + listRecover.get(i).getName();
                File Filerecover = new File(Pmove);

                File file = new File(listRecover.get(i).getPath());
                try {

                    if (file.exists()) {

                        InputStream inputStream = new FileInputStream(file);
                        OutputStream outputStream1 = new FileOutputStream(Filerecover);

                        byte[] buf = new byte[1024];
                        int len;

                        while ((len = inputStream.read(buf)) > 0) {
                            outputStream1.write(buf, 0, len);
                        }
                        outputStream1.flush();
                        inputStream.close();
                        outputStream1.close();

                        refreshGallery(this, Filerecover);

                        file.delete();
                        list_history.add(new InfofileHistory(listRecover.get(i).getPath(), listRecover.get(i).getName(),
                                listRecover.get(i).getDate(), listRecover.get(i).getSize(), typefile));
                        DataHistory.getInstance(getApplicationContext()).daoSql().insert(list_history.get(i));
                    }

                }catch (Exception e) {

                }
                file.delete();
            }
            Intent intent = new Intent(this, PendingMoveActivity.class);
            intent.putExtra(Key.KeyIntent, typefile);
            intent.putExtra(Key.Key_Type_Screen, Key.Viewfile);
            startActivity(intent);
        }
    }

    private void Recover() {
        bt_recover.setOnClickListener(view -> {

//            new AdsUtils(ViewFile.this).showAdsInterstitiaAd(ViewFile.this);

            switch (Type) {
                case "photo":
                    RecoverPhoto(Key.Photo);
                    break;
                case "audio":
                    RecoverAudio(Key.Audio);
                    break;
                case "video":
                    Recoverfile(Key.Video);
                    break;
                case "document":
                    Recoverfile("document");
                    break;
                case "zip":
                    Recoverfile("zip");
                    break;
                case "recyclebin":
                    RecoverPhoto("recyclebin");
                    break;
            }
        });
    }

    private void Recoverfile(String typefile) {
        List<InfofileHistory> list_history = new ArrayList<>();
        String path = "";
        if (listRecover.size() > 0) {
            for (int i = 0; i < listRecover.size(); i ++) {
                try {
                    File currentFile = new File(listRecover.get(i).getPath());

                    switch (typefile) {
                        case "video":
                            path = Key.Path_Recover_Videos + listRecover.get(i).getName();
                            break;
                        case "document":
                            path = Key.Path_Recover_file + listRecover.get(i).getName();
                            break;
                        case "zip":
                            path = Key.Path_Recover_file + listRecover.get(i).getName();
                    }

                    File newfile = new File(path);
                    newfile.createNewFile();


                    if (currentFile.exists()) {

                        InputStream inputStream = new FileInputStream(currentFile);
                        OutputStream outputStream = new FileOutputStream(newfile);


                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] buf = new byte[1024];
                                int len;

                                while (true) {
                                    try {
                                        if (!((len = inputStream.read(buf)) > 0)) break;
                                        outputStream.write(buf, 0, len);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        thread.start();

                        outputStream.flush();
                        inputStream.close();
                        outputStream.close();

                        refreshGallery(this, newfile);


                        list_history.add(new InfofileHistory(listRecover.get(i).getPath(), listRecover.get(i).getName(),
                                listRecover.get(i).getDate(), listRecover.get(i).getSize(), typefile));
                        DataHistory.getInstance(getApplicationContext()).daoSql().insert(list_history.get(i));
                    } else {
                        Toast.makeText(this, R.string.videos_has_faild, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                File file = new File(listRecover.get(i).getPath());
                file.delete();
            }
            listRecover.clear();

            Intent intent = new Intent(this, PendingMoveActivity.class);
            intent.putExtra(Key.KeyIntent, typefile);
            intent.putExtra(Key.Key_Type_Screen, Key.Viewfile);
            startActivity(intent);
        }
    }


    private void setNumberRecover() {
        if (listRecover.size() > 0 ){
            bt_recover.setText(getString(R.string.recover) + listRecover.size());
        }else {
            bt_recover.setText(getString(R.string.recover));
        }
    }
    private void StopLoad() {
        txt_stop.setOnClickListener(view -> {
            ispause = true;
        });
    }
    private void setStatusbar() {
        window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void getDataIntent() {
        if (getIntent() != null) {
            Type = getIntent().getStringExtra(Key.KeyIntent);
        }
        ispause = false;
        adapterFile = new AdapterFile(getApplicationContext(), list, Type);
        recyclerview.setAdapter(adapterFile);

        SetTitle("");
        getData();
    }

    private void ClickFilter() {

        img_filter.setOnClickListener(view -> {

            ispause = true;

            Dialog dialog = new Dialog(this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_filter);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.getWindow().setLayout(-1, -2);
            }

            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM ;
            window.setAttributes(wlp);

            Button month6 = dialog.findViewById(R.id.month6);
            Button month12 = dialog.findViewById(R.id.month12);
            Button years1 = dialog.findViewById(R.id.years1);
            Button years2 = dialog.findViewById(R.id.years2);

            if (SDK_INT >= Build.VERSION_CODES.M) {
                if (is6month == true) {
                    month6.setTextColor(getColor(R.color.blue017));
                    month6.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                }else {
                    month6.setTextColor(getColor(R.color.grey01));
                    month6.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                }
                if (is12month == true) {
                    month12.setTextColor(getColor(R.color.blue017));
                    month12.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                }else {
                    month12.setTextColor(getColor(R.color.grey01));
                    month12.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                }
                if (is1years == true) {
                    years1.setTextColor(getColor(R.color.blue017));
                    years1.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                }else {
                    years1.setTextColor(getColor(R.color.grey01));
                    years1.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                }
                if (is2years == true) {
                    years2.setTextColor(getColor(R.color.blue017));
                    years2.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                }else {
                    years2.setTextColor(getColor(R.color.grey01));
                    years2.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                }
            }

            ImageView cancel = dialog.findViewById(R.id.cancel_button);
            TextView bt_ok = dialog.findViewById(R.id.bt_ok);

            cancel.setOnClickListener(view1 -> {
                dialog.cancel();
            });

            bt_ok.setOnClickListener(view1 -> {
                try {
                    FilterMode();
                }catch (Exception e) {
                    Log.d("taggggbuggg","error: " + e);
                }
                dialog.cancel();
            });

            month6.setOnClickListener(view1 -> {
                if (SDK_INT >= Build.VERSION_CODES.M) {

                    if (is6month == true) {
                        month6.setTextColor(getColor(R.color.grey01));
                        month6.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                        is6month = false;
                    }else {
                        month6.setTextColor(getColor(R.color.blue017));
                        month6.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                        is6month = true;
                    }
                }
            });

            month12.setOnClickListener(view1 -> {
                if (SDK_INT >= Build.VERSION_CODES.M) {
                    if (is12month == true) {
                        month12.setTextColor(getColor(R.color.grey01));
                        month12.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                        is12month = false;
                    }else {
                        month12.setTextColor(getColor(R.color.blue017));
                        month12.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                        is12month = true;
                    }
                }
            });

            years1.setOnClickListener(view1 -> {
                if (SDK_INT >= Build.VERSION_CODES.M) {
                    if (is1years == true) {
                        years1.setTextColor(getColor(R.color.grey01));
                        years1.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                        is1years = false;
                    }else {
                        years1.setTextColor(getColor(R.color.blue017));
                        years1.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                        is1years = true;
                    }
                }
            });

            years2.setOnClickListener(view1 -> {
                if (SDK_INT >= Build.VERSION_CODES.M) {
                    if (is2years == true) {
                        years2.setTextColor(getColor(R.color.grey01));
                        years2.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                        is2years = false;
                    }else {
                        years2.setTextColor(getColor(R.color.blue017));
                        years2.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                        is2years = true;
                    }
                }
            });

            dialog.show();

        });
    }
    private void FilterMode() {
        list.clear();
        if (is6month == true) {
            if (list6Month != null && list6Month.size() > 0) {
                for (int i = 0; i < list6Month.size(); i ++) {
                    list.add(list6Month.get(i));
                }
            }
        }
        if (is12month == true) {
            if (list12Month != null && list12Month.size() > 0) {
                for (int i = 0; i < list12Month.size(); i ++) {
                    list.add(list12Month.get(i));
                }
            }
        }
        if (is1years == true) {
            if (list1Year != null && list1Year.size() > 0) {
                for (int i = 0; i < list1Year.size(); i ++) {
                    list.add(list1Year.get(i));
                }
            }
        }
        if (is2years == true) {
            if (list2Year != null && list2Year.size() > 0) {
                for (int i = 0; i < list2Year.size(); i ++) {
                    list.add(list2Year.get(i));
                }
            }
        }
        String values = " [ " + list.size() + " ]";
        SetTitle(values);
        adapterFile.notifyDataSetChanged();
    }


    private void ClickBack() {
        img_back.setOnClickListener(view -> {

//            new AdsUtils(ViewFile.this).showAdsInterstitiaAd2(ViewFile.this);

            ispause = true;
            isBack3 = true;
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        });
    }
    private void initview() {

        img_back = findViewById(R.id.img_back);
        recyclerview = findViewById(R.id.recyclerview);

        titleviewfile = findViewById(R.id.titleviewfile);
        titleviewfile.setSelected(true);

        txt_scanning  = findViewById(R.id.txt_scanning);
        progrssbar = findViewById(R.id.progressbar);
        img_filter = findViewById(R.id.img_filter);
        cardView = findViewById(R.id.cardview);
        bt_recover = findViewById(R.id.bt_recover);
        layout_nullfile = findViewById(R.id.layout_nullfile);
        img_history = findViewById(R.id.img_history);

        txt_stop = findViewById(R.id.txt_stop);

        list = new ArrayList<>();
        listRecover = new ArrayList<>();

        ispause = false;

        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setHasFixedSize(true);

    }


    @Override
    public void onBackPressed() {

//        new AdsUtils(ViewFile.this).showAdsInterstitiaAd2(ViewFile.this);

        ispause = true;
        isBack3 = true;
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.isback = false;
        isBack3 = false;
        if (!checkPermission()) {

            verifyStoragePermissions(this);
            requestPermission();
        }
        try {
            if (list.size() == 0) {
                getData();
            }
        }catch (Exception e) {

        }
    }


    private void getData() {

        dir = Environment.getExternalStorageDirectory();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ispermission == true) {
                    new getFile().execute();
                }
            }
        },TIME_OUT);

    }

    public class getFile extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {

            File[] FileList = dir.listFiles();

            if (FileList != null) {

                for (File value : FileList) {
                    if (value.isDirectory()) {
                        dir = value;
                        new getFile().doInBackground();
                    } else {
                        if (ispause == true) {
                            break;
                        }

                        File file = new File(value.getPath());
                        int file_size = parseInt(String.valueOf(file.length() / 1024));
                        // get day
                        Date lastModDate = new Date(file.lastModified());

                        if (file_size > 0) {
                            switch (Type) {
                                case "photo":

                                    getFilePhoto(value, lastModDate, file_size);
                                    publishProgress("" + list.size());

                                    break;

                                case "audio":

                                    getFileAudio(value, lastModDate, file_size);
                                    publishProgress("" + list.size());

                                    break;

                                case "video":

                                    getFileVideo(value, lastModDate, file_size);
                                    publishProgress("" + list.size());

                                    break;
                                case "document":

                                    getFileDocument(value, lastModDate, file_size);
                                    publishProgress("" + list.size());

                                    break;

                                case "zip":

                                    getFileZip(value, lastModDate, file_size);
                                    publishProgress("" + list.size());

                                    break;

                                case "recyclebin":

                                    getFileRecycleBin(value, lastModDate, file_size);
                                    publishProgress("" + list.size());

                                    break;
                            }
                        }
                    }
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            txt_scanning.setText(getString(R.string.scanning) +
                    "[ " + list.size() + " ] " + getString(R.string.file));
            Handler handler = new Handler();
            adapterFile.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {

            if (!isBack3) {
                DialogFinish();
            }

            adapterFile.notifyDataSetChanged();
            txt_scanning.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);
            txt_stop.setVisibility(View.GONE);
            String values = " [ " + list.size() + " ]";
            SetTitle(values);
            adapterFile.notifyDataSetChanged();
            progrssbar.setVisibility(View.GONE);
            if (list.size() == 0) {
                recyclerview.setVisibility(View.GONE);
                layout_nullfile.setVisibility(View.VISIBLE);
            }
            getList6Month();
            getList12Month();
            getList1Year();
            getList2Year();

            if (list.size() > 0) {

                List<InforRecent> listrecent = DataRecent.getInstance(getApplicationContext()).daoSql().getall();
                listrecent = DataRecent.getInstance(getApplicationContext()).daoSql().getall();

                for (int i = 0; i < list.size(); i++) {

                    for (int j = 0; j < listrecent.size(); j ++) {

                        if (list.get(i).getPath().equals(listrecent.get(j).getPath())) {
                            DataRecent.getInstance(getApplicationContext()).daoSql().delete(listrecent.get(j));
                        }
                    }

                    DataRecent.getInstance(getApplicationContext()).daoSql().insert(new InforRecent(
                            list.get(i).getPath(), list.get(i).getName(),
                            list.get(i).getDate(), list.get(i).getSize()
                    ));
                }
            }

            super.onPostExecute(s);
        }
    }

    private void SetTitle(String values) {
        switch (Type) {
            case "photo":
                titleviewfile.setText(getString(R.string.photoscan)+values);
                break;
            case "audio":
                titleviewfile.setText(getString(R.string.audioscan)+values);
                break;
            case "video":
                titleviewfile.setText(getString(R.string.videoscan)+values);
                break;
            case "document":
                titleviewfile.setText(getString(R.string.documentscan)+values);
                break;
            case "zip":
                titleviewfile.setText(getString(R.string.zipscan)+values);
                break;
            case "recyclebin":
                titleviewfile.setText(getString(R.string.recyclebinscan)+values);
                break;
        }
    }

    private void getList6Month(){
        list6Month = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime > -180) {
                list6Month.add(list.get(i));
            }
        }
    }

    private void getList12Month(){
        list12Month = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime < -180 && TimeList - thisTime > -365) {
                list12Month.add(list.get(i));
            }
        }
    }

    private void getList1Year(){
        list1Year = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime < -365 && TimeList - thisTime > -730) {
                list1Year.add(list.get(i));
            }
        }
    }

    private void getList2Year(){
        list2Year = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime < -730) {
                list2Year.add(list.get(i));
            }
        }
    }
    private void DialogFinish() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_load_finish);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }

        TextView txt_number = dialog.findViewById(R.id.txt_number);
        TextView txt_memory = dialog.findViewById(R.id.txt_memory);
        Button bt_ok = dialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(view -> {

//            new AdsUtils(ViewFile.this).showAdsInterstitiaAd2(ViewFile.this);

            dialog.cancel();
        });

        txt_number.setText(getString(R.string.file1) + " " + list.size());
        float kb = 0;

        if (list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {

                kb = kb + Float.parseFloat(list.get(i).getSize());

            }
        }

        float mb = kb / 1024;
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        txt_memory.setText(getString(R.string.memory) + " " + decimalFormat.format(mb) + " Mb");

        try {
            dialog.show();
        }catch (Exception e) {
            Log.d("fwfe", "error dialog finish: " +  e);
        }
    }
    private void getFileRecycleBin(File value, Date lastModDate, int file_size) {
        if(value.getPath().contains(".Trash") || value.getPath().contains(".trash")) {
            try {
                Thread.sleep(TIME_OUT);
                list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));

            } catch (Exception e) {

            }
        }
    }
    private void getFileZip(File value, Date lastModDate, int file_size) {
        if (value.getPath().endsWith(".zip")) {
            try {
                Thread.sleep(TIME_OUT);
                list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));
            } catch (Exception e) {

            }
        }
    }
    private void getFileDocument(File value, Date lastModDate, int file_size) {
        if (!value.getPath().contains("Download")) {
            if (value.getName().endsWith(".txt") ||
                    value.getName().endsWith(".pdf") || value.getName().endsWith(".doc")
                    || value.getName().endsWith(".docx") || value.getName().endsWith(".xls")
                    || value.getName().endsWith(".xlxs")
                    || value.getName().endsWith(".ppt") ||
                    value.getName().endsWith(".pptx")) {

                try {
                    Thread.sleep(TIME_OUT);
                    list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));

                } catch (Exception e) {

                }

            }
        }
    }
    private void getFileVideo(File value, Date lastModDate, int file_size) {
        if(value.getName().endsWith(".mp4") &&
                !value.getPath().contains("DCIM") &&
                !value.getPath().contains("Movies")) {

            try {
                Thread.sleep(TIME_OUT);
                list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));
            } catch (Exception e) {

            }
        }
    }
    private void getFileAudio(File value, Date lastModDate, int file_size) {
        if (value.getName().endsWith(".mp3") &&
                !value.getPath().contains("DCIM") && !value.getPath().contains("Download")
                && !value.getPath().contains("NCT") && !value.getPath().contains("Mucsic")) {
            try {
                Thread.sleep(TIME_OUT);
                list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));

            } catch (Exception e) {

            }
        }
    }

    private void getFilePhoto(File value, Date lastModDate, int file_size) {
        if (value.getName().endsWith(".png") ||
                value.getName().endsWith(".jpeg") ||
                value.getName().endsWith(".jpg")) {

            if (!value.getPath().contains("DCIM") &&
                    !value.getPath().contains("Download") &&
                    !value.getPath().contains("Pictures")) {
                try {
                    Thread.sleep(TIME_OUT);
                    list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));

                } catch (Exception e) {

                }
            }

        }
    }
    private void requestPermission() {

        Log.d("Gambi","requestPermission");

//        Bundle bundle = new Bundle();
//        mFirebaseAnalytics.logEvent("requestPermission", bundle);

        if (SDK_INT >= 30) {

            Dialog dialog = new Dialog(this);

//            if (dialog != null && dialog.isShowing()){
//                Log.d("Gambi","requestPermission dialog isShowing");
//            }else{
//                Log.d("Gambi","requestPermission dialog is not Showing");
//            }

//            dialog.dismiss();

            if (!Environment.isExternalStorageManager()) {

                if( askPermission == true){
                    return ;
                }

                askPermission = true;

                Log.d("Gambi","requestPermission show popup");

                dialog.setContentView(R.layout.dialog_permission);
                dialog.setCanceledOnTouchOutside(false);

                Window window = dialog.getWindow();
                if (window == null) {
                    Log.d("Gambi","requestPermission window null");
                    return;
                }

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.getWindow().setLayout(-1, -2);
                }

                Button bt_ok = dialog.findViewById(R.id.bt_ok);
                bt_ok.setOnClickListener(v -> {

                    try {
                        Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                        startActivity(intent);
                        dialog.dismiss();
                    } catch (Exception ex) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(intent);
                        dialog.dismiss();
                    }

                    askPermission = false;

                    dialog.dismiss();
                });

                dialog.show();

            } else {

//                if (dialog != null && dialog.isShowing())
                dialog.dismiss();

                Log.d("Gambi","requestPermission isPermission true");

                ispermission = true;
            }
        } else {
            if (ContextCompat.checkSelfPermission(ViewFileActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(ViewFileActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(ViewFileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            222);
                }
            }
        }
    }

    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.MANAGE_EXTERNAL_STORAGE") == 0;
    }

    private void verifyStoragePermissions(Activity activity) {
        if (SDK_INT < 30) {
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        423
                );
            } else {
                ispermission = true;
            }
        }
    }
}