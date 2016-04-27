package com.codemobi.android.tvthailand.otv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.adapter.OnTapListener;
import com.codemobi.android.tvthailand.otv.model.OTVEpisode;
import com.codemobi.android.tvthailand.otv.model.OTVEpisodes;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nattapong on 9/28/15 AD.
 */
public class OTVEpisodeAdapter extends RecyclerView.Adapter<OTVEpisodeAdapter.ViewHolder> {

    private OnTapListener onTapListener;
    private Context context;
    private OTVEpisodes episodes;
    private String logo;

    public OTVEpisodeAdapter(Context context, OTVEpisodes episodes, String logo) {
        this.context = context;
        this.episodes = episodes;
        this.logo = logo;
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

        OTVEpisode episode = episodes.get(position);
        holder.layoutParts.setVisibility(View.GONE);
        holder.layoutViewCount.setVisibility(View.GONE);

        Glide.with(context)
                .load(logo)
                .centerCrop()
                .placeholder(R.drawable.ic_otv)
                .crossFade()
                .into(holder.mediaThumbnail);

        holder.title.setText(episode.getNameTh() + "  " + episode.getDate());
        holder.aired.setText("Aired : "+episode.getDate());
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.media_thumbnail) ImageView mediaThumbnail;
        @BindView(R.id.tv_label_ep) TextView title;
        @BindView(R.id.tv_on_air_ep) TextView aired;
        @BindView(R.id.view_count_ep) TextView viewCount;
        @BindView(R.id.view_count_ll) LinearLayout layoutViewCount;
        @BindView(R.id.part_updated_ll) LinearLayout layoutParts;
        @BindView(R.id.num_part) TextView tvParts;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
