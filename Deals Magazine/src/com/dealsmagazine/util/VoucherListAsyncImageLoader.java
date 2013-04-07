/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;


public class VoucherListAsyncImageLoader {
	
	String _nullImgUrl = new String("");

	private HashMap<String, SoftReference<Bitmap>> imageCache;

	/*
	 * Soft reference for store the image
	 */
	public VoucherListAsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	public Bitmap loadBitmap(final String imageUrl, final ImageCallback imageCallback) {
		
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			Bitmap bm = softReference.get();
			// Control download status
			if (bm != null || imageUrl.equals(_nullImgUrl)) {
				return bm;
		}
			
		}		
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
			}
		};
		new Thread() {
			@Override
			public void run() {

				Bitmap bm = NetworkUtils.returnBitMap(imageUrl);
				imageCache.put(imageUrl, new SoftReference<Bitmap>(bm));
				Message message = handler.obtainMessage(0, bm);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageBitmap, String imageUrl);
	}

}
