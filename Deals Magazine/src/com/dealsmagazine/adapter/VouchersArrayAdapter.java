/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.adapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.dealsmagazine.adapter.cache.VoucherListViewCache;
import com.dealsmagazine.entity.Voucher;
import com.dealsmagazine.util.ExternalStorageUtils;
import com.dealsmagazine.util.VoucherListAsyncImageLoader;
import com.dealsmagazine.util.VoucherListAsyncImageLoader.ImageCallback;
import com.dealsmagazine.buyer.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/*
 * ListView Vouchers ArrayAdapter
 */
public class VouchersArrayAdapter extends ArrayAdapter<Voucher> {

	private final Activity context;
	private final ArrayList<Voucher> vouchers;
	private int resourceId;
	private ListView listView;
	private VoucherListAsyncImageLoader voucherListAsyncImageLoader;

	String _statusActive = new String("Active");
	String _statusRedeemed = new String("Redeemed");

	/*
	 * Constructor
	 * 
	 * @param context - the application content
	 * 
	 * @param resourceId - the ID of the resource/view
	 * 
	 * @param vouchers - the bound ArrayList
	 */
	public VouchersArrayAdapter(Activity context, int resourceId, ArrayList<Voucher> vouchers, ListView listView) {
		super(context, resourceId, vouchers);
		this.context = context;
		this.vouchers = vouchers;
		this.resourceId = resourceId;
		this.listView = listView;
		voucherListAsyncImageLoader = new VoucherListAsyncImageLoader();
	}

	/*
	 * Updates the view
	 * 
	 * @param position - the ArrayList position to update
	 * 
	 * @param convertView - the view to update/inflate if needed
	 * 
	 * @param parent - the groups parent view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		VoucherListViewCache voucherListViewCache;

		if (rowView == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = vi.inflate(resourceId, null);
			voucherListViewCache = new VoucherListViewCache(rowView);
			rowView.setTag(voucherListViewCache);

		} else {
			voucherListViewCache = (VoucherListViewCache) rowView.getTag();
		}

		Voucher voucher = vouchers.get(position);
		String temp = voucher.voucher_thumbnail_img_url;
		ImageView imageView = voucherListViewCache.getImageView();
		imageView.setTag(temp);

		if (isConnectInternet()) {
			Bitmap cachedImage = voucherListAsyncImageLoader.loadBitmap(temp, new ImageCallback() {
				public void imageLoaded(Bitmap imageBitmap, String temp) {
					ImageView imageViewByTag = (ImageView) listView.findViewWithTag(temp);

					if (imageViewByTag != null) {
						imageViewByTag.setImageBitmap(imageBitmap);

					} else {
					}
				}
			});

			TextView titleText = voucherListViewCache.getTitleTextView();
			titleText.setText(voucher.title);
			TextView dateText = voucherListViewCache.getDateTextView();
			TextView statusText = voucherListViewCache.getStatusTextView();

			if (voucher.status.equals(_statusActive)) {
				dateText.setText("Expires: " + voucher.expire_date);
				statusText.setText("ACTIVE");
				statusText.setTextColor(0xff2A2724); // dark

			} else if (voucher.status.equals(_statusRedeemed)) {
				dateText.setText("Redeemed at: " + voucher.redeem_date);
				statusText.setText("INACTIVE");
				statusText.setTextColor(0xff878179); // light

			} else {
				dateText.setText("Expires: " + voucher.expire_date);
				statusText.setText("INACTIVE");
				statusText.setTextColor(0xff878179); // light
			}

			if (cachedImage == null) {
				imageView.setImageResource(R.drawable.icon);
			} else {
				imageView.setImageBitmap(cachedImage);
			}

		} else {

			TextView titleText = voucherListViewCache.getTitleTextView();
			titleText.setText(voucher.title);
			TextView dateText = voucherListViewCache.getDateTextView();
			TextView statusText = voucherListViewCache.getStatusTextView();

			if (voucher.status.equals(_statusActive)) {
				dateText.setText("Expires: " + voucher.expire_date);
				statusText.setText("ACTIVE");
				statusText.setTextColor(0xff2A2724); // dark

			} else if (voucher.status.equals(_statusRedeemed)) {
				dateText.setText("Redeemed at: " + voucher.redeem_date);
				statusText.setText("INACTIVE");
				statusText.setTextColor(0xff878179); // light

			} else {
				dateText.setText("Expires: " + voucher.expire_date);
				statusText.setText("INACTIVE");
				statusText.setTextColor(0xff878179); // light
			}

			String temp_img = voucher.id;
			int len = 1024;
			byte[] buffer = new byte[len];

			if (!ExternalStorageUtils.isExternalStorageReadOnly()) {
				try {
					File file = new File(context.getExternalFilesDir("voucherImg"), temp_img);
					FileInputStream fis = new FileInputStream(file);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int nrb = fis.read(buffer, 0, len);
					while (nrb != -1) {
						baos.write(buffer, 0, nrb);
						nrb = fis.read(buffer, 0, len);
					}
					buffer = baos.toByteArray();
					Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
					imageView.setImageBitmap(bm);
					fis.close();
				} catch (FileNotFoundException e) {
					imageView.setImageResource(R.drawable.icon);
				} catch (IOException e) {
					imageView.setImageResource(R.drawable.icon);
				}
			}
		}
		return rowView;
	}

	public boolean isConnectInternet() {
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}
}
