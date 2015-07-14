package com.codemobi.android.tvthailand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.dao.section.RadioItemDao;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleArrayAdapter;

import java.util.List;


public class RadioCustomAdapter extends StickyGridHeadersSimpleArrayAdapter<RadioItemDao> {

    private int mHeaderResId;

    private LayoutInflater mInflater;

    private int mItemResId;

    private ImageLoader imageLoader;



	public RadioCustomAdapter(Context context, List<RadioItemDao> items,
			int headerResId, int itemResId, ImageLoader mImageLoader) {
		super(context, items, headerResId, itemResId);
		this.mInflater = LayoutInflater.from(context);
        this.mHeaderResId = headerResId;
        this.mItemResId = itemResId;
        this.imageLoader = mImageLoader;
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
		   holder.imageView = (NetworkImageView) convertView.findViewById(R.id.radio_image);
		   convertView.setTag(holder);
	   } else {
		   holder = (ViewHolder)convertView.getTag();
	   }
	   
	   holder.textView.setText(getItem(position).getTitle());
	   if (getItem(position).getThumbnail() != null) {
			holder.imageView.setImageUrl(getItem(position).getThumbnail(), imageLoader);
		} else {
			holder.imageView.setImageResource(R.drawable.ic_tvthailand_120);
		}
	   
	   return convertView;
	}


    protected class HeaderViewHolder {
        public TextView textView;
    }
    
    protected class ViewHolder {
        public TextView textView;
        public NetworkImageView imageView;
    }

}
