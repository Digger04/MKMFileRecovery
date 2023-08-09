package com.fileclean.mkmfilerecovery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;

public class PreViewVideoActivity extends AppCompatActivity {

    private ImageView img_back;
    private TextView txt_name;
    private VideoView videoView;
    private String path;
    private Window window;

//    private FirebaseAnalytics mFirebaseAnalytics;
//
//    private AdsUtils adsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_view_video);

        initView();
        getDataIntent();
        ClickBack();
        setStatusbar();
    }

    private void ClickBack() {
        img_back.setOnClickListener(view -> {
            finish();
        });
    }


    private void setView() {
        File file = new File(path);
        txt_name.setText(file.getName());
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setVideoPath(path);

        videoView.start();
    }

    private void getDataIntent() {
        if (getIntent() != null) {

            path = getIntent().getStringExtra(Key.KeyIntent);
            setView();
        }
    }
    private void initView() {
        img_back = findViewById(R.id.img_back);
        txt_name = findViewById(R.id.txt_name);
        txt_name.setSelected(true);
        videoView = findViewById(R.id.videoview);
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