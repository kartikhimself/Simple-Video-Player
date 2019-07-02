/*
* The MIT License (MIT)

* Copyright (c) 2015 Michal Tajchert

* permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package com.sdcode.videoplayer.kxUtil;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.sdcode.videoplayer.permission.PermissionCallback;
import com.sdcode.videoplayer.permission.PermissionListener;
import com.sdcode.videoplayer.permission.PermissionRequest;
import com.sdcode.videoplayer.R;
import com.sdcode.videoplayer.video.VideoItem;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class kxUtils {
    private static final String TAG = kxUtils.class.getSimpleName();
    private static final String KEY_PREV_PERMISSIONS = "previous_permissions";
    private static final String KEY_IGNORED_PERMISSIONS = "ignored_permissions";
    private static Context context;
    private static SharedPreferences sharedPreferences;
    private static ArrayList<PermissionRequest> permissionRequests = new ArrayList<>();

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences("pl.tajchert.runtimepermissionhelper", Context.MODE_PRIVATE);
        kxUtils.context = context;
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     */
    public static boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the Activity has access to given permissions.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasPermission(Activity activity, String permission) {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Returns true if the Activity has access to a all given permission.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasPermission(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /*
     * If we override other methods, lets do it as well, and keep name same as it is already weird enough.
     * Returns true if we should show explanation why we need this permission.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permissions) {
        return activity.shouldShowRequestPermissionRationale(permissions);
    }

    public static void askForPermission(Activity activity, String permission, PermissionCallback permissionCallback) {
        askForPermission(activity, new String[]{permission}, permissionCallback);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void askForPermission(Activity activity, String[] permissions, PermissionCallback permissionCallback) {
        if (permissionCallback == null) {
            return;
        }
        if (hasPermission(activity, permissions)) {
            permissionCallback.permissionGranted();
            return;
        }
        PermissionRequest permissionRequest = new PermissionRequest(new ArrayList<>(Arrays.asList(permissions)), permissionCallback);
        permissionRequests.add(permissionRequest);

        activity.requestPermissions(permissions, permissionRequest.getRequestCode());
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionRequest requestResult = new PermissionRequest(requestCode);
        if (permissionRequests.contains(requestResult)) {
            PermissionRequest permissionRequest = permissionRequests.get(permissionRequests.indexOf(requestResult));
            if (verifyPermissions(grantResults)) {
                //permission has been granted
                permissionRequest.getPermissionCallback().permissionGranted();
            } else {
                permissionRequest.getPermissionCallback().permissionRefused();

            }
            permissionRequests.remove(requestResult);
        }
        refreshMonitoredList();
    }


    //permission monitoring part below

    /**
     * Get list of currently granted permissions, without saving it inside kxUtils
     *
     * @return currently granted permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static ArrayList<String> getGrantedPermissions() {
        if (context == null) {
            throw new RuntimeException("Must call init() earlier");
        }
        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsGranted = new ArrayList<>();
        //Group location
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        //Group Calendar
        permissions.add(Manifest.permission.WRITE_CALENDAR);
        permissions.add(Manifest.permission.READ_CALENDAR);
        //Group Camera
        permissions.add(Manifest.permission.CAMERA);
        //Group Contacts
        permissions.add(Manifest.permission.WRITE_CONTACTS);
        permissions.add(Manifest.permission.READ_CONTACTS);
        permissions.add(Manifest.permission.GET_ACCOUNTS);
        //Group Microphone
        permissions.add(Manifest.permission.RECORD_AUDIO);
        //Group Phone
        permissions.add(Manifest.permission.CALL_PHONE);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissions.add(Manifest.permission.READ_CALL_LOG);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissions.add(Manifest.permission.WRITE_CALL_LOG);
        }
        permissions.add(Manifest.permission.ADD_VOICEMAIL);
        permissions.add(Manifest.permission.USE_SIP);
        permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        //Group Body sensors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            permissions.add(Manifest.permission.BODY_SENSORS);
        }
        //Group SMS
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.RECEIVE_WAP_PUSH);
        permissions.add(Manifest.permission.RECEIVE_MMS);
        //Group Storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted.add(permission);
            }
        }
        return permissionsGranted;
    }

    /**
     * Refresh currently granted permission list, and save it for later comparing using @permissionCompare()
     */
    public static void refreshMonitoredList() {
        ArrayList<String> permissions = getGrantedPermissions();
        Set<String> set = new HashSet<>();
        for (String perm : permissions) {
            set.add(perm);
        }
        sharedPreferences.edit().putStringSet(KEY_PREV_PERMISSIONS, set).apply();
    }

    /**
     * Get list of previous Permissions, from last refreshMonitoredList() call and they may be outdated,
     * use getGrantedPermissions() to get current
     */
    public static ArrayList<String> getPreviousPermissions() {
        ArrayList<String> prevPermissions = new ArrayList<>();
        prevPermissions.addAll(sharedPreferences.getStringSet(KEY_PREV_PERMISSIONS, new HashSet<String>()));
        return prevPermissions;
    }

    public static ArrayList<String> getIgnoredPermissions() {
        ArrayList<String> ignoredPermissions = new ArrayList<>();
        ignoredPermissions.addAll(sharedPreferences.getStringSet(KEY_IGNORED_PERMISSIONS, new HashSet<String>()));
        return ignoredPermissions;
    }

    /**
     * Lets see if we already ignore this permission
     */
    public static boolean isIgnoredPermission(String permission) {
        if (permission == null) {
            return false;
        }
        return getIgnoredPermissions().contains(permission);
    }

    /**
     * Use to ignore to particular permission - even if user will deny or add it we won't receive a callback.
     *
     * @param permission permission to ignore
     */
    public static void ignorePermission(String permission) {
        if (!isIgnoredPermission(permission)) {
            ArrayList<String> ignoredPermissions = getIgnoredPermissions();
            ignoredPermissions.add(permission);
            Set<String> set = new HashSet<>();
            set.addAll(ignoredPermissions);
            sharedPreferences.edit().putStringSet(KEY_IGNORED_PERMISSIONS, set).apply();
        }
    }

    /**
     * Used to trigger comparing process - @permissionListener will be called each time permission was revoked, or added (but only once).
     *
     * @param permissionListener Callback that handles all permission changes
     */
    public static void permissionCompare(PermissionListener permissionListener) {
        if (context == null) {
            throw new RuntimeException("Before comparing permissions you need to call kxUtils.init(context)");

        }
        ArrayList<String> previouslyGranted = getPreviousPermissions();
        ArrayList<String> currentPermissions = getGrantedPermissions();
        ArrayList<String> ignoredPermissions = getIgnoredPermissions();
        for (String permission : ignoredPermissions) {
            if (previouslyGranted != null && !previouslyGranted.isEmpty()) {
                if (previouslyGranted.contains(permission)) {
                    previouslyGranted.remove(permission);
                }
            }

            if (currentPermissions != null && !currentPermissions.isEmpty()) {
                if (currentPermissions.contains(permission)) {
                    currentPermissions.remove(permission);
                }
            }
        }
        for (String permission : currentPermissions) {
            if (previouslyGranted.contains(permission)) {
                //All is fine, was granted and still is
                previouslyGranted.remove(permission);
            } else {
                //We didn't have it last time
                if (permissionListener != null) {
                    permissionListener.permissionsChanged(permission);
                    permissionListener.permissionsGranted(permission);
                }
            }
        }
        if (previouslyGranted != null && !previouslyGranted.isEmpty()) {
            //Something was granted and removed
            for (String permission : previouslyGranted) {
                if (permissionListener != null) {
                    permissionListener.permissionsChanged(permission);
                    permissionListener.permissionsRemoved(permission);
                }
            }
        }
        refreshMonitoredList();
    }

    /**
     * Not that needed method but if we override others it is good to keep same.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermission(String permissionName) {
        if (context == null) {
            throw new RuntimeException("Before comparing permissions you need to call kxUtils.init(context)");
        }
        return PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(permissionName);
    }

    public static String makeShortTimeString(final Context context, long secs) {
        long hours, mins;

        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;

        final String durationFormat = context.getResources().getString(
                hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }
    public static String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if(size < sizeMb)
            return df.format(size / sizeKb)+ " Kb";
        else if(size < sizeGb)
            return df.format(size / sizeMb) + " Mb";
        else if(size < sizeTerra)
            return df.format(size / sizeGb) + " Gb";

        return "";
    }
    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
    public static String getFileExtension(String fileName) {
        String fileNameArray[] = fileName.split("\\.");
        return fileNameArray[fileNameArray.length-1];


    }
    public static String getFileNameFromPath(String path){
        if(path == null) return "Unknown File";
        int i = path.lastIndexOf("/");
        if(i == 0) return path;
        return path.substring(i);

    }
    public static Intent shareVideo(final Context context, VideoItem videoItem) {


        if (videoItem == null) return new Intent();

        String filePath = videoItem.getPath();
        if (filePath == null) return new Intent();
        File shareFile = new File(filePath);
        if (!shareFile.exists()) return new Intent();
        String fileType = getFileExtension(filePath);

        if (shareFile.exists()) {
            try {
                if(fileType.equals("Mp4") || filePath.equals("mp4") || filePath.equals("MP4"))
                    return new Intent()
                            .setAction(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_STREAM, VideoPlayerProvider.getUri(context, shareFile))
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            .setType("video/mp4");
                else {
                    return new Intent()
                            .setAction(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_STREAM, VideoPlayerProvider.getUri(context, shareFile))
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            .setType("file/*");
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                return new Intent();
            }
        }
        //Toast.makeText(context, "File cannot share, (:", Toast.LENGTH_SHORT).show();
        return new Intent();
    }

    public static void shareMultiVideo(final Context context, List<VideoItem> videoItems) {
        ArrayList<Uri> files = new ArrayList<>();
        File f;
        for (VideoItem videoItem:videoItems){
            f = new File(videoItem.getPath());
            if(f.exists()) {
                Uri uri;
                try {
                    uri = VideoPlayerProvider.getUri(context, f);

                }catch (IllegalArgumentException e){
                    uri = null;
                    e.printStackTrace();
                }
                if(uri != null) files.add(uri);
            }
        }
        if(files.size() > 0) {
            try {
                Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
                share.putExtra(Intent.EXTRA_SUBJECT, "Share all video files.");
                share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.setType("file/*");
                context.startActivity(Intent.createChooser(share, "Share To"));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
    public static final String about = "";
}