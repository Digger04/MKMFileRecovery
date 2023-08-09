package com.fileclean.mkmfilerecovery.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fileclean.mkmfilerecovery.Activity.DetailsVaultActivity;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHide.InfoFileHide;
import com.fileclean.mkmfilerecovery.R;

import java.util.List;

public class Adapter_Vault extends BaseAdapter {

    private Context context;
    private List<InfoFileHide> list;
    private String type;

    public Adapter_Vault(Context context, List<InfoFileHide> list, String type) {
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

    public List<InfoFileHide> getList() {
        return list;
    }

    public void setList(List<InfoFileHide> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list.size() > 0) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class Viewholder {

        private ImageView imgfile;
        private TextView txt_name;
        private TextView txtsize;
        private TextView txtdate;
        private LinearLayout layoutview;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Viewholder viewholder = null;

        if (view == null) {
            viewholder = new Viewholder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row_vault, null);
            viewholder.imgfile = view.findViewById(R.id.imgfile);
            viewholder.txt_name = view.findViewById(R.id.txt_name);
            viewholder.txtsize = view.findViewById(R.id.txtsize);
            viewholder.txtdate = view.findViewById(R.id.txtdate);
            viewholder.layoutview = view.findViewById(R.id.layoutview);

            view.setTag(viewholder);

        }else {
            viewholder = (Viewholder) view.getTag();
        }

        InfoFileHide infoFileHide = list.get(i);

        switch (type) {
            case "photo":
                viewholder.txt_name.setText(context.getString(R.string.picturehide));
                Glide.with(context).load(infoFileHide.getPath()).error(R.drawable.ic_load).into(viewholder.imgfile);
                break;
            case "video":
                viewholder.txt_name.setText(context.getString(R.string.videohide));
                Glide.with(context).load(infoFileHide.getPath()).error(R.drawable.ic_load).into(viewholder.imgfile);
                break;
            case "audio":
                viewholder.txt_name.setText(context.getString(R.string.audiohide));
                Glide.with(context).load(R.drawable.ic_mp3).error(R.drawable.ic_load).into(viewholder.imgfile);
            case "file":
                viewholder.txt_name.setText(context.getString(R.string.filehide));
                if (infoFileHide.getName().contains(".txt")) {
                    Glide.with(context).load(R.drawable.ic_filetxt).into(viewholder.imgfile);
                }else if (infoFileHide.getName().contains(".pdf")) {
                    Glide.with(context).load(R.drawable.ic_pdf).into(viewholder.imgfile);
                }else if (infoFileHide.getName().contains(".doc") || infoFileHide.getName().contains(".docx")) {
                    Glide.with(context).load(R.drawable.ic_word).into(viewholder.imgfile);
                }else if (infoFileHide.getName().contains(".ppt") || infoFileHide.getName().contains(".pptx")) {
                    Glide.with(context).load(R.drawable.ic_ppt).into(viewholder.imgfile);
                }else if (infoFileHide.getName().contains(".xls") || infoFileHide.getName().contains(".xlxs")) {
                    Glide.with(context).load(R.drawable.ic_excel).into(viewholder.imgfile);
                }
                break;
        }

        Log.d("fwefwfe" ,"path: " + infoFileHide.getPath());
        viewholder.txtsize.setText(context.getString(R.string.filesize) + infoFileHide.getSize() + " Kb");
        viewholder.txtdate.setText(context.getString(R.string.date) + infoFileHide.getDate());

        viewholder.layoutview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsVaultActivity.class);
                intent.putExtra(Key.KeyIntent, infoFileHide.getPath());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
