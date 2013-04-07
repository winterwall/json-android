/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.buyer;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dealsmagazine.globals.Globals;
import com.dealsmagazine.util.Eula;
import com.dealsmagazine.util.ExternalStorageUtils;
import com.dealsmagazine.util.FileUtils;
import com.dealsmagazine.util.NetworkUtils;
import com.dealsmagazine.util.TimerUtils;
import com.dealsmagazine.util.VoucherListUtils;
import com.dealsmagazine.util.VoucherPageUtils;
import com.dealsmagazine.buyer.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Time;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class DealsMagazineActivity extends Activity {

	public final static String KEY_SYNC = "SYNC";

	final int MENU_LOGIN = 1;
	final int MENU_LOGOUT = 2;
	final int MENU_SETTINGS = 3;

	private TextView tv_mainmenu;
	private ImageButton imgbtn_viewdeals;
	private ImageButton imgbtn_myvouchers;

	private boolean isLoginResultValid = false;
	private boolean isSyncOn = false;
	private boolean isDelete = false;
	private int syncTag = 0;
	private String lastcheck = "";

	String _emptyString = new String("");
	Context appContext = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Eula.show(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Get settings
		SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(appContext);
		String sync_key = appContext.getString(R.string.prefs_autosync_key);
		String dele_key = appContext.getString(R.string.prefs_autodelete_key);
		try {
			isSyncOn = sprefs.getBoolean(sync_key, true);
		} catch (ClassCastException e) {
			isSyncOn = false;
		}

		try {
			isDelete = sprefs.getBoolean(dele_key, false);
		} catch (ClassCastException e) {
			isDelete = false;
		}

		// Get sync tag
		Bundle extras = getIntent().getExtras();
		try {
			syncTag = extras.getInt(KEY_SYNC);
		} catch (Exception e) {
			syncTag = 0;
		}

		try {
			lastcheck = ((User) getApplication()).loadLastCheckFromPreferences();
		} catch (Exception e) {
		}

		tv_mainmenu = (TextView) this.findViewById(R.id.text_mainmenu);
		tv_mainmenu.setVisibility(View.INVISIBLE);

		imgbtn_viewdeals = (ImageButton) this.findViewById(R.id.imgbtn_mydeals);
		imgbtn_myvouchers = (ImageButton) this.findViewById(R.id.imgbtn_myvouchers);

		// If the network is unavailable, change to off line mode
		if (!NetworkUtils.isNetworkAvailable(this)) {
			imgbtn_viewdeals.setVisibility(View.GONE);
			tv_mainmenu.setText(this.getString(R.string.offline));
			tv_mainmenu.setVisibility(View.VISIBLE);
		}

		// Set the listener for view deals
		imgbtn_viewdeals.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				viewDeals();
			}
		});

		// Set the listener for my voucher
		imgbtn_myvouchers.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				myVouchers();
			}
		});

		// Delete expired files
		if (isDelete) {
			systemFilesMgt();
		}

		// Background sync task
		if (isUserLogIn() && NetworkUtils.isNetworkAvailable(this) && syncTag == 1 && TimerUtils.isCheckTimeAvailable(lastcheck)) {
			syncTag = 0;
			
			long dateMillis = System.currentTimeMillis();
			Time yourDate = new Time();
			yourDate.set(dateMillis);
			lastcheck = yourDate.format("%Y-%m-%d %H:%M");
			((User) getApplication()).setLastCheck(lastcheck);
			((User) getApplication()).saveLastCheckToPreferences();
			
			try {
				backgroundTask();
			} catch (Exception e) {
			}
		}
	}

	public void viewDeals() {
		Intent ia = new Intent(DealsMagazineActivity.this, DealsWebView.class);
		ia.putExtra(DealsWebView.KEY_PAGE_CONTROLLER, Globals.INT_VIEW_DEALS);
		startActivityForResult(ia, Globals.DEALS_WEB_VIEW);
		finish();
	}

	public void myVouchers() {
		if (NetworkUtils.isNetworkAvailable(this)) {

			// Updated offline vouchers
			if (isUserLogIn() && isSyncOn) {
				try {
					new VoucherListViewTask().execute();
				} catch (Exception e) {
				}
			}

			Intent ia = new Intent(DealsMagazineActivity.this, DealsWebView.class);
			ia.putExtra(DealsWebView.KEY_PAGE_CONTROLLER, Globals.INT_MY_VOUCHERS);
			ia.putExtra(DealsWebView.KEY_FROM_ACTIVITY, Globals.DEALS_MAGAZINE_ACTIVITY);
			startActivityForResult(ia, Globals.DEALS_WEB_VIEW);
			finish();
		} else {
			if (isUserLogIn()) {
				Intent ia = new Intent(DealsMagazineActivity.this, VoucherListView.class);
				startActivityForResult(ia, Globals.VOUCHER_LIST_VIEW);
				finish();
			} else {
				Intent i = new Intent(DealsMagazineActivity.this, NetworkConnectionView.class);
				startActivityForResult(i, Globals.LOGIN_VIEW);
				finish();
			}
		}
	}

	public void backgroundTask() {
		new VaildLoginTask().execute();
	}

	public void logIn() {
		Intent i = new Intent(DealsMagazineActivity.this, LoginView.class);
		startActivity(i);
		finish();
	}

	public void clearUserData() {
		clearCookie();
		((User) getApplication()).setvoucherCount(0);
		((User) getApplication()).saveVoucherCountToPreferences();
		String fname = ((User) getApplication()).getuserId();
		if (fname != null) {
			VoucherListUtils.deleteInternalStoragePrivate(this, fname);
		}
		FileUtils.deleteExternalStorageAll(this, "");
	}

	private void clearCookie() {
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	public void systemFilesMgt() {
		FileUtils.deleteExternalStorageOldFile(this, "voucherImg", 1);
		FileUtils.deleteExternalStorageOldFile(this, "barcode", 6);
	}

	public boolean isUserLogIn() {
		if (!((User) getApplication()).loaduserIdFromPreferences().equals(_emptyString) && !((User) getApplication()).loadusernameFrompreferences().equals(_emptyString) && !((User) getApplication()).loadpasswordFrompreferences().equals(_emptyString)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isUserValidate(String loginresult) {
		boolean boolean_loginResult = false;
		if (loginresult.equals(_emptyString)) {
			return boolean_loginResult;
		} else {
			String string_login_result = loginresult;
			try {
				final JSONObject json_object_validate = new JSONObject(string_login_result);
				boolean_loginResult = json_object_validate.getBoolean("Success");
			} catch (Exception e) {
			}
			return boolean_loginResult;
		}
	}

	private void saveVoucherList(byte[] buffer) {
		try {
			String fname = ((User) getApplication()).loaduserIdFromPreferences();
			String getVoucherResultString = FileUtils.convertByteArrayToString(buffer);

			boolean isGetVoucherSuccess = isgetVoucherSuccess(getVoucherResultString);

			if (buffer != null && isGetVoucherSuccess) {
				VoucherListUtils.writeInternalStoragePrivate(this, fname, buffer);
				VoucherListUtils.writeToExternalStoragePrivate(this, fname, buffer);
			} else {
			}
		} catch (Exception e) {
		}
	}

	private boolean isgetVoucherSuccess(String getVoucherResult) {
		boolean getVoucherResult_boolean = false;
		String jsonResultString = getVoucherResult;

		try {
			final JSONObject json_validate = new JSONObject(jsonResultString);
			getVoucherResult_boolean = json_validate.getBoolean("Success");
			if (getVoucherResult_boolean) {
				getVoucherResult_boolean = true;
			} else {
			}
		} catch (Exception e) {
		}
		return getVoucherResult_boolean;
	}

	public byte[] readInternalStoragePrivate(String filename) {
		int len = 1024 * 128;
		byte[] buffer = new byte[len];
		try {
			FileInputStream fis = openFileInput(filename);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int nrb = fis.read(buffer, 0, len);
			while (nrb != -1) {
				baos.write(buffer, 0, nrb);
				nrb = fis.read(buffer, 0, len);
			}
			buffer = baos.toByteArray();
			fis.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return buffer;
	}

	public void readVoucherFromInternalStorage() {
		new Thread() {
			@Override
			public void run() {
				try {
					// Load JSON from Cache
					String fname = ((User) getApplication()).loaduserIdFromPreferences();
					if (fname != null && fname.length() > 0) {
						byte[] buffer = null;
						try {
							buffer = readInternalStoragePrivate(fname);
						} catch (Exception e) {
						}
						// Parse the JSON file
						String string_voucher_list = new String(buffer);
						final JSONObject json_object_voucher_list = new JSONObject(string_voucher_list);
						JSONArray json_array_voucher_list = json_object_voucher_list.getJSONArray("Data");
						int countVoucher = json_array_voucher_list.length();
						for (int i = 0; i < countVoucher; i++) {
							JSONObject oVoucher = json_array_voucher_list.getJSONObject(i);

							int id_int = oVoucher.getInt("VoucherID");
							String id = Integer.toString(id_int);
							String barcode_img_url = oVoucher.getString("VoucherCodeImageUrl");
							Bitmap b;
							try {
								b = NetworkUtils.returnBitMap(barcode_img_url);
								saveVoucherImage(b, id);
							} catch (Exception e) {
							}
						}
					} else {
					}
				} catch (Exception e) {
				}
			}
		}.start();
	}

	private void saveVoucherImage(Bitmap b, String filename) {
		try {
			if (b != null) {
				byte[] temp = null;
				temp = FileUtils.BitmapToBytes(b);
				if (ExternalStorageUtils.isExternalStorageAvailable()) {
					VoucherPageUtils.writeToExternalStoragePrivate(this, filename, temp);
				}
			} else {
			}
		} catch (Exception e) {
		}
	}

	private void popNotice(String message) {
		Toast toast = null;
		toast = Toast.makeText(appContext, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
	}

	class VoucherListViewTask extends AsyncTask<Void, Void, byte[]> {
		public byte[] doInBackground(Void... params) {
			byte[] buffer = null;
			String username = ((User) getApplication()).loadusernameFrompreferences();
			String password = ((User) getApplication()).loadpasswordFrompreferences();
			String userid = ((User) getApplication()).loaduserIdFromPreferences();
			try {
				buffer = NetworkUtils.getVoucherFromServer(username, password, userid);
			} catch (Exception e) {
			}
			return buffer;
		}

		@Override
		public void onPreExecute() {
		}

		@Override
		public void onPostExecute(byte[] buffer) {
			saveVoucherList(buffer);
			new VoucherListViewImageTask().execute();
		}
	}

	class VoucherListViewImageTask extends AsyncTask<Void, Void, byte[]> {
		public byte[] doInBackground(Void... params) {
			byte[] buffer = null;
			readVoucherFromInternalStorage();
			return buffer;
		}

		@Override
		public void onPreExecute() {
		}

		@Override
		public void onPostExecute(byte[] buffer) {
		}
	}

	class VaildLoginTask extends AsyncTask<Void, Void, String> {
		public String doInBackground(Void... params) {
			String loginResult = "";
			String username = ((User) getApplication()).loadusernameFrompreferences();
			String password = ((User) getApplication()).loadpasswordFrompreferences();
			try {
				loginResult = NetworkUtils.validateLogin(username, password);
			} catch (Exception e) {
			}
			return loginResult;
		}

		@Override
		public void onPreExecute() {
		}

		@Override
		public void onPostExecute(String loginResult) {
			isLoginResultValid = isUserValidate(loginResult);
			if (!isLoginResultValid) {
				// If user password changed, clear the user data
				clearUserData();
				((User) getApplication()).logout();
				popNotice("Your login information has changed. Please login again.");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isUserLogIn()) {
			menu.add(0, MENU_LOGOUT, 0, "Logout");
			menu.add(0, MENU_SETTINGS, 0, "Settings");
		} else {
			menu.add(0, MENU_LOGIN, 0, "Login");
			menu.add(0, MENU_SETTINGS, 0, "Settings");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case MENU_LOGIN:
			logIn();
			return true;

		case MENU_LOGOUT:
			clearUserData();
			((User) getApplication()).logout();
			popNotice("You have been logged out successfully.");
			Intent ia = new Intent(this, DealsMagazineActivity.class);
			startActivity(ia);
			finish();
			return true;

		case MENU_SETTINGS:
			Intent i = new Intent(this, AppPreferenceActivity.class);
			startActivity(i);
			finish();
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}