package com.fileclean.mkmfilerecovery.Activity;

import static android.os.Build.VERSION.SDK_INT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fileclean.mkmfilerecovery.Adapter.Adapter_Vault;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHide.DataHide;
import com.fileclean.mkmfilerecovery.Database.DataHide.InfoFileHide;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewFileVaultActivity extends AppCompatActivity {

    private ImageView img_back;
    private String type;
    private TextView title;
    private LinearLayout layout_add;
    private List<InfoFileHide> listViewFile;
    private ListView listView;
    private Adapter_Vault adapter_vault;
    private ImageView img_filter;
    private List<InfoFileHide> ListVaultLocal;

    private List<InfoFileHide> list6Month;
    private List<InfoFileHide> list12Month;
    private List<InfoFileHide> list1Year;
    private List<InfoFileHide> list2Year;

    private boolean is6month = true;
    private boolean is12month = true;
    private boolean is1years = true;
    private boolean is2years = true;


    private File dir;

    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file_vault);

        initView();
        ClickBack();
        getDataIntent();
        ClickAdd();
        getDataHide();
        setStatusbar();
        FilterFile();
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
    private void FilterMode() {
        listViewFile.clear();
        if (is6month == true && list6Month.size() > 0) {

            for (int i = 0; i < list6Month.size(); i ++) {
                listViewFile.add(list6Month.get(i));
            }
        }
        if (is12month == true && list12Month.size() > 0) {
            for (int i = 0; i < list12Month.size(); i ++) {
                listViewFile.add(list12Month.get(i));
            }
        }
        if (is1years == true && list1Year.size() > 0) {
            for (int i = 0; i < list1Year.size(); i ++) {
                listViewFile.add(list1Year.get(i));
            }
        }
        if (is2years == true && list2Year.size() > 0) {
            for (int i = 0; i < list2Year.size(); i ++) {
                listViewFile.add(list2Year.get(i));
            }
        }
        Collections.reverse(listViewFile);
        adapter_vault.notifyDataSetChanged();
    }
    private void DialogFilter() {
        Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_filter);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        Button month6 = dialog.findViewById(R.id.month6);
        Button month12 = dialog.findViewById(R.id.month12);
        Button years1 = dialog.findViewById(R.id.years1);
        Button years2 = dialog.findViewById(R.id.years2);
        ImageView cancel = dialog.findViewById(R.id.cancel_button);
        TextView bt_ok = dialog.findViewById(R.id.bt_ok);

        cancel.setOnClickListener(view -> {
            dialog.cancel();
        });

        bt_ok.setOnClickListener(view -> {
            if (HomeActivity.listHideLocal.size() > 0 ) {
                FilterMode();
            }else {
                Toast.makeText(this, R.string.nofilesarehide, Toast.LENGTH_SHORT).show();
            }
            dialog.cancel();
        });

        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (is6month == true) {
                month6.setTextColor(getColor(R.color.blue017));
                month6.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
            }else {
                month6.setTextColor(getColor(R.color.grey01));
                month6.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
            }
            if (is12month == true) {
                month12.setTextColor(getColor(R.color.blue017));
                month12.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
            }else {
                month12.setTextColor(getColor(R.color.grey01));
                month12.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
            }
            if (is1years == true) {
                years1.setTextColor(getColor(R.color.blue017));
                years1.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
            }else {
                years1.setTextColor(getColor(R.color.grey01));
                years1.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
            }
            if (is2years == true) {
                years2.setTextColor(getColor(R.color.blue017));
                years2.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
            }else {
                years2.setTextColor(getColor(R.color.grey01));
                years2.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
            }
        }


        month6.setOnClickListener(view1 -> {
            if (SDK_INT >= Build.VERSION_CODES.M) {

                if (is6month == true) {
                    month6.setTextColor(getColor(R.color.grey01));
                    month6.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                    is6month = false;
                }else {
                    month6.setTextColor(getColor(R.color.blue017));
                    month6.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                    is6month = true;
                }
            }
        });

        month12.setOnClickListener(view1 -> {
            if (SDK_INT >= Build.VERSION_CODES.M) {
                if (is12month == true) {
                    month12.setTextColor(getColor(R.color.grey01));
                    month12.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                    is12month = false;
                }else {
                    month12.setTextColor(getColor(R.color.blue017));
                    month12.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                    is12month = true;
                }
            }
        });

        years1.setOnClickListener(view1 -> {
            if (SDK_INT >= Build.VERSION_CODES.M) {
                if (is1years == true) {
                    years1.setTextColor(getColor(R.color.grey01));
                    years1.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                    is1years = false;
                }else {
                    years1.setTextColor(getColor(R.color.blue017));
                    years1.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                    is1years = true;
                }
            }
        });

        years2.setOnClickListener(view1 -> {
            if (SDK_INT >= Build.VERSION_CODES.M) {
                if (is2years == true) {
                    years2.setTextColor(getColor(R.color.grey01));
                    years2.setBackgroundResource(R.drawable.shape_bgr_button_unselect);
                    is2years = false;
                }else {
                    years2.setTextColor(getColor(R.color.blue017));
                    years2.setBackgroundResource(R.drawable.shape_button_fillter_dialog);
                    is2years = true;
                }
            }
        });

        dialog.show();
    }

    private void FilterFile() {
        img_filter.setOnClickListener(view -> {
            DialogFilter();
        });
    }
    private void ClickAdd() {
        layout_add.setOnClickListener(view -> {

            if (type.equals(Key.Photo)) {
                Intent intent = new Intent(this, ViewFileGroupActivity.class);
                intent.putExtra(Key.KeyIntent, Key.KeyPhotoMoveIn);
                startActivity(intent);
            }else if (type.equals(Key.Video)) {
                Intent intent = new Intent(this, MoveInVaultActivity.class);
                intent.putExtra(Key.KeyIntent, Key.Video);
                startActivity(intent);
            }else if (type.equals(Key.Audio)) {
                Intent intent = new Intent(this, MoveInVaultActivity.class);
                intent.putExtra(Key.KeyIntent, Key.Audio);
                startActivity(intent);
            }else if (type.equals(Key.File)) {
                Intent intent = new Intent(this, MoveInVaultActivity.class);
                intent.putExtra(Key.KeyIntent, Key.File);
                startActivity(intent);
            }
        });
    }
    private void getDataIntent() {
        if (getIntent() != null) {
            type = getIntent().getStringExtra(Key.KeyIntent);
            switch (type) {
                case "photo":
                    title.setText(getString(R.string.photovault));
                    break;
                case "video":
                    title.setText(getString(R.string.videovault));
                    break;
                case "audio":
                    title.setText(getString(R.string.audiovault));
                    break;
                case "file":
                    title.setText(getString(R.string.filevault2));
                    break;
            }
            adapter_vault = new Adapter_Vault(this, listViewFile, type);
            listView.setAdapter(adapter_vault);

        }
    }

    private void ClickBack() {
        img_back.setOnClickListener(view -> {

        //    new AdsUtils(ViewFileVault.this).showAdsInterstitiaAd2(ViewFileVault.this);

            finish();
        });
    }
    private void initView() {
        img_back = findViewById(R.id.img_back);
        title = findViewById(R.id.title);
        layout_add = findViewById(R.id.layout_add);
        listView = findViewById(R.id.listview);
        img_filter = findViewById(R.id.img_filter);

        listViewFile = new ArrayList<>();
        ListVaultLocal = new ArrayList<>();
    }
    @Override
    public void onBackPressed() {

    //    new AdsUtils(ViewFileVault.this).showAdsInterstitiaAd2(ViewFileVault.this);

        finish();
    }

    @Override
    protected void onResume() {
        listViewFile.clear();
        getDataHide();
        adapter_vault.notifyDataSetChanged();
        HomeActivity.isback = false;
        super.onResume();
    }

    private void getDataHide() {

        try {
            HomeActivity.listHideLocal.clear();
        }catch (Exception e) {
            Log.d("wfewfew", "getDataHide: " + e);
        }
        HomeActivity.listHideLocal = DataHide.getInstance(getApplicationContext()).daoSql().getall();
        if (HomeActivity.listHideLocal.size() > 0) {
            for (int i = 0; i <HomeActivity.listHideLocal.size(); i++) {
                switch (type) {
                    case "photo":
                        if (HomeActivity.listHideLocal.get(i).getPath().contains(Key.KeyPhotoMoveIn)) {
                            listViewFile.add(HomeActivity.listHideLocal.get(i));
                            adapter_vault.notifyDataSetChanged();
                        }
                        break;
                    case "video":
                        if (HomeActivity.listHideLocal.get(i).getPath().contains(Key.KeyvideoMoveIn)) {
                            listViewFile.add(HomeActivity.listHideLocal.get(i));
                            adapter_vault.notifyDataSetChanged();
                        }
                        break;
                    case "audio":
                        if (HomeActivity.listHideLocal.get(i).getPath().contains(Key.KeyAudioMoveIn)) {
                            listViewFile.add(HomeActivity.listHideLocal.get(i));
                            adapter_vault.notifyDataSetChanged();
                        }
                        break;
                    case "file":
                        if (HomeActivity.listHideLocal.get(i).getPath().contains(Key.KeyDocumentMoveIn)) {
                            listViewFile.add(HomeActivity.listHideLocal.get(i));
                            adapter_vault.notifyDataSetChanged();
                        }
                        break;
                }
            }

            Collections.reverse(listViewFile);
            getList6Month();
            getList12Month();
            getList1Year();
            getList2Year();
        }
    }

    private void getList6Month(){
        list6Month = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < listViewFile.size(); i++) {
            File file = new File(listViewFile.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime > -180) {
                list6Month.add(listViewFile.get(i));
            }
        }
    }

    private void getList12Month(){
        list12Month = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < listViewFile.size(); i++) {
            File file = new File(listViewFile.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime < -180 && TimeList - thisTime > -365) {
                list12Month.add(listViewFile.get(i));
            }
        }
    }

    private void getList1Year(){
        list1Year = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < listViewFile.size(); i++) {
            File file = new File(listViewFile.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime < -365 && TimeList - thisTime > -730) {
                list1Year.add(listViewFile.get(i));
            }
        }
    }

    private void getList2Year(){
        list2Year = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < listViewFile.size(); i++) {
            File file = new File(listViewFile.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime < -730) {
                list2Year.add(listViewFile.get(i));
            }
        }
    }
}