package com.codemobi.android.tvthailand.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.database.ProgramModel;

public class ProgramCursorAdapter extends CursorAdapter {

	public ProgramCursorAdapter(Context context) {
		super(context, null, 0);
	}

	private static final class ViewHolder {
		public TextView label;
		public TextView description;
		public ImageView thumbnail;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		ProgramModel program = ProgramModel.newInstance(cursor, context);
		viewHolder.label.setText(program.getTitle());
		viewHolder.description.setText(program.getDescription());
		viewHolder.thumbnail.setVisibility(View.VISIBLE);
		Glide.with(context)
				.load(program.getThumbnail())
				.placeholder(R.drawable.ic_tvthailand_show_placeholder)
				.crossFade()
				.fitCenter()
				.into(viewHolder.thumbnail);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.whatnew_grid_item, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.label = (TextView) view.findViewById(R.id.title);
		viewHolder.description = (TextView) view.findViewById(R.id.description);
		viewHolder.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

		view.setTag(viewHolder);
		return view;
	}
}
