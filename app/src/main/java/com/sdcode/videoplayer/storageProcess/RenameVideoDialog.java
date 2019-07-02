package com.sdcode.videoplayer.storageProcess;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sdcode.videoplayer.GlobalVar;
import com.sdcode.videoplayer.kxUtil.kxUtils;
import com.sdcode.videoplayer.R;
import com.sdcode.videoplayer.adapter.VideoAdapter;
import com.sdcode.videoplayer.video.VideoItem;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class RenameVideoDialog extends DialogFragment {

    static VideoItem videoItem;
    static Context context;
    static VideoAdapter videoAdapter;
    public static RenameVideoDialog newInstance(Context context, VideoAdapter videoAdapter,VideoItem videoItem) {
        RenameVideoDialog dialog = new RenameVideoDialog();
        RenameVideoDialog.videoItem = videoItem;
        RenameVideoDialog.context = context;
        RenameVideoDialog.videoAdapter = videoAdapter;
        return dialog;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(context).positiveText(getString(R.string.action_rename)).negativeText(getString(R.string.confirm_cancel))
                .title(getString(R.string.action_rename)).input(getString(R.string.enter_new_name), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(isNewNamePossible(input.toString())){
                            if(renameMedia(context,input.toString())){
                                videoAdapter.updateData(videoAdapter.getVideoItems());
                            }
                        }else {
                            Toast.makeText(context,context.getString(R.string.fileNameExist),Toast.LENGTH_LONG).show();
                        }

                    }
                }).build();
    }
    public static boolean renameMedia(Context context, String newName) {
        boolean success = false;
        Uri external = MediaStore.Files.getContentUri("external");
        try {
            File from = new File(videoItem.getPath());
            if(from.exists()) {
                File dir = from.getParentFile();
                String fileEx = kxUtils.getFileExtension(videoItem.getPath());
                File to = new File(dir, newName + "." + fileEx);
                if (success = StorageHelper.moveFile(context, from, to)) {
                    context.getContentResolver().delete(external,
                            MediaStore.MediaColumns.DATA + "=?", new String[]{from.getPath()});

                    scanFile(context, new String[]{to.getAbsolutePath()});
                    videoItem.setVideoTitle(newName);
                    videoItem.setPath(to.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
    public static void scanFile(Context context, String[] path) {
        MediaScannerConnection.scanFile(context.getApplicationContext(), path, null, null);
    }
    private boolean isNewNamePossible(String newName){
        if(videoItem == null) return false;
        String fileEx = kxUtils.getFileExtension(videoItem.getPath());
        if(GlobalVar.getInstance().folderItem == null || GlobalVar.getInstance().folderItem.getVideoItems() == null)
            return false;
        for(VideoItem videoItem: GlobalVar.getInstance().folderItem.getVideoItems()){
            if(newName.equals(videoItem.getVideoTitle()) && fileEx.equals(kxUtils.getFileExtension(videoItem.getPath())))
                return false;
        }
        return true;
    }
}