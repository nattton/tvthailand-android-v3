package com.makathon.tvthailand.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.makathon.tvthailand.R;
import com.makathon.tvthailand.datasource.Program;
import com.makathon.tvthailand.datasource.Programs;

public class AllProgramAdapter extends BaseAdapter implements
		SectionIndexer {
	HashMap<String, Integer> alphaIndexer;
	String[] sections;

	private Activity activity;
	private int resId;
	private Programs programs;
	private static LayoutInflater mInflater = null;
	private boolean[] separatorAtIndex;
	private String vowel = "เแไใโ";
	public AllProgramAdapter(Activity a, Programs c, int resouceId) {
		activity = a;
		programs = c;
        resId = resouceId;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        reloadInderxer();
	}
	
	@Override
	public void notifyDataSetChanged() {
		reloadInderxer();
		super.notifyDataSetChanged();
	}
	
	private String checkVowel(String s){
		for (int i = 0; i < s.length(); i++) {
			String ch = String.valueOf(s.charAt(i));
			if (!vowel.contains(ch)) return ch;
		}
		return "";
	}
	
	private void reloadInderxer() {
		
        alphaIndexer = new HashMap<String, Integer>();
        
        for (int x = programs.size() - 1; x >= 0; x--) {
        	String s = programs.get(x).getTitle();

			// get the first letter of the store
			String ch = checkVowel(s);
			
			// convert to uppercase otherwise lowercase a -z will be sorted
			// after upper A-Z
			ch = ch.toUpperCase();

			// HashMap will prevent duplicates
			alphaIndexer.put(ch, x);
		}
		
		Set<String> sectionLetters = alphaIndexer.keySet();

		// create a list from the set to sort
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);

		Collections.sort(sectionList);

		sections = new String[sectionList.size()];

		sectionList.toArray(sections);

		assignSeparatorPositions(programs);
	}
	
	private void assignSeparatorPositions(Programs items) {
		separatorAtIndex = new boolean[items.size()];
		char currentChar = 0;

		for (int i = 0; i < items.size(); i++) {
			char title = checkVowel(items.get(i).getTitle()).charAt(0);
			if (title != currentChar) {
				separatorAtIndex[i] = true;
			} else {
				separatorAtIndex[i] = false;
			}
			currentChar = title;
		}

	}

	private class ViewHolder {
		TextView title;
		TextView sectionHeader;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(resId, parent, false);

			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.sectionHeader = (TextView) convertView.findViewById(R.id.separator);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Program item = programs.get(position);
		holder.title.setText(item.getTitle());

		if (separatorAtIndex[position]) {
			holder.sectionHeader.setText(String.valueOf(item.getTitle().charAt(0)));
			holder.sectionHeader.setVisibility(View.VISIBLE);
		} else {
			holder.sectionHeader.setVisibility(View.GONE);
		}

		return convertView;
	}

	public int getPositionForSection(int section) {
		return alphaIndexer.get(sections[section]);
	}

	public int getSectionForPosition(int position) {
		return 0;
	}

	public Object[] getSections() {
		return sections;
	}


	@Override
	public int getCount() {
		return programs.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}