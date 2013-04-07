/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.buyer;

import java.util.HashMap;
import java.util.Map;

import com.dealsmagazine.globals.Globals;
import com.dealsmagazine.util.FileUtils;
import com.dealsmagazine.util.NetworkUtils;
import com.dealsmagazine.util.VoucherListUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class DealsWebView extends Activity {

	public final static String KEY_PAGE_CONTROLLER = "PAGE_CONTROLLER";
	public final static String KEY_FROM_ACTIVITY = "FROM_ACTIVITY";

	final int MENU_MENU = 1;
	final int MENU_HOME = 2;
	final int MENU_ACCOUNT = 3;
	final int MENU_SORT = 4;
	final int MENU_VOUCHER = 5;

	// tag_back = 1, back button return main menu
	public int tag_back = 0;

	// tag_from_activity = Globals.DEALS_MAGAZINE_ACTIVITY, back button return main menu
	public int tag_from_activity = 0;
	public int tag_page_controller = 0;

	// users credentials, stored by User.java
	private String username = "";
	private String password = "";

	// URLs
	private String url = "";
	private String url_current = "";
	private String url_previous = "";

	private WebView webview_deals;
	ProgressDialog mProgressDialog;

	String _empty = new String("");
	byte[] b = null;
	Context context = this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deals_web_view);

		if (!NetworkUtils.isNetworkAvailable(context)) {
			Intent ia = new Intent(DealsWebView.this, DealsMagazineActivity.class);
			startActivityForResult(ia, Globals.VOUCHER_LIST_VIEW);
			finish();
		}

		try {
			Bundle extras = getIntent().getExtras();
			tag_page_controller = extras.getInt(KEY_PAGE_CONTROLLER);
		} catch (Exception e) {
			tag_page_controller = 0;
		}

		try {
			Bundle extras = getIntent().getExtras();
			tag_from_activity = extras.getInt(KEY_FROM_ACTIVITY);
		} catch (Exception e) {
			tag_from_activity = 0;
		}

		if (isUserLogIn()) {
			username = ((User) getApplication()).loadusernameFrompreferences();
			password = ((User) getApplication()).loadpasswordFrompreferences();
		} else {
			clearHistory();
			clearCookie();
			username = "";
			password = "";
		}

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Loading...");
		mProgressDialog.setProgressStyle(0);

		webview_deals = (WebView) findViewById(R.id.webview_deals);
		webview_deals.setVisibility(View.GONE);

		webview_deals.setWebViewClient(new MyWebViewClient());
		webview_deals.getSettings().setAppCacheEnabled(true);
		webview_deals.getSettings().setDatabaseEnabled(true);
		webview_deals.getSettings().setDomStorageEnabled(true);
		webview_deals.getSettings().setJavaScriptEnabled(true);
		webview_deals.getSettings().setGeolocationEnabled(true);
		webview_deals.getSettings().setSavePassword(false);
		webview_deals.requestFocus(View.FOCUS_DOWN);
		webview_deals.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				super.onGeolocationPermissionsShowPrompt(origin, callback);
				callback.invoke(origin, true, false);
			}

			public void onProgressChanged(WebView view, int progress) {
				try {
					mProgressDialog.setProgress(progress);
				} catch (Exception e) {
				}
			}
		});
		webview_deals.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					break;
				}
				return false;
			}
		});

		clearHistory();

		// First time when open webview post device=android
		if (tag_page_controller == Globals.INT_VIEW_DEALS) {
			if (isUserLogIn()) {
				url = "https://www.dealsmagazine.com/m/User/Login?sawintro=0&device=android";
				// Initial POST
				String query = "email=" + username + "&password=" + password;
				b = query.getBytes();
				webview_deals.postUrl(url, b);
			} else {
				url = "http://www.dealsmagazine.com/m/?sawintro=0&device=android";
				webview_deals.postUrl(url, b);
			}
		} else if (tag_page_controller == Globals.INT_MY_VOUCHERS) {
			if (isUserLogIn()) {
				url = "https://www.dealsmagazine.com/m/Vouchers?sawintro=0&device=android";
				webview_deals.postUrl(url, b);
			} else {
				Intent intent = new Intent();
				intent.setClass(DealsWebView.this, LoginView.class);
				startActivity(intent);
				finish();
			}
		} else if (tag_page_controller == Globals.INT_CREAT_ACCOUNT) {
			url = "https://www.dealsmagazine.com/m/User/Signup?device=android";
			webview_deals.loadUrl(url);
		} else if (tag_page_controller == Globals.INT_RESET_PASSOWRD) {
			url = "https://www.dealsmagazine.com/m/User/ForgotPassword?device=android";
			webview_deals.loadUrl(url);
		} else {
			url = "http://www.dealsmagazine.com/m/?sawintro=0&device=android";
			webview_deals.loadUrl(url);
		}

	}

	private void clearCookie() {
		try {
			CookieSyncManager.createInstance(this);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
		} catch (Exception e) {

		}
	}

	private void clearHistory() {
		try {
			webview_deals.clearHistory();
		} catch (Exception e) {
		}
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

	public boolean isUserLogIn() {
		if (!((User) getApplication()).loadusernameFrompreferences().equals(_empty) && !((User) getApplication()).loadpasswordFrompreferences().equals(_empty)) {
			return true;
		} else {
			return false;
		}
	}

	public void saveUrl(String url) {
		// For testing use
		url_previous = "http://www.dealsmagazine.com/m/Catalog/Deals?category=10000";
		if (url.equals(url_current)) {
		} else {
			url_previous = url_current;
			url_current = url;
		}
	}

	public void showProgressDialog() {
		// Exception: conflict with back button
		try {
			mProgressDialog.show();
		} catch (Exception e) {
		}
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (!NetworkUtils.isNetworkAvailable(context)) {
				Intent ia = new Intent(DealsWebView.this, DealsMagazineActivity.class);
				startActivityForResult(ia, Globals.VOUCHER_LIST_VIEW);
				finish();
			}

			// sync URL record
			saveUrl(url);
			showProgressDialog();
			try {
				// If it is an external link / request
				if (!url.contains("dealsmagazine.com/m")) {
					mProgressDialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					startActivity(intent);
					return true;
					// Log in
				} else if (url.contains("User/Login")) {
					mProgressDialog.dismiss();
					Intent intent = new Intent();
					intent.setClass(DealsWebView.this, LoginView.class);
					startActivity(intent);
					finish();
					// Log out
				} else if (url.contains("User/Logout")) {
					mProgressDialog.dismiss();
					((User) getApplication()).logout();
					clearUserData();
					Intent intent = new Intent();
					intent.setClass(DealsWebView.this, DealsMagazineActivity.class);
					startActivity(intent);
					finish();
				}
			} catch (Exception e) {

			}

			/**
			 * HTTP REFERER
			 */
			Map<String, String> extraHeaders = new HashMap<String, String>();
			extraHeaders.put("Referer", url_previous);
			view.loadUrl(url, extraHeaders);
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			popNotice("Can't connect to server, please try again.");
			Intent intent = new Intent();
			intent.setClass(DealsWebView.this, DealsMagazineActivity.class);
			startActivity(intent);
			finish();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) {
			}

			// Set WebView VISIBLE
			view.setVisibility(View.VISIBLE);
			super.onPageFinished(view, url);
		}
	}

	private void popNotice(String message) {
		Toast toast = null;
		toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
	}

	@Override
	public void onBackPressed() {

		if (tag_back == 1) {
			tag_back = 0;
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) {
			}
			Intent intent = new Intent();
			intent.setClass(DealsWebView.this, DealsMagazineActivity.class);
			startActivity(intent);
			finish();

		} else if (tag_from_activity == Globals.DEALS_MAGAZINE_ACTIVITY) {
			tag_from_activity = 0;
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) {
			}
			Intent intent = new Intent();
			intent.setClass(DealsWebView.this, DealsMagazineActivity.class);
			startActivity(intent);
			finish();

		} else if (webview_deals.canGoBack()) {
			showProgressDialog();
			webview_deals.goBack();

			// TODO
			// More conditions needed
		} else if (!webview_deals.canGoBack()) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) {
			}
			Intent intent = new Intent();
			intent.setClass(DealsWebView.this, DealsMagazineActivity.class);
			startActivity(intent);
			finish();

		} else {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) {
			}
			Intent intent = new Intent();
			intent.setClass(DealsWebView.this, DealsMagazineActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_MENU, 0, "Start Screen");
		menu.add(0, MENU_HOME, 0, "Home");
		menu.add(0, MENU_ACCOUNT, 0, "Account");
		menu.add(0, MENU_SORT, 0, "Sort Deals");
		menu.add(0, MENU_VOUCHER, 0, "Vouchers");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case MENU_HOME:
			showProgressDialog();
			String url_home = "http://www.dealsmagazine.com/m/?sawintro=0&device=android";
			tag_back = 1;
			byte[] b_home = null;
			webview_deals.postUrl(url_home, b_home);
			clearHistory();
			return true;

		case MENU_ACCOUNT:
			showProgressDialog();
			String url_account = "https://www.dealsmagazine.com/m/Account/Dashboard?sawintro=0&device=android";
			tag_back = 1;
			byte[] b_account = null;
			webview_deals.postUrl(url_account, b_account);
			clearHistory();
			return true;

		case MENU_SORT:
			showProgressDialog();
			String url_sort = "http://www.dealsmagazine.com/m/Catalog/Sort";
			tag_back = 1;
			Map<String, String> extraHeaders = new HashMap<String, String>();
			extraHeaders.put("Referer", url_current);
			webview_deals.loadUrl(url_sort, extraHeaders);
			clearHistory();
			return true;

		case MENU_VOUCHER:
			showProgressDialog();
			String url_voucher = "https://www.dealsmagazine.com/m/Vouchers?sawintro=0&device=android";
			tag_back = 1;
			byte[] b_voucher = null;
			webview_deals.postUrl(url_voucher, b_voucher);
			clearHistory();
			return true;

		case MENU_MENU:
			clearHistory();
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) {
			}
			Intent intent = new Intent();
			intent.setClass(DealsWebView.this, DealsMagazineActivity.class);
			startActivity(intent);
			finish();

		default:
			break;
		}
		return false;
	}

}
