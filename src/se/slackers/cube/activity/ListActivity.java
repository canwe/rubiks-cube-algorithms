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
import se.slackers.cube.Usage;
import se.slackers.cube.adapter.PermutationCursorAdapter;
import se.slackers.cube.model.permutation.Permutation;
import se.slackers.cube.provider.AlgorithmProviderHelper;
import se.slackers.cube.render.PermutationRenderer;
import se.slackers.cube.view.FlowLayout;
import se.slackers.cube.view.PermutationView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class ListActivity extends BaseActivity implements OnLongClickListener, OnClickListener {
	public static final String PERMUTATION = "permutation";

	private static final int MESSAGE_DIALOG = 0;
	private static final int FIRSTSTART_DIALOG = 1;

	private PermutationCursorAdapter pllCursorAdapter;
	private PermutationCursorAdapter ollCursorAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.layout_list);

		final Config config = new Config(this);
		final ContentResolver resolver = getContentResolver();
		final PermutationRenderer renderer = new PermutationRenderer(new Config(this), true);

		final Cursor pllCursor = resolver.query(Permutation.CONTENT_URI_PLL, null, null, null, null);
		final Cursor ollCursor = resolver.query(Permutation.CONTENT_URI_OLL, null, null, null, null);

		startManagingCursor(pllCursor);
		startManagingCursor(ollCursor);

		final FlowLayout pllGrid = (FlowLayout) findViewById(R.id.grid_pll);
		final FlowLayout ollGrid = (FlowLayout) findViewById(R.id.grid_oll);

		pllCursorAdapter = new PermutationCursorAdapter(this, pllCursor, renderer);
		ollCursorAdapter = new PermutationCursorAdapter(this, ollCursor, renderer);

		pllGrid.setAdapter(pllCursorAdapter);
		ollGrid.setAdapter(ollCursorAdapter);

		pllGrid.setOnItemClickListener(this);
		pllGrid.setOnItemLongClickListener(this);
		ollGrid.setOnItemClickListener(this);
		ollGrid.setOnItemLongClickListener(this);

		// fetch version to see if the welcome/update dialog needs to be shown
		final PackageInfo info = getPackageInfo();
		final int version = info.versionCode;

		if (config.isFirstStart()) {
			showDialog(FIRSTSTART_DIALOG);
			config.setMessageDialogVersion(version);
		} else if (config.getMessageDialogVersion() < version) {
			showDialog(MESSAGE_DIALOG);
			config.setMessageDialogVersion(version);
		} else if (config.startWithQuickList()) {
			startActivity(new Intent(this, QuickListActivity.class));
		}
	}

	private PackageInfo getPackageInfo() {
		final PackageManager manager = getPackageManager();
		try {
			return manager.getPackageInfo(getPackageName(), 0);
		} catch (final NameNotFoundException e) {
			return null;
		}
	}

	public void onClick(final View view) {
		final Permutation permutation = ((PermutationView) view).getPermutation();

		// update permutation for statistics
		permutation.setViews(permutation.getViews() + 1);
		AlgorithmProviderHelper.save(getContentResolver(), permutation);

		tracker.trackPageView(String.format(Usage.ALGORITHM_ID, permutation.getName()));

		// start activity
		final Intent intent = new Intent(this, ViewActivity.class);
		final Bundle bundle = new Bundle();
		bundle.putLong(PERMUTATION, permutation.getId());
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public boolean onLongClick(final View view) {
		final Permutation permutation = ((PermutationView) view).getPermutation();

		if (permutation.getQuickList()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final String title = getResources().getString(R.string.quicklist_remove, permutation.getName());
			builder.setMessage(title).setCancelable(false)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, final int id) {
							permutation.setQuickList(false);
							AlgorithmProviderHelper.save(getContentResolver(), permutation);
							switch (permutation.getType()) {
							case PLL:
								pllCursorAdapter.notifyDataSetChanged();
								break;
							case OLL:
								ollCursorAdapter.notifyDataSetChanged();
								break;
							case F2L:
								break;
							}
							tracker.trackPageView(String.format(Usage.QUICK_LIST_REMOVE, permutation.getName()));
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
							tracker.trackPageView(String.format(Usage.QUICK_LIST_ADD, permutation.getName()));
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
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
		case FIRSTSTART_DIALOG: {
			final Dialog dialog = new Dialog(this);
			final PackageInfo info = getPackageInfo();

			final OnClickListener firstDismissListener = new OnClickListener() {
				public void onClick(final View v) {
					dialog.dismiss();
				}
			};

			dialog.setContentView(R.layout.dialog_firststart);
			dialog.setTitle(getResources().getString(R.string.firststart_title)
					+ String.format(" v%s", info.versionName));
			dialog.findViewById(R.id.text).setOnClickListener(firstDismissListener);
			dialog.findViewById(R.id.dialogRoot).setOnClickListener(firstDismissListener);

			return dialog;
		}
		case MESSAGE_DIALOG: {
			final Dialog dialog = new Dialog(this);
			final PackageInfo info = getPackageInfo();

			final OnClickListener dismissListener = new OnClickListener() {
				public void onClick(final View v) {
					dialog.dismiss();
				}
			};

			dialog.setContentView(R.layout.dialog_message);
			dialog.setTitle(getResources().getString(R.string.messageTitle) + String.format(" v%s", info.versionName));
			dialog.findViewById(R.id.text).setOnClickListener(dismissListener);
			dialog.findViewById(R.id.dialogRoot).setOnClickListener(dismissListener);
			return dialog;
		}
		}

		return super.onCreateDialog(id);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.menu_favorite).setVisible(false);
		menu.findItem(R.id.menu_grid).setVisible(false);
		menu.findItem(R.id.menu_new).setVisible(false);
		return true;
	}
}
