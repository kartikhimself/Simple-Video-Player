package com.sdcode.videoplayer.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.sdcode.videoplayer.GlobalVar;
import com.sdcode.videoplayer.R;
import com.sdcode.videoplayer.video.VideoItem;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoPlayingListAdapter extends RecyclerView.Adapter<VideoPlayingListAdapter.ItemHolder> {

    Activity context;
    private ArrayList<VideoItem> videoItems = new ArrayList<>();
    public VideoPlayingListAdapter(Activity context){
        this.context = context;
    }
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_playing, null);
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
        itemHolder.txtVideoPath.setText(videoItem.getPath());
        itemHolder.container.setOnClickListener(v -> {
            GlobalVar.getInstance().playingVideo = videoItem;
            GlobalVar.getInstance().videoService.playVideo(0,false);
        });

        itemHolder.btnRemove.setOnClickListener(v -> {
            videoItems.remove(i);
            GlobalVar.getInstance().videoItemsPlaylist = videoItems;
            updateData(videoItems);
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



    public class ItemHolder extends RecyclerView.ViewHolder {
        protected TextView title, duration,txtVideoPath;
        protected ImageView imageView;
        protected MaterialIconView btnRemove;

        View container;

        public ItemHolder(View view) {
            super(view);
            container = view;
            this.txtVideoPath = view.findViewById(R.id.txtVideoPath);
            this.title = view.findViewById(R.id.txtVideoTitle);
            this.imageView = view.findViewById(R.id.imageView);
            this.duration = view.findViewById(R.id.txtVideoDuration);
            this.btnRemove = view.findViewById(R.id.btn_remove_to_playingList);
        }

    }
    public void deleteVideo(VideoItem videoItem){
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.delete_video))
                .content(context.getString(R.string.confirm) +" " + videoItem.getVideoTitle() + " ?")
                .positiveText(context.getString(R.string.confirm_delete))
                .negativeText(context.getString(R.string.confirm_cancel))
                .onPositive((dialog1, which) -> {
                    File deleteFile = new File(videoItem.getPath());
                    if(deleteFile.exists()){
                        if(deleteFile.delete()){
                            GlobalVar.getInstance().isNeedRefreshFolder = true;
                            if(removeIfPossible(videoItem))
                                context.finish();
                            else {
                                updateData(videoItems);
                            }

                        }
                    }
                })
                .onNegative((dialog12, which) -> dialog12.dismiss())
                .show();
    }
    private boolean removeIfPossible(VideoItem videoItem){
        for(VideoItem video:videoItems) {
            if (videoItem.getPath().equals(video.getPath())){
                videoItems.remove(videoItem);
                break;
            }

        }
        GlobalVar.getInstance().videoItemsPlaylist = new ArrayList<>(videoItems);
        if(videoItems.size() == 0){
            return true;
        }else {
            GlobalVar.getInstance().playNext();
        }
        return false;
    }
}
