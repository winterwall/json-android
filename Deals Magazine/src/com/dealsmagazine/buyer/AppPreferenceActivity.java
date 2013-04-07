/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.buyer;

import com.dealsmagazine.buyer.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/*
 * AppPreference Activity
 * 
 * @Settings
 * 
 */
public class AppPreferenceActivity extends PreferenceActivity {

	/*
	 * Default Constructor
	 */
	public AppPreferenceActivity() {
	}

	/*
	 * Called when the activity is first created. Inflate the Preferences Screen
	 * XML declaration.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflate the XML declaration
		addPreferencesFromResource(R.xml.prefs);

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(AppPreferenceActivity.this, DealsMagazineActivity.class);
		startActivity(intent);
		finish();
	}

}
