package com.trinhbk.lecturelivestream.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.model.Video;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by TrinhBK on 9/21/2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ItemBaseViewHolder> {

    private List<Video> data;
    private OnClickVideo onClickVideo;
//    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public VideoAdapter(List<Video> data, OnClickVideo onClickVideo) {
        this.data = new ArrayList<>();
        this.data = data;
        this.onClickVideo = onClickVideo;
    }

    @Override
    public ItemBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_home_video, parent, false);
        return new ItemBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemBaseViewHolder holder, int position) {
        Video video = data.get(position);
//        viewBinderHelper.bind(holder.swipeRevealLayout, dataObject.getId());
        holder.tvVideoName.setText(video.getVideoName());
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy hh:mm");
        calendar.setTimeInMillis(video.getVideoDate());
        holder.tvVideoDetail.setText(format.format(calendar.getTime()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void move(int from, int to) {
        Video prev = data.remove(from);
        data.add(to > from ? to - 1 : to, prev);
        notifyItemMoved(from, to);
    }

    public class ItemBaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View mViewContent;
        public View mActionContainer;
        TextView tvVideoName;
        TextView tvVideoDetail;
        TextView tvEdit;
        TextView tvShare;
        TextView tvDelete;

        public ItemBaseViewHolder(View itemView) {
            super(itemView);
            mViewContent = itemView.findViewById(R.id.llItemVideoContent);
            mActionContainer = itemView.findViewById(R.id.llItemVideoAction);
            tvVideoName = itemView.findViewById(R.id.tvItemHome);
            tvVideoDetail = itemView.findViewById(R.id.tvItemHomeDetail);
            tvEdit = itemView.findViewById(R.id.tvItemVideoCrop);
//            tvShare = itemView.findViewById(R.id.tvItemVideoShare);
            tvDelete = itemView.findViewById(R.id.tvItemVideoDelete);
            mViewContent.setOnClickListener(this);
            tvEdit.setOnClickListener(this);
//            tvShare.setOnClickListener(this);
            tvDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvItemVideoCrop:
                    onClickVideo.onItemCropVideo(getAdapterPosition());
                    break;
//                case R.id.tvItemVideoShare:
//                    onClickVideo.onItemShareVideo(getAdapterPosition());
//                    break;
                case R.id.tvItemVideoDelete:
                    onClickVideo.onItemDeleteVideo(getAdapterPosition());
                    break;
                case R.id.llItemVideoContent:
                    onClickVideo.onItemWatchVideo(getAdapterPosition());
                    break;
            }
        }
    }

    public interface OnClickVideo {

        void onItemWatchVideo(int position);

        void onItemCropVideo(int position);

        void onItemShareVideo(int position);

        void onItemDeleteVideo(int position);
    }
}