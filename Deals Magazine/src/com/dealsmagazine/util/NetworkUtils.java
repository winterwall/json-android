/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.dealsmagazine.globals.Globals;
import com.dealsmagazine.buyer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * Network
 * 
 */
public class NetworkUtils {

	static String _nullUrl = new String("");

	/*
	 * Check Network Connection
	 */
	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		}
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo == null) {
			return false;
		}
		if (netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/*
	 * Login result
	 * 
	 * @ username
	 * 
	 * @ passwprd
	 */
	public static String validateLogin(String username, String password) {
		String result = "";

		String apitoken = Globals.API_TOKEN;

		// Get the Login Url
		String url_valid = Globals.URL_LOGIN_BASE;
		HttpPost httpRequest = new HttpPost(url_valid);

		// Use BasicNameValuePair to store POST data
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("apitoken", apitoken));
		pairs.add(new BasicNameValuePair("email", username.trim()));
		pairs.add(new BasicNameValuePair("password", password.trim()));

		try {

			// Set Character
			HttpEntity entity = new UrlEncodedFormEntity(pairs, "utf8");

			// Request HttpRequest
			httpRequest.setEntity(entity);

			// AddHeader
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, Globals.NETWORK_CONNECT_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, Globals.NETWORK_READ_TIMEOUT);
			client.setParams(params);

			// Get HTTPResponse
			HttpResponse response = client.execute(httpRequest);

			// Get return String
			result = EntityUtils.toString(response.getEntity());

		} catch (Exception e) {
		}
		return result;
	}

	/*
	 * Get User Information
	 * 
	 * @ username
	 * 
	 * @ passwprd
	 * 
	 * @ userId
	 */
	public static String getUserInfo(String username, String password, String userId) {
		String result = "";

		String apitoken = Globals.API_TOKEN;

		// Get the Login Url
		String url_valid = Globals.URL_BUYER_INFO_BASE;
		HttpPost httpRequest = new HttpPost(url_valid);

		// Use BasicNameValuePair to store POST data
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("apitoken", apitoken));
		pairs.add(new BasicNameValuePair("email", username.trim()));
		pairs.add(new BasicNameValuePair("password", password.trim()));
		pairs.add(new BasicNameValuePair("buyerid", userId.trim()));

		try {

			// Set Character
			HttpEntity entity = new UrlEncodedFormEntity(pairs, "utf8");

			// Request HttpRequest
			httpRequest.setEntity(entity);

			// AddHeader
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, Globals.NETWORK_CONNECT_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, Globals.NETWORK_READ_TIMEOUT);
			client.setParams(params);

			// Get HTTPResponse
			HttpResponse response = client.execute(httpRequest);

			// Get return String
			result = EntityUtils.toString(response.getEntity());

		} catch (Exception e) {
			result = "";

		}
		return result;
	}

	/*
	 * Get Voucher JSON Data From Server By User ID
	 */
	public static byte[] getVoucherFromServer(String username, String password, String userId) {
		byte[] result_byte = null;
		String result_string = "";
		String apitoken = Globals.API_TOKEN;
		String url_getVoucher = Globals.URL_MY_VOUCHER_LIST_BASE;
		HttpPost httpRequest = new HttpPost(url_getVoucher);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("apitoken", apitoken));
		pairs.add(new BasicNameValuePair("email", username.trim()));
		pairs.add(new BasicNameValuePair("password", password.trim()));
		pairs.add(new BasicNameValuePair("buyerid", userId.trim()));

		try {
			HttpEntity entity = new UrlEncodedFormEntity(pairs, "utf8");

			httpRequest.setEntity(entity);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, Globals.NETWORK_CONNECT_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, Globals.NETWORK_READ_TIMEOUT);
			client.setParams(params);
			HttpResponse response = client.execute(httpRequest);
			result_string = EntityUtils.toString(response.getEntity());
			result_byte = FileUtils.convertStringToByteArray(result_string);
		} catch (Exception e) {
			result_byte = null;
		}
		return result_byte;
	}

	/*
	 * Get Bitmap
	 */
	public static Bitmap returnBitMap(String url) {

		if (!url.equals(_nullUrl)) {
			URL myFileUrl = null;
			Bitmap bitmap = null;
			try {
				myFileUrl = new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		} else {
			Bitmap bitmap = BitmapFactory.decodeResource(null, R.drawable.icon);
			return bitmap;
		}
	}

}
