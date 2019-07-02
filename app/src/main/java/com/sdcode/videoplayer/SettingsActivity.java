package com.sdcode.videoplayer;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;

import com.sdcode.videoplayer.customizeUI.WrapContentGridLayoutManager;
import com.sdcode.videoplayer.kxUtil.PreferencesUtility;
import com.sdcode.videoplayer.customizeUI.ThemeChoiceItem;
import com.sdcode.videoplayer.adapter.ThemeChoiceItemAdapter;

import java.util.ArrayList;

public class SettingsActivity extends BaseActivity {

    Switch backgroundAudioSwitch ;
    PreferencesUtility preferencesUtility;
    GlobalVar mGlobalVar = GlobalVar.getInstance();
    ThemeChoiceItemAdapter themeChoiceItemAdapter;
    RecyclerView recyclerView;
    int currentTheme ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferencesUtility = PreferencesUtility.getInstance(this);
        currentTheme = preferencesUtility.getThemeSettings();

        backgroundAudioSwitch = findViewById(R.id.backgroundAudioSwitch);
        backgroundAudioSwitch.setChecked(preferencesUtility.isAllowBackgroundAudio());
        backgroundAudioSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) mGlobalVar.openNotification();
            else mGlobalVar.closeNotification();
            preferencesUtility.setAllowBackgroundAudio(isChecked);

        });
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        themeChoiceItemAdapter = new ThemeChoiceItemAdapter(this);
        recyclerView.setLayoutManager(new WrapContentGridLayoutManager(this, 5));
        recyclerView.setAdapter(themeChoiceItemAdapter);
        themeChoiceItemAdapter.updateData(setupListData());

    }
    @Override
    public boolean onSupportNavigateUp() {
        if(preferencesUtility.getThemeSettings() == currentTheme)
            finish();
        else {
            Intent intent = new Intent(SettingsActivity.this, FirstActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(intent);
            finish();
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if(preferencesUtility.getThemeSettings() == currentTheme)
            super.onBackPressed();
        else {
            Intent intent = new Intent(SettingsActivity.this, FirstActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(intent);
            finish();
        }
    }
    private ArrayList<ThemeChoiceItem> setupListData(){
        ArrayList<ThemeChoiceItem> themeChoiceItems = new ArrayList<>();
        themeChoiceItems.add(new ThemeChoiceItem(R.color.nice_green,0));
        themeChoiceItems.add(new ThemeChoiceItem(R.color.nice_pink,1));
        themeChoiceItems.add(new ThemeChoiceItem(R.color.nice_pink1,2));
        themeChoiceItems.add(new ThemeChoiceItem(R.color.nice_pink2,3));
        themeChoiceItems.add(new ThemeChoiceItem(R.color.nice_blue,4));
        themeChoiceItems.add(new ThemeChoiceItem(R.color.nice_red,5));
        themeChoiceItems.add(new ThemeChoiceItem(R.color.nice_pink3,6));
        themeChoiceItems.add(new ThemeChoiceItem(R.color.nice_green1,7));
        themeChoiceItems.add(new ThemeChoiceItem(R.color.nice_purple,8));
        themeChoiceItems.add(new ThemeChoiceItem(R.color.nice_yellow,9));

        return themeChoiceItems;
    }
}
