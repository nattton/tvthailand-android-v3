package com.codemobi.android.tvthailand.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ProgramModel extends ModelBase {
	protected Context context;
	protected int id;
	protected String programId;
	protected String title;
	protected String thumbnail;
	protected String description;
	protected float rating;

	public ProgramModel() {
		super();
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void fromCursor(Cursor cursor, Context context) {
		this.id = cursor.getInt(cursor
				.getColumnIndex(ProgramTable.ProgramColumns._ID));
		this.programId = cursor.getString(cursor
				.getColumnIndex(ProgramTable.ProgramColumns.PROGRAM_ID));
		this.title = cursor.getString(cursor
				.getColumnIndex(ProgramTable.ProgramColumns.TITLE));
		this.thumbnail = cursor.getString(cursor
				.getColumnIndex(ProgramTable.ProgramColumns.THUMBNAIL));
		this.description = cursor.getString(cursor
				.getColumnIndex(ProgramTable.ProgramColumns.DESCRIPTION));
		this.rating = cursor.getFloat(cursor
				.getColumnIndex(ProgramTable.ProgramColumns.RATING));

		this.context = context;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(ProgramTable.ProgramColumns.PROGRAM_ID, this.programId);
		values.put(ProgramTable.ProgramColumns.TITLE, this.title);
		values.put(ProgramTable.ProgramColumns.THUMBNAIL, this.thumbnail);
		values.put(ProgramTable.ProgramColumns.DESCRIPTION, this.description);
		values.put(ProgramTable.ProgramColumns.RATING, this.rating);
		return values;
	}

	public static ProgramModel newInstance(Cursor cursor, Context context) {
		ProgramModel program = new ProgramModel();
		program.fromCursor(cursor, context);
		return program;
	}
}
