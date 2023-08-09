package com.fileclean.mkmfilerecovery.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fileclean.mkmfilerecovery.Activity.DetailsActivity;
import com.fileclean.mkmfilerecovery.Activity.ViewFileActivity;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Model.InfoFile;
import com.fileclean.mkmfilerecovery.R;

import java.util.List;

public class AdapterFile extends RecyclerView.Adapter<AdapterFile.Viewholder> {

    private Context context;
    private List<InfoFile> list;
    private String type;

    public AdapterFile(Context context, List<InfoFile> list, String type) {
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
        view = LayoutInflater.from(context).inflate(R.layout.row_item, null);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        InfoFile infoFile = list.get(position);
        int size = Integer.parseInt(infoFile.getSize());
        holder.txt_size.setText(size+" Kb");

        holder.checkBox.setChecked(false);
        if (ViewFileActivity.listRecover.size() > 0) {
            for (int i = 0; i < ViewFileActivity.listRecover.size(); i++) {
                if (list.get(position).getPath().equals(ViewFileActivity.listRecover.get(i).getPath())) {
                    holder.checkBox.setChecked(true);
                }
            }
        }

        switch (type) {
            case "photo":
                holder.txt_name.setVisibility(View.GONE);
                Glide.with(context).load(infoFile.getPath()).into(holder.img_file);
                break;
            case "audio":
                holder.txt_name.setText(infoFile.getName());
                Glide.with(context).load(R.drawable.ic_mp3).into(holder.img_file);
                break;
            case "video":
                holder.txt_name.setVisibility(View.GONE);
                Glide.with(context).load(infoFile.getPath()).into(holder.img_file);
                break;
            case "document":
                holder.txt_name.setText(infoFile.getName());
                if (infoFile.getName().endsWith(".txt")){
                    Glide.with(context).load(R.drawable.ic_filetxt).into(holder.img_file);
                }else if (infoFile.getName().endsWith(".pdf")) {
                    Glide.with(context).load(R.drawable.ic_pdf).into(holder.img_file);
                }else if (infoFile.getName().endsWith(".doc") || infoFile.getName().endsWith(".docx")) {
                    Glide.with(context).load(R.drawable.ic_word).into(holder.img_file);
                }else if (infoFile.getName().endsWith(".xls") || infoFile.getName().endsWith(".xlxs")) {
                    Glide.with(context).load(R.drawable.ic_excel).into(holder.img_file);
                }else if (infoFile.getName().endsWith(".ppt") || infoFile.getName().endsWith(".pptx")){
                    Glide.with(context).load(R.drawable.ic_ppt).into(holder.img_file);
                }

                break;
            case "zip":
                holder.txt_name.setText(infoFile.getName());
                Glide.with(context).load(R.drawable.ic_zip).into(holder.img_file);
                break;
            case "recyclebin":
                holder.txt_name.setVisibility(View.GONE);
                Glide.with(context).load(infoFile.getPath()).error(R.drawable.ic_file_trash).into(holder.img_file);
                break;
        }


        holder.layout_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Key.Key_Type_Screen, type);
                intent.putExtra(Key.KeyIntent, list.get(position).getPath());
                context.startActivity(intent);
                ViewFileActivity.ispause = true;
                ViewFileActivity.isBack3 = true;
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked()) {
                    ViewFileActivity.listRecover.add(infoFile);
                    ViewFileActivity.bt_recover.setText(context.getString(R.string.recover) + " [ " +
                            ViewFileActivity.listRecover.size() + " ]");
                }else {
                    ViewFileActivity.listRecover.remove(infoFile);
                    ViewFileActivity.bt_recover.setText(context.getString(R.string.recover) + " [ " +
                            ViewFileActivity.listRecover.size() + " ]");
                }

                if (ViewFileActivity.listRecover.size() > 0) {
                    ViewFileActivity.bt_recover.setBackgroundResource(R.color.blue017);
                }else {
                    ViewFileActivity.bt_recover.setBackgroundResource(R.color.black);
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
        private TextView txt_size;
        private TextView txt_name;
        private LinearLayout layout_view;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            img_file = itemView.findViewById(R.id.img_file);
            checkBox = itemView.findViewById(R.id.checkbox);
            txt_size = itemView.findViewById(R.id.txt_size);
            txt_name = itemView.findViewById(R.id.txt_name);
            layout_view = itemView.findViewById(R.id.layout_view);
        }
    }
}
