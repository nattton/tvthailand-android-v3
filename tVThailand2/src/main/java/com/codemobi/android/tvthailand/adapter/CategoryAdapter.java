package com.codemobi.android.tvthailand.adapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.dao.section.CategoryItemDao;
import com.codemobi.android.tvthailand.manager.SectionManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter {

	public CategoryAdapter() {
		super();
	}

	private static final class ViewHolder {
		TextView title;
		ImageView thumbnail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView =  mInflater.inflate(com.codemobi.android.tvthailand.R.layout.cate_grid_item, parent, false);
			holder = new ViewHolder();

			holder.title = (TextView) convertView
					.findViewById(com.codemobi.android.tvthailand.R.id.tv_label_cate);
			holder.thumbnail = (ImageView) convertView
					.findViewById(com.codemobi.android.tvthailand.R.id.thumb_cate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

        CategoryItemDao item = SectionManager.getInstance().getData().getCategories().get(position);
		holder.title.setText(item.getTitle());
		Glide.with(parent.getContext())
				.load(item.getThumbnail())
				.placeholder(R.drawable.ic_cate_empty)
				.crossFade()
				.centerCrop()
				.into(holder.thumbnail);
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
