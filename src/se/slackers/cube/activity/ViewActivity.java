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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
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

	private AdView adView;

	@Override
	protected void onCreate(final Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.layout_view);

		final Bundle bundle = getIntent().getExtras();
		final long permutationId = bundle.getLong(ListActivity.PERMUTATION);

		if (state != null) {
			favoriteCandidateId = state.getLong(ALGORITHM_CANDIDATE, -1);
		}

		renderer = new PermutationRenderer(config, false);
		permutationName = (TextView) findViewById(R.id.permutationName);
		permutationContainer = (FrameLayout) findViewById(R.id.permutationContainer);
		algorithmContainer = (FrameLayout) findViewById(R.id.algorithmContainer);
		adView = (AdView) this.findViewById(R.id.ad);

		// execute the 'heavier' operations later
		new Handler().post(new Runnable() {
			public void run() {
				permutation = AlgorithmProviderHelper.getPermutationById(getContentResolver(), permutationId);
				updatePermutation(permutation);
				updateViews();

				adView.loadAd(new AdRequest());
			}
		});

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
		favorite = getFavoriteAlgorithm();

		final AlgorithmView algorithmView = new AlgorithmView(this, config, favorite);
		algorithmView.setTextSize(getResources().getDimension(R.dimen.font_size_standard));
		algorithmView.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				showSwitchFavoriteDialog();
			}
		});
		algorithmView.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(final View v) {
				final Algorithm algorithm = algorithmView.getAlgorithm();
				if (algorithm == null) {
					// long press on a user favorite that was deleted should spawn the switch
					// algorithm dialog
					showSwitchFavoriteDialog();
				} else {
					spawnAlgorithmMenuDialog(algorithm.getId(), null);
				}
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

	private void changeFavoriteAlgorithm(final long id) {
		AlgorithmProviderHelper.setFavorite(getContentResolver(), favorite.getId(), id);
		favorite = getFavoriteAlgorithm();

		// update UI
		updateViews();
		Toast.makeText(this, R.string.favorite_algorithm_changed, Toast.LENGTH_LONG).show();
	}

	private Algorithm getFavoriteAlgorithm() {
		try {
			return AlgorithmProviderHelper.getFavoriteAlgorithm(getContentResolver(), permutation);
		} catch (final NoSuchAlgorithmException e) {
			try {
				AlgorithmProviderHelper.randomFavorite(getContentResolver(), permutation.getId());
				return AlgorithmProviderHelper.getFavoriteAlgorithm(getContentResolver(), permutation);
			} catch (final NoSuchAlgorithmException e1) {
			}
			return null;
		}
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
				spawnAlgorithmMenuDialog(id, dialog);
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

		final ContentValues values = new ContentValues();
		values.put(Algorithm.ALGORITHM, algorithm);
		final Uri uri = ContentURI.algorithm(algorithmId);
		getContentResolver().update(uri, values, null, null);
		if (favorite != null) {
			AlgorithmProviderHelper.setFavorite(getContentResolver(), favorite.getId(), algorithmId);
		}
		favorite = getFavoriteAlgorithm();
		updateViews();
		Toast.makeText(this, R.string.msg_algorithm_saved, Toast.LENGTH_LONG).show();
	}

	private void insertNewAlgorithm(final String algorithm) {
		try {
			final Algorithm algo = new Algorithm(permutation.getId(), new Instruction(algorithm), 1, 0);
			final ContentValues values = algo.toContentValues();
			values.remove(Algorithm._ID);
			getContentResolver().insert(ContentURI.algorithms(), values);
			if (favorite != null) {
				AlgorithmProviderHelper.setFavorite(getContentResolver(), favorite.getId(), -1);
			}
			favorite = AlgorithmProviderHelper.getFavoriteAlgorithm(getContentResolver(), permutation);
			updateViews();
			Toast.makeText(this, R.string.msg_algorithm_saved, Toast.LENGTH_LONG).show();
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean spawnAlgorithmMenuDialog(final long id, final Dialog parent) {
		Algorithm algorithm = null;
		try {
			algorithm = AlgorithmProviderHelper.getAlgorithm(getContentResolver(), permutation, id);
			if (algorithm.getBuiltIn() == Algorithm.BUILTIN_ALGORITHM) {
				Toast.makeText(this, R.string.error_edit_builtin, Toast.LENGTH_LONG).show();
				return false;
			}
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		// create the intents
		final Intent editIntent = new Intent(this, InputActivity.class);
		editIntent.putExtra(InputActivity.ALGORITHM, algorithm.getInstruction().render(NotationType.Singmaster));
		editIntent.putExtra(InputActivity.ALGORITHM_ID, algorithm.getId());

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_algorithm_menu);

		final TextView edit = (TextView) dialog.findViewById(R.id.menu_edit);
		final TextView delete = (TextView) dialog.findViewById(R.id.menu_delete);

		edit.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				dialog.dismiss();
				if (parent != null) {
					parent.dismiss();
				}
				startActivityForResult(editIntent, InputActivity.REQUEST_CODE_EDIT);
			}
		});

		delete.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(dialog.getContext());
				builder.setMessage(R.string.confirm_algorithm_remove).setCancelable(false)
						.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface me, final int _a) {
								getContentResolver().delete(ContentURI.algorithm(id), null, null);
								if (favorite == null || id == favorite.getId()) {
									favorite = getFavoriteAlgorithm();
									updateViews();
								}

								dialog.dismiss();
								if (parent != null) {
									parent.dismiss();
								}
							}
						}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface me, final int _a) {
								me.cancel();
							}
						});
				builder.create().show();
			}
		});

		dialog.show();
		return true;
	}
}
