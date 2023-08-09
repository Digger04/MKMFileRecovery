package com.fileclean.mkmfilerecovery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fileclean.mkmfilerecovery.Adapter.Adapter_history;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHistory.DataHistory;
import com.fileclean.mkmfilerecovery.Database.DataHistory.InfofileHistory;
import com.fileclean.mkmfilerecovery.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private String type;
    private ImageView img_back;
    private ListView listView;
    private List<InfofileHistory> list;
    private List<InfofileHistory> listFilter;
    private Adapter_history adapter_history;
    private Window window;
    private LinearLayout layout_historynull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initview();
        getDataIntent();
        ClickBack();
        getData();
        setStatusbar();

    }

    private void setStatusbar() {
        window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    private void getData() {
        list = DataHistory.getInstance(getApplicationContext()).daoSql().getall();
        Collections.reverse(list);
        if (list.size() <= 0) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getType().equals(type)) {
                listFilter.add(list.get(i));
            }
        }

        if (listFilter.size() == 0) {
            layout_historynull.setVisibility(View.VISIBLE);
        }

        adapter_history = new Adapter_history(getApplicationContext(), listFilter, type);
        listView.setAdapter(adapter_history);
    }
    private void ClickBack() {
        img_back.setOnClickListener(view -> {
            finish();
        });
    }
    private void getDataIntent() {

        if (getIntent() != null) {

            type = getIntent().getStringExtra(Key.KeyIntent);

        }
    }

    private void initview() {
        img_back = findViewById(R.id.img_back);
        listView = findViewById(R.id.listview);
        layout_historynull = findViewById(R.id.layout_historynull);

        list = new ArrayList<>();
        listFilter = new ArrayList<>();
    }
}