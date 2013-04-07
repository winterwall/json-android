/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.buyer;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.dealsmagazine.util.NetworkUtils;
import com.dealsmagazine.buyer.R;
import com.dealsmagazine.globals.Globals;

/*
 * The Login Activity
 * 
 * @ Login Validation
 * 
 * @ User information update
 * 
 * @ Account information update
 * 
 */
public class LoginView extends Activity {

	// Handle SharePreferences Tag
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private final String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private final String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";

	private String sUserId = "";
	// private String sPublicId = "";
	// private String sFirstName = "";
	// private String sLastName = "";

	private String errorMessage = "Network error, please try again.";
	// private int voucherCount = 0;
	private boolean isLoginResultValid = false;
	// private boolean isUserSaveSuccess = false;

	// Login result handler
	private final Handler mHandler = new Handler();
	private EditText edittext_username;
	private EditText edittext_password;
	private ProgressDialog my_pdialog;
	private Button btn_login;
	private Button btn_signup;
	private Button btn_reset_passowrd;
	private CheckBox view_rememberMe;

	String _nullLoginResult = new String("");

	// Resolve the BadTokenException
	Context mContext = this;

	/*
	 * Called when the activity is first created.
	 */
	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.login_view);

		my_pdialog = new ProgressDialog(this);
		my_pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		my_pdialog.setTitle(getString(R.string.app_name));

		// Check Network Connection
		if (!NetworkUtils.isNetworkAvailable(this)) {

			// If the Network Connection Fail, Change to NetworkConnectionView
			Intent i = new Intent(LoginView.this, NetworkConnectionView.class);
			startActivity(i);
			finish();
		}

		// Initial Views
		initialLoginView();

		// Save User information in SharedPreferences
		isRememberUser(false);

		// Setup Button Click Listener
		setLoginListener();
	}

	/*
	 * Initial View
	 */
	private void initialLoginView() {
		edittext_username = (EditText) this.findViewById(R.id.edittext_username);
		edittext_password = (EditText) this.findViewById(R.id.edittext_password);

		btn_login = (Button) this.findViewById(R.id.btn_login);
		btn_signup = (Button) this.findViewById(R.id.btn_signup);
		btn_reset_passowrd = (Button) this.findViewById(R.id.btn_forget_password);

		// Hide the Check box
		view_rememberMe = (CheckBox) findViewById(R.id.loginRememberMeCheckBox);
		view_rememberMe.setVisibility(View.GONE);

	}

	/*
	 * Set Listener
	 */
	private void setLoginListener() {

		btn_login.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// Login method
				if (edittext_username.getText().toString().trim().length() == 0 || edittext_password.getText().toString().trim().length() == 0) {
					showMessageBox(getString(R.string.enter_login));
					btn_login.setVisibility(View.VISIBLE);
					return;
				} else {
					new VaildLoginTask().execute();
				}
			}
		});

		btn_signup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent ia = new Intent(LoginView.this, DealsWebView.class);
				ia.putExtra(DealsWebView.KEY_PAGE_CONTROLLER, Globals.INT_CREAT_ACCOUNT);
				startActivityForResult(ia, Globals.LOGIN_VIEW);
				finish();
				LoginView.this.finish();
			}
		});

		btn_reset_passowrd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent ia = new Intent(LoginView.this, DealsWebView.class);
				ia.putExtra(DealsWebView.KEY_PAGE_CONTROLLER, Globals.INT_RESET_PASSOWRD);
				startActivityForResult(ia, Globals.LOGIN_VIEW);
				finish();
				LoginView.this.finish();
			}
		});

		// Remember user name
		view_rememberMe.setOnCheckedChangeListener(rememberMeListener);
	}

	/*
	 * CheckBox Listener
	 */
	private OnCheckedChangeListener rememberMeListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (view_rememberMe.isChecked()) {
				Toast.makeText(LoginView.this, "Name Saved", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/*
	 * Get the UserName and Password
	 */
	private void isRememberUser(boolean isRememberMe) {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		String shareuserName = share.getString(SHARE_LOGIN_USERNAME, "");
		String sharepassword = share.getString(SHARE_LOGIN_PASSWORD, "");

		if (!"".equals(shareuserName)) {
			edittext_username.setText(shareuserName);
		}
		if (!"".equals(sharepassword)) {
			edittext_password.setText(sharepassword);
			view_rememberMe.setChecked(true);
		}
		// If store password, let sign in button get focus
		if (edittext_password.getText().toString().length() > 0) {
		}
		share = null;
	}

	/*
	 * Return the CheckBox result
	 */
	private boolean isRememberMe() {
		if (view_rememberMe.isChecked()) {
			return true;
		}
		return false;
	}

	/*
	 * Save UserName and password to SharedPreferences
	 */
	private void saveSharePreferences(boolean saveUserName, boolean savePassword) {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		if (saveUserName) {
			share.edit().putString(SHARE_LOGIN_USERNAME, edittext_username.getText().toString()).commit();
		}
		if (savePassword) {
			share.edit().putString(SHARE_LOGIN_PASSWORD, edittext_password.getText().toString()).commit();
		}
		share = null;
	}

	/*
	 * Clear the SharedPreferences
	 */
	private void clearSharePassword() {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
		share = null;
	}

	/*
	 * Alert
	 */
	private void showMessageBox(String message) {
		new AlertDialog.Builder(this).setPositiveButton(this.getString(R.string.ok), null).setMessage(message).show();
	}

	/*
	 * JSON Parser, ValidateLogin
	 */
	public boolean isUserValidate(String loginresult) {
		boolean boolean_loginResult = false;
		if (loginresult.equals(_nullLoginResult)) {
			return boolean_loginResult;
		} else {
			String string_login_result = loginresult;
			try {
				final JSONObject json_object_validate = new JSONObject(string_login_result);
				boolean_loginResult = json_object_validate.getBoolean("Success");

				if (boolean_loginResult) {
					JSONObject json_object_validate_data = json_object_validate.getJSONObject("Data");
					int buyerId = json_object_validate_data.getInt("BuyerID");
					sUserId = Integer.toString(buyerId);
				} else {
					errorMessage = json_object_validate.getString("Message");
				}
			} catch (Exception e) {
				showMessageBoxTimeOut(errorMessage);
			}
			return boolean_loginResult;
		}
	}

	/*
	 * JSON Parser, User Data
	 */
	public void getUser(String userinforesult) {
		try {
			// final JSONObject json_object_user = new JSONObject(userinforesult);
			// JSONObject json_object_user_data = json_object_user.getJSONObject("Data");
			//
			// String string_firstname = json_object_user_data.getString("FirstName");
			// sFirstName = string_firstname;
			//
			// String string_lastname = json_object_user_data.getString("LastName");
			// sLastName = string_lastname;
			//
			// String string_publicId = json_object_user_data.getString("PublicID");
			// sPublicId = string_publicId;
			//
			// int int_voucherCount = json_object_user_data.getInt("NumberOfVouchers");
			// voucherCount = int_voucherCount;

		} catch (Exception e) {
		}
	}

	/*
	 * Save Login result
	 */
	final Runnable mUpdateLoginResults = new Runnable() {
		public void run() {

			btn_login.setVisibility(View.INVISIBLE);

			if (!isLoginResultValid) {
				((User) getApplication()).logout();
			} else {
				String username = edittext_username.getText().toString().trim();
				String password = edittext_password.getText().toString().trim();

				((User) getApplication()).setuserId(sUserId);
				((User) getApplication()).setusername(username);
				((User) getApplication()).setpassword(password);
				// ((User) getApplication()).setfirstname(sFirstName);
				// ((User) getApplication()).setlastname(sLastName);
				// ((User) getApplication()).setpublicId(sPublicId);
				// ((User) getApplication()).setvoucherCount(voucherCount);
				((User) getApplication()).saveToPreferences();
				((User) getApplication()).saveVoucherCountToPreferences();
			}
			// Re-enable the Fields and Button
			edittext_username.setEnabled(true);
			edittext_password.setEnabled(true);
			btn_login.setEnabled(true);
		}
	};

	/*
	 * Alert
	 */
	private void showMessageBoxTimeOut(String message) {
		new AlertDialog.Builder(this).setPositiveButton("Back", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(LoginView.this, DealsMagazineActivity.class);
				startActivityForResult(i, 1);
				finish();
			}
		}).setMessage(message).show();
	}

	/*
	 * Validate Task
	 */
	class VaildLoginTask extends AsyncTask<Void, Void, String> {
		public String doInBackground(Void... params) {
			String loginResult = "";
			String username = edittext_username.getText().toString().trim();
			String password = edittext_password.getText().toString().trim();
			try {
				loginResult = NetworkUtils.validateLogin(username, password);

			} catch (Exception e) {
				showMessageBoxTimeOut(getString(R.string.net_time_out));
			}
			return loginResult;

		}

		@Override
		public void onPreExecute() {
			my_pdialog.setMessage(getString(R.string.validating));
			my_pdialog.show();
		}

		@Override
		public void onPostExecute(String loginResult) {

			isLoginResultValid = isUserValidate(loginResult);

			if (isLoginResultValid) {
				if (my_pdialog.isShowing()) {
					try {
						my_pdialog.dismiss();
					} catch (Exception e) {
					}
				}
				new UpdateUserDataTask().execute();
			} else {
				if (!view_rememberMe.isChecked()) {
					clearSharePassword();
				}

				btn_login.setVisibility(View.VISIBLE);
				if (my_pdialog.isShowing()) {
					try {
						my_pdialog.dismiss();
					} catch (Exception e) {
					}
				}

				showMessageBox(errorMessage);

				edittext_username.setEnabled(true);
				edittext_password.setEnabled(true);
				edittext_password.setText("");
				btn_login.setEnabled(true);
			}
		}
	}

	/*
	 * Load User Data Task
	 */
	// TODO
	/**
	 * java.lang.IllegalArgumentException: View not attached to window manager
	 * 
	 * at com.dealsmagazine.buyer.LoginView$UpdateUserDataTask.onPostExecute(LoginView.java:406) at com.dealsmagazine.buyer.LoginView$UpdateUserDataTask.onPostExecute(LoginView.java:1) at android.os.AsyncTask.finish(AsyncTask.java:417) at android.os.AsyncTask.access$300(AsyncTask.java:127)
	 * 
	 */
	class UpdateUserDataTask extends AsyncTask<Void, Void, String> {
		public String doInBackground(Void... params) {
			String temp = "";
			String username = edittext_username.getText().toString().trim();
			String password = edittext_password.getText().toString().trim();
			try {
				temp = NetworkUtils.getUserInfo(username, password, sUserId);
			} catch (Exception e) {
				showMessageBoxTimeOut(getString(R.string.net_time_out));
			}
			return temp;
		}

		@Override
		public void onPreExecute() {
			my_pdialog.setMessage(getString(R.string.loading));
			my_pdialog.show();
		}

		@Override
		public void onPostExecute(String temp) {

			if (my_pdialog.isShowing()) {
				try {
					my_pdialog.dismiss();
				} catch (Exception e) {
				}
			}

			// try {
			// getUser(temp);
			// } catch (Exception e) {
			//
			// }

			if (isRememberMe()) {
				saveSharePreferences(true, true);
			} else {
				saveSharePreferences(true, false);
			}

			mHandler.post(mUpdateLoginResults);

			// Start next Activity
			Intent ia = new Intent(LoginView.this, DealsWebView.class);
			ia.putExtra(DealsWebView.KEY_PAGE_CONTROLLER, Globals.INT_VIEW_DEALS);
			startActivityForResult(ia, Globals.LOGIN_VIEW);
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(LoginView.this, DealsMagazineActivity.class);
		startActivity(intent);
		finish();
	}
}
