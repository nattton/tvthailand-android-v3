package com.codemobi.android.tvthailand.contentprovider;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.provider.SearchRecentSuggestions;

public class ProgramSuggestionProvider extends SearchRecentSuggestionsProvider {
	private static final String AUTHORITY = "com.codemobi.android.tvthailand.contentprovider.ProgramSuggestionProvider";
	
	public static SearchRecentSuggestions getBridge(Context context) {
		return new SearchRecentSuggestions(context, AUTHORITY, DATABASE_MODE_QUERIES);
	}
	
	public ProgramSuggestionProvider() {
		super();
		
		setupSuggestions(AUTHORITY, DATABASE_MODE_QUERIES);
	}
}
