package com.sdcode.videoplayer.folder;

import com.sdcode.videoplayer.video.VideoItem;

import java.util.ArrayList;

public class FolderItem {
    private String folderName;
    private String folderPath;

    private ArrayList<VideoItem> videoItems = new ArrayList<>();

    public FolderItem(String folderName,String folderPath){
        this.folderName = folderName;
        this.folderPath = folderPath;
    }
    public FolderItem(String folderName,String folderPath,ArrayList<VideoItem> videoItems){
        this.folderName = folderName;
        this.folderPath = folderPath;
        this.videoItems = videoItems;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }


    public ArrayList<VideoItem> getVideoItems() {
        return videoItems;
    }

    public void setVideoItems(ArrayList<VideoItem> videoItems) {
        this.videoItems = videoItems;
    }
}
