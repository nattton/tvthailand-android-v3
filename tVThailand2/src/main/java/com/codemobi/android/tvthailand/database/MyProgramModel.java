package com.codemobi.android.tvthailand.database;

import com.codemobi.android.tvthailand.datasource.Program;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class MyProgramModel extends ProgramModel {
	protected boolean isFav = false;
	protected int myVote = 0;
	protected long timeViewed = 0;

	public MyProgramModel() {
		super();
	}

	public boolean isFav() {
		return isFav;
	}

	public void setFav(boolean isFav) {
		this.isFav = isFav;
	}

	public int getMyVote() {
		return myVote;
	}

	public void setMyVote(int myVote) {
		this.myVote = myVote;
	}

	public long getTimeViewed() {
		return timeViewed;
	}

	public void setTimeViewed(long timeViewed) {
		this.timeViewed = timeViewed;
	}


	@Override
	public int getId() {
		return id;
	}

	@Override
	public void fromCursor(Cursor cursor, Context context) {
		super.fromCursor(cursor, context);
		this.isFav = (cursor.getInt(cursor
				.getColumnIndex(MyProgramTable.MyProgramColumns.IS_FAV)) == 1);
		this.myVote = cursor.getInt(cursor
				.getColumnIndex(MyProgramTable.MyProgramColumns.MY_VOTE));
		this.timeViewed = cursor.getLong(cursor
				.getColumnIndex(MyProgramTable.MyProgramColumns.TIME_VIEWED));

	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = super.toContentValues();
		values.put(MyProgramTable.MyProgramColumns.IS_FAV, ((this.isFav) ? 1 : 0));
		values.put(MyProgramTable.MyProgramColumns.MY_VOTE, this.myVote);
		values.put(MyProgramTable.MyProgramColumns.TIME_VIEWED,
				this.timeViewed);
		
		return values;
	}

	public static MyProgramModel newInstance(Cursor cursor, Context context) {
		MyProgramModel program = new MyProgramModel();
		program.fromCursor(cursor, context);
		return program;
	}
	
	public Program toProgram() {
		return new Program(programId, title, thumbnail, description, "", 0, "", "");
	}
}
