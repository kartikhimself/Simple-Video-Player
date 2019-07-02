package com.sdcode.videoplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sdcode.videoplayer.fragment.FragmentFolderList;
import com.sdcode.videoplayer.fragment.MyFragment;
import com.sdcode.videoplayer.kxUtil.PreferencesUtility;
import com.sdcode.videoplayer.kxUtil.kxUtils;
import com.sdcode.videoplayer.permission.PermissionCallback;
import com.sdcode.videoplayer.videoService.VideoService;
import com.sdcode.videoplayer.video.VideoItem;

import java.util.ArrayList;


public abstract class BaseActivity extends AppCompatActivity  {

    GlobalVar mGlobalVar = GlobalVar.getInstance();
    protected static final int PERMISSION_REQUEST_CODE = 888888888;
    boolean isNeedResumePlay = false;
    MyFragment currentFragment  = new FragmentFolderList();
    //protected CastContext castContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int theme = PreferencesUtility.getInstance(this).getThemeSettings();
        if(theme < 0 || theme > 9) theme = 0;
        if(theme== 0) {
            setTheme(R.style.AppThemeLightBasic0);

        }else if(theme== 1) {
            setTheme(R.style.AppThemeLightBasic1);

        }
        if(theme== 2) {
            setTheme(R.style.AppThemeLightBasic2);

        }
        if(theme== 3) {
            setTheme(R.style.AppThemeLightBasic3);

        }
        if(theme== 4) {
            setTheme(R.style.AppThemeLightBasic4);

        }
        if(theme== 5) {
            setTheme(R.style.AppThemeLightBasic5);

        }
        if(theme== 6) {
            setTheme(R.style.AppThemeLightBasic6);

        }
        if(theme== 7) {
            setTheme(R.style.AppThemeLightBasic7);

        }
        if(theme== 8) {
            setTheme(R.style.AppThemeLightBasic8);

        }
        if(theme== 9) {
            setTheme(R.style.AppThemeLightBasic9);

        }
        super.onCreate(savedInstanceState);
        inItService();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(GlobalVar.getInstance().videoService != null)
            unbindService(videoServiceConnection);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected ServiceConnection videoServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            VideoService.VideoBinder binder = (VideoService.VideoBinder) service;
            GlobalVar.getInstance().videoService = binder.getService();

            if(isNeedResumePlay) startPopupMode();
            if(mGlobalVar.isOpenFromIntent){
                mGlobalVar.isOpenFromIntent = false;
                mGlobalVar.videoService.playVideo(0,false);
                showFloatingView(BaseActivity.this,true);
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnection = false;
        }
    };
    boolean isConnection = false;
    protected  static Intent _playIntent;

    public void inItService() {
        _playIntent = new Intent(this, VideoService.class);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(_playIntent);
            } else {
                startService(_playIntent);
            }
        }catch (IllegalStateException e){
            e.printStackTrace();
            return ;
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(_playIntent);
//        } else {
//            startService(_playIntent);
//        }
        //startService(_playIntent);
        bindService(_playIntent, videoServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void startPopupMode() {
        if (_playIntent != null) {
            GlobalVar.getInstance().videoService.playVideo(mGlobalVar.seekPosition,true);
        }
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(currentFragment != null){
            currentFragment.reloadFragment(newConfig.orientation);
        }
        // Checking the orientation of the screen
    }
    public void reloadData(){

    }
    int requestCode = 1;
    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (this.requestCode == resultCode) {
            isNeedResumePlay = true;
            startPopupMode();

        } else {

        }
    }
    @SuppressLint("NewApi")
    public void showFloatingView(Context context, boolean isShowOverlayPermission) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startPopupMode();
            return;
        }

        if (Settings.canDrawOverlays(context)) {
            startPopupMode();
            return;
        }

        if (isShowOverlayPermission) {
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            startActivityForResult(intent, requestCode);
        }
    }

    /// permission
    protected void checkPermissionAndThenLoad() {
        //check for permission
        if (kxUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && kxUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadEverything();
        } else {
            kxUtils.askForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadStorageCallback);
        }
    }

    protected final PermissionCallback permissionReadStorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            if(kxUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && kxUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                loadEverything();
            }else if(!kxUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                kxUtils.askForPermission(BaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionReadStorageCallback);
        }

        @Override
        public void permissionRefused() {
            finish();
        }
    };
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        kxUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    protected void loadEverything(){

    }
    public void updateMultiSelectedState(){

    }
    public void updateListAfterDelete(ArrayList<VideoItem> videoItems){

    }
//    protected void setupCast(){
//        try {
//            castContext = CastContext.getSharedInstance(this);
//        } catch (RuntimeException e) {
//            Throwable cause = e.getCause();
//            while (cause != null) {
//                if (cause instanceof DynamiteModule.LoadingException) {
//                    return;
//                }
//                cause = cause.getCause();
//            }
//            // Unknown error. We propagate it.
//            throw e;
//        }
//    }
}
