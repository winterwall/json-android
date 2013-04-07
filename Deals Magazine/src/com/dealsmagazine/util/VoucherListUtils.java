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

public class VoucherListUtils {

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
				File file = new File(context.getExternalFilesDir(null), filename);
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

	/*
	 * Delete external private file
	 * 
	 * @param filename - the filename to delete
	 */
	public static void deleteExternalStoragePrivateFile(Context context, String filename) {
		File file = new File(context.getExternalFilesDir(null), filename);
		if (file != null) {
			file.delete();
		}
	}

	/*
	 * Writes content to internal storage making the content private to the
	 * application. The method can be easily changed to take the MODE as
	 * argument and let the caller dictate the visibility: MODE_PRIVATE,
	 * MODE_WORLD_WRITEABLE, MODE_WORLD_READABLE, etc.
	 * 
	 * @param filename - the name of the file to create
	 * 
	 * @param content - the content to write
	 */
	public static void writeInternalStoragePrivate(Context context, String filename, byte[] content) {
		try {
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(content);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Delete internal private file
	 * 
	 * @param filename - the filename to delete
	 */
	public static void deleteInternalStoragePrivate(Context context, String filename) {
		File file = context.getFileStreamPath(filename);
		if (file != null) {
			file.delete();
		}
	}

}
