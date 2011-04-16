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

package se.slackers.cube.adapter;

import se.slackers.cube.Config;
import se.slackers.cube.R;
import se.slackers.cube.model.algorithm.Algorithm;
import se.slackers.cube.model.permutation.Permutation;
import se.slackers.cube.view.AlgorithmView;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

public class AlgorithmAdapter extends CursorAdapter {
	private final Config config;
	private final LayoutInflater inflater;
	private final Permutation permutation;
	private long favoriteId = -1;

	public AlgorithmAdapter(final Context context, final Cursor cursor, final Permutation permutation,
			final Config config) {
		super(context, cursor, true);
		this.permutation = permutation;
		this.config = config;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		final Algorithm algorithm = Algorithm.fromCursor(cursor, permutation);

		final AlgorithmView algorithmView = (AlgorithmView) view.findViewById(R.id.algorithm);
		final ImageView image = (ImageView) view.findViewById(R.id.favorite);
		final ImageView locked = (ImageView) view.findViewById(R.id.locked);

		if (algorithm.getBuiltIn() == Algorithm.BUILTIN_ALGORITHM) {
			locked.setVisibility(View.VISIBLE);
		} else {
			locked.setVisibility(View.INVISIBLE);
		}

		algorithmView.setAlgorithm(config, algorithm);

		if ((favoriteId < 0 && algorithm.isFavorite()) || (favoriteId == algorithm.getId())) {
			image.setImageResource(R.drawable.ic_menu_star_selected);
		} else {
			image.setImageResource(R.drawable.ic_menu_star);
		}
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
		return inflater.inflate(R.layout.component_algorithm_row, null);
	}

	public void setFavorite(final long id) {
		favoriteId = id;
	}
}
