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

import se.slackers.cube.config.ColorTheme;
import se.slackers.cube.config.DoubleNotation;
import se.slackers.cube.config.NotationType;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Paint;
import android.preference.PreferenceManager;

public class Config {
	public static final String COLOR_THEME = "color.theme";
	public static final String NOTATION = "cube.notation";
	public static final String NOTATION_DOUBLE = "cube.notation.double";
	public static final String NOTATION_COLOR_REVERSE = "cube.notation.reverse";
	public static final String SHOW_TRIGGERS = "cube.triggers";
	public static final String SHOW_DIALOG = "cube.dialog";

	public static final int COLOR = 0xffffffff;
	public static final int REVERSE_COLOR = 0xffa0a0a0;

	private DoubleNotation doubleTurns;
	private final boolean coloredReverse;
	private boolean triggers;
	private int showDialog;

	private NotationType notationScheme;

	private int unknownFaceColor;
	private int importantFaceColor;
	private int borderColor;
	private int backgroundColor;
	private int edgeColor;
	private int cornerColor;
	private int sideFaceColor;

	private final int cubeSizeList;
	private final int cubeSizeView;

	private final SharedPreferences settings;

	public Config(final Context context) {
		settings = PreferenceManager.getDefaultSharedPreferences(context);

		final Resources resources = context.getResources();
		cubeSizeList = resources.getDimensionPixelSize(R.dimen.cube_size_list);
		cubeSizeView = resources.getDimensionPixelSize(R.dimen.cube_size_view);

		setColorTheme(getString(COLOR_THEME, ColorTheme.Default.name()));
		setNotationScheme(getString(NOTATION, NotationType.Singmaster.name()));
		setDoubleTurns(getString(NOTATION_DOUBLE, DoubleNotation.SuperScript.name()));
		coloredReverse = settings.getBoolean(NOTATION_COLOR_REVERSE, true);
		triggers = settings.getBoolean(SHOW_TRIGGERS, true);
		showDialog = settings.getInt(SHOW_DIALOG, 0);
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

	private void setColorTheme(final String theme) {
		switch (ColorTheme.valueOf(theme)) {
		default:
		case White:
			unknownFaceColor = 0xff4d4d4d;
			importantFaceColor = 0xfff0f0f0;
			borderColor = 0xff000000;
			edgeColor = 0xffcc0000;
			cornerColor = 0xff003399;
			break;
		case Blue:
			unknownFaceColor = 0xff404040;
			importantFaceColor = 0xff3366cc;
			borderColor = 0xff000000;
			edgeColor = 0xffcc0000;
			cornerColor = 0xff00cc00;
			break;
		case Green:
			unknownFaceColor = 0xffbfbfbf;
			importantFaceColor = 0xff00cc00;
			borderColor = 0xff000000;
			edgeColor = 0xffcc0000;
			cornerColor = 0xff0000cc;
			break;
		case Yellow:
			unknownFaceColor = 0xffbfbfbf;
			importantFaceColor = 0xffffcc00;
			borderColor = 0xff000000;
			edgeColor = 0xffcc0000;
			cornerColor = 0xff003399;
			break;
		case Red:
			unknownFaceColor = 0xffbfbfbf;
			importantFaceColor = 0xffcc0000;
			borderColor = 0xff000000;
			edgeColor = 0xff00cc00;
			cornerColor = 0xff003399;
			break;
		case Orange:
			unknownFaceColor = 0xffbfbfbf;
			importantFaceColor = 0xffff6600;
			borderColor = 0xff000000;
			edgeColor = 0xff00cc00;
			cornerColor = 0xff003399;
			break;
		}
		sideFaceColor = importantFaceColor;
		backgroundColor = borderColor;
	}

	public int getListCubeSize() {
		return cubeSizeList;
	}

	public int getViewCubeSize() {
		return cubeSizeView;
	}

	public int getUnknownFaceColor() {
		return unknownFaceColor;
	}

	public Paint getImportantFaceColor() {
		final Paint paint = new Paint();
		paint.setColor(importantFaceColor);
		return paint;
	}

	public Paint getBorderColor() {
		final Paint paint = new Paint();
		paint.setColor(borderColor);
		return paint;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public int getEdgeColor() {
		return edgeColor;
	}

	public int getCornerColor() {
		return cornerColor;
	}

	public int getSideFaceColor() {
		return sideFaceColor;
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

	public void setNotationScheme(final String notationScheme) {
		this.notationScheme = NotationType.valueOf(notationScheme);
	}

	public DoubleNotation getDoubleTurns() {
		return doubleTurns;
	}

	public void setDoubleTurns(final String string) {
		this.doubleTurns = DoubleNotation.valueOf(string);
	}
}
