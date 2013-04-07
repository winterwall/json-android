/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.util;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;

/*
 * File Utils
 */
public class FileUtils {

	/*
	 * Convert between byte[] and String
	 */
	public static byte[] convertStringToByteArray(String s) {
		byte[] b = s.getBytes();
		return b;
	}

	public static String convertByteArrayToString(byte[] b) {
		String s = new String(b);
		return s;
	}

	/*
	 * Convert between Bitmap to Bytes
	 */
	public static byte[] BitmapToBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	/*
	 * Delete files more than m month old
	 */
	public static void deleteExternalStorageOldFile(Context context, String file_dir, int m) {

		try {
			File dir = new File(context.getExternalFilesDir(file_dir).toString());
			File[] files = dir.listFiles();
			Long MAXFILEAGE = 2678400000L * m;
			for (File f : files) {
				Long lastmodified = f.lastModified();
				if (lastmodified + MAXFILEAGE < System.currentTimeMillis()) {
					f.delete();
				}
			}
		} catch (Exception e) {
		}
	}

	/*
	 * Delete all Files
	 */
	public static void deleteExternalStorageAll(Context context, String file_dir) {
		try {
			File dir = new File(context.getExternalFilesDir(file_dir).toString());
			File[] files = dir.listFiles();
			for (File f : files) {
				f.delete();
			}
		} catch (Exception e) {
		}
	}

	/*
	 * Delete File
	 */
	public static void deleteExternalStorageFile(Context context, String file_dir, String filename) {

		try {
			File dir = new File(context.getExternalFilesDir(file_dir).toString(), filename);

			if (dir != null) {
				dir.delete();
			}
		} catch (Exception e) {
		}
	}

}
