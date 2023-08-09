package com.fileclean.mkmfilerecovery.Activity;

import static android.os.Build.VERSION.SDK_INT;
import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;
import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fileclean.mkmfilerecovery.Adapter.Adapter_Move_In;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHide.DataHide;
import com.fileclean.mkmfilerecovery.Database.DataHide.InfoFileHide;
import com.fileclean.mkmfilerecovery.Model.InfoFile;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MoveInVaultActivity extends AppCompatActivity {

    private ImageView img_back;
    private String type;
    private RecyclerView recyclerview;
    private List<InfoFile> list;
    private File dir;
    private LinearLayout layout_nullfile;

    private TextView txt_stop;
    public static Button bt_move;
    private ImageView img_filter;

    private TextView title;

    private int TIME_OUT = 10;

    private Adapter_Move_In adapter_moveIn;

    private boolean ispause = false;
    private boolean isBack = false;

    public static List<InfoFileHide> listHide;

    private String PMove;

    private Window window;
    private ProgressBar progressbar;

    private ImageView img_sort;

    private boolean isold = false;
    private boolean isnew = true;


    private boolean is6month = true;
    private boolean is12month = true;
    private boolean is1years = true;
    private boolean is2years = true;

    private List<InfoFile> list6Month;
    private List<InfoFile> list12Month;
    private List<InfoFile> list1Year;
    private List<InfoFile> list2Year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_in_vault);

        initView();
        ClickBack();
        getIntentData();
        PauseScan();
        ClickMoveFile();
        setStatusbar();
        SortList();
        ClickFilter();
    }

    private void DialogSort() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_sort);
        dialog.setCanceledOnTouchOutside(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        CheckBox checkBox_new = dialog.findViewById(R.id.checkbox_ascending);
        CheckBox checkBox_old = dialog.findViewById(R.id.checkbox_decrease);
        Button bt_apply = dialog.findViewById(R.id.bt_apply);
        ImageView img_cancel = dialog.findViewById(R.id.img_cancel);

        img_cancel.setOnClickListener(view -> {
            dialog.cancel();
        });

        if (isnew == true) {
            checkBox_new.setChecked(true);
        }else if (isold == true) {
            checkBox_old.setChecked(true);
        }

        checkBox_new.setOnClickListener(view -> {
            checkBox_new.setChecked(true);
            checkBox_old.setChecked(false);

            isnew = true;
            isold = false;
        });

        checkBox_old.setOnClickListener(view -> {
            checkBox_old.setChecked(true);
            checkBox_new.setChecked(false);

            isold = true;
            isnew = false;
        });

        bt_apply.setOnClickListener(view -> {

            SortFile();

            dialog.cancel();
        });

        dialog.show();
    }
    private void SortList() {
        img_sort.setOnClickListener(view -> {
            DialogSort();
        });
    }
    private void FilterMode() {
        list.clear();
        if (is6month == true) {
            for (int i = 0; i < list6Month.size(); i ++) {
                list.add(list6Month.get(i));
            }
        }
        if (is12month == true) {
            for (int i = 0; i < list12Month.size(); i ++) {
                list.add(list12Month.get(i));
            }
        }
        if (is1years == true) {
            for (int i = 0; i < list1Year.size(); i ++) {
                list.add(list1Year.get(i));
            }
        }
        if (is2years == true) {
            for (int i = 0; i < list2Year.size(); i ++) {
                list.add(list2Year.get(i));
            }
        }
        String values = " [ " + list.size() + " ]";
        title.setText(getString(R.string.FILE) + values);
        adapter_moveIn.notifyDataSetChanged();
    }

    private void ClickFilter() {
        img_filter.setOnClickListener(view -> {

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

            ImageView cancel = dialog.findViewById(R.id.cancel_button);
            TextView bt_ok = dialog.findViewById(R.id.bt_ok);

            cancel.setOnClickListener(view1 -> {
                dialog.cancel();
            });

            bt_ok.setOnClickListener(view1 -> {
                FilterMode();
                dialog.cancel();
            });

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

        });

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
    private void ClickMoveFile() {
        bt_move.setOnClickListener(view -> {
            if (listHide.size() > 0) {
                for (int i = 0; i < listHide.size(); i++) {
                    File file = new File(listHide.get(i).getPath());

                    Random random = new Random();
                    final int min = 10000;
                    final int max = 99999;
                    int N_EndName = random.nextInt((max - min) + 1) + min;

                    // creat folder
                    String folder_vault = Key.FolderVaultSave;
                    File f = new File(Environment.getExternalStorageDirectory(), folder_vault);
                    if (!f.exists()) {
                        f.mkdirs();
                        MoveFile(file, N_EndName, i, listHide.get(i).getType());
                    }else {
                        MoveFile(file, N_EndName, i,  listHide.get(i).getType());
                    }
                    //     file.delete();
                }

                Intent intent = new Intent(this, PendingMoveActivity.class);
                intent.putExtra(Key.KeyIntent, type);
                intent.putExtra(Key.Key_Type_Screen, Key.FileVault);
                startActivity(intent);

                listHide.clear();
            }
        });
    }

    private void MoveFile(File file, int n_endName, int i, String type) {

     //   new AdsUtils(MoveInVault.this).showAdsInterstitiaAd(MoveInVault.this);

        if (type.equals(Key.File)) {

            PMove = Key.FolderVault + "KeyDocumentMoveIn" + file.getName() + Key.EndNameVault + n_endName;

        }else if (type.equals(Key.Audio)) {
            PMove = Key.FolderVault + "KeyAudioMoveIn" + file.getName() + Key.EndNameVault + n_endName;
        }else if (type.equals(Key.Video)) {
            PMove = Key.FolderVault + "KeyvideoMoveIn" + file.getName() + Key.EndNameVault + n_endName;
        }

        File FileMove = new File(PMove);
        try {

            switch (type) {
                case "photo":
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

                    DataHide.getInstance(getApplicationContext()).daoSql().insert(
                            new InfoFileHide(PMove, listHide.get(i).getName(),
                                    listHide.get(i).getDate(), listHide.get(i).getSize(),
                                    listHide.get(i).getType())
                    );
                    ExifInterface ei = new ExifInterface(file.getPath());
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch(orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmap;
                    }

                    FileMove.createNewFile();
                    FileOutputStream outputStream = new FileOutputStream(FileMove);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

                    break;

                case "video":
                    MoveVideo_Audio(PMove, FileMove, i, file,Key.KeyvideoMoveIn);
                    break;

                case "audio":
                    MoveVideo_Audio(PMove, FileMove, i, file, Key.Audio);

                    break;
                case "file":
                    MoveVideo_Audio(PMove, FileMove, i, file, Key.File);

                    break;

            }
            file.delete();
            refreshGallery(this, file);

        } catch (IOException e) {
            Log.d(Key.KeyLog, "error: " + e);
        }
    }

    public void refreshGallery(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(mediaScanIntent);
    }

    private void MoveVideo_Audio(String PMove, File FileMove, int i, File file, String type) {

        try {
            if (file.exists()) {

                DataHide.getInstance(getApplicationContext()).daoSql().insert(
                        new InfoFileHide(PMove, listHide.get(i).getName(),
                                listHide.get(i).getDate(), listHide.get(i).getSize(),
                                listHide.get(i).getType()));

                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream1 = new FileOutputStream(FileMove);

                byte[] buf = new byte[1024];
                int len;

                while ((len = inputStream.read(buf)) > 0) {
                    outputStream1.write(buf, 0, len);
                }

                outputStream1.flush();
                inputStream.close();
                outputStream1.close();

            } else {
                Toast.makeText(this, R.string.file_has_faild, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {

        }
    }


    private void PauseScan() {
        txt_stop.setOnClickListener(view -> {
            ispause = true;
        });
    }

    private void getIntentData() {
        if (getIntent() != null) {
            type = getIntent().getStringExtra(Key.KeyIntent);
            if (type.equals(Key.Photo) || type.equals(Key.Video)) {
                LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(),3);
                recyclerview.setLayoutManager(linearLayoutManager);
            }else if (type.equals(Key.Audio) || type.equals(Key.File)) {
                LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(),1);
                recyclerview.setLayoutManager(linearLayoutManager);
            }

            adapter_moveIn = new Adapter_Move_In(type, getApplicationContext(),list);
            recyclerview.setAdapter(adapter_moveIn);
            getDataLocal();
        }
    }
    private void ClickBack() {
        img_back.setOnClickListener(view -> {

      //     new AdsUtils(MoveInVault.this).showAdsInterstitiaAd2(MoveInVault.this);

            finish();
            listHide.clear();
            ispause = true;
            isBack = true;
        });
    }

    private void getDataLocal() {
        Log.d("ewwefwe", "type: " + type);
        dir = Environment.getExternalStorageDirectory();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new getFile().execute();
            }
        },TIME_OUT);

    }

    public class getFile extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {

            File[] FileList = dir.listFiles();

            if (FileList != null) {

                for (File value : FileList) {
                    if (value.isDirectory()) {
                        dir = value;
                        new getFile().doInBackground();
                    } else {

                        if (ispause == true) {
                            break;
                        }

                        File file = new File(value.getPath());
                        int file_size = parseInt(String.valueOf(file.length() / 1024));
                        // get day
                        long time = (long) System.currentTimeMillis();

                        //          SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                        //         String dateString = formatter.format(new Date(time));

                        //           Log.d("fwefwewe", " date:  " + dateString);

                        Date lastModDate = new Date(file.lastModified());

                        if (file_size > 0 ) {
                            switch (type) {
                                case "video":
                                    getFileVideos(value, lastModDate, file_size);
                                    publishProgress("" + list.size());
                                    break;
                                case "audio":
                                    getFileAudios(value, lastModDate, file_size);
                                    publishProgress("" + list.size());
                                    break;
                                case "file":
                                    getFileDocument(value, lastModDate, file_size);
                                    publishProgress("" + list.size());
                                    break;
                            }
                        }
                    }
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            title.setText(getString(R.string.scanning) + " [ " + list.size() + " ]");
            adapter_moveIn.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            title.setText(getString(R.string.FILE) + " [ " + list.size() + " ]");
            if (!isBack) {
                DialogFinish();
            }
            txt_stop.setVisibility(View.GONE);
            img_sort.setVisibility(View.VISIBLE);
            img_filter.setVisibility(View.VISIBLE);
            progressbar.setVisibility(View.GONE);
            SortFile();
            if (list.size() == 0) {
                recyclerview.setVisibility(View.GONE);
                layout_nullfile.setVisibility(View.VISIBLE);
            }
            getList6Month();
            getList12Month();
            getList1Year();
            getList2Year();
            super.onPostExecute(s);
        }
    }

    private void getFileDocument(File value, Date lastModDate, int file_size) {
        if (value.getName().endsWith(".txt") || value.getName().endsWith("pdf")
                || value.getName().endsWith(".doc") || value.getName().endsWith(".docx")
                || value.getName().endsWith(".ppt") || value.getName().endsWith(".pptx")
                || value.getName().endsWith(".xls") || value.getName().endsWith(".xlxs")) {

            try {
                Thread.sleep(TIME_OUT);
                list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));

            } catch (Exception e) {

            }
        }
    }
    private void getFileAudios(File value, Date lastModDate, int file_size) {
        if (value.getName().endsWith(".mp3")) {

            if (value.getPath().contains("NCT") ||
                    value.getPath().contains("Download") ||
                    value.getPath().contains("Music")) {

                try {
                    Thread.sleep(TIME_OUT);
                    list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));

                } catch (Exception e) {

                }

            }
        }
    }
    private void getList6Month(){
        list6Month = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime > -180) {
                list6Month.add(list.get(i));
            }
        }
    }

    private void getList12Month(){
        list12Month = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime < -180 && TimeList - thisTime > -365) {
                list12Month.add(list.get(i));
            }
        }
    }

    private void getList1Year(){
        list1Year = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime < -365 && TimeList - thisTime > -730) {
                list1Year.add(list.get(i));
            }
        }
    }

    private void getList2Year(){
        list2Year = new ArrayList<>();
        long thisTime = (long) System.currentTimeMillis()/1000/ 3600 /24;
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i).getPath());
            long TimeList = file.lastModified()/1000 / 3600 /24;
            if (TimeList - thisTime < -730) {
                list2Year.add(list.get(i));
            }
        }
    }
    private void SortFile() {
        if (isold == false) {
            Collections.sort(list, (o2, o1) -> o1.getDate().compareTo(o2.getDate()));
            adapter_moveIn.notifyDataSetChanged();
        }else if (isold == true) {
            Collections.sort(list, (o2, o1) -> o1.getDate().compareTo(o2.getDate()));
            Collections.reverse(list);
            adapter_moveIn.notifyDataSetChanged();
        }
    }
    private void DialogFinish() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_load_finish);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }

        TextView txt_number = dialog.findViewById(R.id.txt_number);
        TextView txt_memory = dialog.findViewById(R.id.txt_memory);
        Button bt_ok = dialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(view -> {
            dialog.cancel();
        });

        txt_number.setText(getString(R.string.file1) + " " + list.size());
        float kb = 0;

        if (list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {

                kb = kb + Float.parseFloat(list.get(i).getSize());

            }
        }

        float mb = kb / 1024;
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        txt_memory.setText(getString(R.string.memory) + " " + decimalFormat.format(mb) + " Mb");

        dialog.show();
    }

    private void getFileVideos(File value, Date lastModDate, int file_size) {
        if (value.getName().endsWith(".mp4")) {

            try {
                Thread.sleep(TIME_OUT);
                list.add(new InfoFile(value.getPath(), value.getName(), lastModDate+"", file_size+""));

            } catch (Exception e) {

            }

        }
    }
    private void initView() {
        img_back = findViewById(R.id.img_back);
        recyclerview = findViewById(R.id.recyclerview);
        title = findViewById(R.id.title);
        txt_stop = findViewById(R.id.txt_stop);
        layout_nullfile = findViewById(R.id.layout_nullfile);
        progressbar = findViewById(R.id.progressbar);

        list = new ArrayList<>();
        recyclerview.setHasFixedSize(true);
        bt_move = findViewById(R.id.bt_move);
        bt_move.setSelected(true);
        img_sort = findViewById(R.id.img_sort);
        img_filter = findViewById(R.id.img_filter);

        title.setSelected(true);
        txt_stop.setSelected(true);

        listHide = new ArrayList<>();

        list6Month = new ArrayList<>();
        list12Month = new ArrayList<>();
        list1Year = new ArrayList<>();
        list2Year = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HomeActivity.isback == true) {
            finish();
        }
        isBack = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    //    new AdsUtils(MoveInVault.this).showAdsInterstitiaAd2(MoveInVault.this);

        listHide.clear();
        ispause = true;
        isBack = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        listHide.clear();
    }
}