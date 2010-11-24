/*******************************************************************************
 * Copyright (c) 2010 Erik Byström.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package se.slackers.cube.activity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import se.slackers.cube.R;
import se.slackers.cube.provider.AlgorithmProviderHelper;
import se.slackers.cube.provider.Permutation;
import se.slackers.cube.render.PermutationRenderer;
import se.slackers.cube.view.PermutationView;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class ListActivity extends BaseActivity implements OnClickListener, OnLongClickListener {
	public static final String PERMUTATION = "permutation";
	public static final int PADDING = 2;
	private static final int MESSAGE_DIALOG = 0;
	private static final int FIRSTSTART_DIALOG = 1;
	private static final int PERMUTATION_INFO = 100;
	private int id = 10000;
	private long activePermutation = -1;
	private int totalPermutationViews = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle state) {
		super.onCreate(state);
		final int margin = config.getMargin();
		setContentView(R.layout.list);

		// restore state if any
		if (state != null) {
			activePermutation = state.getLong(PERMUTATION, -1);
		}

		// fetch all permutations from the db
		final List<Permutation> oll = new LinkedList<Permutation>();
		final List<Permutation> pll = new LinkedList<Permutation>();

		final Cursor cursor = getContentResolver().query(Permutation.CONTENT_URI, null, null, null, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					final Permutation permutation = Permutation.fromCursor(cursor);
					totalPermutationViews += permutation.getViews();
					switch (permutation.getType()) {
					case OLL:
						oll.add(permutation);
						break;
					case PLL:
						pll.add(permutation);
						break;
					}
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}

		Collections.sort(oll);
		Collections.sort(pll);

		final PermutationRenderer renderer = new PermutationRenderer(config, true);
		final int columns = (config.getDisplayWidth() / (config.getListWidth() + 3 * margin));
		final TableLayout ollTable = (TableLayout) findViewById(R.id.ollTable);
		final TableLayout pllTable = (TableLayout) findViewById(R.id.pllTable);

		// add views to OLL-table
		TableRow row = null;
		int nextRow = 0;
		for (final Permutation permutation : oll) {
			if (nextRow == 0) {
				row = new TableRow(this);
				row.setId(id++);
				row.setPadding(margin, margin, margin, margin);
				row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				ollTable.addView(row,
						new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				nextRow = columns;
			}

			final Bitmap bitmap = renderer.render(permutation);
			final PermutationView view = new PermutationView(this, permutation).image(bitmap);
			view.padding(margin).setOnClickListener(this);
			view.setOnLongClickListener(this);
			row.addView(view);
			nextRow--;
		}

		// add views to PLL-table
		row = null;
		nextRow = 0;
		for (final Permutation permutation : pll) {
			if (nextRow == 0) {
				row = new TableRow(this);
				row.setId(id++);
				row.setPadding(PADDING, PADDING, PADDING, PADDING);
				row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				pllTable.addView(row,
						new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				nextRow = columns;
			}

			final Bitmap bitmap = renderer.render(permutation);
			final PermutationView view = new PermutationView(this, permutation).image(bitmap);
			view.padding(margin).setOnClickListener(this);
			view.setOnLongClickListener(this);
			row.addView(view);
			nextRow--;
		}

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

	public void onClick(final View view) {
		if (!(view instanceof PermutationView)) {
			return;
		}
		final Permutation permutation = ((PermutationView) view).getPermutation();

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

	public boolean onLongClick(final View view) {
		if (!(view instanceof PermutationView)) {
			return false;
		}
		activePermutation = ((PermutationView) view).getPermutation().getId();
		showDialog(PERMUTATION_INFO);
		return true;
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
		case FIRSTSTART_DIALOG: {
			final Dialog dialog = new Dialog(this);
			final PackageInfo info = getPackageInfo();

			dialog.setContentView(R.layout.firststart_dialog);
			dialog.setTitle(getResources().getString(R.string.firststart_title)
					+ String.format(" v%s", info.versionName));
			dialog.findViewById(R.id.dialogRoot).setOnClickListener(new OnClickListener() {
				public void onClick(final View v) {
					dialog.dismiss();
				}
			});

			return dialog;
		}
		case MESSAGE_DIALOG: {
			final Dialog dialog = new Dialog(this);
			final PackageInfo info = getPackageInfo();

			dialog.setContentView(R.layout.message_dialog);
			dialog.setTitle(getResources().getString(R.string.messageTitle) + String.format(" v%s", info.versionName));
			dialog.findViewById(R.id.dialogRoot).setOnClickListener(new OnClickListener() {
				public void onClick(final View v) {
					dialog.dismiss();
				}
			});
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
			final Permutation permutation = getPermutationById(activePermutation);
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

	private Permutation getPermutationById(final long id) {
		final Uri uri = ContentUris.withAppendedId(Permutation.CONTENT_URI, id);
		final Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		try {
			if (cursor.moveToFirst()) {
				return Permutation.fromCursor(cursor);
			}
			throw new IllegalArgumentException("The provided id is not a valid algorithm: id=" + id);
		} finally {
			cursor.close();
		}
	}
}
