/*******************************************************************************
 * Copyright (c) 2011 Erik Byström.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package se.slackers.cube;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * @author Erik Byström
 * 
 */
public class Usage {
	public static final String ID = "UA-22737397";
	private static final int INTERVAL = 30;

	public static final String ALGORITHM_ID = "/algorithm/%s";
	public static final String QUICK_LIST_ADD = "/quick-list/add/%s";
	public static final String QUICK_LIST_REMOVE = "/quick-list/remove/%s";
	public static final String QUICK_LIST = "/quick-list";
	public static final String PROPERTIES = "/properties";
	public static final String HELP = "/help";
	public static final String NOTATION = "/notation";
	public static final String PERMUTATION = "/permutation";

	public static GoogleAnalyticsTracker start(final Context context) {
		final GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start(ID, INTERVAL, context);
		return tracker;
	}
}
