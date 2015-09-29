package com.codemobi.android.tvthailand.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.activity.ProgramActivity;
import com.codemobi.android.tvthailand.adapter.CategoryAdapter;
import com.codemobi.android.tvthailand.adapter.OnTapListener;
import com.codemobi.android.tvthailand.dao.section.CategoryItemDao;
import com.codemobi.android.tvthailand.manager.SectionManager;
import com.codemobi.android.tvthailand.manager.bus.MainBus;
import com.squareup.otto.Subscribe;

/**
 * Created by nattapong on 7/10/15 AD.
 */
public class CategoryFragment extends Fragment {

	private CategoryAdapter mAdapter;

	public CategoryFragment() {
		super();
	}

	public static CategoryFragment newInstance() {
		CategoryFragment fragment = new CategoryFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_category, container, false);
		initInstances(rootView);
		return rootView;
	}

	private void initInstances(View rootView) {
		// init instance with rootView.findViewById here
		setRetainInstance(true);

		RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvCategory);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.category_column_num)));

		mAdapter = new CategoryAdapter(getActivity().getApplicationContext());
		mAdapter.setOnTapListener(new OnTapListener() {
			@Override
			public void onTapView(int position) {
				CategoryItemDao item = SectionManager.getInstance().getData().getCategories().get(position);
				Intent intent = new Intent(getActivity().getApplicationContext(), ProgramActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(ProgramActivity.EXTRAS_MODE, ProgramActivity.BY_CATEGORY);
				bundle.putString(ProgramActivity.EXTRAS_TITLE, item.getTitle());
				bundle.putString(ProgramActivity.EXTRAS_ID, item.getId());
				bundle.putString(ProgramActivity.EXTRAS_ICON, item.getThumbnail());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
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
			if (mAdapter != null)
				mAdapter.notifyDataSetChanged();
	}
}
