package com.fileclean.mkmfilerecovery.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fileclean.mkmfilerecovery.Activity.DetailsActivity;
import com.fileclean.mkmfilerecovery.Activity.MoveInVaultActivity;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHide.InfoFileHide;
import com.fileclean.mkmfilerecovery.Model.InfoFile;
import com.fileclean.mkmfilerecovery.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Move_In extends RecyclerView.Adapter<Adapter_Move_In.Viewholder> {

    private Context context;
    private List<InfoFile> list;
    private String type;
    private List<InfoFileHide> list_tg;
    private int count = 0;


    public Adapter_Move_In(String type, Context context, List<InfoFile> list) {
        this.context = context;
        this.list = list;
        this.type = type;
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
        if (type.equals("photo") || type.equals("video")) {
            view = LayoutInflater.from(context).inflate(R.layout.row_move_vault_photo, null);
        }else if (type.equals("audio") || type.equals("file")){
            view = LayoutInflater.from(context).inflate(R.layout.row_move_audios_files, null);
        }
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        if (type.equals("video")) {
            holder.img_play.setVisibility(View.VISIBLE);
        }else if (type.equals("photo")) {
            holder.img_play.setVisibility(View.GONE);
        }

        InfoFile infoFile = list.get(position);

        if (type.equals(Key.Photo) || type.equals(Key.Video)) {
            Glide.with(context).load(infoFile.getPath()).into(holder.img_file);
        }else if (type.equals(Key.Audio)){
            Glide.with(context).load(R.drawable.ic_mp3).into(holder.img_file);
            holder.txt_name.setText(infoFile.getName()+"");
            holder.txt_size.setText(infoFile.getSize()+ " Kb");
        }else if (type.equals(Key.File)) {
            if (infoFile.getName().endsWith(".txt")) {
                Glide.with(context).load(R.drawable.ic_filetxt).into(holder.img_file);
            }else if (infoFile.getName().endsWith(".pdf")) {
                Glide.with(context).load(R.drawable.ic_pdf).into(holder.img_file);
            }else if (infoFile.getName().endsWith(".doc") || infoFile.getName().endsWith(".docx")) {
                Glide.with(context).load(R.drawable.ic_word).into(holder.img_file);
            }else if (infoFile.getName().endsWith(".ppt") || infoFile.getName().endsWith(".pptx")) {
                Glide.with(context).load(R.drawable.ic_ppt).into(holder.img_file);
            }else if (infoFile.getName().endsWith(".xls") || infoFile.getName().endsWith(".xlxs")) {
                Glide.with(context).load(R.drawable.ic_excel).into(holder.img_file);
            }
            holder.txt_name.setText(infoFile.getName()+"");
            holder.txt_size.setText(infoFile.getSize()+ " Kb");
        }

        if (MoveInVaultActivity.listHide.size() > 0) {
            for (int i = 0; i < MoveInVaultActivity.listHide.size(); i++) {
                if (MoveInVaultActivity.listHide.get(i).getPath().equals(infoFile.getPath())) {
                    holder.checkBox.setChecked(true);
                }else {
                    holder.checkBox.setChecked(false);
                }
            }
        }

        holder.layout_view.setOnClickListener(view -> {
            Log.d("weffewfwe", "type: " + type);
            if (type.equals(Key.Photo)) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(Key.Key_Type_Screen, Key.KeyPhotoMoveIn);
                intent.putExtra(Key.KeyIntent, infoFile.getPath());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (type.equals(Key.Video)){
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(Key.Key_Type_Screen, Key.KeyvideoMoveIn);
                intent.putExtra(Key.KeyIntent, infoFile.getPath());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (type.equals(Key.File)) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(Key.Key_Type_Screen, Key.KeyDocumentMoveIn);
                intent.putExtra(Key.KeyIntent, infoFile.getPath());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (type.equals(Key.Audio)) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(Key.Key_Type_Screen, Key.KeyAudioMoveIn);
                intent.putExtra(Key.KeyIntent, infoFile.getPath());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        list_tg = new ArrayList<>();
        list_tg.clear();

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked()) {
                    MoveInVaultActivity.bt_move.setBackgroundResource(R.drawable.shape_button_move);
                    MoveInVaultActivity.bt_move.setTextColor(getContext().getResources().getColor(R.color.white));
                    count++;

                    MoveInVaultActivity.listHide.add(new InfoFileHide(
                            infoFile.getPath(), infoFile.getName(), infoFile.getDate(),
                            infoFile.getSize(), type));

                    MoveInVaultActivity.bt_move.setText(context.getString(R.string.MoveinVault) + " [ " +
                            count + " ]");
                }else {
                    count--;

                    if (count == 0) {
                        MoveInVaultActivity.bt_move.setBackgroundResource(R.drawable.shape_button_move_null);
                        MoveInVaultActivity.bt_move.setTextColor(getContext().getResources().getColor(R.color.grey848));
                    }

                    if (MoveInVaultActivity.listHide.size() > 0) {
                        for (int i = 0; i < MoveInVaultActivity.listHide.size(); i++) {

                            if (infoFile.getPath().equals(MoveInVaultActivity.listHide.get(i).getPath())) {
                                MoveInVaultActivity.listHide.remove(i);
                            }
                        }
                    }

                    MoveInVaultActivity.bt_move.setText(context.getString(R.string.MoveinVault) + " [ " +
                            count + " ]");
                }
            }
        });
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
        private CheckBox checkBox;
        private ImageView img_play;
        private RelativeLayout layout_view;
        private TextView txt_name;
        private TextView txt_size;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            img_file = itemView.findViewById(R.id.img_file);
            checkBox = itemView.findViewById(R.id.checkbox);
            img_play = itemView.findViewById(R.id.img_play);
            layout_view = itemView.findViewById(R.id.layout_view);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_size = itemView.findViewById(R.id.txt_size);
        }
    }
}

