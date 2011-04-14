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

import java.security.NoSuchAlgorithmException;

import se.slackers.cube.Config;
import se.slackers.cube.R;
import se.slackers.cube.adapter.AlgorithmAdapter;
import se.slackers.cube.config.NotationType;
import se.slackers.cube.model.algorithm.Algorithm;
import se.slackers.cube.model.algorithm.Instruction;
import se.slackers.cube.model.permutation.Permutation;
import se.slackers.cube.provider.AlgorithmProviderHelper;
import se.slackers.cube.provider.ContentURI;
import se.slackers.cube.render.PermutationRenderer;
import se.slackers.cube.view.AlgorithmView;
import se.slackers.cube.view.PermutationView;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class ViewActivity extends BaseActivity {
	private static final String ALGORITHM_CANDIDATE = "favorite.candidate";

	private WakeLock wakeLock;
	private PermutationRenderer renderer;

	private TextView permutationName;
	private FrameLayout permutationContainer;
	private FrameLayout algorithmContainer;
	private PermutationView permutationView;

	private Algorithm favorite;
	private long favoriteCandidateId = -1;
	private Permutation permutation;

	@Override
	protected void onCreate(final Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.layout_view);

		final Bundle bundle = getIntent().getExtras();
		final long permutationId = bundle.getLong(ListActivity.PERMUTATION);

		if (state != null) {
			favoriteCandidateId = state.getLong(ALGORITHM_CANDIDATE, -1);
		}

		permutation = AlgorithmProviderHelper.getPermutationById(getContentResolver(), permutationId);
		try {
			favorite = AlgorithmProviderHelper.getFavoriteAlgorithm(getContentResolver(), permutation);
		} catch (final NoSuchAlgorithmException e) {
			favorite = null;
		}

		renderer = new PermutationRenderer(config, false);

		permutationName = (TextView) findViewById(R.id.permutationName);
		permutationContainer = (FrameLayout) findViewById(R.id.permutationContainer);
		algorithmContainer = (FrameLayout) findViewById(R.id.algorithmContainer);

		updatePermutation(permutation);
		updateViews();

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

	private void updateViews() {
		final AlgorithmView algorithmView = new AlgorithmView(this, config, favorite);
		algorithmView.setTextSize(getResources().getDimension(R.dimen.font_size_standard));
		algorithmView.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				showSwitchFavoriteDialog();
			}
		});
		algorithmView.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(final View v) {
				editAlgorithm(algorithmView.getAlgorithm().getId());
				return true;
			}
		});
		algorithmContainer.removeAllViews();
		algorithmContainer.addView(algorithmView);
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

		out.putLong(ALGORITHM_CANDIDATE, favoriteCandidateId);
	}

	/**
	 * Can be called both from the AlgorithmView longclick and onItemLongClick in the switch favorite dialog.
	 * 
	 * @param id
	 */
	protected void editAlgorithm(final long id) {
		try {
			editAlgorithm(AlgorithmProviderHelper.getAlgorithm(getContentResolver(), permutation, id));
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	protected void editAlgorithm(final Algorithm algorithm) {
		final Intent intent = new Intent(this, InputActivity.class);
		intent.putExtra(InputActivity.ALGORITHM, algorithm.getInstruction().render(NotationType.Singmaster));
		intent.putExtra(InputActivity.ALGORITHM_ID, algorithm.getId());
		startActivityForResult(intent, InputActivity.REQUEST_CODE_EDIT);
	}

	private void changeFavoriteAlgorithm(final long id) {
		AlgorithmProviderHelper.setFavorite(getContentResolver(), favorite.getId(), id);
		try {
			favorite = AlgorithmProviderHelper.getFavoriteAlgorithm(getContentResolver(), permutation);
		} catch (final NoSuchAlgorithmException e) {
			favorite = null;
		}

		// update UI
		updateViews();
		Toast.makeText(this, R.string.favorite_algorithm_changed, Toast.LENGTH_LONG).show();
	}

	private void showSwitchFavoriteDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setTitle(R.string.favorite_title);
		dialog.setContentView(R.layout.dialog_favorite_algorithm);

		final ListView list = (ListView) dialog.findViewById(R.id.list);

		final Uri uri = ContentURI.algorithmsForPermutation(permutation.getId());
		final Cursor cursor = managedQuery(uri, null, null, null, null);
		final AlgorithmAdapter algorithmAdapter = new AlgorithmAdapter(this, cursor, permutation, config);

		list.setAdapter(algorithmAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> _a, final View _b, final int _c, final long id) {
				favoriteCandidateId = id;
				algorithmAdapter.setFavorite(id);
				algorithmAdapter.notifyDataSetChanged();
			}
		});
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(final AdapterView<?> _a, final View _b, final int position, final long id) {
				dialog.dismiss();
				editAlgorithm(id);
				return true;
			}
		});

		final Button save = (Button) dialog.findViewById(R.id.save);
		final Button cancel = (Button) dialog.findViewById(R.id.cancel);

		save.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				changeFavoriteAlgorithm(favoriteCandidateId);
				dialog.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_favorite:
			showSwitchFavoriteDialog();
			return true;
		case R.id.menu_new:
			startActivityForResult(new Intent(this, InputActivity.class), InputActivity.REQUEST_CODE_NEW);
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

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		final String algorithm = data.getStringExtra(InputActivity.ALGORITHM);
		final long algorithmId = data.getLongExtra(InputActivity.ALGORITHM_ID, -1);

		switch (requestCode) {
		case InputActivity.REQUEST_CODE_NEW:
			insertNewAlgorithm(algorithm);
			break;
		case InputActivity.REQUEST_CODE_EDIT:
			updateAlgoithm(algorithmId, algorithm);
			break;
		}
	}

	private void updateAlgoithm(final long algorithmId, final String algorithm) {
		if (algorithmId < 0) {
			Config.debug("Edited algorithm has id 0");
			return;
		}

		try {
			final ContentValues values = new ContentValues();
			values.put(Algorithm.ALGORITHM, algorithm);
			final Uri uri = ContentURI.algorithm(algorithmId);
			getContentResolver().update(uri, values, null, null);

			AlgorithmProviderHelper.setFavorite(getContentResolver(), favorite.getId(), algorithmId);
			favorite = AlgorithmProviderHelper.getFavoriteAlgorithm(getContentResolver(), permutation);
			updateViews();
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private void insertNewAlgorithm(final String algorithm) {
		try {
			final Algorithm algo = new Algorithm(permutation.getId(), new Instruction(algorithm), 1);
			getContentResolver().insert(ContentURI.algorithms(), algo.toContentValues());
			AlgorithmProviderHelper.setFavorite(getContentResolver(), favorite.getId(), -1);
			favorite = AlgorithmProviderHelper.getFavoriteAlgorithm(getContentResolver(), permutation);
			updateViews();
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
