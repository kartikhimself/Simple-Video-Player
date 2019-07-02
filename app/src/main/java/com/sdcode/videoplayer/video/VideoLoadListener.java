package com.sdcode.videoplayer.video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudamasayuki on 2017/11/22.
 */

public interface VideoLoadListener {

    void onVideoLoaded(ArrayList<VideoItem> videoItems);

    void onFailed(Exception e);
}
