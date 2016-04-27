package com.codemobi.android.tvthailand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.datasource.Episode;
import com.codemobi.android.tvthailand.datasource.Episodes;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nattapong on 9/28/15 AD.
 */
public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {

    private OnTapListener onTapListener;

    private Episodes episodes;
    private HashMap<String, Integer> videoTypeMap = new HashMap<>();

    public EpisodeAdapter(Episodes episodes) {
        this.episodes = episodes;

        videoTypeMap.put("0", com.codemobi.android.tvthailand.R.drawable.ic_youtube);
        videoTypeMap.put("1", com.codemobi.android.tvthailand.R.drawable.ic_dailymotion);
        videoTypeMap.put("11", com.codemobi.android.tvthailand.R.drawable.ic_chrome);
        videoTypeMap.put("12", com.codemobi.android.tvthailand.R.drawable.ic_player);
        videoTypeMap.put("13", com.codemobi.android.tvthailand.R.drawable.ic_player);
        videoTypeMap.put("14", com.codemobi.android.tvthailand.R.drawable.ic_player);
        videoTypeMap.put("15", com.codemobi.android.tvthailand.R.drawable.ic_player);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_episode_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTapListener != null)
                    onTapListener.onTapView(position);
            }
        });

        Episode item = episodes.get(position);
        if (videoTypeMap.containsKey(item.getSrcType())) {
            holder.mediaThumbnail.setImageResource(videoTypeMap.get(item.getSrcType()));
        }
        holder.title.setText(item.getTitle());
        holder.aired.setText(item.getDate());
        holder.viewCount.setText(item.getViewCount());
        if (!item.getParts().equals("")) {
            holder.layoutParts.setVisibility(View.VISIBLE);
            holder.tvParts.setText(item.getParts());
        }
        else {
            holder.layoutParts.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.media_thumbnail) ImageView mediaThumbnail;
        @BindView(R.id.tv_label_ep) TextView title;
        @BindView(R.id.tv_on_air_ep) TextView aired;
        @BindView(R.id.view_count_ep) TextView viewCount;
        @BindView(R.id.part_updated_ll) LinearLayout layoutParts;
        @BindView(R.id.num_part) TextView tvParts;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
