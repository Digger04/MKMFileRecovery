package com.fileclean.mkmfilerecovery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fileclean.mkmfilerecovery.Activity.SearchActivity;
import com.fileclean.mkmfilerecovery.Database.DataSearch.DataSearch;
import com.fileclean.mkmfilerecovery.Database.DataSearch.InfoSearch;
import com.fileclean.mkmfilerecovery.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_history_Search extends BaseAdapter {

    private Context context;
    private List<InfoSearch> list;
    private List<InfoSearch> listSQL;

    public Adapter_history_Search(Context context, List<InfoSearch> list) {
        this.context = context;
        this.list = list;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<InfoSearch> getListView() {
        return list;
    }

    public void setListView(List<InfoSearch> listView) {
        this.list = listView;
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

        private TextView txt_data;
        private ImageView img_clear;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Viewholder viewholder = null;

        if (view == null) {
            viewholder = new Viewholder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row_history_search, null);

            viewholder.txt_data = view.findViewById(R.id.txt_data);
            viewholder.img_clear = view.findViewById(R.id.img_clear);

            view.setTag(viewholder);

        }else {
            viewholder = (Viewholder) view.getTag();
        }

        InfoSearch infoSearch = list.get(i);

        viewholder.txt_data.setText(infoSearch.getData() +"");

        listSQL = new ArrayList<>();
        listSQL = DataSearch.getInstance(context).daoSql().getall();

        viewholder.img_clear.setOnClickListener(view1 -> {
            DataSearch.getInstance(context).daoSql().delete(listSQL.get(i));
            SearchActivity.list_history_search.remove(i);
            notifyDataSetChanged();
        });

        return view;
    }
}
