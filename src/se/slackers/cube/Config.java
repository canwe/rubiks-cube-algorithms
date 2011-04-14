/*******************************************************************************
 * Copyright (c) 2010 Erik Byström.
 * 
 * This file is part of Rubik's Cube Algorithms.
 * 
 * Rubik's Cube Algorithms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Rubik's Cube Algorithms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Rubik's Cube Algorithms.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package se.slackers.cube;

import se.slackers.cube.config.DoubleNotation;
import se.slackers.cube.config.NotationType;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.Log;

public class Config {
	public static final String QUICKLIST = "quicklist";
	public static final String QUICKLIST_SORT = "quicklist.sort";
	public static final String NOTATION = "cube.notation";
	public static final String NOTATION_DOUBLE = "cube.notation.double";
	public static final String NOTATION_COLOR_REVERSE = "cube.notation.reverse";
	public static final String SHOW_TRIGGERS = "cube.triggers";
	public static final String SHOW_DIALOG = "cube.dialog";

	public static final int COLOR = 0xffffffff;
	public static final int REVERSE_COLOR = 0xffa0a0a0;
	private static final String TAG = "CubeAlgorithms";

	private final boolean coloredReverse;
	private boolean triggers;
	private int showDialog;
	private final boolean startWithQuickList;
	private final boolean sortQuickListByViews;

	private final NotationType notationScheme;
	private final DoubleNotation doubleTurns;

	private final int faceUnknown;
	private final int faceImportant;
	private final int faceSide;
	private final int arrowEdge;
	private final int arrowCorner;
	private final int borderColor;
	private final int backgroundColor;

	private final int cubeSizeList;
	private final int cubeSizeView;

	private final SharedPreferences settings;

	public Config(final Context context) {
		settings = PreferenceManager.getDefaultSharedPreferences(context);

		final Resources resources = context.getResources();
		cubeSizeList = resources.getDimensionPixelSize(R.dimen.cube_size_list);
		cubeSizeView = resources.getDimensionPixelSize(R.dimen.cube_size_view);

		faceUnknown = resources.getColor(R.color.face_unknown);
		faceImportant = resources.getColor(R.color.face_important);
		faceSide = resources.getColor(R.color.face_side);
		arrowEdge = resources.getColor(R.color.arrow_edge);
		arrowCorner = resources.getColor(R.color.arrow_corner);
		borderColor = resources.getColor(R.color.border);
		backgroundColor = resources.getColor(R.color.background);

		notationScheme = NotationType.valueOf(getString(NOTATION, NotationType.Singmaster.name()));
		doubleTurns = DoubleNotation.valueOf(getString(NOTATION_DOUBLE, DoubleNotation.Superscript.name()));

		startWithQuickList = settings.getBoolean(QUICKLIST, false);
		sortQuickListByViews = settings.getBoolean(QUICKLIST_SORT, false);
		coloredReverse = settings.getBoolean(NOTATION_COLOR_REVERSE, true);
		triggers = settings.getBoolean(SHOW_TRIGGERS, true);
		showDialog = settings.getInt(SHOW_DIALOG, 0);
	}

	public boolean startWithQuickList() {
		return startWithQuickList;
	}

	public boolean isFirstStart() {
		return showDialog == 0;
	}

	public int getMessageDialogVersion() {
		return showDialog;
	}

	public void setMessageDialogVersion(final int value) {
		final Editor edit = settings.edit();
		showDialog = value;
		edit.putInt(SHOW_DIALOG, value);
		edit.commit();
	}

	public boolean isColoredReverse() {
		return coloredReverse;
	}

	private String getString(final String key, final String defaultValue) {
		try {
			return settings.getString(key, defaultValue);
		} catch (final Exception e) {
			return defaultValue;
		}
	}

	public int getListCubeSize() {
		return cubeSizeList;
	}

	public int getViewCubeSize() {
		return cubeSizeView;
	}

	private Paint asPaint(final int color) {
		final Paint p = new Paint();
		p.setColor(color);
		return p;
	}

	public Paint getUnknownFaceColor() {
		return asPaint(faceUnknown);
	}

	public Paint getImportantFaceColor() {
		return asPaint(faceImportant);
	}

	public Paint getSideFaceColor() {
		return asPaint(faceSide);
	}

	public Paint getBorderColor() {
		return asPaint(borderColor);
	}

	public Paint getBackgroundColor() {
		return asPaint(backgroundColor);
	}

	public Paint getEdgeColor() {
		return asPaint(arrowEdge);
	}

	public Paint getCornerColor() {
		return asPaint(arrowCorner);
	}

	public boolean isTriggers() {
		return triggers;
	}

	public void setTriggers(final boolean triggers) {
		this.triggers = triggers;
	}

	public NotationType getNotationScheme() {
		return notationScheme;
	}

	public DoubleNotation getDoubleTurns() {
		return doubleTurns;
	}

	public boolean sortQuickListByViews() {
		return sortQuickListByViews;
	}

	public static void debug(final String format, final Object... args) {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			debug(String.format(format, args));
		}
	}

	public static void debug(final String msg) {
		Log.d(TAG, msg);
	}
}
