package com.sdcode.videoplayer.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sdcode.videoplayer.GlobalVar;
import com.sdcode.videoplayer.BaseActivity;
import com.sdcode.videoplayer.customizeUI.FastScroller;
import com.sdcode.videoplayer.FolderDetailActivity;
import com.sdcode.videoplayer.kxUtil.PreferencesUtility;
import com.sdcode.videoplayer.kxUtil.kxUtils;
import com.sdcode.videoplayer.PlayVideoActivity;
import com.sdcode.videoplayer.R;
import com.sdcode.videoplayer.SearchActivity;
import com.sdcode.videoplayer.storageProcess.RenameVideoDialog;
import com.sdcode.videoplayer.video.VideoItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ItemHolder> implements FastScroller.BubbleTextGetter {


    Activity context;

    private ArrayList<VideoItem> videoItems = new ArrayList<>();
    public VideoAdapter(Activity context){
        this.context = context;
    }



    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView;
        if(context instanceof SearchActivity)
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video, null);
        else if(PreferencesUtility.getInstance(context).isAlbumsInGrid())
             itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_grid, null);
        else
             itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video, null);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        VideoItem videoItem = videoItems.get(i);
        itemHolder.title.setText(videoItem.getVideoTitle());
        itemHolder.duration.setText(videoItem.getDuration());
        Glide.with(context.getApplicationContext())
                .load(videoItem.getPath())
                .into(itemHolder.imageView);
        itemHolder.txtVideoPath.setText("/".concat(videoItem.getFolderName()).concat("     ").concat(videoItem.getFileSize()));
        itemHolder.container.setBackgroundColor(videoItem.isSelected() ? ContextCompat.getColor(context,R.color.multiselected) : Color.TRANSPARENT);
        itemHolder.container.setOnLongClickListener(v -> {
            if(context instanceof FolderDetailActivity) {
                GlobalVar.getInstance().isMutilSelectEnalble = true;
                videoItem.setLongClick(true);
                videoItem.setSelected(!videoItem.isSelected());
                itemHolder.container.setBackgroundColor(videoItem.isSelected() ? ContextCompat.getColor(context, R.color.multiselected) : Color.TRANSPARENT);
                if (context instanceof BaseActivity) {
                    ((BaseActivity) context).updateMultiSelectedState();
                }
            }
            return false;
        });
        itemHolder.container.setOnClickListener(v -> {
            GlobalVar.getInstance().videoItemsPlaylist = videoItems;
            GlobalVar.getInstance().playingVideo = videoItem;
            GlobalVar.getInstance().seekPosition = 0;
            if(GlobalVar.getInstance().getPlayer() == null){
                return;
            }else if(!GlobalVar.getInstance().isMutilSelectEnalble) {
                if(!GlobalVar.getInstance().isPlayingAsPopup()){
                    GlobalVar.getInstance().videoService.playVideo(GlobalVar.getInstance().seekPosition,false);
                    Intent intent = new Intent(context, PlayVideoActivity.class);
                    context.startActivity(intent);
                    if(GlobalVar.getInstance().videoService != null)
                        GlobalVar.getInstance().videoService.releasePlayerView();
                }else {
                    ((BaseActivity) context).showFloatingView(context,true);
                }
            }else if(checkError(videoItems,i) && !videoItems.get(i).isLongClick()) {
                videoItem.setSelected(!videoItem.isSelected());
                itemHolder.container.setBackgroundColor(videoItem.isSelected() ? ContextCompat.getColor(context,R.color.multiselected) : Color.TRANSPARENT);
                if(context instanceof BaseActivity){
                    ((BaseActivity) context).updateMultiSelectedState();
                }
            }
            try {
                videoItem.setLongClick(false);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }


        });
        itemHolder.imageViewOption.setOnClickListener(v -> {
            showBottomDialog(videoItem);
        });


    }
    public void updateData(ArrayList<VideoItem> items){
        if(items == null) items = new ArrayList<>();
        ArrayList<VideoItem> r = new ArrayList<>(items);
        int currentSize = videoItems.size();
        if(currentSize != 0) {
            this.videoItems.clear();
            this.videoItems.addAll(r);
            notifyItemRangeRemoved(0, currentSize);
            //tell the recycler view how many new items we added
            notifyItemRangeInserted(0, r.size());
        }
        else {
            this.videoItems.addAll(r);
            notifyItemRangeInserted(0, r.size());
        }

    }
    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    public ArrayList<VideoItem> getVideoItems(){
        if(videoItems == null) return new ArrayList<>();
        return videoItems;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        return null;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        protected TextView title, duration,txtVideoPath;

        protected ImageView imageView,imageViewOption;

        View container;

        public ItemHolder(View view) {
            super(view);
            container = view;
            this.txtVideoPath = view.findViewById(R.id.txtVideoPath);
            this.title = view.findViewById(R.id.txtVideoTitle);
            this.imageView = view.findViewById(R.id.imageView);
            this.duration = view.findViewById(R.id.txtVideoDuration);
            this.imageViewOption = view.findViewById(R.id.imageViewOption);

        }

    }
    private void showBottomDialog(VideoItem videoItem) {
        View view = context.getLayoutInflater().inflate(R.layout.video_option_dialog, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        LinearLayout option_playPopup = view.findViewById(R.id.option_play_popup);
        option_playPopup.setOnClickListener(v -> {
            GlobalVar.getInstance().playingVideo = videoItem;
            GlobalVar.getInstance().videoItemsPlaylist = videoItems;
            if (context instanceof BaseActivity) {
                ((BaseActivity) context).showFloatingView(context, true);
            }
            dialog.dismiss();
        });
        LinearLayout option_play_audio = view.findViewById(R.id.option_play_audio);
        option_play_audio.setOnClickListener(v -> {
            PreferencesUtility.getInstance(context).setAllowBackgroundAudio(true);
            GlobalVar.getInstance().videoItemsPlaylist = videoItems;
            GlobalVar.getInstance().playingVideo = videoItem;
            GlobalVar.getInstance().videoService.playVideo(GlobalVar.getInstance().seekPosition, false);
            dialog.dismiss();
        });
        LinearLayout option_share = view.findViewById(R.id.option_share);
        option_share.setOnClickListener(v -> {
            context.startActivity(Intent.createChooser(kxUtils.shareVideo(context,videoItem),context.getString(R.string.action_share)));
            dialog.dismiss();
        });
        LinearLayout option_rename = view.findViewById(R.id.option_rename);
        option_rename.setOnClickListener(v -> {
            RenameVideoDialog renamePlaylistDialog = RenameVideoDialog.newInstance(context,this,videoItem);
            renamePlaylistDialog.show(((AppCompatActivity) context).getSupportFragmentManager(),"");
            dialog.dismiss();
        });
        LinearLayout option_info = view.findViewById(R.id.option_info);
        option_info.setOnClickListener(v -> {
            dialog.dismiss();
            createDialog(videoItem);
        });
        LinearLayout option_delete = view.findViewById(R.id.option_delete);
        option_delete.setOnClickListener(v -> {
            dialog.dismiss();
            new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.delete_video))
                    .content(context.getString(R.string.confirm) +" " + videoItem.getVideoTitle() + " ?")
                    .positiveText(context.getString(R.string.confirm_delete))
                    .negativeText(context.getString(R.string.confirm_cancel))
                    .onPositive((dialog1, which) -> {
                        File deleteFile = new File(videoItem.getPath());
                        if(deleteFile.exists()){
                            if(deleteFile.delete()){
                                videoItems.remove(videoItem);
                                updateData(videoItems);
                                if(context instanceof BaseActivity){
                                    ((BaseActivity) context).updateListAfterDelete(videoItems);
                                }
                            }
                        }
                    })
                    .onNegative((dialog12, which) -> dialog12.dismiss())
                    .show();
        });
        dialog.setContentView(view);
        dialog.show();
    }
    private void createDialog(VideoItem videoItem){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.content_video_info,null);

        TextView txtVideoTitle = view.findViewById(R.id.txtVideoTitle);
        txtVideoTitle.setText(videoItem.getVideoTitle());

        TextView txtLocation = view.findViewById(R.id.txtLocation_value);
        txtLocation.setText(videoItem.getPath());

        TextView txtVideoFormat = view.findViewById(R.id.txtFormat_value);
        txtVideoFormat.setText(kxUtils.getFileExtension(videoItem.getPath()));

        TextView txtDuration = view.findViewById(R.id.txtDuration_value);
        txtDuration.setText(videoItem.getDuration());

        TextView txtDateAdded = view.findViewById(R.id.txtDateAdded_value);
        txtDateAdded.setText(videoItem.getDate_added());

        TextView txtVideoSize = view.findViewById(R.id.txtFileSize_value);
        txtVideoSize.setText(videoItem.getFileSize());

        TextView txtVideoResolution = view.findViewById(R.id.txResolution_value);
        txtVideoResolution.setText(videoItem.getResolution());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private boolean checkError(List<VideoItem> songs, int position){
        if(songs.size() >= position && position >= 0) return true;
        return false;
    }
    public int getTotalVideoSelected(){
        int totalVideoSelected = 0;
        if(videoItems == null || videoItems.size() == 0) return 0;
        for (VideoItem videoItem: videoItems){
            if(videoItem.isSelected()) totalVideoSelected += 1;
        }
        return totalVideoSelected;
    }
    public ArrayList<VideoItem> getVideoItemsSelected(){
        ArrayList<VideoItem> resultList = new ArrayList<>();
        for (VideoItem videoItem: videoItems){
            if(videoItem.isSelected()) resultList.add(videoItem);
        }
        return resultList;
    }
    public void deleteListVideoSelected(){
        ArrayList<VideoItem> deletedList = getVideoItemsSelected();
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.delete_video))
                .content(context.getString(R.string.confirm) +" " + String.valueOf(deletedList.size()) + " " + context.getString(R.string.video) + " ?")
                .positiveText(context.getString(R.string.confirm_delete))
                .negativeText(context.getString(R.string.confirm_cancel))
                .onPositive((dialog1, which) -> {
                    for(VideoItem item :deletedList){
                            File deleteFile = new File(item.getPath());
                            if (deleteFile.exists()) {
                                if (deleteFile.delete()) {
                                    videoItems.remove(item);
                                }
                            }
                    }
                    updateData(videoItems);
                })
                .onNegative((dialog12, which) -> dialog12.dismiss())
                .show();
    }

}
