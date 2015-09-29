package com.codemobi.android.tvthailand.otv.adapter;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.adapter.OnTapListener;
import com.codemobi.android.tvthailand.otv.model.OTVPart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class OTVPartAdapter extends RecyclerView.Adapter<OTVPartAdapter.ViewHolder> {

	private OnTapListener onTapListener;
	private Context context;
	private ArrayList<OTVPart> parts;

	public OTVPartAdapter(Context context, ArrayList<OTVPart> parts) {
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

		OTVPart item = parts.get(position);
		holder.title.setText(item.getNameTh());
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
		ImageView thumbnail;
		TextView title;
		public ViewHolder(View itemView) {
			super(itemView);
			title = (TextView)itemView.findViewById(R.id.title);
			thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
		}
	}
}
