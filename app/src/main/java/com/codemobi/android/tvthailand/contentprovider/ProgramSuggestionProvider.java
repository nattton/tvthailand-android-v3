package com.codemobi.android.tvthailand.contentprovider;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.provider.SearchRecentSuggestions;

import com.codemobi.android.tvthailand.BuildConfig;

public class ProgramSuggestionProvider extends SearchRecentSuggestionsProvider {
	public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".contentprovider.ProgramSuggestionProvider";
	public static final int MODE = DATABASE_MODE_QUERIES;
	
	public ProgramSuggestionProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
