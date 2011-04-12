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

import java.util.Map;

import se.slackers.cube.Config;
import se.slackers.cube.R;
import se.slackers.cube.model.algorithm.Algorithm;
import se.slackers.cube.model.permutation.Permutation;
import se.slackers.cube.render.PermutationRenderer;
import se.slackers.cube.view.AlgorithmView;
import se.slackers.cube.view.PermutationView;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * @author Erik Byström
 * 
 */
public class PermutationAlgorithmAdapter extends CursorAdapter {
	private final PermutationRenderer renderer;
	private final Map<Long, Algorithm> algorithms;
	private final LayoutInflater inflater;
	private final Config config;
	private final int fontSize;

	public PermutationAlgorithmAdapter(final Context context, final Cursor c, final PermutationRenderer renderer,
			final Map<Long, Algorithm> algorithms, final Config config) {
		super(context, c, true);
		this.renderer = renderer;
		this.algorithms = algorithms;
		this.config = config;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fontSize = context.getResources().getDimensionPixelSize(R.dimen.font_size_smaller);
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		final Permutation permutation = Permutation.fromCursor(cursor);
		final Algorithm algorithm = algorithms.get(permutation.getId());

		final PermutationView permutationView = (PermutationView) view.findViewById(R.id.placeholder_permutation);
		permutationView.setPermutation(permutation);
		permutationView.image(renderer.render(permutation));

		final AlgorithmView algorithmView = (AlgorithmView) view.findViewById(R.id.placeholder_algorithm);
		algorithmView.setAlgorithm(config, algorithm);
		algorithmView.setMaxLines(2);
		algorithmView.setTextSize(fontSize);
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		return inflater.inflate(R.layout.component_permutation_favorite, null);
	}
}
