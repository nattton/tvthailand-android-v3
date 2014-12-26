package com.makathon.tvthailand.adapter;

import com.makathon.tvthailand.R;
import com.makathon.tvthailand.dao.section.ChannelItemDao;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.makathon.tvthailand.manager.SectionManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChannelAdapter extends BaseAdapter{
	private ImageLoader imageLoader;
	
	public ChannelAdapter(ImageLoader mImageLoader) {

        imageLoader = mImageLoader;
	}

	private static final class ViewHolder {
		TextView channel_tv;
		NetworkImageView channel_icon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.channel_grid_item, parent, false);
            holder = new ViewHolder();
			holder.channel_tv = (TextView) convertView.findViewById(R.id.tv_channel);
			holder.channel_icon = (NetworkImageView) convertView.findViewById(R.id.imv_channel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		ChannelItemDao item = SectionManager.getInstance().getData().getChannels()[position];
		holder.channel_tv.setText(item.getTitle());
		if (item.getThumbnail() != null) {
			holder.channel_icon.setImageUrl(item.getThumbnail(), imageLoader);
		} else {
			holder.channel_icon.setImageResource(R.drawable.ic_tvthailand_120);
		}
		
		return convertView;
	}

    @Override
    public int getCount() {
        if (SectionManager.getInstance().getData() == null
                || SectionManager.getInstance().getData().getChannels() == null)
            return 0;
        return SectionManager.getInstance().getData().getChannels().length;
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
