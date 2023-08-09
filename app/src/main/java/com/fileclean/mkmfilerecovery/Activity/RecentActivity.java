package com.fileclean.mkmfilerecovery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fileclean.mkmfilerecovery.Adapter.Adapter_recent;
import com.fileclean.mkmfilerecovery.Database.DataRecent.DataRecent;
import com.fileclean.mkmfilerecovery.Database.DataRecent.InforRecent;
import com.fileclean.mkmfilerecovery.R;

import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class RecentActivity extends AppCompatActivity {

    private ImageView img_back;
    private TextView titlerecent;
    private RecyclerView recyclerView;
    private Window window;
    private SpotsDialog progressDialog;
    private Adapter_recent adapter_recent;
    private List<InforRecent> list;
    private LinearLayout layout_null;

//    private FirebaseAnalytics mFirebaseAnalytics;
//
//    private AdsUtils adsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        initView();
        ClickBack();
        setStatusbar();
        getData();
    }

    public class getFile extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {
                list = DataRecent.getInstance(getApplicationContext()).daoSql().getall();

            }catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {

            Collections.reverse(list);
            adapter_recent = new Adapter_recent(getApplicationContext(), list);
            recyclerView.setAdapter(adapter_recent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (list.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }else {
                        layout_null.setVisibility(View.VISIBLE);
                    }
                    progressDialog.cancel();
                }
            },2000);

            super.onPostExecute(s);
        }
    }

    private void getData() {
        new getFile().execute();
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

      //      new AdsUtils(RecentFile.this).showAdsInterstitiaAd2(RecentFile.this);

            startActivity(new Intent(this, HomeActivity.class));
        });
    }
    private void initView() {
        img_back = findViewById(R.id.img_back);
        titlerecent = findViewById(R.id.titlerecent);
        recyclerView = findViewById(R.id.recyclerview);
        layout_null = findViewById(R.id.layout_null);

        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        progressDialog = new SpotsDialog(this, R.style.Custom);
        progressDialog.show();
    }

    @Override
    public void onBackPressed() {

     //   new AdsUtils(RecentFile.this).showAdsInterstitiaAd2(RecentFile.this);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}