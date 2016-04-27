package com.codemobi.android.tvthailand.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.datasource.Part;
import com.codemobi.android.tvthailand.datasource.Parts;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nattapong on 9/29/15 AD.
 */
public class PartAdapter extends RecyclerView.Adapter<PartAdapter.ViewHolder> {
    private OnTapListener onTapListener;
    private Context context;
    private Parts parts;

    public PartAdapter(Context context, Parts parts) {
        this.context = context;
        this.parts = parts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_part_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (onTapListener != null)
                    onTapListener.onTapView(position);
            }
        });

        Part item = parts.get(position);
        holder.title.setText(item.getTitle());
        Glide.with(context)
                .load(item.getThumbnail())
                .placeholder(R.drawable.ic_tvthailand_show_placeholder)
                .crossFade()
                .fitCenter()
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return parts.size();
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail) ImageView thumbnail;
        @BindView(R.id.title) TextView title;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
