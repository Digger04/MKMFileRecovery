package com.fileclean.mkmfilerecovery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.R;

public class PendingMoveActivity extends AppCompatActivity {

    private ImageView img_gif;
    private Button bt_ok;
    private String type;
    private RelativeLayout layout_full;

    private Window window;
    private String Type_screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_move);

        initView();
        getIntentData();
        Finish();
        setStatusbar("white");

    }

    private void getIntentData() {

        if (getIntent() != null) {
            type = getIntent().getStringExtra(Key.KeyIntent);
            Type_screen = getIntent().getStringExtra(Key.Key_Type_Screen);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide.with(getApplicationContext()).load(R.drawable.gif_tick).into(img_gif);
                    bt_ok.setVisibility(View.VISIBLE);
                    setStatusbar("green00");
                    layout_full.setBackgroundColor(getResources().getColor(R.color.green00));
                }
            },3000);

        }
    }
    private void initView() {
        img_gif = findViewById(R.id.img_gif);
        bt_ok = findViewById(R.id.bt_ok);
        layout_full = findViewById(R.id.layout_full);
        Glide.with(getApplicationContext()).load(R.drawable.gif_recover).into(img_gif);
    }
    private void setStatusbar(String color) {
        window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (color.equals("white")) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        }else {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.green00));
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void Finish() {
        bt_ok.setOnClickListener(view -> {

            if (Type_screen.equals(Key.Recent)) {
                startActivity(new Intent(this, RecentActivity.class));
                finish();
            }else {
                HomeActivity.isback = true;
                finish();
            }
     /*       if (Type_screen.equals(Key.FileVault)) {
                Intent intent = new Intent(PendingMove.this, ViewFileVault.class);
                intent.putExtra(Key.KeyIntent, type);
                startActivity(intent);
            }else if (Type_screen.equals(Key.Viewfile)) {
                Intent intent = new Intent(PendingMove.this, ViewFile.class);
                intent.putExtra(Key.KeyIntent, type);
                startActivity(intent);
            }else if (Type_screen.equals(Key.Search)) {
                startActivity(new Intent(this, MainActivity.class));
            } */
        });
    }
}