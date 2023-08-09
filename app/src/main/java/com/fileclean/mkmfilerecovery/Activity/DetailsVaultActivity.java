package com.fileclean.mkmfilerecovery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fileclean.mkmfilerecovery.BuildConfig;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHide.DataHide;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DetailsVaultActivity extends AppCompatActivity {

    private ImageView img_back;
    private ImageView img_file;
    private TextView txt_name;
    private Button bt_moveout;
    private Button bt_open;

    private File file;
    private String path;
    private String Stype;
    private String Pmove;

    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_vault);

        initView();
        ClickBack();
        getIntentData();
        OpenFile();
        MoveOut();
        setStatusbar();
    }

    private void OpenFile() {

        switch (Stype) {
            case "photo":
                bt_open.setOnClickListener(view -> {

             //       new AdsUtils(Details_Vault.this).showAdsInterstitiaAd2(Details_Vault.this);

                    Intent intent = new Intent(this, PreViewImageActivity.class);
                    intent.putExtra(Key.KeyIntent, path);
                    startActivity(intent);
                });
                break;
            case "video":
                bt_open.setOnClickListener(view -> {

               //     new AdsUtils(Details_Vault.this).showAdsInterstitiaAd2(Details_Vault.this);

                    Intent intent = new Intent(this, PreViewVideoActivity.class);
                    intent.putExtra(Key.KeyIntent, path);
                    startActivity(intent);
                });
                break;
            case "audio":
                bt_open.setOnClickListener(view -> {

         //           new AdsUtils(Details_Vault.this).showAdsInterstitiaAd2(Details_Vault.this);

                    DialogPlayMp3();
                });
                break;
            case "file":
                bt_open.setOnClickListener(view -> {
                    Uri targetUri = FileProvider.getUriForFile(DetailsVaultActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(path));
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent2.setDataAndType(targetUri, "application/*");
                    startActivityForResult(intent2, 2222);
                });
                break;
        }
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
            playr.stop();
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
            if (playr.isPlaying()){
                img_pause.setImageResource(R.drawable.ic_play);
                playr.pause();
            }else {
                img_pause.setImageResource(R.drawable.ic_pause_24);
                playr.start();
            }
        });

        dialog.show();

    }

    private void MoveFile() {
        String namefile = file.getName().substring(17, file.getName().length() -13);

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
                for (int i = 0; i < HomeActivity.listHideLocal.size(); i++) {
                    if (file.getPath().equals(HomeActivity.listHideLocal.get(i).getPath())) {
                        DataHide.getInstance(getApplicationContext()).daoSql().delete(HomeActivity.listHideLocal.get(i));
                        HomeActivity.listHideLocal.remove(HomeActivity.listHideLocal.get(i));
                    }
                }
            }
        }catch (Exception e) {

        }
    }

    public void refreshGallery(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(mediaScanIntent);
    }

    private void MovePhoto() {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        String PMove = Key.Path_Recover_Photo + file.getName() + ".png";
        File FileRecover = new File(PMove);
        try {
            FileOutputStream outputStream = new FileOutputStream(FileRecover);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
            }
            refreshGallery(this, FileRecover);
            file.delete();
            for (int i = 0; i < HomeActivity.listHideLocal.size(); i++) {
                if (file.getPath().equals(HomeActivity.listHideLocal.get(i).getPath())) {
                    DataHide.getInstance(getApplicationContext()).daoSql().delete(HomeActivity.listHideLocal.get(i));
                    HomeActivity.listHideLocal.remove(HomeActivity.listHideLocal.get(i));
                }
            }

        } catch (FileNotFoundException e) {
            Log.d("ewfwefw", "Error: " + e);
            e.printStackTrace();
        }

    }
    private void MoveOut() {
        bt_moveout.setOnClickListener(view -> {
            switch (Stype) {
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
            }

            Intent intent = new Intent(this, PendingMoveActivity.class);
            intent.putExtra(Key.KeyIntent, Stype);
            intent.putExtra(Key.Key_Type_Screen, Key.FileVault);
            startActivity(intent);
        });
    }

    private void MoveVideo_Audio(String typefile) {

        String namefile = file.getName().substring(14, file.getName().length() -13);

        switch (typefile) {
            case "video":
                Pmove = Key.Path_Recover_Videos + namefile;
                break;
            case "audio":
                Pmove = Key.Path_Recover_Audio + namefile;
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
                for (int i = 0; i < HomeActivity.listHideLocal.size(); i++) {
                    if (file.getPath().equals(HomeActivity.listHideLocal.get(i).getPath())) {
                        DataHide.getInstance(getApplicationContext()).daoSql().delete(HomeActivity.listHideLocal.get(i));
                        HomeActivity.listHideLocal.remove(HomeActivity.listHideLocal.get(i));
                    }
                }
            }

        }catch (Exception e) {

        }
    }

    private void FilterStype() {
        if (file.getName().contains(Key.KeyPhotoMoveIn)) {
            Stype = Key.Photo;
            Glide.with(getApplicationContext()).load(file.getPath()).into(img_file);
        }else if (file.getName().contains(Key.KeyAudioMoveIn)) {
            Stype = Key.Audio;
            Glide.with(getApplicationContext()).load(R.drawable.ic_mp3).into(img_file);
        }else if (file.getName().contains(Key.KeyDocumentMoveIn)) {
            Stype = Key.File;

            if (file.getName().contains(".txt")) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_filetxt).into(img_file);
            }else if (file.getName().contains(".pdf")) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_pdf).into(img_file);
            }else if (file.getName().contains(".doc") || file.getName().contains(".docx")) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_word).into(img_file);
            }else if (file.getName().contains(".ppt") || file.getName().contains(".pptx")) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_ppt).into(img_file);
            }else if (file.getName().contains(".xls") || file.getName().contains(".xlxs")) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_excel).into(img_file);
            }else {
                Glide.with(getApplicationContext()).load(R.drawable.ic_simple).into(img_file);
            }

        }else if (file.getName().contains(Key.Video)) {
            Stype = Key.Video;
            Glide.with(getApplicationContext()).load(file.getPath()).into(img_file);
        }
    }

    private void setView() {
        file = new File(path);
        txt_name.setText(file.getName()+"");
    }
    private void getIntentData() {
        if (getIntent() != null) {
            path = getIntent().getStringExtra(Key.KeyIntent);
            setView();
            FilterStype();
        }
    }
    private void ClickBack() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

          //      new AdsUtils(Details_Vault.this).showAdsInterstitiaAd2(Details_Vault.this);

                finish();
            }
        });
    }

    private void initView() {
        img_back = findViewById(R.id.img_back);
        bt_open = findViewById(R.id.bt_open);
        bt_moveout = findViewById(R.id.bt_move_out);
        txt_name = findViewById(R.id.txt_name);
        img_file = findViewById(R.id.img_file);
    }

    @Override
    public void onBackPressed() {

  //      new AdsUtils(Details_Vault.this).showAdsInterstitiaAd2(Details_Vault.this);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HomeActivity.isback == true) {
            finish();
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
}