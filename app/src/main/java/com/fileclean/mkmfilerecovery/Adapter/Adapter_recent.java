package com.fileclean.mkmfilerecovery.Adapter;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;
import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fileclean.mkmfilerecovery.Activity.PendingMoveActivity;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHide.DataHide;
import com.fileclean.mkmfilerecovery.Database.DataHide.InfoFileHide;
import com.fileclean.mkmfilerecovery.Database.DataHistory.DataHistory;
import com.fileclean.mkmfilerecovery.Database.DataHistory.InfofileHistory;
import com.fileclean.mkmfilerecovery.Database.DataRecent.DataRecent;
import com.fileclean.mkmfilerecovery.Database.DataRecent.InforRecent;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Adapter_recent extends RecyclerView.Adapter<Adapter_recent.Viewholder> {

    private Context context;
    private List<InforRecent> list;
    private String Pmove;
    private String type;

    public Adapter_recent(Context context, List<InforRecent> list) {
        this.context = context;
        this.list = list;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<InforRecent> getList() {
        return list;
    }

    public void setList(List<InforRecent> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.row_item_recent, null);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        InforRecent inforRecent = list.get(position);
        holder.name.setText(inforRecent.getName());
        holder.date.setText(inforRecent.getDate());

        if (inforRecent.getName().endsWith(".png") || inforRecent.getName().endsWith(".jpg")
                || inforRecent.getName().endsWith(".jpeg") || inforRecent.getName().endsWith(".mp4")) {

            Glide.with(context).load(inforRecent.getPath()).error(R.drawable.ic_error).into(holder.img_file);

        }else if (inforRecent.getName().endsWith(".zip")) {

            Glide.with(context).load(R.drawable.ic_zip).error(R.drawable.ic_error).into(holder.img_file);

        }else if (inforRecent.getName().endsWith(".mp3")) {

            Glide.with(context).load(R.drawable.ic_mp3).error(R.drawable.ic_error).into(holder.img_file);

        }else if (inforRecent.getName().endsWith(".txt")) {

            Glide.with(context).load(R.drawable.ic_filetxt).error(R.drawable.ic_error).into(holder.img_file);

        }else if (inforRecent.getName().endsWith(".pdf")) {

            Glide.with(context).load(R.drawable.ic_pdf).error(R.drawable.ic_error).into(holder.img_file);

        }else if (inforRecent.getName().endsWith(".doc") || inforRecent.getName().endsWith(".docx")) {

            Glide.with(context).load(R.drawable.ic_word).error(R.drawable.ic_error).into(holder.img_file);

        }else if (inforRecent.getName().endsWith(".xls") || inforRecent.getName().endsWith(".xlxs")) {

            Glide.with(context).load(R.drawable.ic_excel).error(R.drawable.ic_error).into(holder.img_file);

        }else if (inforRecent.getName().endsWith(".ppt") || inforRecent.getName().endsWith(".pptx")) {

            Glide.with(context).load(R.drawable.ic_ppt).error(R.drawable.ic_error).into(holder.img_file);

        }

        holder.bt_move.setOnClickListener(view -> {
            getType(inforRecent.getPath());
            HideFileDetails(inforRecent.getPath());
            DataRecent.getInstance(getContext()).daoSql().delete(list.get(position));
            Intent intent = new Intent(context, PendingMoveActivity.class);
            intent.putExtra(Key.Key_Type_Screen, Key.Recent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.bt_recover.setOnClickListener(view -> {
            if (inforRecent.getName().contains(".img") || inforRecent.getName().contains(".jpg")
                    || inforRecent.getName().contains(".png") || inforRecent.getName().contains(".jpeg")) {

                MovePhoto(inforRecent.getPath());

            }else if (inforRecent.getName().contains(".mp3")) {

                MoveVideo_audio(Key.Audio, inforRecent.getPath());

            }if (inforRecent.getPath().contains(".mp4")) {

                MoveVideo_audio(Key.Video, inforRecent.getPath());

            }else {
                MoveFile(inforRecent.getPath());
            }

            DataRecent.getInstance(getContext()).daoSql().delete(list.get(position));

            Intent intent = new Intent(context, PendingMoveActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Key.Key_Type_Screen, Key.Recent);
            context.startActivity(intent);

        });

    }

    private void getType(String path) {

        if (path.contains(".mp3")) {
            type = Key.KeyAudioMoveIn;
        }else if (path.contains(".mp4")) {
            type = Key.KeyvideoMoveIn;
        }else if (path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg")) {
            type = Key.KeyPhotoMoveIn;
        }else if (path.contains(".txt") || path.contains(".pdf") || path.contains(".doc") ||
                path.contains(".docx") || path.contains(".xls") || path.contains(".xlxs") ||
                path.contains(".ppt") || path.contains(".pptx")) {
            type = Key.KeyDocumentMoveIn;
        }
    }

    private void HideFileDetails(String path) {
        File file = new File(path);

        Random random = new Random();
        final int min = 10000;
        final int max = 99999;
        int N_EndName = random.nextInt((max - min) + 1) + min;

        // creat folder
        String folder_vault = Key.FolderVaultSave;
        File f = new File(Environment.getExternalStorageDirectory(), folder_vault);
        if (!f.exists()) {
            f.mkdirs();
            if (type != null) {
                HideFile(file, N_EndName, type);
            }
        }else {
            if (type != null) {
                HideFile(file, N_EndName, type);
            }
        }

    }

    private void HideFile(File file, int n_endName, String type) {
        int file_size = parseInt(String.valueOf(file.length() / 1024));
        // get day
        Date lastModDate = new Date(file.lastModified());

        if (type.equals(Key.KeyDocumentMoveIn)) {

            Pmove = Key.FolderVault + type + file.getName() + Key.EndNameVault + n_endName;

        }else {
            Pmove = Key.FolderVault + type + Key.EndNameVault + n_endName;
        }

        File FileMove = new File(Pmove);
        try {

            switch (type) {
                case "KeyPhotoMoIn":
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

                    DataHide.getInstance(getContext()).daoSql().insert(
                            new InfoFileHide(Pmove, file.getName(),
                                    lastModDate+"", file_size+"",
                                    type)
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
                    try {
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
                    }catch (Exception e) {
                        //by default do not rotate the image
                        Log.d("fwe", "wefwef: " + e);
                    }

                    break;

                case "KeyvideoMoveIn":
                    MoveIn(Pmove, FileMove, file);
                    break;

                case "KeyAudioMoveIn":
                    MoveIn(Pmove, FileMove, file);

                    break;
                case "KeyDocumentMoveIn":
                    MoveIn(Pmove, FileMove, file);
                    break;

            }
            file.delete();
            refreshGallery(context, file);

        } catch (IOException e) {
            Log.d(Key.KeyLog, "error: " + e);
        }
    }

    private void MoveIn(String pmove, File fileMove, File file) {
        int file_size = parseInt(String.valueOf(file.length() / 1024));
        // get day
        Date lastModDate = new Date(file.lastModified());

        try {
            if (file.exists()) {

                DataHide.getInstance(getContext()).daoSql().insert(
                        new InfoFileHide(Pmove, file.getName(),
                                lastModDate+"", file_size+"", type));

                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream1 = new FileOutputStream(fileMove);

                byte[] buf = new byte[1024];
                int len;

                while ((len = inputStream.read(buf)) > 0) {
                    outputStream1.write(buf, 0, len);
                }

                outputStream1.flush();
                inputStream.close();
                outputStream1.close();

            } else {
                Toast.makeText(context, R.string.file_has_faild, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {

        }
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
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
            }catch (Exception e) {

            }
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

    public class Viewholder extends RecyclerView.ViewHolder {

        private ImageView img_file;
        private TextView name;
        private TextView date;
        private TextView bt_recover;
        private TextView bt_move;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            img_file = itemView.findViewById(R.id.img_file);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            bt_recover = itemView.findViewById(R.id.bt_recover);
            bt_move = itemView.findViewById(R.id.bt_move);
        }
    }
}

