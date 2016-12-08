package com.ouj.bolo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/11/26.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoItemHolder> {

    private String[] videoUrlList;

    public VideoListAdapter(String[] videoUrlList) {
        this.videoUrlList = videoUrlList;
    }

    @Override
    public VideoItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoItemHolder(contentView);
    }

    @Override
    public void onBindViewHolder(VideoItemHolder holder, int position) {
        holder.bindData(videoUrlList[position]);
    }

    @Override
    public int getItemCount() {
        return videoUrlList.length;
    }
}
