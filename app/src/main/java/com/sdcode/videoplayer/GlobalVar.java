package com.sdcode.videoplayer;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.sdcode.videoplayer.folder.FolderItem;
import com.sdcode.videoplayer.videoService.VideoService;
import com.sdcode.videoplayer.video.VideoItem;

import java.util.ArrayList;

public class GlobalVar {
    private static final GlobalVar ourInstance = new GlobalVar();

    public static GlobalVar getInstance() {
        return ourInstance;
    }

    private GlobalVar() {
    }


    public VideoItem playingVideo;
    public VideoService videoService;
    public boolean isMutilSelectEnalble = false;
    public long seekPosition = 0;
    public boolean isPlaying = true;
    public boolean isNeedRefreshFolder = false;
    public boolean isOpenFromIntent = false;

    public ArrayList<VideoItem> videoItemsPlaylist = new ArrayList<>();
    public ArrayList<VideoItem> allVideoItems = new ArrayList<>();
    public FolderItem folderItem;


    public boolean isSeekBarProcessing = false;

    public String getPath(){
        if(playingVideo != null)
            return playingVideo.getPath();
        return "77777777777";
    }

    public SimpleExoPlayer getPlayer(){
        if(videoService == null)
            return  null;
        return videoService.getVideoPlayer();
    }
    public boolean isPlayingAsPopup(){
        if(videoService == null) return false;
        return videoService.isPlayingAsPopup();
    }
    public void playNext(){
        if(videoService == null) return;
        videoService.handleAction(VideoService.NEXT_ACTION);
    }
    public void playPrevious(){
        if(videoService == null) return;
        videoService.handleAction(VideoService.PREVIOUS_ACTION);
    }
    public void closeNotification(){
        if(videoService == null) return;
        videoService.closeBackgroundMode();
    }
    public void openNotification(){

        if(videoService == null) return;
        videoService.openBackgroundMode();
    }
    public void pausePlay(){
        if(videoService == null) return;
        videoService.handleAction(VideoService.TOGGLEPAUSE_ACTION);
    }
    public void createShuffle(){
        if(videoService == null) return;
        videoService.createShuffleArray();
    }
    public int getCurrentPosition(){
        if(videoService == null) return  - 1;
        return videoService.getCurrentPosition();
    }

}
