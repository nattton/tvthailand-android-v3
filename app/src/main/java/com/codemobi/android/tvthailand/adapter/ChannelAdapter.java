package com.codemobi.android.tvthailand.adapter;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.view.SquareImageView;
import com.codemobi.android.tvthailand.dao.section.ChannelItemDao;
import com.codemobi.android.tvthailand.manager.SectionManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChannelAdapter extends BaseAdapter{
	
	public ChannelAdapter() {

	}

	private static final class ViewHolder {
		TextView channelTextView;
		SquareImageView channelThumbnail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(com.codemobi.android.tvthailand.R.layout.channel_grid_item, parent, false);
            holder = new ViewHolder();
			holder.channelTextView = (TextView) convertView.findViewById(com.codemobi.android.tvthailand.R.id.tv_channel);
			holder.channelThumbnail = (SquareImageView) convertView.findViewById(com.codemobi.android.tvthailand.R.id.imv_channel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		ChannelItemDao item = SectionManager.getInstance().getData().getChannels().get(position);
		holder.channelTextView.setText(item.getTitle());

		Glide.with(parent.getContext())
				.load(item.getThumbnail())
				.placeholder(R.drawable.ic_tvthailand_120)
				.crossFade()
				.fitCenter()
				.into(holder.channelThumbnail);
		
		return convertView;
	}

    @Override
    public int getCount() {
        if (SectionManager.getInstance().getData() == null
                || SectionManager.getInstance().getData().getChannels() == null)
            return 0;
        return SectionManager.getInstance().getData().getChannels().size();
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
