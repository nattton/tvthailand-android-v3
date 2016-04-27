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
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.datasource.Programs;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nattapong on 9/28/15 AD.
 */
public class ProgramListAdapter extends RecyclerView.Adapter<ProgramListAdapter.ViewHolder> {
    private OnTapListener onTapListener;
    private Context context;
    private Programs programs;

    public ProgramListAdapter(Context context, Programs programs) {
        this.context = context;
        this.programs = programs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_program_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (onTapListener != null)
                    onTapListener.onTapView(position);
            }
        });
        Program item = programs.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        Glide.with(context)
                .load(item.getThumbnail())
                .centerCrop()
                .placeholder(R.drawable.ic_tvthailand_show_placeholder)
                .crossFade()
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.description) TextView description;
        @BindView(R.id.thumbnail) ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
