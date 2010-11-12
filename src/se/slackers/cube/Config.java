/*******************************************************************************
 * Copyright (c) 2010 Erik Byström.
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

import se.slackers.cube.config.ColorTheme;
import se.slackers.cube.config.CubeSize;
import se.slackers.cube.config.DoubleNotation;
import se.slackers.cube.config.NotationType;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.util.DisplayMetrics;

public class Config {
	public static final String COLOR_THEME = "color.theme";
	public static final String CUBE_SIZE = "cube.size";
	public static final String NOTATION = "cube.notation";
	public static final String NOTATION_DOUBLE = "cube.notation.double";
	public static final String NOTATION_COLOR_REVERSE = "cube.notation.reverse";
	public static final String SHOW_TRIGGERS = "cube.triggers";
	public static final String PREFERENCES = "cube.preferences";
	public static final String SHOW_DIALOG = "cube.dialog";

	public static final int COLOR = 0xffffffff;
	public static final int REVERSE_COLOR = 0xffa0a0a0;

	private final float scale;
	private final int displayWidth;
	private DoubleNotation doubleTurns;
	private final boolean coloredReverse;
	private boolean triggers;
	private int showDialog;

	private NotationType notationScheme;

	private int listWidth;
	private int viewWidth;
	private int margin;
	private int unknownFaceColor;
	private int importantFaceColor;
	private int borderColor;
	private int backgroundColor;
	private int edgeColor;
	private int cornerColor;
	private final int displayHeight;
	private int sideFaceColor;

	private final SharedPreferences settings;

	public Config(final Activity activity) {
		final DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		scale = dm.density;
		displayWidth = dm.widthPixels;
		displayHeight = dm.heightPixels;

		settings = activity.getSharedPreferences(PREFERENCES, 0);
		setCubeSize(getString(CUBE_SIZE, CubeSize.Small.name()));
		setColorTheme(getString(COLOR_THEME, ColorTheme.Default.name()));
		setNotationScheme(getString(NOTATION, NotationType.Singmaster.name()));
		setDoubleTurns(getString(NOTATION_DOUBLE, DoubleNotation.SuperScript.name()));
		coloredReverse = settings.getBoolean(NOTATION_COLOR_REVERSE, true);
		triggers = settings.getBoolean(SHOW_TRIGGERS, true);
		showDialog = settings.getInt(SHOW_DIALOG, 0);
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

	private void setCubeSize(final String size) {
		viewWidth = Math.min(displayWidth, displayHeight) / 3;
		switch (CubeSize.valueOf(size)) {
		case Small:
			listWidth = 50;
			break;
		case Medium:
			listWidth = 75;
			break;
		case Large:
			listWidth = 100;
			break;
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
		margin = 2;
		sideFaceColor = importantFaceColor;
		backgroundColor = borderColor;
	}

	public int getDisplayHeight() {
		return displayHeight;
	}

	public float getScale() {
		return scale;
	}

	public int getListWidth() {
		return (int) (listWidth * scale);
	}

	public int getViewWidth() {
		return (int) (viewWidth * scale);
	}

	public int getDisplayWidth() {
		return displayWidth;
	}

	public int getMargin() {
		return margin;
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
