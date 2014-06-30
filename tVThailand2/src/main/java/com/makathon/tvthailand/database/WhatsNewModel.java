package com.makathon.tvthailand.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class WhatsNewModel extends ProgramModel {
	private String lastEpName;

	public WhatsNewModel() {
		super();
	}

	public String getLastEpName() {
		return lastEpName;
	}

	public void setLastEpName(String lastEpName) {
		this.lastEpName = lastEpName;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void fromCursor(Cursor cursor, Context context) {
		super.fromCursor(cursor, context);
		this.lastEpName = cursor.getString(cursor
				.getColumnIndex(WhatsNewTable.WhatsNewColumns.LAST_EP));
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = super.toContentValues();
		values.put(WhatsNewTable.WhatsNewColumns.LAST_EP, this.lastEpName);
		return values;
	}

	public static WhatsNewModel newInstance(Cursor cursor, Context context) {
		WhatsNewModel program = new WhatsNewModel();
		program.fromCursor(cursor, context);
		return program;
	}
}
