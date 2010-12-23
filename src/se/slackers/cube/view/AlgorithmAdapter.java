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

import java.util.List;

import se.slackers.cube.Config;
import se.slackers.cube.R;
import se.slackers.cube.model.algorithm.Algorithm;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class AlgorithmAdapter extends ArrayAdapter<Algorithm> {
	private final Config config;
	private Algorithm favorite;

	public AlgorithmAdapter(final Context context, final Config config, final List<Algorithm> algorithms,
			final Algorithm favorite) {
		super(context, R.layout.algorithm_row, R.id.algorithm, algorithms);
		this.config = config;
		this.favorite = favorite;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.algorithm_row, null);
		}
		final Algorithm algorithm = getItem(position);
		final AlgorithmView algorithmView = (AlgorithmView) convertView.findViewById(R.id.algorithm);
		algorithmView.setAlgorithm(config, algorithm);

		final ImageView image = (ImageView) convertView.findViewById(R.id.favorite);
		if (favorite != null && favorite.equals(algorithm)) {
			image.setImageResource(R.drawable.ic_menu_star_selected);
		} else {
			image.setImageResource(R.drawable.ic_menu_star);
		}

		return convertView;
	}

	public void setFavorite(final Algorithm algorithm) {
		this.favorite = algorithm;
		notifyDataSetChanged();
	}
}
