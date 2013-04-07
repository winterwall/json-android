/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.buyer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.dealsmagazine.adapter.VouchersArrayAdapter;
import com.dealsmagazine.entity.Voucher;
import com.dealsmagazine.globals.Globals;
import com.dealsmagazine.util.ExternalStorageUtils;
import com.dealsmagazine.util.FileUtils;
import com.dealsmagazine.util.NetworkUtils;
import com.dealsmagazine.util.VoucherListUtils;
import com.dealsmagazine.buyer.R;

/*
 * The VoucherList Activity
 * 
 * @ JSON parser
 * 
 * @ Voucher data management
 * 
 */
public class VoucherListView extends ListActivity {

	final int MENU_CLEARCAHE = 1;
	final ArrayList<Voucher> vouchers = new ArrayList<Voucher>();

	private ImageButton imgbtn_voucherlist_back;
	private ProgressDialog mypDialog;
	private VouchersArrayAdapter vouchersArrayAdapter;
	private ListView voucherListView;
	private AlertDialog.Builder ad_clear_confirm;

	Context appContext = this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voucherlist_view);

		// Notice Pop Window
		mypDialog = new ProgressDialog(this);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mypDialog.setTitle(getString(R.string.app_name));

		// Setup the ListView Adapter that is loaded when selecting "Sync" menu
		voucherListView = (ListView) findViewById(android.R.id.list);

		imgbtn_voucherlist_back = (ImageButton) this.findViewById(R.id.imgbtn_back1);

		// VoucherList Adapter
		vouchersArrayAdapter = new VouchersArrayAdapter(this, R.layout.voucherlist_item, vouchers, voucherListView);

		ad_clear_confirm = new AlertDialog.Builder(this);
		ad_clear_confirm.setTitle("Clear Confirm");
		ad_clear_confirm.setMessage("Are you want to clear the saved vouchers?");
		ad_clear_confirm.setCancelable(true);
		ad_clear_confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				clearAllCache();
			}
		});

		ad_clear_confirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		imgbtn_voucherlist_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent ia = new Intent(VoucherListView.this, DealsMagazineActivity.class);
				startActivityForResult(ia, Globals.DEALS_MAGAZINE_ACTIVITY);
				finish();
			}
		});

		// Listener ListView
		voucherListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> _av, View _v, int _index, long _id) {
				((User) getApplication()).setvoucherPosition(_index);
				((User) getApplication()).saveVoucherPositionToPreferences();
				Voucher selectVoucher = (Voucher) _av.getItemAtPosition(_index);
				showVoucherData(selectVoucher);
			}
		});

		voucherListView.setAdapter(vouchersArrayAdapter);

		// If auto sync preference is true, dispatch a sync task
		boolean isAutoDelete = prefsGetAutoDelete();

		// VoucherListView Settings
		if (isAutoDelete) {
			// Clear all data including settings
			String fname = ((User) getApplication()).getuserId();
			VoucherListUtils.deleteInternalStoragePrivate(this, fname);
			VoucherListUtils.deleteExternalStoragePrivateFile(this, fname);
		}

		// Display Voucher
		if (NetworkUtils.isNetworkAvailable(this)) {

			getVoucherListFromCache();

		} else {
			
			getOfflineVoucherList();
		
		}

	}

	/*
	 * Get OffLine Voucher
	 */
	public void getOfflineVoucherList() {
		readVoucherFromExternalStorage();
		vouchersArrayAdapter.notifyDataSetChanged();
	}

	public void getVoucherListFromCache() {
		readVoucherFromInternalStorage();
		vouchersArrayAdapter.notifyDataSetChanged();
	}

	/*
	 * JSON Parser, Display Voucher from Cache
	 */
	public void readVoucherFromInternalStorage() {
		new Thread() {
			@Override
			public void run() {
				try {
					// Load JSON from Cache
					String fname = ((User) getApplication()).loaduserIdFromPreferences();
					if (fname != null && fname.length() > 0) {
						byte[] buffer = readInternalStoragePrivate(fname);

						// Parse the JSON file
						String string_voucher_list = new String(buffer);
						final JSONObject json_object_voucher_list = new JSONObject(string_voucher_list);
						JSONArray json_array_voucher_list = json_object_voucher_list.getJSONArray("Data");
						int countVoucher = json_array_voucher_list.length();
						((User) getApplication()).setvoucherCount(countVoucher);
						((User) getApplication()).saveVoucherCountToPreferences();
						Voucher voucher;
						for (int i = 0; i < countVoucher; i++) {
							JSONObject oVoucher = json_array_voucher_list.getJSONObject(i);

							int id_int = oVoucher.getInt("VoucherID");
							String id = Integer.toString(id_int);
							String voucher_code = oVoucher.getString("VoucherCode");
							String expire_date = oVoucher.getString("DateExpires");
							String purched_date = oVoucher.getString("DatePurchased");
							String redeem_date = oVoucher.getString("DateRedeemed");
							String status = oVoucher.getString("Status");
							String business_name = oVoucher.getString("SellerName");
							String title = oVoucher.getString("DealTitle");
							String content = oVoucher.getString("DealOptionTitle");
							String price = oVoucher.getString("Price");
							String value = oVoucher.getString("Value");
							String saving = oVoucher.getString("Savings");
							String fine_print = oVoucher.getString("FinePrint");
							String barcode_img_url = oVoucher.getString("VoucherCodeImageUrl");
							String voucher_img_url = oVoucher.getString("LargeImageUrl");
							String voucher_thumbnail_img_url = oVoucher.getString("SmallImageUrl");
							String instruction = oVoucher.getString("VoucherInstructions");
							JSONArray addressData = oVoucher.getJSONArray("Addresses");
							JSONObject address_data = addressData.getJSONObject(0);
							String name = address_data.getString("Name");
							String address1 = address_data.getString("Address1");
							String address2 = address_data.getString("Address2");
							String city = address_data.getString("City");
							String state = address_data.getString("State");
							String zipcode = address_data.getString("Zip");
							String phone = address_data.getString("Phone");
							Double latitude = address_data.getDouble("Latitute");
							Double longitude = address_data.getDouble("Longitude");

							voucher = new Voucher();

							voucher.id = id;
							voucher.voucher_code = voucher_code;
							voucher.expire_date = expire_date;
							voucher.purched_date = purched_date;
							voucher.redeem_date = redeem_date;
							voucher.status = status;
							voucher.business_name = business_name;
							voucher.title = title;
							voucher.content = content;
							voucher.price = price;
							voucher.value = value;
							voucher.saving = saving;
							voucher.fine_print = fine_print;
							voucher.barcode_img_url = barcode_img_url;
							voucher.voucher_img_url = voucher_img_url;
							voucher.voucher_thumbnail_img_url = voucher_thumbnail_img_url;
							voucher.recipient = "";
							voucher.terms = "";
							voucher.instruction = instruction;
							voucher.name = name;
							voucher.address1 = address1;
							voucher.address2 = address2;
							voucher.city = city;
							voucher.state = state;
							voucher.zipcode = zipcode;
							voucher.phone = phone;
							voucher.latitude = (int) (latitude * 1E6);
							voucher.longitude = (int) (longitude * 1E6);

							vouchers.add(voucher);

						}
						VoucherListView.this.runOnUiThread(new Runnable() {
							public void run() {
								vouchersArrayAdapter.notifyDataSetChanged();
							}
						});
					} else {
					}
				} catch (Exception e) {
				}
			}
		}.start();
	}

	/*
	 * JSON Parser, Display Voucher from SD card
	 */
	public void readVoucherFromExternalStorage() {
		new Thread() {
			@Override
			public void run() {
				try {
					// load JSON from SD Card
					String fname = ((User) getApplication()).loaduserIdFromPreferences();
					if (fname != null && fname.length() > 0) {
						byte[] buffer = readExternallStoragePrivate(fname);

						// Parse the JSON file
						String string_voucher_list = new String(buffer);
						final JSONObject json_object_voucher_list = new JSONObject(string_voucher_list);
						JSONArray json_array_voucher_list = json_object_voucher_list.getJSONArray("Data");
						int countVoucher = json_array_voucher_list.length();
						Voucher voucher;
						for (int i = 0; i < countVoucher; i++) {
							JSONObject oVoucher = json_array_voucher_list.getJSONObject(i);

							int id_int = oVoucher.getInt("VoucherID");
							String id = Integer.toString(id_int);
							String voucher_code = oVoucher.getString("VoucherCode");
							String expire_date = oVoucher.getString("DateExpires");
							String purched_date = oVoucher.getString("DatePurchased");
							String redeem_date = oVoucher.getString("DateRedeemed");
							String status = oVoucher.getString("Status");
							String business_name = oVoucher.getString("SellerName");
							String title = oVoucher.getString("DealTitle");
							String content = oVoucher.getString("DealOptionTitle");
							String price = oVoucher.getString("Price");
							String value = oVoucher.getString("Value");
							String saving = oVoucher.getString("Savings");
							String fine_print = oVoucher.getString("FinePrint");
							String barcode_img_url = oVoucher.getString("VoucherCodeImageUrl");
							String voucher_img_url = oVoucher.getString("LargeImageUrl");
							String voucher_thumbnail_img_url = oVoucher.getString("SmallImageUrl");
							String instruction = oVoucher.getString("VoucherInstructions");
							JSONArray addressData = oVoucher.getJSONArray("Addresses");
							JSONObject address_data = addressData.getJSONObject(0);
							String name = address_data.getString("Name");
							String address1 = address_data.getString("Address1");
							String address2 = address_data.getString("Address2");
							String city = address_data.getString("City");
							String state = address_data.getString("State");
							String zipcode = address_data.getString("Zip");
							String phone = address_data.getString("Phone");
							Double latitude = address_data.getDouble("Latitute");
							Double longitude = address_data.getDouble("Longitude");

							voucher = new Voucher();

							voucher.id = id;
							voucher.voucher_code = voucher_code;
							voucher.expire_date = expire_date;
							voucher.purched_date = purched_date;
							voucher.redeem_date = redeem_date;
							voucher.status = status;
							voucher.business_name = business_name;
							voucher.title = title;
							voucher.content = content;
							voucher.price = price;
							voucher.value = value;
							voucher.saving = saving;
							voucher.fine_print = fine_print;
							voucher.barcode_img_url = barcode_img_url;
							voucher.voucher_img_url = voucher_img_url;
							voucher.voucher_thumbnail_img_url = voucher_thumbnail_img_url;
							voucher.recipient = "";
							voucher.terms = "";
							voucher.instruction = instruction;
							voucher.name = name;
							voucher.address1 = address1;
							voucher.address2 = address2;
							voucher.city = city;
							voucher.state = state;
							voucher.zipcode = zipcode;
							voucher.phone = phone;
							voucher.latitude = (int) (latitude * 1E6);
							voucher.longitude = (int) (longitude * 1E6);

							vouchers.add(voucher);

						}
						// Only the original owner thread can touch its views
						VoucherListView.this.runOnUiThread(new Runnable() {
							public void run() {
								vouchersArrayAdapter.notifyDataSetChanged();
							}
						});
					} else {
					}
				} catch (Exception e) {
				}
			}
		}.start();

	}

	/*
	 * Read Voucher from Cache
	 */
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

	/*
	 * Read Voucher from SD Card
	 */
	public byte[] readExternallStoragePrivate(String filename) {
		int len = 1024 * 128;
		byte[] buffer = new byte[len];
		if (!ExternalStorageUtils.isExternalStorageReadOnly()) {
			try {
				File file = new File(getExternalFilesDir(null), filename);
				FileInputStream fis = new FileInputStream(file);
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
		}
		return buffer;
	}

	/*
	 * Retrieves the Auto delete preference
	 * 
	 * @return the value of auto delete
	 */
	public boolean prefsGetAutoDelete() {
		boolean v = false;
		SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(appContext);
		String key = appContext.getString(R.string.prefs_autodelete_key);
		try {
			v = sprefs.getBoolean(key, false);
		} catch (ClassCastException e) {
			// if exception, do nothing; that is return default value of false.
		}
		return v;
	}

	/*
	 * Retrieves the Auto Save to SD Card preference
	 * 
	 * @return the value of auto delete
	 */
	public boolean prefsGetAutoSave() {
		boolean v = true;
		SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(appContext);
		String key = appContext.getString(R.string.prefs_autosave_key);
		try {
			v = sprefs.getBoolean(key, false);
		} catch (ClassCastException e) {
		}
		return v;
	}

	/*
	 * Sets the auto delete preference
	 * 
	 * @param v the value to set
	 */
	public void prefsSetAutoDelete(boolean v) {
		SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(appContext);
		Editor e = sprefs.edit();
		String key = appContext.getString(R.string.prefs_autodelete_key);
		e.putBoolean(key, v);
		e.commit();
	}

	/*
	 * Transfer Data using Intent,
	 * 
	 * @param voucher the Array<voucher>
	 */
	private void showVoucherData(Voucher voucher) {

		Intent i = new Intent(this, VoucherPageView.class);

		i.putExtra(VoucherPageView.KEY_VOUCHER_ID, voucher.getId());
		i.putExtra(VoucherPageView.KEY_VOUCHER_TITLE, voucher.getTitle());
		i.putExtra(VoucherPageView.KEY_VOUCHER_CONTENT, voucher.getContent());
		i.putExtra(VoucherPageView.KEY_VOUCHER_PRICE, voucher.getPrice());
		i.putExtra(VoucherPageView.KEY_VOUCHER_SAVING, voucher.getSaving());
		i.putExtra(VoucherPageView.KEY_VOUCHER_VALUE, voucher.getValue());
		i.putExtra(VoucherPageView.KEY_VOUCHER_STATUS, voucher.getStatus());
		i.putExtra(VoucherPageView.KEY_VOUCHER_CODE, voucher.getVoucherCode());
		i.putExtra(VoucherPageView.KEY_PURCHED_DATE, voucher.getPurchedDate());
		i.putExtra(VoucherPageView.KEY_EXPIRE_DATE, voucher.getExpireDate());
		i.putExtra(VoucherPageView.KEY_REDEEM_DATE, voucher.getRedeemDate());
		i.putExtra(VoucherPageView.KEY_VOUCHER_IMG_URL, voucher.getVImgUrl());
		i.putExtra(VoucherPageView.KEY_BARCODE_IMG_URL, voucher.getBCImgUrl());
		i.putExtra(VoucherPageView.KEY_FINE_PRINT, voucher.getFinePrint());
		i.putExtra(VoucherPageView.KEY_TERMS, voucher.getTerms());
		i.putExtra(VoucherPageView.KEY_INSTRUCTION, voucher.getInstruction());
		i.putExtra(VoucherPageView.KEY_BUSINESS_NAME, voucher.getBusinessName());
		i.putExtra(VoucherPageView.KEY_ADDRESS1, voucher.getAddress1());
		i.putExtra(VoucherPageView.KEY_ADDRESS2, voucher.getAddress2());
		i.putExtra(VoucherPageView.KEY_CITY, voucher.getCity());
		i.putExtra(VoucherPageView.KEY_STATE, voucher.getState());
		i.putExtra(VoucherPageView.KEY_ZIPCODE, voucher.getZipcode());
		i.putExtra(VoucherPageView.KEY_PHONE, voucher.getPhone());
		i.putExtra(VoucherPageView.KEY_LATITUDE, voucher.getLatitude());
		i.putExtra(VoucherPageView.KEY_LONGITUDE, voucher.getLongitude());

		// Start new Activity
		startActivityForResult(i, Globals.VOUCHERPAGE_VIEW);
		finish();
	}

	/*
	 * Invoked at the time to create the menu
	 * 
	 * @param the menu to create
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, MENU_CLEARCAHE, 0, "Clear All").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	/*
	 * Invoked when a menu item has been selected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		// Case: Clear Internal Store
		case MENU_CLEARCAHE:
			ad_clear_confirm.create().show();
			break;
		default:
			break;
		}
		return true;
	}

	/*
	 * Clear the saved files
	 */
	public void clearAllCache() {
		FileUtils.deleteExternalStorageAll(this, "barcode");
		FileUtils.deleteExternalStorageAll(this, "voucherImg");
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(VoucherListView.this, DealsMagazineActivity.class);
		startActivity(intent);
		finish();
	}
}