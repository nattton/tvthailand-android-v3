package com.makathon.tvthailand.adapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.database.WhatsNewModel;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WhatsNewCursorAdapter extends CursorAdapter {
	private ImageLoader imageLoader;

	public WhatsNewCursorAdapter(Context context, ImageLoader imageLoader) {
		super(context, null, 0);
		this.imageLoader = imageLoader;
	}

	private static final class ViewHolder {
		public TextView title;
		public TextView description;
		public NetworkImageView thumbnail;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		WhatsNewModel program = WhatsNewModel.newInstance(cursor, context);
		viewHolder.title.setText(program.getTitle());
		viewHolder.description.setText(program.getLastEpName());
		viewHolder.thumbnail.setImageUrl(program.getThumbnail(), imageLoader);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.whatnew_grid_item, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.title = (TextView) view.findViewById(R.id.title);
		viewHolder.title.setSelected(true);
		viewHolder.description = (TextView) view.findViewById(R.id.description);
		viewHolder.description.setSelected(true);
		viewHolder.thumbnail = (NetworkImageView) view
				.findViewById(R.id.thumbnail);
		view.setTag(viewHolder);
		return view;
	}

}
