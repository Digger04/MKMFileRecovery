package com.fileclean.mkmfilerecovery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fileclean.mkmfilerecovery.Console.Key;
import com.fileclean.mkmfilerecovery.Database.DataHistory.InfofileHistory;
import com.fileclean.mkmfilerecovery.R;

import java.util.List;

public class Adapter_history extends BaseAdapter {

    private Context context;
    private List<InfofileHistory> list;
    private String type;

    public Adapter_history(Context context, List<InfofileHistory> list, String type) {
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

    public List<InfofileHistory> getList() {
        return list;
    }

    public void setList(List<InfofileHistory> list) {
        this.list = list;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

        InfofileHistory infofileHistory = list.get(i);

        switch (type) {
            case "photo":
                Glide.with(context).load("/storage/emulated/0/Pictures/" +infofileHistory.getName()).error(R.drawable.ic_load).into(viewholder.imgfile);
                break;
            case "video":
                Glide.with(context).load(Key.Path_Recover_Videos +infofileHistory.getName()).error(R.drawable.ic_load).into(viewholder.imgfile);
                break;
            case "audio":
                Glide.with(context).load(R.drawable.ic_mp3).error(R.drawable.ic_load).into(viewholder.imgfile);
            case "file":
                if (infofileHistory.getName().contains(".txt")) {
                    Glide.with(context).load(R.drawable.ic_filetxt).into(viewholder.imgfile);
                }else if (infofileHistory.getName().contains(".pdf")) {
                    Glide.with(context).load(R.drawable.ic_pdf).into(viewholder.imgfile);
                }else if (infofileHistory.getName().contains(".doc") || infofileHistory.getName().contains(".docx")) {
                    Glide.with(context).load(R.drawable.ic_word).into(viewholder.imgfile);
                }else if (infofileHistory.getName().contains(".ppt") || infofileHistory.getName().contains(".pptx")) {
                    Glide.with(context).load(R.drawable.ic_ppt).into(viewholder.imgfile);
                }else if (infofileHistory.getName().contains(".xls") || infofileHistory.getName().contains(".xlxs")) {
                    Glide.with(context).load(R.drawable.ic_excel).into(viewholder.imgfile);
                }
                break;
        }

        viewholder.txt_name.setText(infofileHistory.getName());
        viewholder.txtsize.setText(context.getString(R.string.filesize) + infofileHistory.getSize() + " Kb");
        viewholder.txtdate.setText(context.getString(R.string.date) + infofileHistory.getDate());

        return view;
    }
}
