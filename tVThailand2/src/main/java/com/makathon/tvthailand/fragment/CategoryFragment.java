package com.makathon.tvthailand.fragment;

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
import com.makathon.tvthailand.MainApplication;
import com.makathon.tvthailand.MainApplication.TrackerName;
import com.makathon.tvthailand.MyVolley;
import com.makathon.tvthailand.ProgramActivity;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.adapter.CategoryAdapter;
import com.makathon.tvthailand.dao.section.CategoryItemDao;
import com.makathon.tvthailand.manager.SectionManager;
import com.makathon.tvthailand.manager.bus.MainBus;
import com.squareup.otto.Subscribe;

public class CategoryFragment extends SherlockFragment implements OnItemClickListener {

    private CategoryAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.cate_grid_view, container,
				false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
		gridview.setAdapter(mAdapter = new CategoryAdapter(MyVolley.getImageLoader()));
		gridview.setOnItemClickListener(this);
		return rootView;
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        CategoryItemDao category = SectionManager.getInstance().getData().getCategories().get(position);
		
		 Tracker t = ((MainApplication) getActivity().getApplication()).getTracker(
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

    @Override
    public void onResume() {
        super.onResume();
        MainBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainBus.getInstance().unregister(this);
    }

    @Subscribe
    public void onSectionLoaded(SectionManager.EventType eventType) {
        if (eventType == SectionManager.EventType.Loaded)
            mAdapter.notifyDataSetChanged();
    }
}
