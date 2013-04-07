/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.entity;

/*
 * Voucher class
 * 
 * JSON Format
 * 
 */
public class Voucher {
	public String id;
	public String voucher_code;
	public String expire_date;
	public String purched_date;
	public String redeem_date;
	public String status;
	public String business_name;
	public String title;
	public String content;
	public String price;
	public String value;
	public String saving;
	public String fine_print;
	public String barcode_img_url;
	public String voucher_img_url;
	public String voucher_thumbnail_img_url;
	public String recipient;
	public String terms;
	public String instruction;
	public String name;
	public String address1;
	public String address2;
	public String city;
	public String state;
	public String zipcode;
	public String phone;
	public int latitude;
	public int longitude;

	public String getId() {
		return id;
	}

	public String getVoucherCode() {
		return voucher_code;
	}

	public String getExpireDate() {
		return expire_date;
	}

	public String getPurchedDate() {
		return purched_date;
	}

	public String getRedeemDate() {
		return redeem_date;
	}

	public String getStatus() {
		return status;
	}

	public String getBusinessName() {
		return business_name;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getPrice() {
		return price;
	}

	public String getValue() {
		return value;
	}

	public String getSaving() {
		return saving;
	}

	public String getFinePrint() {
		return fine_print;
	}

	public String getBCImgUrl() {
		return barcode_img_url;
	}

	public String getVImgUrl() {
		return voucher_img_url;
	}

	public String getVTNImgUrl() {
		return voucher_thumbnail_img_url;
	}

	public String getRecipient() {
		return recipient;
	}

	public String getTerms() {
		return terms;
	}

	public String getInstruction() {
		return instruction;
	}

	public String getName() {
		return name;
	}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public String getPhone() {
		return phone;
	}

	public int getLatitude() {
		return latitude;
	}

	public int getLongitude() {
		return longitude;
	}
}
