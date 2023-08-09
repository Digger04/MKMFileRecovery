package com.fileclean.mkmfilerecovery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fileclean.mkmfilerecovery.MainApp;
import com.fileclean.mkmfilerecovery.Model.LanguageCode;
import com.fileclean.mkmfilerecovery.R;
import com.fileclean.mkmfilerecovery.Utils.MyUtils;
import com.fileclean.mkmfilerecovery.Utils.SharedPreferencesUtils;

import java.util.List;
import java.util.Objects;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private final List<String> list_language;
    private final Context context;
    private final ILanguageAdapter adapter;

    public LanguageAdapter(Context context, List<String> list_language, ILanguageAdapter adapter) {
        this.list_language = list_language;
        this.context = context;
        this.adapter = adapter;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.language_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String language = list_language.get(position);

        holder.txt_language.setText(language);
        if (Objects.equals(MainApp.mmkv.decodeString(SharedPreferencesUtils.KEY_LOCALE_LANGUAGE, LanguageCode.English), LanguageCode.getLanguageCode(language))) {
            holder.selected.setVisibility(View.VISIBLE);
        } else {
            holder.selected.setVisibility(View.INVISIBLE);
        }
        if (TextUtilsCompat.getLayoutDirectionFromLocale(MyUtils.convertLanguage(language)) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            holder.txt_language.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        } else {
            holder.txt_language.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        holder.itemView.setOnClickListener(view -> {
            if (!Objects.equals(MainApp.mmkv.decodeString(SharedPreferencesUtils.KEY_LOCALE_LANGUAGE, LanguageCode.English), LanguageCode.getLanguageCode(language))) {
                adapter.onItemClick2(language);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_language.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_language;
        ImageView selected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_language = itemView.findViewById(R.id.tv_language_item);
            selected = itemView.findViewById(R.id.selected_language);
        }
    }
}

