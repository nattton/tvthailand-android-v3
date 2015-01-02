package com.makathon.tvthailand.adapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.dao.section.CategoryItemDao;
import com.makathon.tvthailand.manager.SectionManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter {
	private ImageLoader imageLoader;

	public CategoryAdapter(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	private static final class ViewHolder {
		TextView title;
		NetworkImageView thumbnail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView =  mInflater.inflate(R.layout.cate_grid_item, parent, false);
			holder = new ViewHolder();

			holder.title = (TextView) convertView
					.findViewById(R.id.tv_label_cate);
			holder.thumbnail = (NetworkImageView) convertView
					.findViewById(R.id.thumb_cate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

        CategoryItemDao item = SectionManager.getInstance().getData().getCategories().get(position);
		holder.title.setText(item.getTitle());
		holder.thumbnail.setImageUrl(item.getThumbnail(), imageLoader);
		return convertView;
	}

	@Override
	public int getCount() {
        if (SectionManager.getInstance().getData() == null
                || SectionManager.getInstance().getData().getCategories() == null)
            return 0;
		return SectionManager.getInstance().getData().getCategories().size();
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
