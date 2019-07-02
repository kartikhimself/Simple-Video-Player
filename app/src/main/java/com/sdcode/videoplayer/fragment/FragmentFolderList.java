package com.sdcode.videoplayer.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdcode.videoplayer.GlobalVar;
import com.sdcode.videoplayer.customizeUI.WrapContentLinearLayoutManager;
import com.sdcode.videoplayer.adapter.FolderAdapter;
import com.sdcode.videoplayer.folder.FolderItem;
import com.sdcode.videoplayer.folder.FolderLoader;
import com.sdcode.videoplayer.R;
import com.sdcode.videoplayer.video.VideoItem;
import com.sdcode.videoplayer.video.VideoLoadListener;
import com.sdcode.videoplayer.video.VideoLoader;


import java.util.ArrayList;
import java.util.Collections;

public class FragmentFolderList extends MyFragment {

    RecyclerView recyclerView;
    FolderAdapter folderAdapter;
    ArrayList<FolderItem> folderItems;
    public FragmentFolderList() {

    }

    public static FragmentFolderList newInstance() {
        FragmentFolderList fragment = new FragmentFolderList();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_folder_list, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        setLayoutManager();
        folderAdapter = new FolderAdapter(getActivity());
        recyclerView.setAdapter(folderAdapter);

        loadEveryThing();
        return rootView;
    }

    private void setLayoutManager() {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    }
    private void loadEveryThing(){
        VideoLoader videoLoader = new VideoLoader(getActivity());
        videoLoader.loadDeviceVideos(new VideoLoadListener() {
            @Override
            public void onVideoLoaded(final ArrayList<VideoItem> items) {
                GlobalVar.getInstance().allVideoItems = items;
                folderItems = FolderLoader.getFolderList(items);
                folderAdapter.updateData(folderItems);
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });

    }
    @Override
    public void sortAZ(){
        if(folderItems != null && folderItems.size() > 0){
            folderItems = sortFolderAZ(folderItems);
            folderAdapter.updateData(folderItems);
        }

    }
    @Override
    public void sortZA(){
        if(folderItems != null && folderItems.size() > 0){
            folderItems = sortFolderZA(folderItems);
            folderAdapter.updateData(folderItems);
        }

    }
    @Override
    public void sortSize(){
        if(folderItems != null && folderItems.size() > 0){
            folderItems = sortFolderNumberSong();
            folderAdapter.updateData(folderItems);
        }

    }
    private ArrayList<FolderItem> sortFolderAZ(ArrayList<FolderItem> folders){
        ArrayList<FolderItem> m_folders = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < folders.size();i++){
            names.add(folders.get(i).getFolderName() + "_" + folders.get(i).getFolderPath());
        }
        Collections.sort(names, String::compareToIgnoreCase);

        for(int i = 0; i < names.size(); i ++){
            String path = names.get(i);
            for (int j = 0; j < folders.size();j++){
                if(path.equals(folders.get(j).getFolderName() + "_" + folders.get(j).getFolderPath())){
                    m_folders.add(folders.get(j));
                }
            }
        }


        return m_folders;
    }
    private ArrayList<FolderItem> sortFolderZA(ArrayList<FolderItem> folders){
        ArrayList<FolderItem> m_folders = sortFolderAZ(folders);
        Collections.reverse(m_folders);

        return m_folders;

    }

    private ArrayList<FolderItem> sortFolderNumberSong(){
        ArrayList<FolderItem> m_folders = folderItems;
        for (int i = 0; i < m_folders.size() -1;i++) {
            for(int j = 0; j < m_folders.size() - 1 - i; j++){
                if(m_folders.get(j).getVideoItems().size() < m_folders.get(j+1).getVideoItems().size()){
                    Collections.swap(m_folders,j,j+1);
                }
            }
        }

        return m_folders;

    }
}