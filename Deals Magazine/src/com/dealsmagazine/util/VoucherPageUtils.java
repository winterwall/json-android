/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class VoucherPageUtils {

	/*
	 * Write to external storage using the latest Level 8 APIs.
	 * 
	 * @param filename - the filename to write to
	 * 
	 * @param content - the content to write
	 */
	public static void writeToExternalStoragePrivate(Context context, String filename, byte[] content) {
		if (ExternalStorageUtils.isExternalStorageAvailable() && !ExternalStorageUtils.isExternalStorageReadOnly()) {
			try {
				File file = new File(context.getExternalFilesDir("voucherImg"), filename);
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(content);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
