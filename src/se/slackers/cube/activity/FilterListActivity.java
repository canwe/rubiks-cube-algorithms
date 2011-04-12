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

import java.util.HashMap;
import java.util.Map;

import se.slackers.cube.Config;
import se.slackers.cube.R;
import se.slackers.cube.adapter.PermutationAlgorithmAdapter;
import se.slackers.cube.model.algorithm.Algorithm;
import se.slackers.cube.model.permutation.Permutation;
import se.slackers.cube.provider.AlgorithmProviderHelper;
import se.slackers.cube.render.PermutationRenderer;
import se.slackers.cube.view.PermutationView;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class FilterListActivity extends ListActivity implements OnItemClickListener, OnItemLongClickListener {
	public static final String PERMUTATION = "permutation";
	private PermutationAlgorithmAdapter adapter;
	private WakeLock wakeLock;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.layout_quick_list);

		final Map<Long, Algorithm> algorithms = new HashMap<Long, Algorithm>();

		final ContentResolver resolver = getContentResolver();
		final Cursor favorites = resolver.query(Algorithm.CONTENT_URI, null, Algorithm.RANK + "<>0", null, null);
		try {
			if (favorites.moveToFirst()) {
				do {
					final Algorithm algorithm = Algorithm.fromCursor(favorites, null);
					algorithms.put(algorithm.getPermutationId(), algorithm);
				} while (favorites.moveToNext());
			}
		} finally {
			favorites.close();
		}

		final Config config = new Config(this);

		final boolean sortByViews = config.sortQuickListByViews();
		final String sortBy = (sortByViews ? Permutation.VIEWS + " DESC," : "") + Permutation.TYPE + ","
				+ Permutation.NAME;

		final Cursor cursor = resolver
				.query(Permutation.CONTENT_URI, null, Permutation.QUICKLIST + "<>0", null, sortBy);
		startManagingCursor(cursor);

		final PermutationRenderer renderer = new PermutationRenderer(config, true);
		adapter = new PermutationAlgorithmAdapter(this, cursor, renderer, algorithms, config);
		setListAdapter(adapter);

		getListView().setOnItemClickListener(this);
		getListView().setOnItemLongClickListener(this);

		// Look up the AdView as a resource and load a request.
		final AdView adView = (AdView) this.findViewById(R.id.ad);
		adView.loadAd(new AdRequest());

		// prevent the screen from turning off
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "permutationView-algorithm lock");
	}

	public void onItemClick(final AdapterView<?> adapter, final View view, final int position, final long id) {
		final Permutation permutation = ((PermutationView) view.findViewById(R.id.placeholder_permutation))
				.getPermutation();

		// update permutation for statistics
		permutation.setViews(permutation.getViews() + 1);
		AlgorithmProviderHelper.save(getContentResolver(), permutation);

		// start activity
		final Intent intent = new Intent(this, ViewActivity.class);
		final Bundle bundle = new Bundle();
		bundle.putLong(PERMUTATION, permutation.getId());
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
		final Permutation permutation = ((PermutationView) view.findViewById(R.id.placeholder_permutation))
				.getPermutation();

		if (permutation.getQuickList()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final String title = getResources().getString(R.string.quicklist_remove, permutation.getName());
			builder.setMessage(title).setCancelable(false)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, final int id) {
							permutation.setQuickList(false);
							AlgorithmProviderHelper.save(getContentResolver(), permutation);
							adapter.notifyDataSetChanged();
						}
					}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, final int id) {
							dialog.cancel();
						}
					});
			builder.create().show();
		} else {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final String title = getResources().getString(R.string.quicklist_add, permutation.getName());
			builder.setMessage(title).setCancelable(false)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, final int id) {
							permutation.setQuickList(true);
							AlgorithmProviderHelper.save(getContentResolver(), permutation);
						}
					}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, final int id) {
							dialog.cancel();
						}
					});
			builder.create().show();
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.default_menu, menu);

		menu.findItem(R.id.menu_favorite).setVisible(false);
		menu.findItem(R.id.menu_filtered).setVisible(false);
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
		case R.id.menu_grid:
			startActivityForResult(new Intent(this, se.slackers.cube.activity.ListActivity.class), item.getItemId());
			return true;
		}
		return false;
	}

	@Override
	protected void onPause() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (wakeLock != null && !wakeLock.isHeld()) {
			wakeLock.acquire();
		}
		super.onResume();
	}
}
