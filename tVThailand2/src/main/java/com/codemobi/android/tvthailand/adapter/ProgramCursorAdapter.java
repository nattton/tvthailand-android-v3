package com.codemobi.android.tvthailand.adapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.database.ProgramModel;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProgramCursorAdapter extends CursorAdapter {
	private ImageLoader imageLoader;

	public ProgramCursorAdapter(Context context, ImageLoader imageLoader) {
		super(context, null, 0);
		this.imageLoader = imageLoader;
	}

	private static final class ViewHolder {
		public TextView label;
		public TextView description;
		public NetworkImageView thumbnail;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		ProgramModel program = ProgramModel.newInstance(cursor, context);
		viewHolder.label.setText(program.getTitle());
		viewHolder.description.setText(program.getDescription());
		viewHolder.thumbnail.setVisibility(View.VISIBLE);
		viewHolder.thumbnail.setImageUrl(program.getThumbnail(), imageLoader);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.whatnew_grid_item, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.label = (TextView) view.findViewById(R.id.title);
		viewHolder.description = (TextView) view.findViewById(R.id.description);
		viewHolder.thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);

		view.setTag(viewHolder);
		return view;
	}
}
