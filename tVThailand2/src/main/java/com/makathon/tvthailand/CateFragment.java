package com.makathon.tvthailand;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.TVThailandApp.TrackerName;
import com.makathon.tvthailand.adapter.CategoryAdapter;
import com.makathon.tvthailand.datasource.AppUtility;
import com.makathon.tvthailand.datasource.Categories;
import com.makathon.tvthailand.datasource.Category;
import com.makathon.tvthailand.datasource.Categories.OnCategoryChangeListener;

public class CateFragment extends SherlockFragment implements OnItemClickListener {

	Categories mCategories;
	CategoryAdapter mAdapter;
	private GridView gridview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.cate_grid_view, container,
				false);

		gridview = (GridView) rootView.findViewById(R.id.gridview);
		mCategories = AppUtility.getInstance().getCategories(getActivity());
		mAdapter = new CategoryAdapter(getActivity(), mCategories,
				R.layout.cate_grid_item, MyVolley.getImageLoader());

		gridview.setAdapter(mAdapter);
		gridview.setOnItemClickListener(this);
		
		mCategories.setOnCategoryChangeListener(new OnCategoryChangeListener() {

			@Override
			public void onCategoryChange(Categories categories) {
				mAdapter.notifyDataSetChanged();
			}
		});
		return rootView;

	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Category category = mCategories.get(position);
		
		 Tracker t = ((TVThailandApp) getActivity().getApplication()).getTracker(
		            TrackerName.APP_TRACKER);
		 t.setScreenName("Category");
		 t.send(new HitBuilders.AppViewBuilder().setCustomDimension(1, category.getTitle()).build());
		
		Intent intent = new Intent(getActivity(), ProgramActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(ProgramActivity.EXTRAS_MODE, ProgramActivity.BY_CATEGORY);
		bundle.putString(ProgramActivity.EXTRAS_TITLE, category.getTitle());
		bundle.putString(ProgramActivity.EXTRAS_ID, category.getId());
		bundle.putString(ProgramActivity.EXTRAS_ICON, category.getThumbnail());
		intent.putExtras(bundle);
		getActivity().startActivity(intent);
	}

}
