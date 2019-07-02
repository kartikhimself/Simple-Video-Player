package com.sdcode.videoplayer.video;

/**
 * Created by sudamasayuki on 2017/11/22.
 */
public class VideoItem  {
    private  String videoTitle;
    private  String path;
    private  String duration;
    private  String folderName;
    private  String fileSize;
    private  String resolution;
    private long fileSizeAsFloat;
    private  String date_added;
    private boolean isLongClick = false;
    private boolean isSelected = false;

    public VideoItem(String videoTitle, String path, String duration, String folderName, String fileSize, String resolution, long fileSizeAsFloat, String date_added) {

        this.path = path;
        this.duration = duration;
        this.videoTitle = videoTitle;
        this.folderName = folderName;
        this.fileSize = fileSize;
        this.resolution = resolution;
        this.fileSizeAsFloat = fileSizeAsFloat;
        this.date_added = date_added;
    }
    public VideoItem() {
        this.videoTitle = "";
        this.path = "";
        this.duration = "";
    }
        public String getPath() {
        return path;
    }

    public String getDuration() {
        return duration;
    }



    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setPath(String value){
        this.path = value;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public boolean isLongClick() {
        return isLongClick;
    }

    public void setLongClick(boolean longClick) {
        isLongClick = longClick;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public float getFileSizeAsFloat() {
        return fileSizeAsFloat;
    }

    public void setFileSizeAsFloat(long fileSizeAsFloat) {
        this.fileSizeAsFloat = fileSizeAsFloat;
    }
}


