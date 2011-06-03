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

package se.slackers.cube.activity;

import se.slackers.cube.R;
import se.slackers.cube.model.permutation.Permutation;
import android.content.ContentValues;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class CubePreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(final Bundle state) {
		super.onCreate(state);
		addPreferencesFromResource(R.xml.preferences);

		final Preference reset = findPreference("reset");
		reset.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				final ContentValues values = new ContentValues();
				values.put(Permutation.VIEWS, 0);
				getContentResolver().update(Permutation.CONTENT_URI, values, null, null);
				Toast.makeText(CubePreferencesActivity.this, R.string.pref_reset_toast, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
	}
}
