package com.codemobi.android.tvthailand.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.activity.ProgramActivity;
import com.codemobi.android.tvthailand.dao.section.CategoryItemDao;
import com.codemobi.android.tvthailand.manager.SectionManager;
import com.codemobi.android.tvthailand.utils.Contextor;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>
{
	private OnTapListener onTapListener;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.title) TextView mTitle;
		@BindView(R.id.thumbnail) ImageView mThumbnail;

		ViewHolder (View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	public CategoryAdapter(Context context)
	{

	}

	@Override
	public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.cardview_category_item, parent, false);

		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position)
	{
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onTapListener != null)
					onTapListener.onTapView(position);
			}
		});

		CategoryItemDao item = SectionManager.getInstance().getData().getCategories().get(position);
		holder.mTitle.setText(item.getTitle());
		Glide.with(Contextor.getInstance().getContext())
				.load(item.getThumbnail())
				.centerCrop()
				.placeholder(R.drawable.ic_cate_empty)
				.crossFade()
				.into(holder.mThumbnail);
	}

	@Override
	public int getItemCount()
	{
		if (SectionManager.getInstance().getData() == null
				|| SectionManager.getInstance().getData().getCategories() == null)
			return 0;
		return SectionManager.getInstance().getData().getCategories().size();
	}

	public void setOnTapListener (OnTapListener onTapListener)
	{
		this.onTapListener = onTapListener;
	}
}
