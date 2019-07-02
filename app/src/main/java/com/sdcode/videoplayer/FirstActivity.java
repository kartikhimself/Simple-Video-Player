package com.sdcode.videoplayer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sdcode.videoplayer.fragment.FragmentFolderList;
import com.sdcode.videoplayer.kxUtil.PreferencesUtility;
import com.sdcode.videoplayer.kxUtil.kxUtils;
import com.sdcode.videoplayer.video.VideoItem;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class FirstActivity extends BaseActivity implements RatingDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Toolbar toolbar = findViewById(R.id.toolbar);
        currentFragment  = new FragmentFolderList();
        setSupportActionBar(toolbar);
        //setupCast();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (kxUtils.isMarshmallow()) checkPermissionAndThenLoad();
        else loadEverything();

        String action = getIntent().getAction();
        if(Intent.ACTION_VIEW.equals(action)) {

            Intent receivedIntent = getIntent();
            Uri receivedUri = receivedIntent.getData();

            assert receivedUri != null;
            String _file = receivedUri.toString();
            mGlobalVar.playingVideo = new VideoItem();
            mGlobalVar.playingVideo.setPath(_file);
            mGlobalVar.playingVideo.setVideoTitle(kxUtils.getFileNameFromPath(_file));
            mGlobalVar.videoItemsPlaylist = new ArrayList<>();
            mGlobalVar.videoItemsPlaylist.add(mGlobalVar.playingVideo);
            if(mGlobalVar.videoService == null){
                mGlobalVar.isOpenFromIntent = true;
            }else {
                mGlobalVar.videoService.playVideo(0,false);
                showFloatingView(FirstActivity.this,true);
                finish();
            }
        }
        createDialog();
        int laughtCount = PreferencesUtility.getInstance(this).getlaughCount();
        if(laughtCount == 8) showRateDialog();
        PreferencesUtility.getInstance(this).setLaughCount(laughtCount+1);

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
    @Override
    protected void loadEverything(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,currentFragment).commit();
    }
    @Override
    public void reloadData(){
        currentFragment = new FragmentFolderList();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,currentFragment).commit();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mGlobalVar.isNeedRefreshFolder && currentFragment != null){
            mGlobalVar.isNeedRefreshFolder = false;
            reloadData();
        }
//        if(castContext == null){
//             return;
//        }
//        if(mGlobalVar.videoService == null || mGlobalVar.videoService.getPlayerManager() == null){
//            return;
//        }
//        mGlobalVar.videoService.getPlayerManager().updateCast(castContext);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.secound, menu);
//        if(currentFragment instanceof FragmentVideoList){
//            if(PreferencesUtility.getInstance(FirstActivity.this).isAlbumsInGrid())
//                getMenuInflater().inflate(R.menu.first, menu);
//            else
//                getMenuInflater().inflate(R.menu.first1, menu);
//        }
//        else if (currentFragment instanceof FragmentFolderList)
//        {
//            getMenuInflater().inflate(R.menu.secound, menu);
//        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.secound, menu);
//        if(currentFragment instanceof FragmentVideoList){
//            if(PreferencesUtility.getInstance(FirstActivity.this).isAlbumsInGrid())
//                getMenuInflater().inflate(R.menu.first, menu);
//            else
//                getMenuInflater().inflate(R.menu.first1, menu);
//        }
//        else if (currentFragment instanceof FragmentFolderList)
//        {
//            getMenuInflater().inflate(R.menu.secound, menu);
//        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Handler handler = new Handler();
        //noinspection SimplifiableIfStatement
        if(id == R.id.action_search){
            if (kxUtils.isMarshmallow())
            {
                if (kxUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    navigateToSearch(this);
                }
            }
            else navigateToSearch(this);
        } if(id == R.id.action_setting){
            startActivity(new Intent(FirstActivity.this,SettingsActivity.class));
        }else if(id == R.id.menu_sort_by_az){
            currentFragment.sortAZ();
        }else if(id == R.id.menu_sort_by_za){
            currentFragment.sortZA();
        }else if(id == R.id.menu_sort_by_total_videos){
            currentFragment.sortSize();
        }else if(id == R.id.action_go_to_playing) {
            if (mGlobalVar.videoService == null || mGlobalVar.playingVideo == null) {
                Toast.makeText(this, getString(R.string.no_video_playing), Toast.LENGTH_SHORT).show();
                return false;
            }
            final Intent intent = new Intent(FirstActivity.this, PlayVideoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_stay_x);

        }else if(id == R.id.action_about){
            startActivity(new Intent(FirstActivity.this,AboutActivity.class));
        }else if(id == R.id.action_sleepTimer){
            if(dialog != null) dialog.show();
        }else if(id == R.id.action_musicPlayer){
            Uri uri = Uri.parse("market://details?id=" + "com.sdcode.etmusicplayer");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + "com.sdcode.etmusicplayer")));
            }
        }


        return false;
    }
    public static void navigateToSearch(Activity context) {
        final Intent intent = new Intent(context, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    AlertDialog dialog;
    Button btnDownload;
    TextView textView1;

    private void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.content_download_app,null);
        btnDownload =  view.findViewById(R.id.btn_download);
        textView1 =  view.findViewById(R.id.txt_promote_text);


        btnDownload.setOnClickListener(view1 -> {
            dialog.cancel();
            Uri uri = Uri.parse("market://details?id=" + "com.sdcode.timerswitch");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + "com.sdcode.timerswitch")));
            }
        });
        builder.setView(view);
        dialog = builder.create();
    }

    public  void showRateDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Normal", "Ok, but need improve more", "Excellent, I like it !!!"))
                .setDefaultRating(1)
                .setTitle("Rate this application")
                .setDescription("Your rating help us understand and keep update the application.")
                .setCommentInputEnabled(false)
                .setDefaultComment("This app is pretty cool !")
                .setStarColor(R.color.nice_pink3)
                .setNoteDescriptionTextColor(R.color.blackL)
                .setTitleTextColor(R.color.blackL)
                .setDescriptionTextColor(R.color.blackL)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.red)
                .setCommentTextColor(R.color.blackl1)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(FirstActivity.this)
                .show();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        if(i == 5){
            Uri uri = Uri.parse("market://details?id=" + "com.sdcode.videoplayer");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        }else {
            Toast.makeText(FirstActivity.this,"Thank you, we will try to improve app",Toast.LENGTH_SHORT).show();
        }
    }
}
