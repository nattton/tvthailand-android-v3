package com.codemobi.android.tvthailand.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import com.codemobi.android.tvthailand.BuildConfig;
import com.codemobi.android.tvthailand.database.DatabaseHelper;
import com.codemobi.android.tvthailand.database.EpisodeTable;
import com.codemobi.android.tvthailand.database.EpisodeTable.EpisodeColumns;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class EpisodeContentProvider extends ContentProvider {

	// database
	private DatabaseHelper database;

	// Used for the UriMacher
	private static final int EPISODES = 1;
	private static final int _ID = 2;

	private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".contentprovider.EpisodeContentProvider";

	private static final String BASE_PATH = "episodes";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/episodes";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/episode";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, EPISODES);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", _ID);
	}

	@Override
	public boolean onCreate() {
		database = new DatabaseHelper(getContext());
		return database != null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(EpisodeTable.TABLE_NAME);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case EPISODES:
			break;
		case _ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(EpisodeColumns._ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@SuppressLint("NewApi")
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id;
		switch (uriType) {
		case EPISODES:
			id = sqlDB.insertWithOnConflict(EpisodeTable.TABLE_NAME, null,
					values, SQLiteDatabase.CONFLICT_REPLACE);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		if (id == -1) {
			id = sqlDB.update(EpisodeTable.TABLE_NAME, values, null, null);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(CONTENT_URI + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted;
		switch (uriType) {
		case EPISODES:
			rowsDeleted = sqlDB.delete(EpisodeTable.TABLE_NAME, selection,
					selectionArgs);
			break;
		case _ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(EpisodeTable.TABLE_NAME,
						EpisodeColumns._ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(EpisodeTable.TABLE_NAME,
						EpisodeColumns._ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated;
		switch (uriType) {
		case EPISODES:
			rowsUpdated = sqlDB.update(EpisodeTable.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		case _ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(EpisodeTable.TABLE_NAME, values,
						EpisodeColumns._ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(EpisodeTable.TABLE_NAME, values,
						EpisodeColumns._ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		String[] available = { EpisodeColumns._ID, EpisodeColumns.PROGRAM_ID,
				EpisodeColumns.EP_ID, EpisodeColumns.TITLE,
				EpisodeColumns.VIDEO_ENCRYPT, EpisodeColumns.SRC_TYPE,
				EpisodeColumns.DATE, EpisodeColumns.VIEW_COUNT,
				EpisodeColumns.PARTS, EpisodeColumns.PASSWORD };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<>(
					Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

}
