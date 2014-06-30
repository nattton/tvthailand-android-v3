package com.makathon.tvthailand.adapter;

import java.util.HashMap;

import com.makathon.tvthailand.R;
import com.makathon.tvthailand.database.EpisodeModel;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EpisodeCursorAdapter extends CursorAdapter {
	private HashMap<String, Integer> videoTypeMap = new HashMap<String, Integer>();

	public EpisodeCursorAdapter(Context context) {
		super(context, null, 0);
		videoTypeMap.put("0", R.drawable.ic_youtube);
		videoTypeMap.put("1", R.drawable.ic_dailymotion);
		videoTypeMap.put("11", R.drawable.ic_chrome);
		videoTypeMap.put("12", R.drawable.ic_player);
		videoTypeMap.put("13", R.drawable.ic_player);
		videoTypeMap.put("14", R.drawable.ic_player);
		videoTypeMap.put("15", R.drawable.ic_player);
	}

	private static final class ViewHolder {
		public ImageView mediaThumnail;
		public TextView title;
		public TextView aired;
		public TextView viewCount;

		public LinearLayout layoutParts;
		public TextView tvParts;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		EpisodeModel item = EpisodeModel.newInstance(cursor, context);
		if (videoTypeMap.containsKey(item.getSrcType())) {
			holder.mediaThumnail.setImageResource(videoTypeMap.get(item
					.getSrcType()));
		} else {
//			holder.mediaThumnail.setImageResource(R.drawable.ic_error);
		}

		holder.title.setText(item.getTitle());
		holder.aired.setText(item.getDate());
		holder.viewCount.setText(item.getViewCount());
		if (!item.getParts().equals("")) {
			holder.layoutParts.setVisibility(View.VISIBLE);
			holder.tvParts.setText(item.getParts());
		} else {
			holder.layoutParts.setVisibility(View.GONE);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.episode_list_item, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.mediaThumnail = (ImageView) view
				.findViewById(R.id.media_thumnail);
		holder.title = (TextView) view.findViewById(R.id.tv_label_ep);
		holder.aired = (TextView) view.findViewById(R.id.tv_on_air_ep);
		holder.viewCount = (TextView) view.findViewById(R.id.view_count_ep);
		holder.layoutParts = (LinearLayout) view
				.findViewById(R.id.part_updated_ll);
		holder.tvParts = (TextView) view.findViewById(R.id.num_part);
		view.setTag(holder);
		return view;
	}

}
