package com.fileclean.mkmfilerecovery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fileclean.mkmfilerecovery.R;

public class SettingActivity extends AppCompatActivity {

    private ImageView img_back;

    private LinearLayout share, rate;
    private Window window;
    private LinearLayout language;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initview();
        ClickBack();
        setStatusbar();
        setLanguage();
        ClickShare();
        ClickRate();

    }

    private void ClickRate() {
        rate.setOnClickListener(v -> {
            new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())
            );
        });
    }

    private void ClickShare() {
        share.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=filerecover.restorefile.datarecovery"
            );
            sendIntent.setType("text/plain");

            Intent shareIntent =
                    Intent.createChooser(sendIntent, getString(R.string.share));
            startActivity(shareIntent);
        });
    }

    private void setLanguage() {
        language.setOnClickListener(view -> {
            startActivity(new Intent(this, SettinglanguageActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
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
            startActivity(new Intent(this, HomeActivity.class));
        });
    }
    private void initview() {
        img_back = findViewById(R.id.img_back);
        language = findViewById(R.id.language);
        share = findViewById(R.id.share);
        rate = findViewById(R.id.rateUs);
    }
}