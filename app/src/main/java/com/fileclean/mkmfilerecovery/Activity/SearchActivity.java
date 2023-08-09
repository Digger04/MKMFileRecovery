package com.fileclean.mkmfilerecovery.Activity;

import static android.os.Build.VERSION.SDK_INT;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fileclean.mkmfilerecovery.Adapter.Adapter_history_Search;
import com.fileclean.mkmfilerecovery.Adapter.Adapter_list_search;
import com.fileclean.mkmfilerecovery.Database.DataSearch.DataSearch;
import com.fileclean.mkmfilerecovery.Database.DataSearch.InfoSearch;
import com.fileclean.mkmfilerecovery.Model.InfoFile;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.BuildConfig;

public class SearchActivity extends AppCompatActivity {

    private ImageView img_back;
    private ImageView img_search;
    private EditText edt_Search;
    private ListView listView;
    private Adapter_history_Search adapter_history_search;
    public static List<InfoSearch> list_history_search;
    private String data;
    private TextView txt_clearAll;
    private LinearLayout layout_des;
    private RelativeLayout layout_clear;
    private RecyclerView listsearch;
    private CardView card_scanning;
    private TextView txt_scanning;
    private File dir;
    private ImageView img_pause;
    private ProgressBar progressbar;
    private Window window;

    private LinearLayout layout_null;

    private boolean ispermission = false;


    private List<InfoFile> ListALl;

    private Adapter_list_search adapter_list_search;

    private boolean StopThread = false;

//    private FirebaseAnalytics mFirebaseAnalytics;
//
//    private AdsUtils adsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initview();
        ClickBack();
        Search();
        SelectList();
        ClearHistory();
        PauseThread();
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
    private void PauseThread() {
        img_pause.setOnClickListener(view -> {
            StopThread = true;
        });
    }
    private void ClearHistory() {

        txt_clearAll.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.title_delete_allhistory_search));
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for (int j = 0; j < list_history_search.size(); j++) {
                        DataSearch.getInstance(getApplicationContext()).daoSql().delete(list_history_search.get(j));
                    }
                    list_history_search.clear();
                    adapter_history_search.notifyDataSetChanged();
                }
            });

            builder.show();
        });
    }

    private void SelectList() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                edt_Search.setText(list_history_search.get(i).getData());
                getFileSearch();

            }
        });
    }

    public class getFile extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            progressbar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            File[] FileList = dir.listFiles();

            if (FileList != null) {

                for (File value : FileList) {
                    if (value.isDirectory()) {
                        dir = value;
                        new getFile().doInBackground();
                    } else {

                        if (StopThread == true) {
                            break;
                        }

                        File file = new File(value.getPath());
                        int file_size = parseInt(String.valueOf(file.length() / 1024));
                        // get day
                        Date lastModDate = new Date(file.lastModified());

                        if (value.getName().contains(data)) {
                            ListALl.add(new InfoFile(value.getPath(),
                                    value.getName(), lastModDate+"", file_size+""));
                        }

                        publishProgress("" + ListALl.size());

                    }
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            txt_scanning.setText(getString(R.string.scanning) + " [ " + ListALl.size() + " ]");
            adapter_list_search.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            card_scanning.setVisibility(View.GONE);
            progressbar.setVisibility(View.GONE);
            if (ListALl.size() == 0) {
                layout_null.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(s);
        }
    }

    private void getFileSearch() {
        if (edt_Search.getText().toString().trim().length() > 0) {
            data = edt_Search.getText().toString().trim();
            DataSearch.getInstance(getApplicationContext()).daoSql().insert(new InfoSearch(data));
            list_history_search.add(new InfoSearch(data));
            adapter_history_search.notifyDataSetChanged();
            layout_des.setVisibility(View.GONE);
            layout_clear.setVisibility(View.GONE);

            listView.setVisibility(View.GONE);
            listsearch.setVisibility(View.VISIBLE);
            card_scanning.setVisibility(View.VISIBLE);

            ListALl.clear();

            dir = Environment.getExternalStorageDirectory();

            adapter_list_search = new Adapter_list_search(getApplicationContext(), ListALl);
            LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(),1);
            listsearch.setLayoutManager(linearLayoutManager);
            listsearch.setHasFixedSize(true);
            listsearch.setAdapter(adapter_list_search);

            new getFile().execute();
        }
    }

    private void Search() {
        img_search.setOnClickListener(view -> {

     //       new AdsUtils(SearchFile.this).showAdsInterstitiaAd2(SearchFile.this);

            getFileSearch();
            HideKeyBoard();
        });
    }

    private void HideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void ClickBack() {
        img_back.setOnClickListener(view -> {

        //    new AdsUtils(SearchFile.this).showAdsInterstitiaAd2(SearchFile.this);
            finish();
        });
    }

    private void initview() {
        txt_clearAll = findViewById(R.id.clearall);
        img_back = findViewById(R.id.img_back);
        img_search = findViewById(R.id.img_search);
        edt_Search = findViewById(R.id.edt_Search);
        listView = findViewById(R.id.listview);
        layout_des = findViewById(R.id.layout_des);
        layout_clear = findViewById(R.id.layout_clear);
        card_scanning = findViewById(R.id.card_scanning);
        listsearch = findViewById(R.id.list_search);
        txt_scanning = findViewById(R.id.txt_scanning);
        img_pause = findViewById(R.id.img_pause);
        layout_null = findViewById(R.id.layout_null);

        progressbar = findViewById(R.id.progressbar);

        list_history_search = new ArrayList<>();
        list_history_search = DataSearch.getInstance(getApplicationContext()).daoSql().getall();
        Collections.reverse(list_history_search);
        adapter_history_search = new Adapter_history_Search(this, list_history_search);
        listView.setAdapter(adapter_history_search);
        ListALl = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {

        //   new AdsUtils(SearchFile.this).showAdsInterstitiaAd2(SearchFile.this);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPermission()) {
            verifyStoragePermissions(this);
            requestPermission();
        }
    }

    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.MANAGE_EXTERNAL_STORAGE") == 0;
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
            if (ContextCompat.checkSelfPermission(SearchActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(SearchActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(SearchActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            222);
                }
            }
        }
    }
}