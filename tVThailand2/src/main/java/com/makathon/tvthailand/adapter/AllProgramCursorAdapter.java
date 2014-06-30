package com.makathon.tvthailand.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.jsoup.helper.StringUtil;

import com.makathon.tvthailand.R;
import com.makathon.tvthailand.database.ProgramModel;
import com.makathon.tvthailand.database.ProgramTable;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class AllProgramCursorAdapter extends CursorAdapter implements
		SectionIndexer {
	private final String vowel = "เแไใโ";

	private AlphabetIndexer mAlphabetIndexer;

	private boolean[] separatorAtIndex;

	public AllProgramCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor, 0);
		reIndexer(context, cursor);
		Log.d("AllProgramCursorAdapter", "AllProgramCursorAdapter");
	}

	private void reIndexer(Context context, Cursor cursor) {
		if (cursor == null) {
			mAlphabetIndexer.setCursor(cursor);
			return;
		}
		separatorAtIndex = new boolean[cursor.getCount()];
		char currentChar = 0;
		HashMap<String, Integer> alphaIndexer = new HashMap<String, Integer>();
		if (cursor.moveToFirst()) {
			while (cursor.moveToNext()) {
				int pos = cursor.getPosition();
				ProgramModel program = ProgramModel
						.newInstance(cursor, context);
				char title = checkVowel(program.getTitle()).charAt(0);
				String ch = String.valueOf(title).toUpperCase();

				if (title != currentChar) {
					separatorAtIndex[pos] = true;
				} else {
					separatorAtIndex[pos] = false;
				}
				currentChar = title;

				alphaIndexer.put(ch, pos);
			}
		}
		Set<String> sectionLetters = alphaIndexer.keySet();
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);

		Collections.sort(sectionList);

		String alphabet = StringUtil.join(sectionList, "");
		Log.e("KeySet", alphabet);
		mAlphabetIndexer = new AlphabetIndexer(cursor,
				cursor.getColumnIndex(ProgramTable.ProgramColumns.TITLE),
				alphabet);
		mAlphabetIndexer.setCursor(cursor);
	}

	private String checkVowel(String s) {
		for (int i = 0; i < s.length(); i++) {
			String ch = String.valueOf(s.charAt(i));
			if (!vowel.contains(ch))
				return ch;
		}
		return "";
	}

	private static final class ViewHolder {
		public TextView title;
		public TextView sectionHeader;
	}

	@Override
	public void changeCursor(Cursor cursor) {
		reIndexer(null, cursor);
		Log.d("AllProgramCursorAdapter", "changeCursor");
		super.changeCursor(cursor);
	}

	@Override
	public Cursor swapCursor(Cursor newCursor) {
		Log.d("AllProgramCursorAdapter", "swapCursor");
		return super.swapCursor(newCursor);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		ProgramModel program = ProgramModel.newInstance(cursor, context);
		viewHolder.title.setText(program.getTitle());
		if (separatorAtIndex[cursor.getPosition()]) {
			viewHolder.sectionHeader.setVisibility(View.VISIBLE);
			viewHolder.sectionHeader.setText(checkVowel(program.getTitle()));
		} else {
			viewHolder.sectionHeader.setVisibility(View.GONE);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.all_list_item, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.title = (TextView) view.findViewById(R.id.title);
		viewHolder.sectionHeader = (TextView) view.findViewById(R.id.separator);
		view.setTag(viewHolder);
		return view;
	}

	@Override
	public int getPositionForSection(int section) {
		return mAlphabetIndexer.getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		return mAlphabetIndexer.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		return mAlphabetIndexer.getSections();
	}

}
