package com.sdcode.videoplayer.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sdcode.videoplayer.kxUtil.PreferencesUtility;
import com.sdcode.videoplayer.customizeUI.ThemeChoiceItem;
import com.sdcode.videoplayer.R;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ThemeChoiceItemAdapter extends RecyclerView.Adapter<ThemeChoiceItemAdapter.ItemHolder> {


    Activity context;
    ArrayList<ThemeChoiceItem> themeChoiceItems = new ArrayList<>();
    int choiceId = 0;
    PreferencesUtility preferencesUtility;

    public ThemeChoiceItemAdapter(Activity context){
        this.context = context;
        preferencesUtility = new PreferencesUtility(context);
        choiceId = preferencesUtility.getThemeSettings();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.theme_choice_item, null);

        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        ThemeChoiceItem themeChoiceItem = themeChoiceItems.get(i);
        itemHolder.layoutContainer.setBackgroundColor(ContextCompat.getColor(context,themeChoiceItem.getColor()));
        itemHolder.imageView.setColor(themeChoiceItem.getId() == choiceId ? Color.GREEN : Color.WHITE);
        itemHolder.imageView.setVisibility(themeChoiceItem.getId() == choiceId ? View.VISIBLE:View.INVISIBLE);

        itemHolder.container.setOnClickListener(v -> {
            choiceId = themeChoiceItem.getId();
            updateData(themeChoiceItems);
            preferencesUtility.setThemSettings(themeChoiceItem.getId());
        });

    }
    public void updateData(ArrayList<ThemeChoiceItem> items){

        if(items == null) items = new ArrayList<>();
        ArrayList<ThemeChoiceItem> r = new ArrayList<>(items);
        int currentSize = themeChoiceItems.size();
        if(currentSize != 0) {
            this.themeChoiceItems.clear();
            this.themeChoiceItems.addAll(r);
            notifyDataSetChanged();
        }
        else {
            this.themeChoiceItems.addAll(r);
            notifyDataSetChanged();
        }
    }
    @Override
    public int getItemCount() {
        return themeChoiceItems.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout layoutContainer;

        protected MaterialIconView imageView;

        View container;

        public ItemHolder(View view) {
            super(view);
            container = view;
            this.layoutContainer = view.findViewById(R.id.layout_root);
            this.imageView = view.findViewById(R.id.btn_choice);

        }
    }

}
