package com.fileclean.mkmfilerecovery.Activity;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;
import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fileclean.mkmfilerecovery.BuildConfig;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHide.DataHide;
import com.fileclean.mkmfilerecovery.Database.DataHide.InfoFileHide;
import com.fileclean.mkmfilerecovery.Database.DataHistory.DataHistory;
import com.fileclean.mkmfilerecovery.Database.DataHistory.InfofileHistory;
import com.fileclean.mkmfilerecovery.Model.InfoFile;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DetailsActivity extends AppCompatActivity {

    private ImageView img_file, img_back, img_fullscreen;
    private TextView txt_name, txt_type, txt_size, txt_date, txt_path;
    private String path;
    private TextView txt_fullscreen;
    private TextView bt_recover;
    private String type;
    private String Pmove;
    private Window window;

    private String PMove;

//    private FirebaseAnalytics mFirebaseAnalytics;
//
//    private AdsUtils adsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initView();
        getIntenData();
        ClickBack();
        FullScreen();
        Recover();
        setStatusbar();
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
    private void FullScreen() {
        img_fullscreen.setOnClickListener(view -> {
            if (type != null) {
                setFullscreen();
            }
        });
    }

    private void DialogPlayMp3() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_play_audio);
        dialog.setCanceledOnTouchOutside(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        ImageView img_pause = dialog.findViewById(R.id.img_pause);
        SeekBar seekBar = dialog.findViewById(R.id.seekbar);
        TextView cancel = dialog.findViewById(R.id.txt_cancel);
        TextView timestar = dialog.findViewById(R.id.time_star);
        TextView timeend = dialog.findViewById(R.id.time_end);

        MediaPlayer playr = MediaPlayer.create(this, Uri.parse(path));
        if (playr != null) {
            int time = playr.getDuration()/1000;
            int mtime = time/60;
            int stime = time%60;
            if (mtime < 10) {
                if (stime < 10) {
                    timeend.setText("0" + mtime + ":0" + stime);
                }else {
                    timeend.setText("0" + mtime + ":" + stime);
                }
            }else {
                timeend.setText(mtime + ":" + stime);

            }

            playr.start();

            seekBar.setMax(time);
        }


        Handler mHandler = new Handler();
//Make sure you update Seekbar on UI thread
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(playr != null){
                    int mCurrentPosition = playr.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        cancel.setOnClickListener(view -> {
            if (playr != null && playr.isPlaying()) {
                playr.stop();
            }
            dialog.cancel();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (seekBar.getProgress() < 10) {
                    timestar.setText("00:0" +seekBar.getProgress());
                }else if (seekBar.getProgress() < 60){
                    timestar.setText("00:" + seekBar.getProgress());
                }else {
                    int mtime = seekBar.getProgress()/60;
                    int stime = seekBar.getProgress() % 60;
                    if (mtime<10){
                        if (stime < 10) {
                            timestar.setText("0" + mtime +":0" + stime);
                        }else {
                            timestar.setText("0" + mtime +":" + stime);
                        }
                    }else {
                        timestar.setText(mtime +":" + stime);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        File file = new File(path);
        txt_name.setText(file.getName());

        img_pause.setOnClickListener(view -> {
            if (playr != null) {
                if (playr.isPlaying()){
                    img_pause.setImageResource(R.drawable.ic_play);
                    playr.pause();
                }else {
                    img_pause.setImageResource(R.drawable.ic_pause_24);
                    playr.start();
                }
            }
        });

        dialog.show();

    }

    private void setFullscreen() {
        if (type.equals(Key.KeyPhotoMoveIn)) {
            Intent intent = new Intent(this, PreViewImageActivity.class);
            intent.putExtra(Key.KeyIntent, path);
            startActivity(intent);
        }else if (type.equals(Key.KeyvideoMoveIn)){
            Intent intent = new Intent(this, PreViewVideoActivity.class);
            intent.putExtra(Key.KeyIntent, path);
            startActivity(intent);
        }else if (type.equals(Key.KeyAudioMoveIn)) {
            DialogPlayMp3();
        }else if (type.equals(Key.KeyDocumentMoveIn)) {

            Uri targetUri = FileProvider.getUriForFile(DetailsActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(path));
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent2.setDataAndType(targetUri, "application/*");
            startActivityForResult(intent2, 2222);

        }else {
            if (ViewFileActivity.Type != null) {
                setFullscreen2();
            }
        }
    }

    private void setFullscreen2() {
        switch (ViewFileActivity.Type) {
            case "photo":
                Intent intent = new Intent(this, PreViewImageActivity.class);
                intent.putExtra(Key.KeyIntent, path);
                startActivity(intent);
                break;
            case "video":
                Intent intent1 = new Intent(this, PreViewVideoActivity.class);
                intent1.putExtra(Key.KeyIntent, path);
                startActivity(intent1);
                break;
            case "audio":
                DialogPlayMp3();
                break;
            case "recyclebin":
                Intent intent3 = new Intent(this, PreViewImageActivity.class);
                intent3.putExtra(Key.KeyIntent, path);
                startActivity(intent3);
                break;
            case "document":
                Uri targetUri = FileProvider.getUriForFile(DetailsActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(path));
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent2.setDataAndType(targetUri, "application/*");
                startActivityForResult(intent2, 2222);
                break;
            case "zip":
                //    String[] separated = path.split("\\.zip");
                //   String targetPath = separated[0] + "_tg";
                //   unzip(path,targetPath);
                Uri targetUri1 = FileProvider.getUriForFile(DetailsActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(path));
                Intent intent5 = new Intent(Intent.ACTION_VIEW);
                intent5.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent5.setDataAndType(targetUri1, "application/*");
                startActivityForResult(intent5, 2222);
                break;
        }
    }

    private void MovePhoto() {
        List<InfofileHistory> list_history = new ArrayList<>();
        try {
            ViewFileGroupActivity.listRecover.add(new InfoFile(path, "","",""));
        }catch (Exception e) {
            Log.d("wgwgew", "error list details recover: " + e);
        }
        File file = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        String PMove = Key.Path_Recover_Photo + file.getName();
        File FileRecover = new File(PMove);
        try {
            FileOutputStream outputStream = new FileOutputStream(FileRecover);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
            }
            refreshGallery(this, FileRecover);
            file.delete();

            int file_size = parseInt(String.valueOf(file.length() / 1024));
            // get day
            Date lastModDate = new Date(file.lastModified());

            list_history.add(new InfofileHistory(path, file.getName(),
                    lastModDate+"", file_size+"", type));
            DataHistory.getInstance(getApplicationContext()).daoSql().insert(list_history.get(0));

        } catch (FileNotFoundException e) {
            Log.d("ewfwefw", "Error: " + e);
            e.printStackTrace();
        }
    }

    public void refreshGallery(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(mediaScanIntent);
    }

    private void MoveFile() {
        List<InfofileHistory> list_history = new ArrayList<>();

        File file = new File(path);

        String namefile = file.getName();

        Pmove = Key.Path_Recover_file + namefile;
        File Filerecover = new File(Pmove);
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

                int file_size = parseInt(String.valueOf(file.length() / 1024));
                // get day
                Date lastModDate = new Date(file.lastModified());

                list_history.add(new InfofileHistory(path, file.getName(),
                        lastModDate+"", file_size+"", type));
                DataHistory.getInstance(getApplicationContext()).daoSql().insert(list_history.get(0));
            }
        }catch (Exception e) {

        }
    }

    private void HideFileDetails() {
        File file = new File(path);

        Random random = new Random();
        final int min = 10000;
        final int max = 99999;
        int N_EndName = random.nextInt((max - min) + 1) + min;

        // creat folder
        String folder_vault = Key.FolderVaultSave;
        File f = new File(Environment.getExternalStorageDirectory(), folder_vault);
        if (!f.exists()) {
            f.mkdirs();
            HideFile(file, N_EndName, type);
        }else {
            HideFile(file, N_EndName, type);
        }

    }

    private void HideFile(File file, int n_endName, String type) {
        int file_size = parseInt(String.valueOf(file.length() / 1024));
        // get day
        Date lastModDate = new Date(file.lastModified());

        if (type.equals(Key.KeyDocumentMoveIn)) {

            PMove = Key.FolderVault + type + file.getName() + Key.EndNameVault + n_endName;

        }else {
            PMove = Key.FolderVault + type + Key.EndNameVault + n_endName;
        }

        File FileMove = new File(PMove);
        try {

            switch (type) {
                case "KeyPhotoMoIn":
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

                    DataHide.getInstance(getApplicationContext()).daoSql().insert(
                            new InfoFileHide(PMove, file.getName(),
                                    lastModDate+"", file_size+"",
                                    type)
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

                    break;

                case "KeyvideoMoveIn":
                    MoveVideo_Audio(PMove, FileMove, file);
                    break;

                case "KeyAudioMoveIn":
                    MoveVideo_Audio(PMove, FileMove, file);

                    break;
                case "KeyDocumentMoveIn":
                    MoveVideo_Audio(PMove, FileMove, file);
                    break;

            }
            file.delete();
            refreshGallery(this, file);

        } catch (IOException e) {
            Log.d(Key.KeyLog, "error: " + e);
        }
    }

    private void MoveVideo_Audio(String PMove, File FileMove, File file) {

        int file_size = parseInt(String.valueOf(file.length() / 1024));
        // get day
        Date lastModDate = new Date(file.lastModified());

        try {
            if (file.exists()) {

                DataHide.getInstance(getApplicationContext()).daoSql().insert(
                        new InfoFileHide(PMove, file.getName(),
                                lastModDate+"", file_size+"", type));

                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream1 = new FileOutputStream(FileMove);

                byte[] buf = new byte[1024];
                int len;

                while ((len = inputStream.read(buf)) > 0) {
                    outputStream1.write(buf, 0, len);
                }

                outputStream1.flush();
                inputStream.close();
                outputStream1.close();

            } else {
                Toast.makeText(this, R.string.file_has_faild, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {

        }
    }

    private void MoveVideo_Audio(String typefile) {
        List<InfofileHistory> list_history = new ArrayList<>();

        File file = new File(path);
        switch (typefile) {
            case "video":
                Pmove = Key.Path_Recover_Videos + file.getName() + ".mp4";
                break;
            case "audio":
                Pmove = Key.Path_Recover_Audio + file.getName() + ".mp3";
                break;

        }
        File Filerecover = new File(Pmove);
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

                int file_size = parseInt(String.valueOf(file.length() / 1024));
                // get day
                Date lastModDate = new Date(file.lastModified());

                list_history.add(new InfofileHistory(path, file.getName(),
                        lastModDate+"", file_size+"", type));
                DataHistory.getInstance(getApplicationContext()).daoSql().insert(list_history.get(0));
            }

        }catch (Exception e) {

        }
    }

    private void Recover() {
        bt_recover.setOnClickListener(view -> {

         //   new AdsUtils(Details.this).showAdsInterstitiaAd(Details.this);

            switch (type) {
                case "photo":
                    MovePhoto();
                    break;
                case "video":
                    MoveVideo_Audio(Key.Video);
                    break;
                case "audio":
                    MoveVideo_Audio(Key.Audio);
                    break;
                case "file":
                    MoveFile();
                    break;
                case "KeyPhotoMoIn":
                    HideFileDetails();
                    break;
                case "KeyvideoMoveIn":
                    HideFileDetails();
                    break;
                case "KeyAudioMoveIn":
                    HideFileDetails();
                    break;
                case "KeyDocumentMoveIn":
                    HideFileDetails();
                    break;
            }
            Intent intent = new Intent(this, PendingMoveActivity.class);
            intent.putExtra(Key.KeyIntent, type);
            intent.putExtra(Key.Key_Type_Screen, Key.Viewfile);
            startActivity(intent);

        });
    }
    private void ClickBack() {
        img_back.setOnClickListener(view -> {

        //    new AdsUtils(Details.this).showAdsInterstitiaAd2(Details.this);

            finish();
        });
    }
    private void getIntenData() {
        if (getIntent() != null) {
            path = getIntent().getStringExtra(Key.KeyIntent);
            type = getIntent().getStringExtra(Key.Key_Type_Screen);
            Glide.with(getApplicationContext()).load(path).into(img_file);
            if (type.equals(Key.KeyPhotoMoveIn) || type.equals(Key.KeyvideoMoveIn)){
                Glide.with(getApplicationContext()).load(path).into(img_file);
                File file = new File(path);
                int file_size = parseInt(String.valueOf(file.length() / 1024));
                // get day
                Date lastModDate = new Date(file.lastModified());
                txt_name.setText(file.getName());
                txt_path.setText(path);
                txt_date.setText(lastModDate + "");
                txt_size.setText(file_size +" Kb");

                bt_recover.setText(getString(R.string.MoveinVault));
            }else if (type.equals(Key.KeyDocumentMoveIn)){
                txt_fullscreen.setText(getString(R.string.open));
                img_fullscreen.setImageResource(R.drawable.ic_open);
                Glide.with(getApplicationContext()).load(R.drawable.ic_document_details).into(img_file);
                bt_recover.setText(getString(R.string.MoveinVault));

                File file = new File(path);
                int file_size = parseInt(String.valueOf(file.length() / 1024));
                // get day
                Date lastModDate = new Date(file.lastModified());
                txt_name.setText(file.getName());
                txt_path.setText(path);
                txt_date.setText(lastModDate + "");
                txt_size.setText(file_size +" Kb");

            }else if (type.equals(Key.KeyAudioMoveIn)){
                txt_fullscreen.setText(getString(R.string.open));
                img_fullscreen.setImageResource(R.drawable.ic_open);
                Glide.with(getApplicationContext()).load(R.drawable.ic_details_mucsic).into(img_file);
                bt_recover.setText(getString(R.string.MoveinVault));

                File file = new File(path);
                int file_size = parseInt(String.valueOf(file.length() / 1024));
                // get day
                Date lastModDate = new Date(file.lastModified());
                txt_name.setText(file.getName());
                txt_path.setText(path);
                txt_date.setText(lastModDate + "");
                txt_size.setText(file_size +" Kb");

            }else {
                setView();
            }
        }
    }

    private void setView() {

        switch (type) {
            case "photo":
                Glide.with(getApplicationContext()).load(path).into(img_file);
                break;
            case "video":
                txt_fullscreen.setText(getString(R.string.open));
                img_fullscreen.setImageResource(R.drawable.ic_open);
                Glide.with(getApplicationContext()).load(path).into(img_file);
                break;
            case "audio":
                txt_fullscreen.setText(getString(R.string.open));
                img_fullscreen.setImageResource(R.drawable.ic_open);
                Glide.with(getApplicationContext()).load(R.drawable.ic_details_mucsic).into(img_file);
                break;
            case "recyclebin":
                Glide.with(getApplicationContext()).load(path).into(img_file);
                break;
            case "document":
                txt_fullscreen.setText(getString(R.string.open));
                img_fullscreen.setImageResource(R.drawable.ic_open);
                Glide.with(getApplicationContext()).load(R.drawable.ic_document_details).into(img_file);
                break;
            case "zip":
                txt_fullscreen.setText(getString(R.string.open));
                img_fullscreen.setImageResource(R.drawable.ic_open);
                Glide.with(getApplicationContext()).load(R.drawable.zip_details).into(img_file);
                break;
        }

        File file = new File(path);
        int file_size = parseInt(String.valueOf(file.length() / 1024));
        // get day
        Date lastModDate = new Date(file.lastModified());
        txt_name.setText(file.getName());
        txt_path.setText(path);
        txt_date.setText(lastModDate + "");
        txt_size.setText(file_size +" Kb");
    }

    private void initView() {

        img_back = findViewById(R.id.img_back);
        img_file = findViewById(R.id.img_file);
        txt_name = findViewById(R.id.txt_name);
        txt_type = findViewById(R.id.txt_type);
        txt_size = findViewById(R.id.txt_size);
        txt_date = findViewById(R.id.txt_date);
        txt_path = findViewById(R.id.txt_path);
        img_fullscreen = findViewById(R.id.img_fullscreen);
        txt_fullscreen = findViewById(R.id.txt_fullscreen);
        bt_recover = findViewById(R.id.bt_recover);
    }

    @Override
    public void onBackPressed() {

   //     new AdsUtils(Details.this).showAdsInterstitiaAd2(Details.this);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HomeActivity.isback == true) {
            finish();
        }else {
            Log.d("ewfwef", "lll: " + HomeActivity.isback);
        }
    }
}