package com.fileclean.mkmfilerecovery.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.fileclean.mkmfilerecovery.Activity.DetailsActivity;
import com.fileclean.mkmfilerecovery.Activity.ViewFileGroupActivity;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Model.InfoFile;
import com.fileclean.mkmfilerecovery.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class GroupSection extends Section {

    private Context context;
    public List<InfoFile> files;
    public String dateTime;
    private final ClickListener clickListener;
    private boolean similar = false;
    private boolean isCheckAll = false;

    public boolean isExpanded = true;
    public boolean isChecked = false;
    public int selectedAmount = 0;

    private Dialog dialog;
    public static String type;

    public GroupSection(Context context, List<InfoFile> files, String dateTime,
                        @NonNull final ClickListener clickListener) {
        super(SectionParameters.builder().itemResourceId(R.layout.row_image_grid).headerResourceId(R.layout.group_item).build());
        this.context = context;
        this.files = files;
        this.dateTime = dateTime;
        this.clickListener = clickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return isExpanded ? files.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        InfoFile file = files.get(position);
        holder.txt_name.setText(file.getName());
        holder.txt_days.setText(file.getDate());
        int size = Integer.parseInt(file.getSize());
        holder.txt_size.setText(size + " Kb");

        RequestOptions requestOptions = new RequestOptions()
                .signature(new ObjectKey(file.getDate().split("-")[0] + "-" + file.getDate().split("-")[1]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .priority(Priority.LOW)
                .format(DecodeFormat.PREFER_ARGB_8888);
        Glide.with(context).asBitmap().load(file.getPath()).apply(requestOptions).into(holder.img_file);
        holder.check_dele.setChecked(file.isSelected());
        holder.layout_view.setOnClickListener(view -> {
            openImageDetailDialog(file);
        });

  /*      if (similar) {

            if (file.getPath().equals("") && file.getDate().equals("") &&
                    file.getName().equals("") && file.getSize().equals("0")) {

                holder.layout_view.setVisibility(View.INVISIBLE);

            } else {
                holder.layout_view.setVisibility(View.VISIBLE);
                holder.layout_view.setOnClickListener(view -> {
                    openImageDetailDialog(file);
                });
            }

            if (position == 0) {
                holder.layout_similar.setVisibility(View.VISIBLE);
            }

            try {

                if (file.isSimilar()) {
                    holder.layout_similar.setVisibility(View.VISIBLE);
                } else {
                    holder.layout_similar.setVisibility(View.INVISIBLE);
                }

            } catch (Exception e) {
                Log.d("qfw", "error: " + e);
            }

        } */

        holder.check_dele.setOnClickListener(view -> {
            file.setSelected(holder.check_dele.isChecked());
            clickListener.onSelectedFile(holder.check_dele.isChecked(), size);
            boolean ismatch = false;
            if (ViewFileGroupActivity.listRecover.size() > 0) {
                for (int i = 0; i < ViewFileGroupActivity.listRecover.size(); i++) {
                    if (ViewFileGroupActivity.listRecover.get(i).getPath().equals(file.getPath())) {
                        ViewFileGroupActivity.listRecover.remove(i);
                        ismatch = true;
                        break;
                    }
                }

                if (ismatch == false) {
                    ViewFileGroupActivity.listRecover.add(file);

                }
            }else {
                ViewFileGroupActivity.listRecover.add(file);
            }
        });
    }

    public void Clear() {
        files.clear();
        return;
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        //super.onBindItemViewHolder(holder, position, payloads);
        ItemViewHolder vh = (ItemViewHolder) holder;
        InfoFile file = files.get(position);
        if (!payloads.isEmpty()) {
            for (Object payload : payloads) {
                if (payload.equals("notify")) {
                    vh.check_dele.setChecked(file.isSelected());
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder) {
        //super.onBindHeaderViewHolder(viewHolder);
        HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

        if (similar) {
            DecimalFormat decimalFormat = new DecimalFormat("##.##");
            holder.datetime.setText("File Size: " + decimalFormat.format(getTotalSize() / 1024) + " MB");
        } else {
            holder.datetime.setText(dateTime);
        }

        holder.checkBox.setOnClickListener(view -> {
            ViewFileGroupActivity.ispause = true;
            if (!holder.checkBox.isChecked()) {
                ViewFileGroupActivity.listRecover.clear();
            }
        });


        holder.amount.setText("(" + getSelectedAmount() + "/" + files.size() + ")");
        isCheckAll = true;
        holder.checkBox.setChecked(isChecked);
        isCheckAll = false;
        holder.layout.setOnClickListener(view -> clickListener.onHeaderRootViewClicked(GroupSection.this));
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {

            for (int i = 0; i < files.size(); i ++ ) {
                try {
                    ViewFileGroupActivity.listRecover.add(files.get(i));
                }catch (Exception e) {

                }
            }
            //if (b != isChecked) {
            if (!isCheckAll) {
                isChecked = b;
                int s = getSelectedAmount();
                long size = getSelectedSize();

                if (b) selectedAmount = files.size();
                else selectedAmount = 0;

                for (InfoFile file : files) {
                    file.setSelected(b);
                }

                if (isChecked) {
                    clickListener.onHeaderRootViewChecked(GroupSection.this, files.size() - s, getTotalSize() - size);
                } else {
                    clickListener.onHeaderRootViewChecked(GroupSection.this, s, size);
                }
            }
            isCheckAll = false;
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, List<Object> payloads) {
        //super.onBindHeaderViewHolder(viewHolder, payloads);
        HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
        if (!payloads.isEmpty()) {
            for (Object payload : payloads) {
                if (payload.equals("notify")) {
                    holder.amount.setText("(" + getSelectedAmount() + "/" + files.size() + ")");
                    if (similar) {
                        DecimalFormat decimalFormat = new DecimalFormat("##.##");
                        holder.datetime.setText("File Size: " + decimalFormat.format(getSize() / 1024) + " MB");
                    }
                } else if (payload.equals("selected")) {
                    isCheckAll = true;
                    holder.checkBox.setChecked(isChecked);
                } else if (payload.equals("expand")) {
                    if (isExpanded) {
                        Glide.with(context).load(R.drawable.ic_baseline_keyboard_arrow_up_24).into(holder.collapse);
                    } else {
                        Glide.with(context).load(R.drawable.ic_baseline_keyboard_arrow_down_24).into(holder.collapse);
                    }
                }
            }
        }
    }

    public void setSelectedSection() {
        for (InfoFile file : files) {
            file.setSelected(isChecked);
        }
    }

    public int getSelectedAmount() {
        selectedAmount = 0;
        for (InfoFile file : files) {
            if (file.isSelected()) selectedAmount = selectedAmount + 1;
        }
        return selectedAmount;
    }

    public long getSelectedSize() {
        long c = 0;
        for (InfoFile file : files) {
            if (file.isSelected()) c += Integer.parseInt(file.getSize());
        }
        return c;
    }

    public long getTotalSize() {
        long c = 0;
        for (InfoFile file : files) {
            c += Integer.parseInt(file.getSize());
        }
        return c;
    }

    public int deleteFileByPath(String path) {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getPath().equals(path)) {
                File f = new File(path);
                //noinspection ResultOfMethodCallIgnored
                f.delete();
                files.remove(i);
                return i;
            }
        }
        return 0;
    }

    public float getSize() {
        float size = 0;
        for (InfoFile file : files) {
            size += Integer.parseInt(file.getSize());
        }
        return size;
    }

    public boolean isSimilar() {
        return similar;
    }

    public void setSimilar(boolean similar) {
        this.similar = similar;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout;
        CheckBox checkBox;
        TextView datetime, amount;
        ImageView collapse;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.header_container);
            checkBox = itemView.findViewById(R.id.check_box);
            datetime = itemView.findViewById(R.id.date_time);
            amount = itemView.findViewById(R.id.selected_amount);
            collapse = itemView.findViewById(R.id.collapse_icon);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView img_file;
        TextView txt_name;
        TextView txt_days;
        TextView txt_size;
        LinearLayout layout_view;
        CheckBox check_dele;
        LinearLayout layout_similar;
        CardView cardView;
        TextView text_similar;
        CheckBox check_delete_all;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            img_file = itemView.findViewById(R.id.img_file);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_days = itemView.findViewById(R.id.txt_days);
            txt_size = itemView.findViewById(R.id.txt_size);
            layout_view = itemView.findViewById(R.id.layout_view);
            check_dele = itemView.findViewById(R.id.check_dele);
            layout_similar = itemView.findViewById(R.id.layout_similar);
            //cardView = itemView.findViewById(R.id.carview);
            check_delete_all = itemView.findViewById(R.id.check_dele_all);
            text_similar = itemView.findViewById(R.id.text_similar);

        }
    }

    @SuppressLint("SimpleDateFormat")
    private void openImageDetailDialog(InfoFile file) {

        Intent intent = new Intent(context, DetailsActivity.class);
        ViewFileGroupActivity.isBack2 = true;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Key.Key_Type_Screen, type);
        intent.putExtra(Key.KeyIntent, file.getPath());
        context.startActivity(intent);

    }

   /* private void ExportImages(InfoFile file) {
        try {
            Bitmap bm = BitmapFactory.decodeFile(file.getPath());

            String path = "/storage/emulated/0/Pictures/" + file.getName();

            File file1 = new File(path);
            file1.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file1);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            refreshGallery(context, file1);
            Toast.makeText(context, R.string.image_has_just_saved, Toast.LENGTH_LONG).show();
            dialog.cancel();
        } catch (Exception e) {

        }
    } */

   /* public void openFile(File file) {
        // Get URI and MIME type of file
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
   /*     String mime = context.getContentResolver().getType(uri);

        // Open file with user selected app
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent); */

    /*    Intent intent;
        if (file.getName().endsWith(".mp4")) {
            intent = new Intent(context, PreviewVideos.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(context, PreviewImage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra("path", file.getPath());
        intent.putExtra("name", file.getName());
        context.startActivity(intent);

    } */

    public void refreshGallery(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(mediaScanIntent);
    }

    public interface ClickListener {

        void onHeaderRootViewClicked(@NonNull final GroupSection section);

        void onHeaderRootViewChecked(@NonNull final GroupSection section, int count, long size);

        void onItemRootViewClicked(@NonNull final GroupSection section, final int itemAdapterPosition);

        void onSelectedFile(boolean isSelected, int size);
    }

}

