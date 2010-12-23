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

import se.slackers.cube.Config;
import se.slackers.cube.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.admob.android.ads.AdManager;

public abstract class BaseActivity extends Activity {

	protected static final int MENU_PROPERTIES = 1;
	protected static final int MENU_INFO = 2;
	protected static final int MENU_HELP = 3;

	protected Config config;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		config = new Config(this);

		AdManager.setTestDevices(new String[] { AdManager.TEST_EMULATOR });
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, BaseActivity.MENU_INFO, 0, R.string.info).setIcon(R.drawable.ic_menu_info_details);
		menu.add(0, BaseActivity.MENU_HELP, 0, R.string.help).setIcon(R.drawable.ic_menu_help);
		menu.add(0, BaseActivity.MENU_PROPERTIES, 0, R.string.properties).setIcon(R.drawable.ic_menu_preferences);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case BaseActivity.MENU_PROPERTIES:
			startActivityForResult(new Intent(this, CubePreferencesActivity.class), BaseActivity.MENU_PROPERTIES);
			return true;
		case BaseActivity.MENU_INFO:
			startActivityForResult(new Intent(this, InfoActivity.class), BaseActivity.MENU_INFO);
			return true;
		case BaseActivity.MENU_HELP:
			startActivityForResult(new Intent(this, HelpActivity.class), BaseActivity.MENU_HELP);
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch (requestCode) {
		case BaseActivity.MENU_PROPERTIES:
			final Intent intent = new Intent();
			intent.setClass(this, this.getClass());
			final Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			this.startActivity(intent);
			this.finish();
			break;
		}
	}
}
