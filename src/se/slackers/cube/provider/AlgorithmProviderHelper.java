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

package se.slackers.cube.provider;

import se.slackers.cube.config.NotationType;
import se.slackers.cube.model.algorithm.Algorithm;
import se.slackers.cube.model.permutation.Permutation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class AlgorithmProviderHelper {
	public static boolean save(final ContentResolver resolver, final Permutation permutation) {
		final Uri uri = Permutation.CONTENT_URI;
		final ContentValues values = new ContentValues();
		values.put(Permutation.NAME, permutation.getName());
		values.put(Permutation.VIEWS, permutation.getViews());
		values.put(Permutation.TYPE, permutation.getType().name());
		values.put(Permutation.ROTATION, permutation.getRotation());

		return 0 != resolver.update(uri, values, Permutation._ID + "=" + permutation.getId(), null);
	}

	public static boolean save(final ContentResolver resolver, final Algorithm algorithm) {
		final Uri uri = Algorithm.CONTENT_URI;
		final ContentValues values = new ContentValues();
		values.put(Algorithm.ALGORITHM, algorithm.getInstruction().render(NotationType.Singmaster));
		values.put(Algorithm.RANK, algorithm.getRank());

		return 0 != resolver.update(uri, values, Algorithm._ID + "=" + algorithm.getId(), null);
	}

	public static Permutation getPermutationById(final ContentResolver resolver, final long id) {
		final Cursor cursor = resolver.query(ContentURI.permutation(id), null, null, null, null);
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
