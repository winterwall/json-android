/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.util;

/*
 * Timer class for notify and download management
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimerUtils {

	static final String _emptyString = new String("");

	static SimpleDateFormat df_day = new SimpleDateFormat("yyyy-MM-dd");

	public static boolean isDownloadTimeAvailable(String updated) {

		boolean b = false;

		// create new Date object, by default it will be set to the current time
		// Last updated time
		Date date_updated = new Date();

		// Current time
		Date date_now = new Date();

		try {
			// Format the updated date to each day begin,
			// example: 2012-3-12 12:30 will change to 2012-3-12 00:00
			date_updated = df_day.parse(updated);
		} catch (Exception e) {
		}

		// long_schedule is the schedule time point when download is available,
		// example: 1331683200000
		Long long_schedule = date_updated.getTime();

		// add 24 hours
		long_schedule += (24 * 60 * 60 * 1000);

		// Now time in long type
		Long long_now = date_now.getTime();

		// Current time should only later than schedule time
		b = long_now > long_schedule;

		// no updated record
		if (updated.equals(_emptyString)) {
			b = true;
		}

		return b;
	}

	// if database updated 2 days before, give the download notice
	public static boolean isCheckTimeAvailable(String updated) {

		boolean b = false;

		Date date_updated = new Date();
		Date date_now = new Date();

		try {
			date_updated = df_day.parse(updated);
		} catch (Exception e) {
		}

		Long long_schedule = date_updated.getTime();

		// add 48 hours
		long_schedule += (24 * 60 * 60 * 1000);
		Long long_now = date_now.getTime();
		b = long_now > long_schedule;

		if (updated.equals(_emptyString)) {
			b = true;
		}

		return b;
	}

}
