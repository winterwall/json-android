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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dealsmagazine.buyer.R;
import com.dealsmagazine.globals.Globals;
import com.dealsmagazine.util.ExternalStorageUtils;
import com.dealsmagazine.util.FileUtils;
import com.dealsmagazine.util.HtmlUtils;
import com.dealsmagazine.util.NetworkUtils;
import com.dealsmagazine.util.VoucherPageUtils;

//import android.util.Log;

/*
 * Voucher page activity
 * 
 * @ Voucher Page
 * 
 * @ Download Voucher Image
 * 
 * @ Save Voucher Image
 * 
 */
public class VoucherPageView extends Activity {

	final int MENU_REFRESH = 1;

	public final static String KEY_VOUCHER_ID = "VOUCHER_ID";
	public final static String KEY_VOUCHER_TITLE = "VOUCHER_TITLE";
	public final static String KEY_VOUCHER_CONTENT = "VOUCHER_CONTENT";
	public final static String KEY_VOUCHER_PRICE = "VOUCHER_PRICE";
	public final static String KEY_VOUCHER_SAVING = "VOUCHER_SAVING";
	public final static String KEY_VOUCHER_VALUE = "VOUCHER_VALUE";
	public final static String KEY_VOUCHER_STATUS = "VOUCHER_STATUS";
	public final static String KEY_VOUCHER_CODE = "VOUCHER_CODE";
	public final static String KEY_EXPIRE_DATE = "VOUCHER_DATE";
	public final static String KEY_PURCHED_DATE = "PURCHED_DATE";
	public final static String KEY_REDEEM_DATE = "REDEEM_DATE";
	public final static String KEY_FINE_PRINT = "FINE_PRINT";
	public final static String KEY_TERMS = "TERMS";
	public final static String KEY_INSTRUCTION = "INSTRUCTION";
	public final static String KEY_BARCODE_IMG_URL = "BARCODE_IMG_URL";
	public final static String KEY_VOUCHER_IMG_URL = "VOUCHER_IMG_URL";
	public final static String KEY_BUSINESS_NAME = "BUSINESS_NAME";
	public final static String KEY_ADDRESS1 = "ADDRESS1";
	public final static String KEY_ADDRESS2 = "ADDRESS2";
	public final static String KEY_CITY = "CITY";
	public final static String KEY_STATE = "STATE";
	public final static String KEY_ZIPCODE = "ZIPCODE";
	public final static String KEY_PHONE = "PHONE";
	public final static String KEY_LATITUDE = "LATITUDE";
	public final static String KEY_LONGITUDE = "LONGITUDE";

	// private int iLatitude;
	// private int iLongitude;

	private String sVoucherId;
	private String sVoucherTitle;
	private String sVoucherContent;
	private String sVoucherPrice;
	private String sVoucherSaving;
	private String sVoucherValue;
	private String sVoucherStatus;
	private String sVoucherCode;
	private String sPurchedDate;
	private String sExpireDate;
	private String sRedeemDate;
	private String sFinePrint;
	private String sTerms;
	private String sInstruction;
	private String sVoucherImgUrl;
	private String sBusinessName;
	private String sAddress1;
	private String sAddress2;
	private String sCity;
	private String sState;
	private String sZipcode;
	private String sPhone;
	private String sLocation;
	private String sRecipient;
	private String sRecipientFirstName;
	private String sRecipientLastName;

	private ImageButton imgbtn_voucherpage_back;
	private ProgressBar pb_voucher_progress;
	private LinearLayout ll_redeem_date;
	private LinearLayout ll_expire_date;

	private TextView tv_vouchertitle;
	private TextView tv_vouchercontent;
	private TextView tv_vouchercode;
	private TextView tv_purched_date;
	private TextView tv_expire_date;
	private TextView tv_redeem_date;
	private TextView tv_fine_print;
	private TextView tv_recipient;
	private TextView tv_terms;
	private TextView tv_instruction;
	private TextView tv_busniess_name;
	private TextView tv_location;
	private TextView tv_phone;
	private TextView tv_price;
	private TextView tv_value;
	private TextView tv_save;

	private ImageView img_voucher;

	String _nullVoucherCode = new String("");
	String _nullRedeem = new String("-");
	String _nullTerms = new String("");
	String _voucherStatusProcessing = new String("Processing");
	String _voucherStatusActive = new String("Active");
	String _voucherStatusRedeemed = new String("Redeemed");

	Context appContext = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voucherpage_view);

		tv_vouchertitle = (TextView) this.findViewById(R.id.vouchertitle);
		tv_vouchercontent = (TextView) this.findViewById(R.id.vouchercontent);
		tv_vouchercode = (TextView) this.findViewById(R.id.vouchercode);
		tv_purched_date = (TextView) this.findViewById(R.id.purched_date);
		tv_expire_date = (TextView) this.findViewById(R.id.expire_date);
		tv_redeem_date = (TextView) this.findViewById(R.id.redeem_date);
		tv_fine_print = (TextView) this.findViewById(R.id.fine_print);
		tv_recipient = (TextView) this.findViewById(R.id.recipient);
		tv_terms = (TextView) this.findViewById(R.id.terms);
		tv_instruction = (TextView) this.findViewById(R.id.instruction);
		tv_busniess_name = (TextView) this.findViewById(R.id.busniess_name);
		tv_location = (TextView) this.findViewById(R.id.location);
		tv_phone = (TextView) this.findViewById(R.id.phone);
		tv_price = (TextView) this.findViewById(R.id.price);
		tv_value = (TextView) this.findViewById(R.id.value);
		tv_save = (TextView) this.findViewById(R.id.save);
		img_voucher = (ImageView) this.findViewById(R.id.img_voucher);
		pb_voucher_progress = (ProgressBar) this.findViewById(R.id.voucher_progress);
		ll_redeem_date = (LinearLayout) this.findViewById(R.id.ll_redeem_date);
		ll_expire_date = (LinearLayout) this.findViewById(R.id.ll_expire_date);

		refreshVoucherPageView();

		imgbtn_voucherpage_back = (ImageButton) this.findViewById(R.id.imgbtn_back2);

		imgbtn_voucherpage_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent ia = new Intent(VoucherPageView.this, VoucherListView.class);
				startActivityForResult(ia, Globals.VOUCHER_LIST_VIEW);
				finish();
			}
		});

		showVoucherImage(sVoucherId);
	}

	/*
	 * Delete VoucherImg
	 * 
	 * @ String - voucher id
	 */
	public void deleteVoucherImg(String filename) {
		if (ExternalStorageUtils.isExternalStorageAvailable() && !ExternalStorageUtils.isExternalStorageReadOnly()) {
			try {
				FileUtils.deleteExternalStorageFile(this, "VoucherImg", filename);
			} catch (Exception e) {
			}
		}
	}

	/*
	 * Display VoucherPage
	 */
	public void refreshVoucherPageView() {

		Bundle extras = getIntent().getExtras();

		sVoucherId = extras.getString(KEY_VOUCHER_ID);
		sVoucherTitle = extras.getString(KEY_VOUCHER_TITLE);
		sVoucherContent = extras.getString(KEY_VOUCHER_CONTENT);
		sVoucherPrice = extras.getString(KEY_VOUCHER_PRICE);
		sVoucherSaving = extras.getString(KEY_VOUCHER_SAVING);
		sVoucherValue = extras.getString(KEY_VOUCHER_VALUE);
		sVoucherStatus = extras.getString(KEY_VOUCHER_STATUS);
		sVoucherCode = extras.getString(KEY_VOUCHER_CODE);
		sPurchedDate = extras.getString(KEY_PURCHED_DATE);
		sExpireDate = extras.getString(KEY_EXPIRE_DATE);
		sRedeemDate = extras.getString(KEY_REDEEM_DATE);
		sFinePrint = extras.getString(KEY_FINE_PRINT);
		sTerms = extras.getString(KEY_TERMS);
		sInstruction = extras.getString(KEY_INSTRUCTION);

		sVoucherImgUrl = extras.getString(KEY_VOUCHER_IMG_URL);
		sBusinessName = extras.getString(KEY_BUSINESS_NAME);
		sAddress1 = extras.getString(KEY_ADDRESS1);
		sAddress2 = extras.getString(KEY_ADDRESS2);
		sCity = extras.getString(KEY_CITY);
		sState = extras.getString(KEY_STATE);
		sZipcode = extras.getString(KEY_ZIPCODE);
		sPhone = extras.getString(KEY_PHONE);
		// iLatitude = extras.getInt(KEY_LATITUDE);
		// iLongitude = extras.getInt(KEY_LONGITUDE);

		sRecipientFirstName = ((User) getApplication()).loadfirstnameFrompreference();
		sRecipientLastName = ((User) getApplication()).loadlastnameFrompreference();

		tv_vouchertitle.setText(sVoucherTitle);
		tv_vouchercontent.setText(sVoucherContent);

		tv_value.setText(sVoucherValue);
		tv_price.setText(sVoucherPrice);
		tv_save.setText(sVoucherSaving);

		if (sVoucherCode.equals(_nullVoucherCode)) {
			tv_vouchercode.setText(sVoucherStatus);

		} else {
			tv_vouchercode.setText("VOUCHER#" + sVoucherCode);
		}

		sRecipient = sRecipientFirstName + " " + sRecipientLastName;
		tv_recipient.setText(sRecipient);

		tv_purched_date.setText(sPurchedDate);

		if (sRedeemDate.equals(_nullRedeem)) {
			// Hidden redeemed date
			ll_redeem_date.setVisibility(View.GONE);

		} else {
			tv_redeem_date.setText(sRedeemDate);
			// Hidden expire date
			ll_expire_date.setVisibility(View.GONE);
		}

		tv_expire_date.setText(sExpireDate);

		if (sTerms.equals(_nullTerms)) {
			tv_terms.setVisibility(View.GONE);

		} else {
			tv_terms.setText(sTerms);
		}

		tv_fine_print.setText(HtmlUtils.convertHtmltoString(sFinePrint));

		tv_instruction.setText(sInstruction);

		tv_busniess_name.setText(sBusinessName);

		sLocation = sAddress1 + " " + sAddress2 + ", " + sCity + ", " + sState + " " + sZipcode;
		tv_location.setText(sLocation);
		tv_phone.setText(sPhone);
	}

	// Voucher Image DownLoader
	public void showVoucherImage(String filename) {
		int len = 1024;
		byte[] buffer = new byte[len];
		if (!ExternalStorageUtils.isExternalStorageReadOnly()) {
			try {
				File file = new File(getExternalFilesDir("voucherImg"), filename);
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int nrb = fis.read(buffer, 0, len);
				while (nrb != -1) {
					baos.write(buffer, 0, nrb);
					nrb = fis.read(buffer, 0, len);
				}
				buffer = baos.toByteArray();
				Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
				pb_voucher_progress.setVisibility(View.GONE);
				img_voucher.setImageBitmap(bm);
				fis.close();
			} catch (FileNotFoundException e) {
				if (NetworkUtils.isNetworkAvailable(this)) {
					new VoucherImgDownloaderTask().execute();
				} else {
					// Resolve the BadTokenException
					Context context = this;
					CharSequence text = getString(R.string.nosdcard);
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
			} catch (IOException e) {
			}
		}

	}

	private void showVoucherImage(Bitmap b) {
		if (b != null) {
			byte[] temp = null;
			temp = FileUtils.BitmapToBytes(b);
			if (ExternalStorageUtils.isExternalStorageAvailable()) {
				VoucherPageUtils.writeToExternalStoragePrivate(this, sVoucherId, temp);
			}
			img_voucher.setImageBitmap(b);

		} else {
		}
	}

	class VoucherImgDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		public Bitmap doInBackground(String... params) {
			Bitmap b;
			Bundle extras = getIntent().getExtras();
			sVoucherImgUrl = extras.getString(KEY_VOUCHER_IMG_URL);
			b = NetworkUtils.returnBitMap(sVoucherImgUrl);
			return b;
		}

		@Override
		public void onPreExecute() {
			pb_voucher_progress.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPostExecute(Bitmap bm) {
			showVoucherImage(bm);
			pb_voucher_progress.setVisibility(View.GONE);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, MENU_REFRESH, 0, "Refresh").setIcon(android.R.drawable.ic_menu_rotate);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case MENU_REFRESH:
			img_voucher.setImageBitmap(null);
			deleteVoucherImg(sVoucherId);
			showVoucherImage(sVoucherId);
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(VoucherPageView.this, VoucherListView.class);
		startActivity(intent);
		finish();
	}

}
