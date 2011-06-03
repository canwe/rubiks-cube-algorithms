/*******************************************************************************
 * Copyright (c) 2011 Erik Byström.
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
package se.slackers.cube.adapter;

import se.slackers.cube.model.permutation.Permutation;
import se.slackers.cube.render.PermutationRenderer;
import se.slackers.cube.view.PermutationView;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * @author Erik Byström
 * 
 */
public class PermutationCursorAdapter extends CursorAdapter {
	private final PermutationRenderer renderer;

	public PermutationCursorAdapter(final Context context, final Cursor c, final PermutationRenderer renderer) {
		super(context, c, true);
		this.renderer = renderer;
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		final Permutation permutation = Permutation.fromCursor(cursor);
		((PermutationView) view).setPermutation(permutation);
		((PermutationView) view).image(renderer.render(permutation));
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		return new PermutationView(context);
	}
}
