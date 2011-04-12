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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import se.slackers.cube.R;
import se.slackers.cube.adapter.AlgorithmAdapter;
import se.slackers.cube.model.algorithm.Algorithm;
import se.slackers.cube.model.permutation.Permutation;
import se.slackers.cube.provider.AlgorithmProviderHelper;
import se.slackers.cube.provider.ContentURI;
import se.slackers.cube.render.PermutationRenderer;
import se.slackers.cube.view.AlgorithmView;
import se.slackers.cube.view.PermutationView;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class ViewActivity extends BaseActivity implements OnItemClickListener {
	private static final int SHOW_CHANGE_ALGORITHM_DIALOG = 1001;

	private static final String ALGORITHM_CANDIDATE = "favorite.candidate";

	private WakeLock wakeLock;
	private PermutationRenderer renderer;

	private TextView permutationName;
	private FrameLayout permutationContainer;
	private FrameLayout algorithmContainer;
	private PermutationView permutationView;

	private List<Algorithm> algorithms;
	private AlgorithmAdapter algorithmAdapter;

	private Algorithm favorite;
	private long candidate_id = -1;
	private Permutation permutation;

	@Override
	protected void onCreate(final Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.layout_view);

		final Bundle bundle = getIntent().getExtras();
		final long permutationId = bundle.getLong(ListActivity.PERMUTATION);

		if (state != null) {
			candidate_id = state.getLong(ALGORITHM_CANDIDATE, -1);
		}

		permutation = AlgorithmProviderHelper.getPermutationById(getContentResolver(), permutationId);
		algorithms = getAlgorithms(permutation);

		favorite = algorithms.get(0);
		try {
			favorite = getFavoriteAlgorithm(permutation);
		} catch (final IllegalArgumentException e) {
			// ignore
		}

		renderer = new PermutationRenderer(config, false);

		permutationName = (TextView) findViewById(R.id.permutationName);
		permutationContainer = (FrameLayout) findViewById(R.id.permutationContainer);
		algorithmContainer = (FrameLayout) findViewById(R.id.algorithmContainer);
		algorithmAdapter = new AlgorithmAdapter(this, config, algorithms, favorite);

		updatePermutation(permutation);
		updateAlgorithm();

		// Look up the AdView as a resource and load a request.
		final AdView adView = (AdView) this.findViewById(R.id.ad);
		adView.loadAd(new AdRequest());

		// prevent the screen from turning off
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "permutationView-algorithm lock");
	}

	private void updatePermutation(final Permutation permutation) {
		final Bitmap bitmap = renderer.render(permutation);
		permutationView = new PermutationView(this, permutation).image(bitmap);
		permutationContainer.removeAllViews();
		permutationContainer.addView(permutationView);

		// set algorithm name
		permutationName.setText(permutation.getName());
	}

	private void updateAlgorithm() {
		final AlgorithmView algorithmView = new AlgorithmView(this, config, favorite);
		algorithmView.setTextSize(getResources().getDimension(R.dimen.font_size_standard));
		algorithmView.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				showDialog(SHOW_CHANGE_ALGORITHM_DIALOG);
			}
		});
		algorithmContainer.removeAllViews();
		algorithmContainer.addView(algorithmView);
	}

	private Algorithm getFavoriteAlgorithm(final Permutation permutation) {
		final Uri uri = ContentURI.favoriteForPermutation(permutation.getId());
		final Cursor cursor = managedQuery(uri, null, null, null, null);
		try {
			if (cursor.moveToFirst()) {
				return Algorithm.fromCursor(cursor, permutation);
			}
			throw new IllegalArgumentException("Favorite algorithms doesn't exist");
		} finally {
			cursor.close();
		}
	}

	private List<Algorithm> getAlgorithms(final Permutation permutation) {
		final Uri uri = ContentURI.algorithmsForPermutation(permutation.getId());
		final Cursor cursor = managedQuery(uri, null, null, null, null);
		try {
			final List<Algorithm> results = new LinkedList<Algorithm>();
			if (cursor.moveToFirst()) {
				do {
					results.add(Algorithm.fromCursor(cursor, permutation));
				} while (cursor.moveToNext());
			}
			Collections.sort(results);
			return results;
		} finally {
			cursor.close();
		}
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

	@Override
	protected void onDestroy() {
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(final Bundle out) {
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}

		out.putLong(ALGORITHM_CANDIDATE, candidate_id);
	}

	public void onItemClick(final AdapterView<?> adapter, final View view, final int position, final long id) {
		final Algorithm candidate = algorithmAdapter.getItem(position);
		algorithmAdapter.setFavorite(candidate);

		candidate_id = candidate.getId();
	}

	private void changeFavoriteAlgorithm() {
		final Algorithm previous = favorite;

		for (final Algorithm algorithm : algorithms) {
			if (algorithm.getId() == candidate_id) {
				favorite = algorithm;

				previous.setRank(0);
				favorite.setRank(1);

				// update favorite algorithm in the db
				AlgorithmProviderHelper.save(getContentResolver(), previous);
				AlgorithmProviderHelper.save(getContentResolver(), favorite);

				// update UI
				algorithmAdapter.setFavorite(favorite);
				updateAlgorithm();

				Toast.makeText(this, R.string.favorite_algorithm_changed, Toast.LENGTH_LONG).show();
				break;
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
		case SHOW_CHANGE_ALGORITHM_DIALOG:
			final Dialog dialog = new Dialog(this);
			dialog.setTitle(R.string.favorite_title);
			dialog.setContentView(R.layout.dialog_favorite_algorithm);

			final ListView list = (ListView) dialog.findViewById(R.id.list);
			list.setAdapter(algorithmAdapter);
			list.setOnItemClickListener(this);

			final Button save = (Button) dialog.findViewById(R.id.save);
			final Button cancel = (Button) dialog.findViewById(R.id.cancel);

			save.setOnClickListener(new OnClickListener() {
				public void onClick(final View v) {
					changeFavoriteAlgorithm();
					dialog.dismiss();
				}
			});
			cancel.setOnClickListener(new OnClickListener() {
				public void onClick(final View v) {
					dialog.dismiss();
				}
			});
			return dialog;
		}

		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(final int id, final Dialog dialog) {
		switch (id) {
		case SHOW_CHANGE_ALGORITHM_DIALOG:
			algorithmAdapter.setFavorite(favorite);
			break;
		}
		super.onPrepareDialog(id, dialog);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case SHOW_CHANGE_ALGORITHM_DIALOG:
			showDialog(SHOW_CHANGE_ALGORITHM_DIALOG);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.menu_grid).setVisible(false);
		menu.findItem(R.id.menu_filtered).setVisible(false);
		return true;
	}
}
