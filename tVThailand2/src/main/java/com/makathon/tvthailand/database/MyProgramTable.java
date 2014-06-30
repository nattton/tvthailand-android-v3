package com.makathon.tvthailand.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyProgramTable extends ProgramTable {
	 // Database table
	public static final String TABLE_NAME = "my_program";
	public static class MyProgramColumns {
		  public static final String IS_FAV = "is_fav";
		  public static final String MY_VOTE = "my_vote";
		  public static final String TIME_VIEWED = "time_viewed";
    }
	
	  public static void onCreate(SQLiteDatabase database) {
		  
		  StringBuilder sb = new StringBuilder();
	        sb.append("CREATE TABLE " + TABLE_NAME + " (");
	        sb.append(ProgramColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
	        sb.append(ProgramColumns.PROGRAM_ID + " TEXT UNIQUE NOT NULL, ");
	        sb.append(ProgramColumns.TITLE + " TEXT NOT NULL, ");
	        sb.append(ProgramColumns.DESCRIPTION + " TEXT, ");
	        sb.append(ProgramColumns.THUMBNAIL + " TEXT, ");
	        sb.append(ProgramColumns.RATING + " REAL, ");
	        sb.append(MyProgramColumns.IS_FAV + " INTEGER, ");
	        sb.append(MyProgramColumns.MY_VOTE + " INTEGER, ");
	        sb.append(MyProgramColumns.TIME_VIEWED + " INTEGER ");
	        sb.append(");");
	    database.execSQL(sb.toString());
	  }

	  public static void onUpgrade(SQLiteDatabase database, int oldVersion,int newVersion) {
	    Log.w(MyProgramTable.class.getName(), "Upgrading database from version "
	        + oldVersion + " to " + newVersion
	        + ", which will destroy all old data");
	    database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	    onCreate(database);
	  }
}
