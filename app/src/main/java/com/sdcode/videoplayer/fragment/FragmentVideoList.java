package com.sdcode.videoplayer.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdcode.videoplayer.GlobalVar;
import com.sdcode.videoplayer.BaseActivity;
import com.sdcode.videoplayer.customizeUI.WrapContentGridLayoutManager;
import com.sdcode.videoplayer.customizeUI.WrapContentLinearLayoutManager;
import com.sdcode.videoplayer.FirstActivity;
import com.sdcode.videoplayer.FolderDetailActivity;
import com.sdcode.videoplayer.kxUtil.PreferencesUtility;
import com.sdcode.videoplayer.PlayVideoActivity;
import com.sdcode.videoplayer.R;
import com.sdcode.videoplayer.SearchActivity;
import com.sdcode.videoplayer.adapter.VideoAdapter;
import com.sdcode.videoplayer.kxUtil.kxUtils;
import com.sdcode.videoplayer.video.VideoItem;
import com.sdcode.videoplayer.video.VideoLoadListener;
import com.sdcode.videoplayer.video.VideoLoader;

import java.util.ArrayList;
import java.util.Collections;


public class FragmentVideoList extends MyFragment{

    RecyclerView recyclerView;
    VideoAdapter videoAdapter;
    VideoLoader videoLoader;
    ArrayList<VideoItem> videoItems = new ArrayList<>();
    public FragmentVideoList() {
        // Required empty public constructor
    }

    public static FragmentVideoList newInstance(String param1, String param2) {
        FragmentVideoList fragment = new FragmentVideoList();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        setLayoutManager(getCurrentOrientation());
        videoAdapter = new VideoAdapter(getActivity());
        recyclerView.setAdapter(videoAdapter);
        loadEveryThing();
        return rootView;
    }
    @Override
    public void reloadFragment(int orientation){
        doLayoutChange(orientation);
    }
    @Override
    public int getTotalSelected(){
        if(videoAdapter == null)
            return 0;
        return videoAdapter.getTotalVideoSelected();
    }
    @Override
    public void playItemSelected(){
        ArrayList<VideoItem> videoItems = videoAdapter.getVideoItemsSelected();
        if(videoItems.size() > 0 && getActivity() != null){
            GlobalVar.getInstance().videoItemsPlaylist = videoItems;
            GlobalVar.getInstance().playingVideo = videoItems.get(0);
            if(!GlobalVar.getInstance().isPlayingAsPopup()){
                GlobalVar.getInstance().videoService.playVideo(GlobalVar.getInstance().seekPosition,false);
                Intent intent = new Intent(getActivity(), PlayVideoActivity.class);
                getActivity().startActivity(intent);
                if(GlobalVar.getInstance().videoService != null)
                    GlobalVar.getInstance().videoService.releasePlayerView();
            }else {
                ((BaseActivity) getActivity()).showFloatingView(getActivity(),true);
            }
        }
    }
    @Override
    public void sortAZ(){
        if(videoItems != null && videoItems.size() > 0){
            videoItems = sortVideoAZ(videoItems);
            videoAdapter.updateData(videoItems);
        }


    }

    @Override
    public void sortZA(){
        if(videoItems != null && videoItems.size() > 0){
            videoItems = sortVideoZA(videoItems);
            videoAdapter.updateData(videoItems);
        }
    }

    @Override
    public void sortSize(){
        if(videoItems != null && videoItems.size() > 0){
            videoItems = sortSongSize();
            videoAdapter.updateData(videoItems);
        }
    }
    @Override
    public void shareSelected(){
        if(videoAdapter == null || getActivity() == null) return;
        ArrayList<VideoItem> videoItems = videoAdapter.getVideoItemsSelected();
        kxUtils.shareMultiVideo(getActivity(),videoItems);
    }
    @Override
    public void deleteSelected(){
        videoAdapter.deleteListVideoSelected();
    }
    @Override
    public void updateVideoList(ArrayList<VideoItem> videoItems){
        if(videoItems == null) return;
        this.videoItems = videoItems;
        GlobalVar.getInstance().folderItem.setVideoItems(videoItems);
        GlobalVar.getInstance().isNeedRefreshFolder = true;
    }
    @Override
    public void releaseUI(){
        for(VideoItem videoItem:videoItems){
            videoItem.setSelected(false);
        }
        videoAdapter.updateData(videoItems);
    }
    private void doLayoutChange(int orientation){
        if(getActivity() instanceof FirstActivity || getActivity() instanceof FolderDetailActivity) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (PreferencesUtility.getInstance(getActivity()).isAlbumsInGrid()) {
                    recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(), 4));
                } else {
                    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                }
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (PreferencesUtility.getInstance(getActivity()).isAlbumsInGrid()) {
                    recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(), 2));
                } else {
                    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                }
            }
            videoAdapter.updateData(videoItems);
        }
    }
    private int getCurrentOrientation(){
        return getResources().getConfiguration().orientation;
    }
    private void setLayoutManager(int orientation) {
        if(getActivity() instanceof FirstActivity || getActivity() instanceof FolderDetailActivity) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (PreferencesUtility.getInstance(getActivity()).isAlbumsInGrid()) {
                    recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(), 4));
                } else {
                    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                }
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (PreferencesUtility.getInstance(getActivity()).isAlbumsInGrid()) {
                    recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(), 2));
                } else {
                    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                }
            }
        }
    }

    private void loadEveryThing(){
        if(getActivity() instanceof FirstActivity) {
            videoLoader = new VideoLoader(getActivity());
            videoLoader.loadDeviceVideos(new VideoLoadListener() {
                @Override
                public void onVideoLoaded(final ArrayList<VideoItem> items) {
                    videoItems = items;
                    GlobalVar.getInstance().allVideoItems = videoItems;
                    videoAdapter.updateData(items);

                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        }else if(getActivity() instanceof FolderDetailActivity){
            videoItems = GlobalVar.getInstance().folderItem.getVideoItems();
            videoAdapter.updateData(videoItems);
        }else if(getActivity() instanceof SearchActivity){

        }
    }

    private ArrayList<VideoItem> sortVideoAZ(ArrayList<VideoItem> videoItems){
        ArrayList<VideoItem> m_videos = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < videoItems.size();i++){
            names.add(videoItems.get(i).getFolderName() + "_" + videoItems.get(i).getPath());
        }
        Collections.sort(names, String::compareToIgnoreCase);

        for(int i = 0; i < names.size(); i ++){
            String path = names.get(i);
            for (int j = 0; j < videoItems.size();j++){
                if(path.equals(videoItems.get(j).getFolderName() + "_" + videoItems.get(j).getPath())){
                    m_videos.add(videoItems.get(j));
                }
            }
        }


        return m_videos;
    }
    private ArrayList<VideoItem> sortVideoZA(ArrayList<VideoItem> videoItems){
        ArrayList<VideoItem> m_videos = sortVideoAZ(videoItems);
        Collections.reverse(m_videos);

        return m_videos;

    }

    private ArrayList<VideoItem> sortSongSize() throws NumberFormatException{
        ArrayList<VideoItem> m_videos = videoItems;
        for (int i = 0; i < m_videos.size() -1;i++) {
            for(int j = 0; j < m_videos.size() - 1 - i; j++){

                if(m_videos.get(j).getFileSizeAsFloat() < m_videos.get(j+1).getFileSizeAsFloat()){
                    Collections.swap(m_videos,j,j+1);
                }
            }
        }

        return m_videos;

    }
}
