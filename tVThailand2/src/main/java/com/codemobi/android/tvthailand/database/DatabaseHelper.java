package com.codemobi.android.tvthailand.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "tvthailand.db";
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		WhatsNewTable.onCreate(database);
		ProgramTable.onCreate(database);
		MyProgramTable.onCreate(database);
		EpisodeTable.onCreate(database);
	}

	// Method is called during an upgrade of the database,
	// e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		WhatsNewTable.onUpgrade(database, oldVersion, newVersion);
		ProgramTable.onUpgrade(database, oldVersion, newVersion);
		MyProgramTable.onUpgrade(database, oldVersion, newVersion);
		EpisodeTable.onUpgrade(database, oldVersion, newVersion);
	}

}
