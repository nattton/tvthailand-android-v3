package com.makathon.tvthailand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.dao.show.ShowCollectionDao;
import com.makathon.tvthailand.dao.show.ShowItemDao;
import com.makathon.tvthailand.manager.ShowManager;

public class ShowAdapter extends BaseAdapter{
	private ImageLoader imageLoader;

	public ShowAdapter(ImageLoader mImageLoader) {
        imageLoader = mImageLoader;
	}

	private static final class ViewHolder {
		public TextView title;
		public TextView description;
		public NetworkImageView thumbnail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.whatnew_grid_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.title);
            holder.title.setSelected(true);
		    holder.description = (TextView)convertView.findViewById(R.id.description);
		    holder.description.setSelected(true);
		    holder.thumbnail = (NetworkImageView)convertView.findViewById(R.id.thumbnail);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		ShowItemDao item = ShowManager.getInstance().getData().getShows().get(position);
		holder.title.setText(item.getTitle());
		holder.description.setText(item.getDescription());
		holder.thumbnail.setVisibility(View.VISIBLE);
        if (item.getThumbnailURL() != null) {
            holder.thumbnail.setImageUrl(item.getThumbnailURL(), imageLoader);
        } else {
            holder.thumbnail.setImageResource(R.drawable.ic_tvthailand_120);
        }

        return convertView;
	}

	@Override
	public int getCount() {
        if (ShowManager.getInstance().getData() == null ||
                ShowManager.getInstance().getData().getShows() == null) {
            return 0;
        }
		return ShowManager.getInstance().getData().getShows().size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
