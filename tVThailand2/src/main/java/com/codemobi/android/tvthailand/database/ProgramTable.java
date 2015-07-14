package com.codemobi.android.tvthailand.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class ProgramTable  {
	 // Database table
	public static final String TABLE_NAME = "program";
	public static class ProgramColumns implements BaseColumns {
		  public static final String PROGRAM_ID = "program_id";
		  public static final String TITLE = "title";
		  public static final String DESCRIPTION = "description";
		  public static final String THUMBNAIL = "thumbnail";
		  public static final String RATING = "rating";
    }
	
	  public static void onCreate(SQLiteDatabase database) {
		  
		  StringBuilder sb = new StringBuilder();
	        sb.append("CREATE TABLE " + TABLE_NAME + " (");
	        sb.append(ProgramColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
	        sb.append(ProgramColumns.PROGRAM_ID + " TEXT UNIQUE NOT NULL, ");
	        sb.append(ProgramColumns.TITLE + " TEXT NOT NULL, ");
	        sb.append(ProgramColumns.DESCRIPTION + " TEXT, ");
	        sb.append(ProgramColumns.THUMBNAIL + " TEXT, ");
	        sb.append(ProgramColumns.RATING + " REAL ");
	        sb.append(");");
	    database.execSQL(sb.toString());
	  }

	  public static void onUpgrade(SQLiteDatabase database, int oldVersion,int newVersion) {
	    Log.w(ProgramTable.class.getName(), "Upgrading database from version "
	        + oldVersion + " to " + newVersion
	        + ", which will destroy all old data");
	    database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	    onCreate(database);
	  }
}
