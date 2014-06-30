package com.makathon.tvthailand.account;


import com.actionbarsherlock.app.SherlockFragment;
import com.makathon.tvthailand.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*****
 * 
 * @author april
 * 15 October 2013 - Fix Google+ Sign-in bug, according to android sdk updated to r22.2.1
 *
 ******/

public class AccountSplashLoginFragment extends SherlockFragment {

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.account_login, container, false);
		
		return view;
		
	}
	
	@Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      
	  }
	
	
	

}
