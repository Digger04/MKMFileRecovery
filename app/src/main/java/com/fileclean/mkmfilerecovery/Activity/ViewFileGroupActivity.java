package com.fileclean.mkmfilerecovery.Activity;

import static android.os.Build.VERSION.SDK_INT;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;
import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
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

import com.fileclean.mkmfilerecovery.Adapter.GroupSection;
import com.fileclean.mkmfilerecovery.BuildConfig;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHide.DataHide;
import com.fileclean.mkmfilerecovery.Database.DataHide.InfoFileHide;
import com.fileclean.mkmfilerecovery.Database.DataHistory.DataHistory;
import com.fileclean.mkmfilerecovery.Database.DataHistory.InfofileHistory;
import com.fileclean.mkmfilerecovery.Database.DataRecent.DataRecent;
import com.fileclean.mkmfilerecovery.Database.DataRecent.InforRecent;
import com.fileclean.mkmfilerecovery.Model.DelayScrollRecyclerView;
import com.fileclean.mkmfilerecovery.Model.InfoFile;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class ViewFileGroupActivity extends AppCompatActivity implements GroupSection.ClickListener{

    private ImageView img_back;
    private TextView titleviewfile;
    private DelayScrollRecyclerView recyclerview;
    private TextView txt_scanning;
    public static String Type;
    private ProgressBar progrssbar;
    private ImageView img_filter;

    private List<InfoFile> list;
    private File dir;

    private LinearLayout layout_ic_recover;
    private LinearLayout layout_ic_vault;

    private ImageView img_filtervault;

    private SectionedRecyclerViewAdapter sectionAdapter;

    private TextView txt_stop;

    private int TIME_OUT = 10;

    private boolean isPermission = false;
    private boolean askPermission = false;

    private ImageView img_history;

    private Window window;

    private CardView cardView;
    public static TextView bt_recover;

    private String PMove;

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
    private List<InfoFileHide> listhide;

    public static boolean ispause = false;
    public static boolean isBack2 = false;

    private int count = 0;
    private long totalSize = 0;

//    private FirebaseAnalytics mFirebaseAnalytics;
//
//    private AdsUtils adsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file_group);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        new AdsUtils(ViewFileGroup.this).showAdsInterstitiaAd2(ViewFileGroup.this);
        listRecover.clear();
        ispause = true;
        isBack2 = true;
        finish();
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
                isPermission = true;
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

                isPermission = true;
            }
        } else {
            if (ContextCompat.checkSelfPermission(ViewFileGroupActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(ViewFileGroupActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(ViewFileGroupActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            222);
                }
            }
        }
    }


    protected void onResume() {
        super.onResume();

        if (!checkPermission()) {

            verifyStoragePermissions(this);
            requestPermission();
        }

        isBack2 = false;

        try {
            if (list.size() == 0) {
                getData();
            }
        }catch (Exception e) {

        }

        if (HomeActivity.isback == true) {

            for (int j = 0; j < listRecover.size(); j++) {
                try {
                    File file = new File(listRecover.get(j).getPath());
                    for (final Map.Entry<String, Section> entry : sectionAdapter.getCopyOfSectionsMap().entrySet()) {
                        GroupSection group = (GroupSection) entry.getValue();
                        SectionAdapter adapter = sectionAdapter.getAdapterForSection(group);
                        adapter.notifyItemRemoved(group.deleteFileByPath(file.getPath()));
                        adapter.notifyHeaderChanged("notify");
                    }
                    file.delete();
                    refreshGallery(this, file);
                }catch (Exception e) {

                }
            }

            listRecover.clear();
            HomeActivity.isback = false;
            //       if (MainActivity.isback == false) {
            //           getData();
            //      }else {
            //          MainActivity.isback = true;
            //       }
        }
    }

    private void History() {
        img_history.setOnClickListener(view -> {

            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra(Key.KeyIntent, Type);
            startActivity(intent);
            ispause = true;
            isBack2 = true;
        });
    }

    private void RecoverPhoto(String type) {
        if (listRecover.size() > 0) {

//            Bundle bundle = new Bundle();
//            mFirebaseAnalytics.logEvent("RecoverPhoto", bundle);

            List<InfofileHistory> list_history = new ArrayList<>();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < listRecover.size(); i++) {
                        try {

                            Bitmap bm = BitmapFactory.decodeFile(listRecover.get(i).getPath());
                            String path = "/storage/emulated/0/Pictures/" + listRecover.get(i).getName();
                            File file1 = new File(path);
                            file1.createNewFile();
                            FileOutputStream outputStream = new FileOutputStream(file1);
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            refreshGallery(getApplicationContext(), file1);

                            switch (type) {
                                case "photo":
                                    list_history.add(new InfofileHistory(listRecover.get(i).getPath(), listRecover.get(i).getName(),
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

                        try {
                            File file = new File(listRecover.get(i).getPath());
                            file.delete();
                            refreshGallery(getApplicationContext(), file);
                        }catch (Exception e) {
                            Log.d("ewfew", "error: " + e);
                        }
                    }
                }
            });
            thread.start();

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

    private void Recover() {
        bt_recover.setOnClickListener(view -> {

            if (listRecover.size() > 0 || listhide.size() > 0) {
                switch (Type) {
                    case "photo":
                        RecoverPhoto(Key.Photo);
                        break;
                    case "recyclebin":
                        RecoverPhoto(Key.Recyclebin);
                        break;
                    case "KeyPhotoMoIn":
                        MovePhoto();
                        break;
                }
            }else {
                Toast.makeText(this, getString(R.string.nophoto), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void StopLoad() {
        txt_stop.setOnClickListener(view -> {
            ispause = true;
        });
    }

    private void MovePhoto() {

        if (listRecover.size() > 0) {
            for (int i = 0 ; i < listRecover.size(); i++) {
                listhide.add(new InfoFileHide(listRecover.get(i).getPath(),
                        listRecover.get(i).getName(), listRecover.get(i).getDate(),
                        listRecover.get(i).getSize(), Type));
            }
        }

        if (listhide.size() > 0) {
            for (int i = 0; i < listhide.size(); i++) {
                File file = new File(listhide.get(i).getPath());

                Random random = new Random();
                final int min = 10000;
                final int max = 99999;
                int N_EndName = random.nextInt((max - min) + 1) + min;

                // creat folder
                String folder_vault = Key.FolderVaultSave;
                File f = new File(Environment.getExternalStorageDirectory(), folder_vault);
                if (!f.exists()) {
                    f.mkdirs();
                    MoveFile(file, N_EndName, i, listhide.get(i).getType());
                }else {
                    MoveFile(file, N_EndName, i,  listhide.get(i).getType());
                }
                //     file.delete();
            }

            Intent intent = new Intent(this, PendingMoveActivity.class);
            intent.putExtra(Key.KeyIntent, Type);
            intent.putExtra(Key.Key_Type_Screen, Key.FileVault);
            startActivity(intent);

            listhide.clear();
        }

    }

    private void MoveFile(File file, int n_endName, int i, String type) {

        PMove = Key.FolderVault + type + Key.EndNameVault + n_endName;

        File FileMove = new File(PMove);
        try {

            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

            DataHide.getInstance(getApplicationContext()).daoSql().insert(
                    new InfoFileHide(PMove, listhide.get(i).getName(),
                            listhide.get(i).getDate(), listhide.get(i).getSize(),
                            listhide.get(i).getType())
            );
            ExifInterface ei = new ExifInterface(file.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

            FileMove.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(FileMove);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            file.delete();
            refreshGallery(this, file);

        } catch (IOException e) {
            Log.d(Key.KeyLog, "error: " + e);
        }
    }

    private void setNumberRecover() {
        if (listRecover.size() > 0 ){
        }else {
            if (Type.equals(Key.KeyPhotoMoveIn)) {
                bt_recover.setText(getString(R.string.MoveinVault));
            }else {
                bt_recover.setText(getString(R.string.recover));
            }
        }
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
        sectionAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (sectionAdapter.getSectionItemViewType(position) == SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER) {
                    return 4;
                }
                return 1;
            }
        });
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(sectionAdapter);



        ispause = false;
        GroupSection.type = Type;

        if (Type.equals(Key.KeyPhotoMoveIn)) {
            layout_ic_recover.setVisibility(View.GONE);
            layout_ic_vault.setVisibility(View.VISIBLE);
        }

        SetTitle("");
        getData();
    }
    private void SetTitle(String values) {
        switch (Type) {
            case "photo":
                titleviewfile.setText(getString(R.string.photoscan)+values);
                break;
            case "recyclebin":
                titleviewfile.setText(getString(R.string.recyclebinscan)+values);
                break;
            case "KeyPhotoMoIn":
                titleviewfile.setText(getString(R.string.photovault) + values);
                FilterList();
                break;
        }
    }

    private void FilterList() {
        img_filtervault.setOnClickListener(view -> {
            Filter();
        });
    }
    private void getData() {
        dir = Environment.getExternalStorageDirectory();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPermission == true) {
                    new getFile().execute();
                }
            }
        },TIME_OUT);

    }

    public class getFile extends AsyncTask<Void, String, String> {

        @SuppressLint("SimpleDateFormat")
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


                                    if (value.getName().endsWith(".png") ||
                                            value.getName().endsWith(".jpeg") ||
                                            value.getName().endsWith(".jpg")) {

                                        if (!value.getPath().contains("Pictures")) {

                                            try {
                                                Thread.sleep(TIME_OUT);
                                                list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));
                                                publishProgress("" + list.size());
                                            } catch (Exception e) {

                                            }
                                        }

                                    }

                                    break;

                                case "recyclebin":

                                    if(value.getPath().contains(".Trash") || value.getPath().contains(".trash")) {
                                        try {
                                            Thread.sleep(TIME_OUT);
                                            list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));
                                            publishProgress("" + list.size());
                                        } catch (Exception e) {

                                        }
                                    }

                                    break;

                                case "KeyPhotoMoIn":
                                    if (value.getName().endsWith(".png") ||
                                            value.getName().endsWith(".jpeg") ||
                                            value.getName().endsWith(".jpg")) {

                                        try {
                                            Thread.sleep(TIME_OUT);
                                            list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));
                                            publishProgress("" + list.size());
                                        } catch (Exception e) {

                                        }

                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(String... values) {
            txt_scanning.setText(getString(R.string.scanning) +
                    "[ " + list.size() + " ] " + getString(R.string.file));

            try {
                addNewFile(list.get((parseInt(values[0]) - 1)));
            }catch (Exception e) {
                Log.d("fewwef: ", "wfwefw:  " + e);
            }

            super.onProgressUpdate(values);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {

            if (!isBack2) {
                DialogFinish();
            }

            txt_scanning.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);
            txt_stop.setVisibility(View.GONE);
            String values = " [ " + list.size() + " ]";
            SetTitle(values);
            progrssbar.setVisibility(View.GONE);

            if (list.size() > 0) {
                getList6Month();
                getList12Month();
                getList1Year();
                getList2Year();
            }
            ispause = true;
            if (list.size() == 0) {
                layout_nullfile.setVisibility(View.VISIBLE);
            }else {
                layout_nullfile.setVisibility(View.GONE);
            }
            if (Type.equals(Key.KeyPhotoMoveIn)) {
                bt_recover.setBackgroundTintList(ContextCompat.getColorStateList(ViewFileGroupActivity.this, R.color.black));
                bt_recover.setText(getString(R.string.MoveinVault));
            }else {
                bt_recover.setBackgroundTintList(ContextCompat.getColorStateList(ViewFileGroupActivity.this, R.color.black));
                bt_recover.setText(getString(R.string.recover));
            }

            addRecent();

            super.onPostExecute(s);
        }
    }

    private void addRecent() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (list.size() > 0) {

                    List<InforRecent> listrecent = new ArrayList<>();
                    try {
                        listrecent = DataRecent.getInstance(getApplicationContext()).daoSql().getall();
                    }catch (Exception e) {
                        Log.d("efwfwe", "error: " + e);
                    }

                    if (listrecent != null) {
                        Add(listrecent);
                    }
                }
            }
        });
        thread.start();
    }

    private void Add(List<InforRecent> listrecent) {
        for (int i = 0; i < list.size(); i++) {

            for (int j = 0; j < listrecent.size(); j ++) {

                try {
                    if (list.get(i).getPath().equals(listrecent.get(j).getPath())) {
                        DataRecent.getInstance(getApplicationContext()).daoSql().delete(listrecent.get(j));
                    }
                }catch (Exception e) {
                    Log.d("ewewe", "error: " + e);
                }
            }

            try {
                DataRecent.getInstance(getApplicationContext()).daoSql().insert(new InforRecent(
                        list.get(i).getPath(), list.get(i).getName(),
                        list.get(i).getDate(), list.get(i).getSize()
                ));
            }catch (Exception e) {
                Log.d("ewewf", "error: " + e);
            }
        }
    }
    private void getList6Month(){
        list6Month = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
                for (int i = 0; i < list.size(); i++) {
                    File file = new File(list.get(i).getPath());
                    long TimeList = file.lastModified()/1000 / 3600 /24;
                    if (TimeList - thisTime > -180) {
                        try {
                            list6Month.add(list.get(i));
                        }catch (Exception e) {
                            Log.d("wfwefew", "error list 6month: " + e);
                        }
                    }
                }
            }
        });

        thread.start();
    }

    private void getList12Month(){
        list12Month = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
                for (int i = 0; i < list.size(); i++) {
                    File file = new File(list.get(i).getPath());
                    long TimeList = file.lastModified()/1000 / 3600 /24;
                    if (TimeList - thisTime < -180 && TimeList - thisTime > -365) {
                        try {
                            list12Month.add(list.get(i));
                        }catch (Exception e) {
                            Log.d("wfwefew", "error list 12month: " + e);
                        }
                    }
                }
            }
        });

        thread.start();
    }

    private void getList1Year(){

        list1Year = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
                for (int i = 0; i < list.size(); i++) {
                    File file = new File(list.get(i).getPath());
                    long TimeList = file.lastModified()/1000 / 3600 /24;
                    if (TimeList - thisTime < -365 && TimeList - thisTime > -730) {
                        try {
                            list1Year.add(list.get(i));
                        }catch (Exception e) {
                            Log.d("wfwefew", "error list 1year: " + e);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    private void getList2Year(){
        list2Year = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
                for (int i = 0; i < list.size(); i++) {
                    File file = new File(list.get(i).getPath());
                    long TimeList = file.lastModified()/1000 / 3600 /24;
                    if (TimeList - thisTime < -730) {
                        try {
                            list2Year.add(list.get(i));
                        }catch (Exception e) {
                            Log.d("wfwefew", "error list 2year: " + e);
                        }
                    }
                }
            }
        });

        thread.start();
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

//            new AdsUtils(ViewFileGroup.this).showAdsInterstitiaAd2(ViewFileGroup.this);

            dialog.cancel();
        });

        if (list != null) {
            txt_number.setText(getString(R.string.file1) + " " + list.size());
        }
        float kb = 0;

        if (list != null && list.size() > 0) {

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


    private void addNewFile(InfoFile file) {
        if (sectionAdapter.getSection(getTag(file.getDate())) != null) {
            GroupSection group = ((GroupSection) sectionAdapter.getSection(getTag(file.getDate())));
            group.files.add(file);
            SectionAdapter adapter = sectionAdapter.getAdapterForSection(group);
            adapter.notifyItemInserted(group.files.size() - 1);
            adapter.notifyHeaderChanged("notify");
        } else {
            List<InfoFile> list = new ArrayList<>();
            list.add(file);
            GroupSection group = new GroupSection(this, list, getTag(file.getDate()), this);

            sectionAdapter.addSection(getTag(file.getDate()), group);
            sectionAdapter.notifyItemInserted(sectionAdapter.getSectionCount());
        }
    }
    private void Filter() {

        Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_filter);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
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
            for (int i = 0; i < list.size(); i ++) {
                Clear(list.get(i));
            }
            sectionAdapter.notifyDataSetChanged();

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
    }

    private void FilterMode() {
        list.clear();
        if (is6month == true) {
            if (list6Month.size() > 0) {
                for (int i = 0; i < list6Month.size(); i ++) {
                    list.add(list6Month.get(i));
                    addNewFile(list.get(list.size()-1));
                }
            }

        }
        if (is12month == true) {
            if (list12Month.size() > 0) {
                for (int i = 0; i < list12Month.size(); i ++) {
                    list.add(list12Month.get(i));
                    addNewFile(list.get(list.size()-1));
                }
            }
        }
        if (is1years == true) {
            if (list1Year.size() > 0) {
                for (int i = 0; i < list1Year.size(); i ++) {
                    list.add(list1Year.get(i));
                    addNewFile(list.get(list.size()-1));
                }
            }
        }
        if (is2years == true) {
            if (list2Year.size() > 0) {
                for (int i = 0; i < list2Year.size(); i ++) {
                    list.add(list2Year.get(i));
                    addNewFile(list.get(list.size()-1));
                }
            }

        }
        String values = " [ " + list.size() + " ]";
        SetTitle(values);
    }

    private String getTag(String dateTime) {
        String key;
        if (dateTime.split("-")[0].equals(Calendar.getInstance().get(Calendar.YEAR) + "")) {
            key = dateTime.split("-")[0] + "-" + dateTime.split("-")[1];
        }
        //for other year, hash to year
        else {
            key = dateTime.split("-")[0];
        }
        return key;
    }
    private void Clear(InfoFile file) {
        try {
            GroupSection group = ((GroupSection) sectionAdapter.getSection(getTag(file.getDate())));
            group.files.clear();
        }catch (Exception e) {
            Log.d("wfewwf", "error: " + e);
        }
    }
    private void ClickFilter() {

        img_filter.setOnClickListener(view -> {

            ispause = true;

//            new AdsUtils(ViewFileGroup.this).showAdsInterstitiaAd2(ViewFileGroup.this);

            Filter();
        });
    }
    private void ClickBack() {
        img_back.setOnClickListener(view -> {

//            new AdsUtils(ViewFileGroup.this).showAdsInterstitiaAd2(ViewFileGroup.this);
            listRecover.clear();
            ispause = true;
            isBack2 = true;
            finish();
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

        layout_ic_recover = findViewById(R.id.layout_ic_recover);
        layout_ic_vault = findViewById(R.id.layout_ic_vault);

        txt_stop = findViewById(R.id.txt_stop);

        list = new ArrayList<>();
        listRecover = new ArrayList<>();
        listhide = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setHasFixedSize(true);

        img_filtervault = findViewById(R.id.img_filtervault);
    }

    @Override
    public void onHeaderRootViewClicked(@NonNull GroupSection section) {
        final SectionAdapter adapter = sectionAdapter.getAdapterForSection(section);

        // store info of current section state before changing its state
        final boolean wasExpanded = section.isExpanded;
        final int previousItemsTotal = section.getContentItemsTotal();

        section.isExpanded = !wasExpanded;
        adapter.notifyHeaderChanged("expand");

        if (wasExpanded) {
            section.getSelectedAmount();
            adapter.notifyItemRangeRemoved(0, previousItemsTotal);
        } else {
            adapter.notifyAllItemsInserted();
        }
    }

    @Override
    public void onHeaderRootViewChecked(@NonNull GroupSection section, int count, long size) {
        SectionAdapter adapter = sectionAdapter.getAdapterForSection(section);
        adapter.notifyHeaderChanged("notify");
        adapter.notifyAllItemsChanged("notify");

        if (section.isChecked) {
            this.count += count;
            this.totalSize += size;
        } else {
            this.count -= count;
            this.totalSize -= size;
        }
        if (Type.equals(Key.KeyPhotoMoveIn)) {
            bt_recover.setText(getString(R.string.MoveinVault) + " [ " + convertSize(totalSize) + "/" + this.count + " ] ");
        }else {
            bt_recover.setText(getString(R.string.recover) + " [ " + convertSize(totalSize) + "/" + this.count + " ] ");

        }
        if (this.count == 0 && ispause) {
            list.clear();
            bt_recover.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));

            if(Type.equals(Key.KeyPhotoMoveIn)) {
                bt_recover.setText(getString(R.string.MoveinVault));
            }else {
                bt_recover.setText(getString(R.string.recover));
            }
        } else if (ispause) {
            bt_recover.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue017));
        }
    }

    @Override
    public void onItemRootViewClicked(@NonNull GroupSection section, int itemAdapterPosition) {

    }

    @Override
    public void onSelectedFile(boolean isSelected, int size) {
        if (isSelected) {
            count++;
            totalSize += size;
        } else {
            count--;
            totalSize -= size;
        }
        if (count == 0 && ispause) {
            list.clear();
            bt_recover.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            if (Type.equals(Key.KeyPhotoMoveIn)) {
                bt_recover.setText(getString(R.string.MoveinVault));
            }else {
                bt_recover.setText(getString(R.string.recover));
            }
        } else if (ispause) {
            bt_recover.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue017));
            if (Type.equals(Key.KeyPhotoMoveIn)){
                bt_recover.setText(getString(R.string.MoveinVault) + " [ " + convertSize(totalSize) + "/" + count + " ] ");
            }else {
                bt_recover.setText(getString(R.string.recover) + " [ " + convertSize(totalSize) + "/" + count + " ] ");
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private String convertSize(long totalSize) {
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        float fileSizeInMB = (float) totalSize / 1024;
        if ((float) totalSize < 1024)
            return ((float) Math.floor((float) totalSize * 10) / 10) + "KB";
        else return ((float) Math.floor(fileSizeInMB * 10) / 10) + "MB";
    }
}