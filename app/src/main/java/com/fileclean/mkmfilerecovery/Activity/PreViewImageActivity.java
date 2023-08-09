package com.fileclean.mkmfilerecovery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;

public class PreViewImageActivity extends AppCompatActivity {

    private ImageView img_back;
    private TextView txt_name;
    private ImageView img_file;
    private String path;
    private Window window;

//    private FirebaseAnalytics mFirebaseAnalytics;
//
//    private AdsUtils adsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_view_image);

        initView();
        getDataIntent();
        ClickBack();
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


    private void ClickBack() {
        img_back.setOnClickListener(view -> {
            finish();
        });
    }

    private void getDataIntent() {
        if (getIntent() != null) {
            path = getIntent().getStringExtra(Key.KeyIntent);
            setView();
        }
    }

    private void setView() {
        File file = new File(path);
        txt_name.setText(file.getName());
        Glide.with(getApplicationContext()).load(path).error(R.drawable.ic_file_trash).into(img_file);
    }

    private void initView() {
        img_back = findViewById(R.id.img_back);
        txt_name = findViewById(R.id.txt_name);
        img_file = findViewById(R.id.img_file);
        txt_name.setSelected(true);
    }
}