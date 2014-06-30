package com.makathon.tvthailand.account;

import java.util.Arrays;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.makathon.tvthailand.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

/*****
 * 
 * @author april
 * 15 October 2013 - Fix Google+ Sign-in bug, according to android sdk updated to r22.2.1
 *
 ******/

public class AccountActivity extends SherlockFragmentActivity implements
	View.OnClickListener, PlusClient.ConnectionCallbacks, PlusClient.OnConnectionFailedListener,
    PlusClient.OnAccessRevokedListener  {	
	
    private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;
   
	private static final int SPLASH = 0;
	private static final int PROFILE = 1;
	private static final int FRAGMENT_COUNT = PROFILE + 1;

	private SherlockFragment[] fragments = new SherlockFragment[FRAGMENT_COUNT];
	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private boolean isResumed = false;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.account_main);

		/** FACEBOOK auth setup **/
		LoginButton authButton = (LoginButton) findViewById(R.id.login_fb_button);
		authButton.setFragment(fragments[PROFILE]);
		authButton.setReadPermissions(Arrays.asList("basic_info", "email",
				"user_birthday"));

		
		/** GOOGLE+ auth setup **/
        mPlusClient = new PlusClient.Builder(this, this, this)
        				.setScopes(Scopes.PLUS_LOGIN,"https://www.googleapis.com/auth/userinfo.email")
        				.build();
//				        .setActions(MomentUtil.ACTIONS)
//				        .build();

        
		findViewById(R.id.login_gp_button).setOnClickListener(this);
		findViewById(R.id.btn_logout_account).setOnClickListener(this);
		
		
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");



		FragmentManager fm = getSupportFragmentManager();
		fragments[SPLASH] = (SherlockFragment) fm
				.findFragmentById(R.id.splashLoginFragment);
		fragments[PROFILE] = (SherlockFragment) fm
				.findFragmentById(R.id.accountProfileFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();

	}

	@Override
	protected void onStart() {
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	protected void onStop() {
		mPlusClient.disconnect();
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		isResumed = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Facebook onActivityResult
		uiHelper.onActivityResult(requestCode, resultCode, data);

		// Google+ onActivityResult
        if (requestCode == REQUEST_CODE_SIGN_IN
                || requestCode == REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES) {
            if (resultCode == RESULT_OK && !mPlusClient.isConnected()
                    && !mPlusClient.isConnecting()) {
                // This time, connect should succeed.
                mPlusClient.connect();
            }
        }
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);

	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		
		Session session = Session.getActiveSession();
	
		if (session != null && session.isOpened()) {
			// IF facebook session is already open, show profile fragment
			showFragment(PROFILE, false);
		} else if (User.getInstance().getPerson() != null) {
			// IF google+ User not null, show profile fragment
			showFragment(PROFILE, false);
		} else {
			// otherwise present the splash screen and ask the user to login,
			showFragment(SPLASH, false);
		}
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (isResumed) {
			FragmentManager manager = getSupportFragmentManager();
			int backStackSize = manager.getBackStackEntryCount();
			for (int i = 0; i < backStackSize; i++) {
				manager.popBackStack();
			}
			
			// check for the OPENED state instead of session.isOpened() 
			if (state.equals(SessionState.OPENED)) {
				showFragment(PROFILE, false);
			} else if (state.isClosed()) {
				showFragment(SPLASH, false);
			}
		}
	}

	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}


	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View view) {
		 switch(view.getId()) {
		 case R.id.login_gp_button:
             int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
             if (available != ConnectionResult.SUCCESS) {
//            	 Log.e("GP Signin Button Click", "Signin Button");
                 showDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
                 return;
             }

             try {

                 mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
             } catch (IntentSender.SendIntentException e) {
                 // Fetch a new result to start.
                 mPlusClient.connect();
             }
			 break;
         case R.id.btn_logout_account:
        	 	logoutFromAccount();
             break;	
			 
		 }
		 

	}
	
	
    @SuppressWarnings("deprecation")
	@Override
    protected Dialog onCreateDialog(int id) {
        if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
            return super.onCreateDialog(id);
        }

        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return null;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    available, this, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
        }
        return new AlertDialog.Builder(this)
                .setMessage(R.string.plus_generic_error)
                .setCancelable(true)
                .create();
    }	
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
        mConnectionResult = result;
	}

	@Override
	public void onDisconnected() {
		Log.d("Gooplus Connect", "disconnected");
        mPlusClient.connect();

	}

	@Override
	public void onConnected(Bundle connectionHint) {
        String currentPersonName = mPlusClient.getCurrentPerson() != null
                ? mPlusClient.getCurrentPerson().getDisplayName()
                : getString(R.string.unknown_person);
        String accountName = mPlusClient.getAccountName();
        
          // Googple plus conneted, setUser, setEmail
          User.getInstance().setUser(mPlusClient.getCurrentPerson()); 
          User.getInstance().setEmail(accountName);
//          Log.e("Googleplus Login--Success", currentPersonName+" | "+accountName); 
          
          showFragment(PROFILE, false);
	
	}
	
	@Override
	public void onAccessRevoked(ConnectionResult status) {
//        if (status.isSuccess()) {
////            mSignInStatus.setText(R.string.revoke_access_status);
//        	Log.e("GooglplusOnAccessRevoked---","Revoked access");
//        } else {
////            mSignInStatus.setText(R.string.revoke_access_error_status);
//        	Log.e("GooglplusOnAccessRevoked---","Unable to revoke access");
//            mPlusClient.disconnect();
//        }
//        mPlusClient.connect();		
	}

	
	
	
	
	
	/******
	 * 
	 *  LOGOUT DIALOG 
	 *  
	 *  ******/
	public void logoutFromAccount() {
		
		/** Facebook Dialog **/
		if (User.getInstance().getGraphUser() != null) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set dialog message
			alertDialogBuilder
					.setMessage(
							"Logged in as: "
									+ User.getInstance().getDisplayName())
					.setCancelable(false)
					.setPositiveButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							})
					.setNegativeButton("Log out",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									logoutFacebook();
								}
							});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}

		/** Google+ Dialog **/
		if (User.getInstance().getPerson() != null) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set dialog message
			alertDialogBuilder
					.setMessage(
							"Logged in as: "
									+ User.getInstance().getDisplayName())
					.setCancelable(false)
					.setPositiveButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							})
					.setNegativeButton("Log out",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									logoutGooglePlus();
								}
							});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}

	}

	
	/**
	 * 
	 *  Facebook Logout function 
	 *  
	 *  **/
	private void logoutFacebook() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			session.closeAndClearTokenInformation();
			User.getInstance().clearUser();
//			Log.e("Facebook Logout", "Logout Success");
			
			// Once Logout Success, show Login Page
			showFragment(SPLASH, false);
		}

	}

	
	/**
	 * 
	 *  Google+ Logout function 
	 *  
	 *  **/
	private void logoutGooglePlus() {
		if (mPlusClient.isConnected()) {
			mPlusClient.clearDefaultAccount();
			mPlusClient.disconnect();
			mPlusClient.connect();
			User.getInstance().clearUser();
//			Log.e("Gooplus Logout", "Logout Success");
			
			// Once Logout Success, show Login Page
			showFragment(SPLASH, false);
		}
	}


	

    
    
}
