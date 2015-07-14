package com.codemobi.android.tvthailand.account;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.model.GraphUser;
import com.google.android.gms.plus.model.people.Person;

/*****
 * 
 * @author april
 * 15 October 2013 - Fix Google+ Sign-in bug, according to android sdk updated to r22.2.1
 *
 ******/

public class User {
	private static User instance = new User();

	private User() {
	}

	public static synchronized User getInstance() {
		return instance;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
	
	private static final String PREF_NAME = "USER_PROFILE";
	private static final String PREF_USER_ID = "PREF_USER_ID";
	private static final String PREF_EMAIL = "PREF_EMAIL";
	private static final String PREF_DISPLAY_NAME = "PREF_DISPLAY_NAME";
	private static final String PREF_FULL_NAME = "PREF_FULL_NAME";
	private static final String PREF_GENDER = "PREF_GENDER";
	private static final String PREF_BIRTHDAY = "PREF_BIRTHDAY";
	private static final String PREF_USER_IMAGE_URL = "PREF_USER_IMAGE_URL";
	
	
	private GraphUser graphUser = null;
	private Person person = null;
	
	private String userID = "";
	private String email = "";
	private String displayName = "";
	private String fullname = "";
	private String gender = "";
	private String  birthday = "";
	private String userImageURL = "";
	
	public String getUserID() {
		return userID;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getFullName() {
		return fullname;
	}
	public String getGender() {
		return gender;
	}
	public String getBirthday() {
		return birthday;
	}
	public String getUserImageURL() {
		return userImageURL;
	}

	public User(GraphUser graphUser, Person person) {
		this.graphUser = graphUser;
		this.person = person;
	}
	
	public void setUser(GraphUser graphUser, Person person) {
		this.graphUser = graphUser;
		this.person = person;
		notifyUserProfileChanged();
	}
	/**
	 * Set Facebook
	 * @param graphUser
	 */
	public void setUser(GraphUser graphUser) {
		this.graphUser = graphUser;
		this.userID = graphUser.getId().toString();
		this.email = (graphUser.getProperty("email") != null) ? graphUser.getProperty("email").toString() : "";
		this.displayName = graphUser.getName();
		this.fullname = graphUser.getName();
		this.gender = (graphUser.getProperty("gender") != null) ? graphUser.getProperty("gender").toString() : "";
		this.birthday = graphUser.getBirthday();
		this.userImageURL = "http://graph.facebook.com/"+graphUser.getId()+"/picture?type=large";
		notifyUserProfileChanged();
	}
	/**
	 * Set Google Plus
	 * @param person
	 */
	public void setUser(Person person) {
		this.person = person;
		this.userID = person.getId();
//		this.email = (person.getEmails() != null && person.getEmails().size()>0) ? person.getEmails().get(0).toString() : "";
		this.displayName = person.getDisplayName();
		this.fullname = person.getName().toString();  // {"familyName":"Smith","givenName":"April"}
		int personGender = person.getGender();
		if (Person.Gender.FEMALE == personGender) {
			this.gender = "female";
		} else if (Person.Gender.MALE == personGender) {
			this.gender = "male";
		} else {
			this.gender = "other";
		}
		this.birthday = person.getBirthday();
		this.userImageURL = "https://plus.google.com/s2/photos/profile/"+person.getId()+"?sz=200";
		notifyUserProfileChanged();
	}
	
	public void clearUser(){
		this.person = null;
		this.graphUser = null;
		this.userID = "";
		this.email = "";
		this.displayName = "";
		this.fullname = "";
		this.gender = ""; 
		this.birthday = "";
		this.userImageURL = "";
		notifyUserProfileChanged();
	}
	
	public boolean isLogin (){
		return (User.getInstance().getGraphUser()==null&&User.getInstance().getPerson()==null) ? false : true ;
	}

	public GraphUser getGraphUser() {
		return graphUser;
	}

	public Person getPerson() {
		return person;
	}

	public interface OnUserProfileChangeListener {
		void onUserProfileChange(User user);
	}

	private OnUserProfileChangeListener onUserProfileChangeListener;

	public void setOnUserProfileChangeListener(
			OnUserProfileChangeListener onUserProfileChangeListener) {
		this.onUserProfileChangeListener = onUserProfileChangeListener;
	}

	private void notifyUserProfileChanged() {
		if (this.onUserProfileChangeListener != null) {
			this.onUserProfileChangeListener.onUserProfileChange(this);
		}
	}
	
	public void saveUserProfileToPref(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PREF_USER_ID, userID);	
		editor.putString(PREF_EMAIL, email);	
		editor.putString(PREF_DISPLAY_NAME, displayName);	
		editor.putString(PREF_FULL_NAME, fullname);	
		editor.putString(PREF_GENDER, gender);	
		editor.putString(PREF_BIRTHDAY, birthday);	
		editor.putString(PREF_USER_IMAGE_URL, userImageURL);	
		editor.commit();
		
	}
	
	public void logout(Context context) {
		clearUser();
		SharedPreferences prefs = context.getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(PREF_USER_ID);
		editor.remove(PREF_EMAIL);
		editor.remove(PREF_DISPLAY_NAME);
		editor.remove(PREF_FULL_NAME);
		editor.remove(PREF_GENDER);
		editor.remove(PREF_BIRTHDAY);
		editor.remove(PREF_USER_IMAGE_URL);
		editor.commit();
		
	}
	
	public String userToString(){
		return "userID: " + userID + ", "+
			   "email: " + email + ", "+
			   "displayName: " + displayName + ", "+
			   "fullname: " + fullname + ", "+
			   "gender: " + gender + ", "+
			   "birthday: " + birthday + ", "+
			   "userImageURL: " + userImageURL ;
	}

}
