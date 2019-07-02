package com.sdcode.videoplayer;


import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class PlayerManager{

    GlobalVar mGlobalVar = GlobalVar.getInstance();

    private  SimpleExoPlayer simpleExoPlayer;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    Context context;

    public PlayerManager(Context context) {
        this.context = context;
        initExoPlayer();
    }

    public SimpleExoPlayer getCurrentPlayer(){
        return simpleExoPlayer;
    }
    public SimpleExoPlayer getSimpleExoPlayer(){
        return simpleExoPlayer;
    }
    private void initExoPlayer(){
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getString(R.string.app_name)));
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        simpleExoPlayer =  ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        simpleExoPlayer.setPlayWhenReady(true);

    }

    public void prepare(boolean resetPosition, boolean resetState){

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

//            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(GlobalVar.getInstance().getPath()),
//                    mediaDataSourceFactory, extractorsFactory, null, null);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(mediaDataSourceFactory).setExtractorsFactory(extractorsFactory)
                .createMediaSource(Uri.parse(GlobalVar.getInstance().getPath()));
        simpleExoPlayer.prepare(mediaSource, resetPosition, resetState);

    }
    public boolean getPlayWhenReady(){
        if(simpleExoPlayer == null)
            return false;
        return simpleExoPlayer.getPlayWhenReady();
    }
    public void setPlayWhenReady(boolean value){
        if(simpleExoPlayer == null) return;
        simpleExoPlayer.setPlayWhenReady(value);
    }
    public void setVolume(float volume){
        if(simpleExoPlayer == null ) return;
            simpleExoPlayer.setVolume(volume);
    }
    public void onFullScreen() {
        if (simpleExoPlayer == null) return;
        mGlobalVar.seekPosition = simpleExoPlayer.getCurrentPosition();
        mGlobalVar.isPlaying = simpleExoPlayer.getPlayWhenReady();

    }
    public void releasePlayer(){
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            trackSelector = null;
        }
    }


//    private static MediaQueueItem buildMediaQueueItem(VideoItem videoItem) {
//        String type = MimeTypes.BASE_TYPE_VIDEO + "/" + kxUtils.getFileExtension(videoItem.getPath());
//        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_TV_SHOW);
//        movieMetadata.putString(MediaMetadata.KEY_TITLE, videoItem.getVideoTitle());
//        MediaInfo mediaInfo = new MediaInfo.Builder(videoItem.getPath())
//                .setStreamType(MediaInfo.STREAM_TYPE_NONE).setContentType(type)
//                .setMetadata(movieMetadata).build();
//        return new MediaQueueItem.Builder(mediaInfo).build();
//    }

}
