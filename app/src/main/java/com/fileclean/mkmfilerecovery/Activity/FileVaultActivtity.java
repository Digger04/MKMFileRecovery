package com.fileclean.mkmfilerecovery.Activity;

import static android.os.Build.VERSION.SDK_INT;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fileclean.mkmfilerecovery.BuildConfig;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHide.DataHide;
import com.fileclean.mkmfilerecovery.Database.DataHide.InfoFileHide;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.sql.Date;

public class FileVaultActivtity extends AppCompatActivity {

    private ImageView img_back;
    private TextView number_photo, number_video, number_audio, number_file;
    private LinearLayout layout_photo, layout_video, layout_audio, layout_file;

    private TextView txt_video, txt_audio, txt_file, txt_photo;

    private int N_photo, N_video, N_audio, N_file;
    private String type;
    private Window window;

    private File dir;

    private boolean ispermission = false;

//    private FirebaseAnalytics mFirebaseAnalytics;
//    private AdsUtils adsUtils;

//    private ShimmerFrameLayout shimmerFrameLayout;
    private FrameLayout frAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_vault_activtity);

        initview();
        ClickBack();
        SelectFun();
        setStatusbar();
    }

    private void SelectFun() {

        layout_photo.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewFileVaultActivity.class);
            intent.putExtra(Key.KeyIntent, Key.Photo);
            startActivity(intent);
        });

        layout_video.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewFileVaultActivity.class);
            intent.putExtra(Key.KeyIntent, Key.Video);
            startActivity(intent);
        });

        layout_audio.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewFileVaultActivity.class);
            intent.putExtra(Key.KeyIntent, Key.Audio);
            startActivity(intent);
        });

        layout_file.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewFileVaultActivity.class);
            intent.putExtra(Key.KeyIntent, Key.File);
            startActivity(intent);
        });
    }
    private void ClickBack() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initview() {
        img_back = findViewById(R.id.img_back);

        number_photo = findViewById(R.id.number_photo);
        number_video = findViewById(R.id.number_video);
        number_audio = findViewById(R.id.number_audio);
        number_file = findViewById(R.id.number_file);

        txt_video = findViewById(R.id.txt_video);
        txt_video.setSelected(true);

        txt_photo = findViewById(R.id.txt_photo);
        txt_photo.setSelected(true);

        txt_audio = findViewById(R.id.txt_audio);
        txt_audio.setSelected(true);

        txt_file = findViewById(R.id.txt_file);
        txt_file.setSelected(true);

        layout_photo = findViewById(R.id.layout_photo);
        layout_audio = findViewById(R.id.layout_audio);
        layout_video = findViewById(R.id.layout_video);
        layout_file = findViewById(R.id.layout_file);

    }

    private void setViewcl() {
        N_photo = 0;
        N_video = 0;
        N_audio = 0;
        N_file = 0;
        for (int i = 0; i < HomeActivity.listHideLocal.size(); i++) {
            Log.d("fwefw", "\n type: " + HomeActivity.listHideLocal.get(i).getType());
            if (HomeActivity.listHideLocal.get(i).getPath().contains(Key.KeyPhotoMoveIn)) {
                N_photo++;
            }
            if (HomeActivity.listHideLocal.get(i).getPath().contains(Key.Video)) {
                N_video++;
            }
            if (HomeActivity.listHideLocal.get(i).getPath().contains(Key.KeyAudioMoveIn)) {
                N_audio++;
            }
            if (HomeActivity.listHideLocal.get(i).getPath().contains(Key.KeyDocumentMoveIn)) {
                N_file++;
            }
        }

        number_photo.setText(N_photo +"");
        number_video.setText(N_video+"");
        number_file.setText(N_file+"");
        number_audio.setText(N_audio+"");
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            HomeActivity.listHideLocal.clear();
        }catch (Exception e) {

        }
        HomeActivity.listHideLocal = DataHide.getInstance(getApplicationContext()).daoSql().getall();
        setViewcl();
        if (!checkPermission()) {
            verifyStoragePermissions(this);
            requestPermission();
        }
        getData();
    }

    public class getFileLocal extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            File[] FileList = dir.listFiles();

            if (FileList != null) {

                for (File value : FileList) {
                    if (value.isDirectory()) {
                        dir = value;
                        new getFileLocal().doInBackground();
                    } else if (value.getPath().contains(Key.FolderVault)){

                        File file = new File(value.getPath());
                        int file_size = parseInt(String.valueOf(file.length() / 1024));
                        // get day
                        Date lastModDate = new Date(file.lastModified());
                        if (value.getName().contains(Key.KeyPhotoMoveIn)) {
                            type = Key.KeyPhotoMoveIn;
                        }else if (value.getName().contains(Key.Video)) {
                            type = Key.Video;
                        }else if (value.getName().contains(Key.Audio)) {
                            type = Key.Audio;
                        }else if (value.getName().contains(Key.File)) {
                            type = Key.File;
                        }

                        DataHide.getInstance(getApplicationContext()).daoSql().insert(new InfoFileHide(value.getPath(),
                                value.getName(), lastModDate+"", file_size+"", type));

                        publishProgress(value.getPath()+"");
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            setViewcl();
            super.onPostExecute(s);
        }
    }

    private void getData() {
        HomeActivity.listHideLocal.clear();
        HomeActivity.listHideLocal = DataHide.getInstance(getApplicationContext()).daoSql().getall();
        if (HomeActivity.listHideLocal.size() == 0) {
            dir =  Environment.getExternalStorageDirectory();
            new getFileLocal().execute();
            HomeActivity.listHideLocal = DataHide.getInstance(getApplicationContext()).daoSql().getall();
        }
        //    listHideLocal = DataHide.getInstance(getApplicationContext()).daoSql().getall();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    private void requestPermission() {
        if (SDK_INT >= 30) {

            if (!Environment.isExternalStorageManager()) {
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_permission);
                dialog.setCanceledOnTouchOutside(false);

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.getWindow().setLayout(-1, -2);
                }

                Button bt_ok = dialog.findViewById(R.id.bt_ok);
                bt_ok.setOnClickListener(v -> {
                    try {
                        Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                        startActivity(intent);
                        dialog.dismiss();
                    } catch (Exception ex) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                    dialog.dismiss();
                });

                dialog.show();

            } else {
                ispermission = true;
            }
        } else {
            if (ContextCompat.checkSelfPermission(FileVaultActivtity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(FileVaultActivtity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(FileVaultActivtity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            222);
                }
            }
        }
    }

    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permissionpermiss
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
    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.MANAGE_EXTERNAL_STORAGE") == 0;
    }

    private void setStatusbar() {
        window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.greyf2));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}