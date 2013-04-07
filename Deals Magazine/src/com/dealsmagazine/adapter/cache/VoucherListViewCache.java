/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.adapter.cache;

import com.dealsmagazine.buyer.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Voucher ListView cache class
 */
public class VoucherListViewCache {
	
	// SVN test
	
	private View v_baseview;
	private ImageView iv_test;
	private TextView tv_title;
	private TextView tv_date;
	private TextView tv_status;
	
	public VoucherListViewCache( View baseView){
		this.v_baseview = baseView;
	}
	
	public ImageView getImageView(){
		if(iv_test == null){
			iv_test = (ImageView) v_baseview.findViewById(R.id.imageViewtest);
		}
		return iv_test;
	}
	
	public TextView getTitleTextView(){
		if(tv_title ==null){
			tv_title = (TextView) v_baseview.findViewById(R.id.rowtext_title);
		}
		return tv_title;
	}
	
	public TextView getDateTextView(){
		if(tv_date ==null){
			tv_date = (TextView) v_baseview.findViewById(R.id.rowtext_date);
		}
		return tv_date;
	}
	
	public TextView getStatusTextView(){
		if(tv_status ==null){
			tv_status = (TextView) v_baseview.findViewById(R.id.rowtext_status);
			
		}
		return tv_status;
	}

}
