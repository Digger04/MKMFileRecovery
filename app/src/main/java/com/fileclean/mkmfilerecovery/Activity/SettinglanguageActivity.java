package com.fileclean.mkmfilerecovery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.fileclean.mkmfilerecovery.Adapter.ILanguageAdapter;
import com.fileclean.mkmfilerecovery.Adapter.LanguageAdapter;
import com.fileclean.mkmfilerecovery.MainApp;
import com.fileclean.mkmfilerecovery.Model.LanguageCode;
import com.fileclean.mkmfilerecovery.R;
import com.fileclean.mkmfilerecovery.Utils.MyUtils;
import com.fileclean.mkmfilerecovery.Utils.SharedPreferencesUtils;
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity;
import com.zeugmasolutions.localehelper.LocaleHelper;
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegate;
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegateImpl;

import java.util.Locale;

public class SettinglanguageActivity extends LocaleAwareCompatActivity implements ILanguageAdapter {

    private final LocaleHelperActivityDelegate localeDelegate = new LocaleHelperActivityDelegateImpl();
    private ImageView back_language;
    private Window window;
    private TextView txt_language;

    private String lg;

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return localeDelegate.getAppCompatDelegate(super.getDelegate());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localeDelegate.onCreate(this);
        setContentView(R.layout.activity_settinglanguage);

        initView();
        clickBack();
        setStatusBar();
        setView();
    }

    private void setStatusBar() {
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    private void clickBack() {
        back_language.setOnClickListener(view -> {
            setLanguage();
            startActivity(new Intent(this, SettingActivity.class));
        });
    }

    private void setLanguage() {
        if (lg.equals("vi")) {
            onItemClick2("Tiếng việt");
        }else if (lg.equals("en")) {
            onItemClick2("English");
        }else if (lg.equals("de")) {
            onItemClick2("Deutsch");
        }else if (lg.equals("fr")) {
            onItemClick2("Français");
        }else if (lg.equals("id")) {
            onItemClick2("Bahasa Indo");
        }else if (lg.equals("it")) {
            onItemClick2("Italiano");
        }else if (lg.equals("ja")) {
            onItemClick2("日本");
        }else if (lg.equals("ko")) {
            onItemClick2("한국인");
        }else if (lg.equals("pt")) {
            onItemClick2("Português");
        }else if (lg.equals("tr")) {
            onItemClick2("Türkçe");
        }else if (lg.equals("th")) {
            onItemClick2("ไทย");
        }else if (lg.equals("ru")) {
            onItemClick2("Русский");
        }else if (lg.equals("hi")) {
            onItemClick2("हिन्दी");
        }else if (lg.equals("iw")) {
            onItemClick2("עִברִית");
        }
    }


    @Override
    public void onBackPressed() {
        setLanguage();
        startActivity(new Intent(this, SettingActivity.class));
    }
    private void initView() {
        lg = MainApp.mmkv.decodeString(SharedPreferencesUtils.KEY_LOCALE_LANGUAGE);
        back_language = findViewById(R.id.back_language);
        txt_language = findViewById(R.id.txt_language);
    }
    private void setView() {

        try {

            RecyclerView recyclerView = findViewById(R.id.rv_language);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            LanguageAdapter adapter = new LanguageAdapter(this, LanguageCode.languages, this);
            recyclerView.setAdapter(adapter);

            if (lg.equals("vi")) {
                txt_language.setText("Cài đặt");
            }else if (lg.equals("en")) {
                txt_language.setText("Settings");
            }else if (lg.equals("de")) {
                txt_language.setText("Einstellung");
            }else if (lg.equals("fr")) {
                txt_language.setText("Paramètre");
            }else if (lg.equals("id")) {
                txt_language.setText("Pengaturan");
            }else if (lg.equals("it")) {
                txt_language.setText("Impostazioni");
            }else if (lg.equals("ja")) {
                txt_language.setText("設定");
            }else if (lg.equals("ko")) {
                txt_language.setText("환경");
            }else if (lg.equals("pt")) {
                txt_language.setText("Contexto");
            }else if (lg.equals("tr")) {
                txt_language.setText("Ayar");
            }else if (lg.equals("th")) {
                txt_language.setText("การตั้งค่า");
            }else if (lg.equals("ru")) {
                txt_language.setText("Параметр");
            }else if (lg.equals("hi")) {
                txt_language.setText("स्थापना");
            }else if (lg.equals("iw")) {
                txt_language.setText("הגדרה");
            }
        }catch (Exception e) {
            Log.d("erefw", "ddd: " + e);
        }


    }


    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemClick2(String value) {
        Locale mCurrentLocale = MyUtils.convertLanguage(value);
        localeDelegate.setLocale(this, mCurrentLocale);
        MainApp.mmkv.encode(SharedPreferencesUtils.KEY_LOCALE_LANGUAGE, LanguageCode.getLanguageCode(value));
        startActivity(new Intent(this, SettingActivity.class));
    }

    @NonNull
    @Override
    public Context createConfigurationContext(@NonNull Configuration overrideConfiguration) {
        Context context = super.createConfigurationContext(overrideConfiguration);
        return LocaleHelper.INSTANCE.onAttach(context);
    }

    @NonNull
    @Override
    public Context getApplicationContext() {
        return localeDelegate.getApplicationContext(super.getApplicationContext());
    }
}