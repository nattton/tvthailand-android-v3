package com.makathon.tvthailand.account;



import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.account.User.OnUserProfileChangeListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*****
 * 
 * @author april
 * 15 October 2013 - Fix Google+ Sign-in bug, according to android sdk updated to r22.2.1
 *
 ******/

public class AccountProfileFragment extends SherlockFragment implements OnUserProfileChangeListener {

	private User userProfile = User.getInstance();
	private TextView displayName;
	private ProfilePictureView displayPicture;
	private AccountActivity activity;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback sessionCallback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AccountActivity) getActivity();
        uiHelper = new UiLifecycleHelper(getActivity(), sessionCallback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.account_profile, container, false);

		displayName = (TextView) view.findViewById(R.id.account_user_name);
		displayPicture = (ProfilePictureView) view
				.findViewById(R.id.account_profile_pic);
		displayPicture.setDrawingCacheEnabled(true);
		userProfile.setOnUserProfileChangeListener(this);
		initSession(savedInstanceState);
		updateUI();

		return view;

	}
	
    private void updateUI() {
    	displayPicture.setProfileId(userProfile.getUserID());
    	displayName.setText(userProfile.getDisplayName());	
	}

	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
//                tokenUpdated();
//            	Log.e("SessionState", "SessionState.OPENED_TOKEN_UPDATED");
            } else {
                makeMeRequest(session);
            }
        } else {
        	displayName.setText("User");
        }
    }
	
    private void makeMeRequest(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                    	
                    	userProfile.setUser(user);
                    	
                    }
                }
            }
        });
        request.executeAsync();
        
    }
	
    /**
     * Resets the view to the initial defaults.
     */
    private void initSession(Bundle savedInstanceState) {

        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            makeMeRequest(session);
        }
    }

	@Override
	public void onUserProfileChange(User user) {
		updateUI();
//		Log.e("User: ", User.getInstance().userToString());
	}
	




}
