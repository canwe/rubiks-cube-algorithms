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
import android.view.MenuInflater;
import android.view.MenuItem;

public abstract class BaseActivity extends Activity {
	protected Config config;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		config = new Config(this);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.default_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_properties:
			startActivityForResult(new Intent(this, CubePreferencesActivity.class), item.getItemId());
			return true;
		case R.id.menu_info:
			startActivityForResult(new Intent(this, InfoActivity.class), item.getItemId());
			return true;
		case R.id.menu_notation:
			startActivityForResult(new Intent(this, NotationActivity.class), item.getItemId());
			return true;
		case R.id.menu_filtered:
			startActivityForResult(new Intent(this, FilterListActivity.class), item.getItemId());
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch (requestCode) {
		case R.id.menu_properties:
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
