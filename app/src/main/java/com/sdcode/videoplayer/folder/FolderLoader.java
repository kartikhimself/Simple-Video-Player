package com.sdcode.videoplayer.folder;

import com.sdcode.videoplayer.video.VideoItem;

import java.io.File;
import java.util.ArrayList;

public class FolderLoader {
    public static ArrayList<FolderItem>  getFolderList(ArrayList<VideoItem> videoItems){
        File file;
        ArrayList<FolderItem> folderItems = new ArrayList<>();
        if(videoItems != null && videoItems.size() > 0){
            for(int i = 0; i < videoItems.size();i ++){
                file = new File(videoItems.get(i).getPath());

                String filePath = file.getParent();
                String fileName = "Unknow Folder";
                File _parentFile = file.getParentFile();
                if (_parentFile.exists()) {
                    fileName = _parentFile.getName();
                }
                if(!isFileExits(folderItems,filePath)){
                    folderItems.add(new FolderItem(fileName,filePath));
                }
                for (FolderItem item :folderItems) {
                    if(item.getFolderPath().contains(filePath)){
                        item.getVideoItems().add(videoItems.get(i));
                    }

                }

            }
        }

        return folderItems;
    }
    private static boolean isFileExits(ArrayList<FolderItem> folderItems, String path){
        for (FolderItem item :folderItems) {
            if(item.getFolderPath().contains(path))
                return true;
        }
        return false;
    }


}
