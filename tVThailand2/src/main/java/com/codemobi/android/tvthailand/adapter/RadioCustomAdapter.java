package com.codemobi.android.tvthailand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.view.SquareImageView;
import com.codemobi.android.tvthailand.dao.section.RadioItemDao;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleArrayAdapter;

import java.util.List;


public class RadioCustomAdapter extends StickyGridHeadersSimpleArrayAdapter<RadioItemDao> {

    private LayoutInflater mInflater;
	private int mHeaderResId;
    private int mItemResId;

	public RadioCustomAdapter(Context context, List<RadioItemDao> items, int headerResId, int itemResId) {
		super(context, items, headerResId, itemResId);
		this.mInflater = LayoutInflater.from(context);
        this.mHeaderResId = headerResId;
        this.mItemResId = itemResId;
	}

	protected class HeaderViewHolder {
		public TextView textView;
	}

	protected class ViewHolder {
		public TextView textView;
		public SquareImageView imageView;
	}

	@Override
	public long getHeaderId(int position) {
		return getItem(position).getHeaderId();
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(mHeaderResId, parent, false);
			holder = new HeaderViewHolder();
			holder.textView = (TextView)convertView.findViewById(android.R.id.text1);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder)convertView.getTag();
		}
		
		holder.textView.setText(getItem(position).getCategory());
		
		return convertView;
	}
    
   @Override
	public View getView(int position, View convertView, ViewGroup parent) {
	   ViewHolder holder;
	   if (convertView == null) {
		   convertView = mInflater.inflate(mItemResId, parent, false);
		   holder = new ViewHolder();
		   holder.textView = (TextView) convertView.findViewById(R.id.radio_textview);
		   holder.imageView = (SquareImageView) convertView.findViewById(R.id.radio_image);
		   convertView.setTag(holder);
	   } else {
		   holder = (ViewHolder)convertView.getTag();
	   }

	   holder.textView.setText(getItem(position).getTitle());
	   Glide.with(parent.getContext())
			   .load(getItem(position).getThumbnail())
			   .placeholder(R.drawable.ic_tvthailand_120)
			   .crossFade()
			   .fitCenter()
			   .into(holder.imageView);
	   
	   return convertView;
	}

}
