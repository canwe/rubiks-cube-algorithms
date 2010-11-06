package se.slackers.cube.activity;

import se.slackers.cube.Config;
import se.slackers.cube.R;
import se.slackers.cube.config.ColorTheme;
import se.slackers.cube.config.CubeSize;
import se.slackers.cube.config.DoubleNotation;
import se.slackers.cube.config.NotationType;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class CubePreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(final Bundle state) {
		super.onCreate(state);
		getPreferenceManager().setSharedPreferencesName(Config.PREFERENCES);
		setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		final Context context = this;
		final PreferenceScreen root = getPreferenceManager().createPreferenceScreen(context);

		final PreferenceCategory cubeCategory = new PreferenceCategory(context);
		cubeCategory.setTitle(R.string.categoryAppearance);
		root.addPreference(cubeCategory);

		// Color theme
		final ListPreference colorThemeList = new ListPreference(context);
		colorThemeList.setKey(Config.COLOR_THEME);
		colorThemeList.setDefaultValue(ColorTheme.White.name());
		colorThemeList.setTitle(R.string.themeTitle);
		colorThemeList.setSummary(R.string.themeSummary);
		colorThemeList.setDialogTitle(R.string.themeDialog);
		colorThemeList.setEntries(R.array.themeContent);
		colorThemeList.setEntryValues(R.array.themeValues);

		// Cube size
		final ListPreference sizeList = new ListPreference(context);
		sizeList.setKey(Config.CUBE_SIZE);
		sizeList.setDefaultValue(CubeSize.Small.name());
		sizeList.setTitle(R.string.sizeTitle);
		sizeList.setSummary(R.string.sizeSummary);
		sizeList.setDialogTitle(R.string.sizeDialog);
		sizeList.setEntries(R.array.sizeContent);
		sizeList.setEntryValues(R.array.sizeValues);

		cubeCategory.addPreference(colorThemeList);
		cubeCategory.addPreference(sizeList);

		// Notation
		final PreferenceCategory notationCategory = new PreferenceCategory(context);
		notationCategory.setTitle(R.string.categoryNotation);
		root.addPreference(notationCategory);

		final ListPreference notation = new ListPreference(this);
		notation.setKey(Config.NOTATION);
		notation.setDefaultValue(NotationType.Singmaster.name());
		notation.setTitle(R.string.notationTitle);
		notation.setSummary(R.string.notationSummary);
		notation.setDialogTitle(R.string.notationTitle);
		notation.setEntries(R.array.notationContent);
		notation.setEntryValues(R.array.notationValues);

		final ListPreference doubleNotation = new ListPreference(context);
		doubleNotation.setKey(Config.NOTATION_DOUBLE);
		doubleNotation.setDefaultValue(DoubleNotation.SuperScript.name());
		doubleNotation.setTitle(R.string.notationDoubleTitle);
		doubleNotation.setSummary(R.string.notationDoubleSummary);
		doubleNotation.setDialogTitle(R.string.notationDoubleTitle);
		doubleNotation.setEntries(R.array.notationDoubleContent);
		doubleNotation.setEntryValues(R.array.notationDoubleValues);

		final CheckBoxPreference triggers = new CheckBoxPreference(context);
		triggers.setKey(Config.SHOW_TRIGGERS);
		triggers.setDefaultValue(true);
		triggers.setTitle(R.string.notationTriggerTitle);
		triggers.setSummary(R.string.notationTriggerSummary);

		final CheckBoxPreference reverseColor = new CheckBoxPreference(context);
		reverseColor.setKey(Config.NOTATION_COLOR_REVERSE);
		reverseColor.setDefaultValue(true);
		reverseColor.setTitle(R.string.notationReverseTitle);
		reverseColor.setSummary(R.string.notationReverseSummary);

		notationCategory.addPreference(notation);
		notationCategory.addPreference(doubleNotation);
		notationCategory.addPreference(triggers);
		notationCategory.addPreference(reverseColor);

		return root;
	}
}