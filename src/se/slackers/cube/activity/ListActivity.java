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
import se.slackers.cube.provider.AlgorithmProviderHelper;
import se.slackers.cube.view.PermutationTableLayout;
import se.slackers.cube.view.PermutationTableLayout.TableCellListener;
import se.slackers.cube.view.PermutationView;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class ListActivity extends BaseActivity implements TableCellListener {
	public static final String PERMUTATION = "permutation";
	public static final int PADDING = 2;
	private static final int MESSAGE_DIALOG = 0;
	private static final int FIRSTSTART_DIALOG = 1;
	private static final int PERMUTATION_INFO = 100;
	private long activePermutation = -1;
	private int totalPermutationViews = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.list);

		// restore state if any
		if (state != null) {
			activePermutation = state.getLong(PERMUTATION, -1);
		}

		final Cursor pllCursor = getContentResolver().query(Permutation.CONTENT_URI, null, Permutation.PLL_FILTER,
				null, null);
		final Cursor ollCursor = getContentResolver().query(Permutation.CONTENT_URI, null, Permutation.OLL_FILTER,
				null, null);

		final PermutationTableLayout pllTable = (PermutationTableLayout) findViewById(R.id.pllTable);
		final PermutationTableLayout ollTable = (PermutationTableLayout) findViewById(R.id.ollTable);

		pllTable.config(config).tableCellListener(this).init(pllCursor);
		ollTable.config(config).tableCellListener(this).init(ollCursor);

		startManagingCursor(pllCursor);
		startManagingCursor(ollCursor);

		// Note(erik.b): Not needed until users can add their own algorithms
		// final Handler handler = new Handler();
		// getContentResolver().registerContentObserver(Permutation.CONTENT_URI, true, new ContentObserver(handler) {
		// @Override
		// public void onChange(final boolean selfChange) {
		// ollTable.init(ollCursor);
		// }
		// });

		// fetch version to see if the welcome/update dialog needs to be shown
		final PackageInfo info = getPackageInfo();
		final int version = info.versionCode;

		if (config.isFirstStart()) {
			showDialog(FIRSTSTART_DIALOG);
			config.setMessageDialogVersion(version);
		} else if (config.getMessageDialogVersion() < version) {
			showDialog(MESSAGE_DIALOG);
			config.setMessageDialogVersion(version);
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

	public void onClick(final PermutationView view) {
		final Permutation permutation = (view).getPermutation();

		// update permutation for statistics
		permutation.setViews(permutation.getViews() + 1);
		AlgorithmProviderHelper.save(getContentResolver(), permutation);
		totalPermutationViews++;

		// start activity
		final Intent intent = new Intent(this, ViewActivity.class);
		final Bundle bundle = new Bundle();
		bundle.putLong(PERMUTATION, permutation.getId());
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public boolean onLongClick(final PermutationView view) {
		activePermutation = (view).getPermutation().getId();
		showDialog(PERMUTATION_INFO);
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

			dialog.setContentView(R.layout.firststart_dialog);
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

			dialog.setContentView(R.layout.message_dialog);
			dialog.setTitle(getResources().getString(R.string.messageTitle) + String.format(" v%s", info.versionName));
			dialog.findViewById(R.id.text).setOnClickListener(dismissListener);
			dialog.findViewById(R.id.dialogRoot).setOnClickListener(dismissListener);
			return dialog;
		}
		case PERMUTATION_INFO:
			return createPermutationInfoDialog();
		}

		return super.onCreateDialog(id);
	}

	private Dialog createPermutationInfoDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.permutation_info);

		// add a close handler
		dialog.findViewById(R.id.dialogRoot).setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				dialog.dismiss();
			}
		});
		return dialog;
	}

	@Override
	protected void onSaveInstanceState(final Bundle out) {
		out.putLong(PERMUTATION, activePermutation);
	}

	@Override
	protected void onPrepareDialog(final int id, final Dialog dialog) {
		switch (id) {
		case PERMUTATION_INFO:
			final Permutation permutation = AlgorithmProviderHelper.getPermutationById(getContentResolver(),
					activePermutation);
			final int views = permutation.getViews();

			double percent = 0;
			if (totalPermutationViews != 0) {
				percent = 100.0 * views / totalPermutationViews;
			}

			final TextView title = (TextView) dialog.findViewById(R.id.title);
			final TextView text = (TextView) dialog.findViewById(R.id.usage);
			title.setText(permutation.getName());
			text.setText(String.format("%d/%d (%.2f%%)", views, totalPermutationViews, percent));
			break;
		}
	}

}
