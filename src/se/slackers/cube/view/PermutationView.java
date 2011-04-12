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

import se.slackers.cube.model.permutation.Permutation;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PermutationView extends ImageView {
	private Permutation permutation;

	public PermutationView(final Context context) {
		super(context);
	}

	public PermutationView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public PermutationView(final Context context, final Permutation permutation) {
		super(context);
		this.permutation = permutation;
	}

	public void setPermutation(final Permutation permutation) {
		this.permutation = permutation;
	}

	public Permutation getPermutation() {
		return permutation;
	}

	public PermutationView image(final Bitmap bitmap) {
		setImageBitmap(bitmap);
		return this;
	}
}
