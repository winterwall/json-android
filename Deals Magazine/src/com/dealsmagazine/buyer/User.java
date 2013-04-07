/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.buyer;

/*
 * User class, maintain global application state
 * 
 */
import android.app.Application;
import android.content.SharedPreferences;

import com.dealsmagazine.util.AES;

public class User extends Application {

	public static final String KEY_CURRENT_USER = "USER_MGMT";
	public static final String KEY_VOUCHER_COUNT = "VOUCHER_COUNT";
	public static final String KEY_VOUCHER_POSITION = "VOUCHER_POSITION";

	// AES encrypt user password
	AES aes = new AES();

	private int voucherCount = 0;
	private int voucherPosition = 0;

	private String userId = "";
	private String username = "";
	private String password = "";
	private String firstname = "";
	private String lastname = "";
	private String publicId = "";
	
	// TODO
	private String lastCheck = "";

	// Set User information
	public void setuserId(String userId) {
		this.userId = userId.trim();
	}

	public void setusername(String username) {
		this.username = username.trim();
	}

	public void setpassword(String password) {
		try {
			this.password = AES.bytesToHex(aes.encrypt(password.trim()));
		} catch (Exception e) {

		}

	}

	public void setfirstname(String firstname) {
		this.firstname = firstname.trim();
	}

	public void setlastname(String lastname) {
		this.lastname = lastname.trim();
	}

	public void setpublicId(String publicid) {

		this.publicId = publicid.trim();
	}

	public void setvoucherCount(int voucherCount) {
		this.voucherCount = voucherCount;
	}

	public void setvoucherPosition(int voucherPosition) {
		this.voucherPosition = voucherPosition;
	}

	// Get User information
	public String getuserId() {
		return userId;
	}

	public String getusername() {
		return username;
	}

	public String getpassword() {
		String result = "";
		try {
			result = new String(aes.decrypt(password));
		} catch (Exception e) {

		}
		return result;
	}

	public String getFirstName() {
		return firstname;
	}

	public String getLastName() {
		return lastname;
	}

	public String getpublicId() {
		return publicId;
	}

	public int getvoucherCount() {
		return voucherCount;
	}

	public int getvoucherPosition() {
		return voucherPosition;
	}

	/*
	 * Logout, Clear User Information
	 */
	public void logout() {
		userId = "";
		username = "";
		password = "";
		firstname = "";
		lastname = "";
		publicId = "";
		voucherCount = 0;
		voucherPosition = 0;
		saveToPreferences();
	}

	// Update User information to SharedPreferences
	public void saveToPreferences() {
		SharedPreferences setting = getSharedPreferences(KEY_CURRENT_USER, 0);
		SharedPreferences.Editor editor = setting.edit();
		editor.putString("userId", userId);
		editor.putString("username", username);
		editor.putString("password", password);
		editor.putString("firstname", firstname);
		editor.putString("lastname", lastname);
		editor.putString("publicId", publicId);
		editor.commit();
	}

	public void saveVoucherCountToPreferences() {
		SharedPreferences setting = getSharedPreferences(KEY_VOUCHER_COUNT, 0);
		SharedPreferences.Editor editor = setting.edit();
		editor.putInt("VoucherCount", voucherCount);
		editor.commit();
	}

	public void saveVoucherPositionToPreferences() {
		SharedPreferences setting = getSharedPreferences(KEY_VOUCHER_POSITION, 0);
		SharedPreferences.Editor editor = setting.edit();
		editor.putInt("VoucherPosition", voucherPosition);
		editor.commit();
	}

	// Get User information from SharedPreferences
	public String loaduserIdFromPreferences() {
		SharedPreferences getting = getSharedPreferences(KEY_CURRENT_USER, 0);
		userId = getting.getString("userId", "");
		return userId;
	}

	public String loadusernameFrompreferences() {
		SharedPreferences getting = getSharedPreferences(KEY_CURRENT_USER, 0);
		username = getting.getString("username", "");
		return username;
	}

	public String loadpasswordFrompreferences() {
		SharedPreferences getting = getSharedPreferences(KEY_CURRENT_USER, 0);
		password = getting.getString("password", "");
		String result = "";
		try {
			result = new String(aes.decrypt(password));
		} catch (Exception e) {

		}
		return result;
	}

	public String loadfirstnameFrompreference() {
		SharedPreferences getting = getSharedPreferences(KEY_CURRENT_USER, 0);
		firstname = getting.getString("firstname", "");
		return firstname;
	}

	public String loadlastnameFrompreference() {
		SharedPreferences getting = getSharedPreferences(KEY_CURRENT_USER, 0);
		lastname = getting.getString("lastname", "");
		return lastname;
	}

	public String loadpublicIdFrompreferences() {
		SharedPreferences getting = getSharedPreferences(KEY_CURRENT_USER, 0);
		publicId = getting.getString("publicId", "");
		return publicId;
	}

	public int loadVoucherCountFrompreferences() {
		SharedPreferences getting = getSharedPreferences(KEY_VOUCHER_COUNT, 0);
		voucherCount = getting.getInt("VoucherCount", 0);
		return voucherCount;
	}

	public int loadVoucherPositionFrompreferences() {
		SharedPreferences getting = getSharedPreferences(KEY_VOUCHER_POSITION, 0);
		voucherPosition = getting.getInt("VoucherPosition", 0);
		return voucherPosition;
	}
	
	// Last user password check time
	public void setLastCheck(String lastCheck){
		this.lastCheck = lastCheck;
	}
	
	public void saveLastCheckToPreferences() {
		SharedPreferences setting = getSharedPreferences(KEY_CURRENT_USER, 0);
		SharedPreferences.Editor editor = setting.edit();
		editor.putString("lastcheck", lastCheck);
		editor.commit();
	}
	
	public String loadLastCheckFromPreferences() {
		SharedPreferences getting = getSharedPreferences(KEY_CURRENT_USER, 0);
		lastCheck = getting.getString("lastcheck", "");
		return lastCheck;
	}

}
