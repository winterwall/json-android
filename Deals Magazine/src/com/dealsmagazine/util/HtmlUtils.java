/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.util;

import android.text.Html;
import android.text.Spanned;

public class HtmlUtils {

	/*
	 * Convert Html to String
	 */
	public static String convertHtmltoString(String html) {
		String result = "";

		if (html == null || html.equals("")) {
			result = "";

		} else {
			Spanned html_spanned = Html.fromHtml(html);
			result = html_spanned.toString();

		}
		return result;
	}

}
