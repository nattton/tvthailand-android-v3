package com.makathon.tvthailand.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class EpisodeTable  {
	 // Database table
	public static final String TABLE_NAME = "episode";
	public static class EpisodeColumns implements BaseColumns {
		public static final String PROGRAM_ID = "program_id";
		  public static final String EP_ID = "ep_id";
		  public static final String EP = "ep";
		  public static final String TITLE = "title";
		  public static final String VIDEO_ENCRYPT = "video_encrypt";
		  public static final String SRC_TYPE = "src_type";
		  public static final String DATE = "date";
		  public static final String VIEW_COUNT = "view_count";
		  public static final String PARTS = "parts";
		  public static final String PASSWORD = "password";
		  
    }
	
	  public static void onCreate(SQLiteDatabase database) {
		  StringBuilder sb = new StringBuilder();
	        sb.append("CREATE TABLE " + TABLE_NAME + " (");
	        sb.append(EpisodeColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
	        sb.append(EpisodeColumns.PROGRAM_ID + " TEXT NOT NULL, ");
	        sb.append(EpisodeColumns.EP_ID + " TEXT UNIQUE NOT NULL, ");
	        sb.append(EpisodeColumns.EP + " INTEGER, ");
	        sb.append(EpisodeColumns.TITLE + " TEXT NOT NULL, ");
	        sb.append(EpisodeColumns.VIDEO_ENCRYPT + " TEXT NOT NULL, ");
	        sb.append(EpisodeColumns.SRC_TYPE + " TEXT NOT NULL, ");
	        sb.append(EpisodeColumns.DATE + " TEXT, ");
	        sb.append(EpisodeColumns.VIEW_COUNT + " TEXT, ");
	        sb.append(EpisodeColumns.PARTS + " TEXT, ");
	        sb.append(EpisodeColumns.PASSWORD + " TEXT ");
	        sb.append(");");
	    database.execSQL(sb.toString());
	  }

	  public static void onUpgrade(SQLiteDatabase database, int oldVersion,int newVersion) {
	    Log.w(EpisodeTable.class.getName(), "Upgrading database from version "
	        + oldVersion + " to " + newVersion
	        + ", which will destroy all old data");
	    database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	    onCreate(database);
	  }
}
