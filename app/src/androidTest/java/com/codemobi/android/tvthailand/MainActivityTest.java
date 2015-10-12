package com.codemobi.android.tvthailand;

import com.codemobi.android.tvthailand.activity.ProgramActivity;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	public void tearDown() throws Exception {
//		solo.finishOpenedActivities();
	}
	
	private void didStart() throws Exception {
//		solo.goBack();
	}
	
//	public void testSearch() throws Exception {
//		didStart();
//		solo.clickOnActionBarItem(0);
//		solo.typeText(0, "Music");
//		testSelectProgram();
//	}

//	public void testOpenAccount() throws Exception {
//		didStart();
//		solo.clickOnMenuItem("Account");
//		solo.assertCurrentActivity("Wrong activity", AccountActivity.class);
//	}
	
//	public void testOpenFavorite() throws Exception {
//		didStart();
//		solo.clickOnMenuItem("Favorite");
//		solo.assertCurrentActivity("Wrong activity", ProgramLoaderActivity.class);
//		assertTrue("Title is not found", solo.waitForText("Favorite"));
//	}
	
//	public void testOpenRecently() throws Exception {
//		didStart();
//		solo.clickOnMenuItem("Recently");
//		solo.assertCurrentActivity("Should be ProgramLoaderActivity", ProgramLoaderActivity.class);
//		assertTrue("Title is not found", solo.waitForText("Recently"));
//	}
	
//	public void testOpenAbout() throws Exception {
//		didStart();
//		solo.clickOnMenuItem("About");
//		solo.assertCurrentActivity("Wrong activity", AboutActivity.class);
//		assertTrue("Title is not found", solo.waitForText("About"));
//	}
	
	public void testSelectTabs() throws Exception {
		didStart();
		solo.clickOnText("CHANNELS");
		solo.clickOnText("RADIOS");
		solo.clickOnText("CATEGORIES");
	}
	
	public void testSelectCategories() throws Exception {
		didStart();
		TextView selectedText = solo.clickInList(1).get(0);
		solo.assertCurrentActivity("Wrong activity", ProgramActivity.class);
		assertTrue("Title is not found", solo.waitForText(selectedText.getText().toString()));
		testSelectProgram();
	}
	
	public void testSelectChannels() throws Exception {
		didStart();
		
		String strChanel = "ช่อง 3";
		solo.clickOnText("CHANNELS");
		solo.clickOnText(strChanel);
		solo.assertCurrentActivity("Wrong activity", ProgramActivity.class);
		assertTrue("Title is not found", solo.waitForText(strChanel));
		testSelectProgram();
	}
	
	private void testSelectProgram() throws Exception {
		TextView selectedText = solo.clickInList(1).get(0);
//		solo.assertCurrentActivity("Wrong activity", EpisodeActivity.class);
//		assertTrue("Title is not found", solo.waitForText(selectedText.getText().toString(), 5, 1000));
	}
	
	private void testSelectEpisode() throws Exception {
		TextView selectedText = solo.clickInList(2).get(0);
//		assertTrue("Title is not found", solo.waitForText(selectedText.getText().toString(), 5, 1000));
	}
}
