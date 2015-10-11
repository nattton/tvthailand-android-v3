package com.codemobi.android.tvthailand.datasource;

public interface OnLoadDataListener {
	void onLoadStart();
	void onLoadFinished();
	void onLoadError(String error);
}
