package com.fileclean.mkmfilerecovery.Adapter;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fileclean.mkmfilerecovery.Activity.PendingMoveActivity;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHistory.DataHistory;
import com.fileclean.mkmfilerecovery.Database.DataHistory.InfofileHistory;
import com.fileclean.mkmfilerecovery.Model.InfoFile;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Adapter_list_search extends RecyclerView.Adapter<Adapter_list_search.Viewholder> {

    private Context context;
    private List<InfoFile> list;
    private String Pmove;

    public Adapter_list_search(Context context, List<InfoFile> list) {
        this.context = context;
        this.list = list;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<InfoFile> getList() {
        return list;
    }

    public void setList(List<InfoFile> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.row_file_search, null);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        InfoFile infoFile = list.get(position);

        if (infoFile.getName().contains(".img") || infoFile.getName().contains(".png")
                || infoFile.getName().contains(".jpeg")) {
            Glide.with(context).load(infoFile.getPath()).into(holder.img_file);
        }else if (infoFile.getName().contains(".xls") || infoFile.getName().contains(".xlxs")){
            Glide.with(context).load(R.drawable.ic_excel).into(holder.img_file);
        }else if (infoFile.getName().contains(".pdf")) {
            Glide.with(context).load(R.drawable.ic_pdf).into(holder.img_file);
        }else if (infoFile.getName().contains(".txt")) {
            Glide.with(context).load(R.drawable.ic_filetxt).into(holder.img_file);
        }else if (infoFile.getName().contains(".doc") || infoFile.getName().contains(".docx")) {
            Glide.with(context).load(R.drawable.ic_word).into(holder.img_file);
        }else if (infoFile.getName().contains(".ppt") || infoFile.getName().contains(".pptx")) {
            Glide.with(context).load(R.drawable.ic_ppt).into(holder.img_file);
        }

        holder.txt_name.setText(infoFile.getName()+"");
        holder.txt_date.setText(infoFile.getDate()+"");
        holder.txt_size.setText(infoFile.getSize()+" Kb");
        holder.txt_path.setText(infoFile.getPath());

        holder.bt_recover.setOnClickListener(view -> {

            if (infoFile.getName().contains(".img") || infoFile.getName().contains(".jpg")
                    || infoFile.getName().contains(".png") || infoFile.getName().contains(".jpeg")) {

                MovePhoto(infoFile.getPath());

            }else if (infoFile.getName().contains(".mp3")) {

                MoveVideo_audio(Key.Audio, infoFile.getPath());

            }if (infoFile.getPath().contains(".mp4")) {

                MoveVideo_audio(Key.Video, infoFile.getPath());

            }else {
                MoveFile(infoFile.getPath());
            }

            Intent intent = new Intent(context, PendingMoveActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Key.Key_Type_Screen, Key.Search);
            context.startActivity(intent);
        });

    }

    private void MoveFile(String path) {

        List<InfofileHistory> list_history = new ArrayList<>();

        File file = new File(path);

        String namefile = file.getName();

        Pmove = Key.Path_Recover_file + namefile;
        File Filerecover = new File(Pmove);
        try {
            if (file.exists()) {

                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream1 = new FileOutputStream(Filerecover);

                byte[] buf = new byte[1024];
                int len;

                while ((len = inputStream.read(buf)) > 0) {
                    outputStream1.write(buf, 0, len);
                }

                outputStream1.flush();
                inputStream.close();
                outputStream1.close();

                refreshGallery(context, Filerecover);

                file.delete();

                int file_size = parseInt(String.valueOf(file.length() / 1024));
                // get day
                Date lastModDate = new Date(file.lastModified());

                list_history.add(new InfofileHistory(path, file.getName(),
                        lastModDate+"", file_size+"", Key.Document));
                DataHistory.getInstance(context).daoSql().insert(list_history.get(0));
            }
        }catch (Exception e) {

        }
    }

    private void MoveVideo_audio(String type, String path) {
        List<InfofileHistory> list_history = new ArrayList<>();

        File file = new File(path);
        switch (type) {
            case "video":
                Pmove = Key.Path_Recover_Videos + file.getName() + ".mp4";
                break;
            case "audio":
                Pmove = Key.Path_Recover_Audio + file.getName() + ".mp3";
                break;

        }
        File Filerecover = new File(Pmove);
        try {

            if (file.exists()) {

                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream1 = new FileOutputStream(Filerecover);

                byte[] buf = new byte[1024];
                int len;

                while ((len = inputStream.read(buf)) > 0) {
                    outputStream1.write(buf, 0, len);
                }

                outputStream1.flush();
                inputStream.close();
                outputStream1.close();

                refreshGallery(context, Filerecover);

                file.delete();

                int file_size = parseInt(String.valueOf(file.length() / 1024));
                // get day
                Date lastModDate = new Date(file.lastModified());

                list_history.add(new InfofileHistory(path, file.getName(),
                        lastModDate+"", file_size+"", type));
                DataHistory.getInstance(context).daoSql().insert(list_history.get(0));
            }

        }catch (Exception e) {

        }
    }

    private void MovePhoto (String path) {
        List<InfofileHistory> list_history = new ArrayList<>();
        File file = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        String PMove = Key.Path_Recover_Photo + file.getName();
        File FileRecover = new File(PMove);
        try {
            FileOutputStream outputStream = new FileOutputStream(FileRecover);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
            refreshGallery(context, FileRecover);
            file.delete();

            int file_size = parseInt(String.valueOf(file.length() / 1024));
            // get day
            Date lastModDate = new Date(file.lastModified());

            list_history.add(new InfofileHistory(path, file.getName(),
                    lastModDate+"", file_size+"", Key.Photo));
            DataHistory.getInstance(context).daoSql().insert(list_history.get(0));

        } catch (FileNotFoundException e) {
            Log.d("ewfwefw", "Error: " + e);
            e.printStackTrace();
        }
    }

    public void refreshGallery(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(mediaScanIntent);
    }

    @Override
    public int getItemCount() {
        if (list.size() > 0) {
            return list.size();
        }
        return 0;
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        private ImageView img_file;
        private TextView txt_name;
        private TextView txt_date;
        private TextView txt_size;
        private TextView txt_path;
        private Button bt_recover;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            img_file = itemView.findViewById(R.id.img_file);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_size = itemView.findViewById(R.id.txt_size);
            txt_path = itemView.findViewById(R.id.txt_path);
            bt_recover = itemView.findViewById(R.id.bt_recover);

        }
    }
}

