package com.makathon.tvthailand;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.makathon.tvthailand.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class GooglePlusActivity extends Activity implements
		OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {
	private static final String TAG = "ExampleActivity";
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		mPlusClient = new PlusClient.Builder(this, this, this)
//				.setVisibleActivities("http://schemas.google.com/AddActivity",
//						"http://schemas.google.com/BuyActivity").build();
		mPlusClient = new PlusClient.Builder(this, this, this).setActions(
			        "http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
			        .setScopes("PLUS_LOGIN") 
			        .build();
		
		setContentView(R.layout.google_plus_sign_in);
		
		findViewById(R.id.sign_in_button).setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (SendIntentException e) {
                mPlusClient.connect();
            }
        }
        // Save the result and resolve the connection failure upon a user click.
        mConnectionResult = result;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		String accountName = mPlusClient.getAccountName();
        Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDisconnected() {
		Log.d(TAG, "disconnected");
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
	        if (mConnectionResult == null) {
	            mConnectionProgressDialog.show();
	        } else {
	            try {
	                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
	            } catch (SendIntentException e) {
	                // Try connecting again.
	                mConnectionResult = null;
	                mPlusClient.connect();
	            }
	        }
	    }
	}

}