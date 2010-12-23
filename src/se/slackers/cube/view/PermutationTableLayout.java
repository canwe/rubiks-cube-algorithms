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

package se.slackers.cube.view;

import se.slackers.cube.Config;
import se.slackers.cube.model.permutation.Permutation;
import se.slackers.cube.render.PermutationRenderer;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * @author Erik Byström
 * 
 */
public class PermutationTableLayout extends TableLayout implements OnClickListener, OnLongClickListener {
	private TableCellListener listener;
	private PermutationRenderer renderer;
	private int margin;
	private int columns;

	public PermutationTableLayout(final Context context) {
		super(context);
	}

	public PermutationTableLayout(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public PermutationTableLayout config(final Config config) {
		margin = config.getMargin();
		renderer = new PermutationRenderer(config, true);
		columns = (config.getDisplayWidth() / (config.getListWidth() + 3 * margin));
		return this;
	}

	public PermutationTableLayout tableCellListener(final TableCellListener listener) {
		this.listener = listener;
		return this;
	}

	public void init(final Cursor cursor) {
		final Context context = getContext();
		removeAllViews();

		if (cursor.moveToFirst()) {
			TableRow row = null;
			int nextRow = 0;
			int id = getId() * 1000;
			do {
				final Permutation permutation = Permutation.fromCursor(cursor);
				if (nextRow == 0) {
					row = new TableRow(context);
					row.setId(id++);
					row.setPadding(margin, margin, margin, margin);
					row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
					addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					nextRow = columns;
				}

				final Bitmap bitmap = renderer.render(permutation);
				final PermutationView view = new PermutationView(context, permutation).image(bitmap);
				view.padding(margin).setOnClickListener(this);
				view.setOnLongClickListener(this);
				row.addView(view);
				nextRow--;
			} while (cursor.moveToNext());
		}
	}

	public boolean onLongClick(final View view) {
		if (listener == null) {
			return false;
		}
		if (!(view instanceof PermutationView)) {
			return false;
		}

		return listener.onLongClick((PermutationView) view);
	}

	public void onClick(final View view) {
		if (!(view instanceof PermutationView)) {
			return;
		}

		if (listener != null) {
			listener.onClick((PermutationView) view);
		}
	}

	public interface TableCellListener {
		void onClick(PermutationView view);

		boolean onLongClick(PermutationView view);
	}
}
