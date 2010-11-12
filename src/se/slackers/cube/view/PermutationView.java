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

package se.slackers.cube.view;

import se.slackers.cube.provider.Permutation;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class PermutationView extends ImageView {
	private final Permutation permutation;

	public PermutationView(final Context context, final Permutation permutation) {
		super(context);
		this.permutation = permutation;
	}

	public Permutation getPermutation() {
		return permutation;
	}

	public PermutationView image(final Bitmap bitmap) {
		setImageBitmap(bitmap);
		return this;
	}

	public PermutationView padding(final int padding) {
		setPadding(padding, padding, padding, padding);
		return this;
	}
}
