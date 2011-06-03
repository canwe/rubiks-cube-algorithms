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

import se.slackers.cube.model.algorithm.Algorithm;
import se.slackers.cube.model.permutation.Permutation;
import android.content.ContentUris;
import android.net.Uri;
import android.net.Uri.Builder;

/**
 * @author Erik Byström
 * 
 */
public class ContentURI {
	private static final String ALGORITHMS = "algorithms";
	private static final String FAVORITE = "favorite";

	/**
	 * Return the content uri for all algorithms for a specific permutation.
	 * 
	 * @param permutationId
	 * @return
	 */
	public static Uri algorithmsForPermutation(final long permutationId) {
		final Builder builder = ContentUris.withAppendedId(Permutation.CONTENT_URI, permutationId).buildUpon();
		builder.appendPath(ALGORITHMS);
		return builder.build();
	}

	/**
	 * Return the content uri for the favorite algorithm of a permutation.
	 * 
	 * @param permutationId
	 * @return
	 */
	public static Uri favoriteForPermutation(final long permutationId) {
		final Builder builder = ContentUris.withAppendedId(Permutation.CONTENT_URI, permutationId).buildUpon();
		builder.appendPath(FAVORITE);
		return builder.build();

	}

	public static Uri permutation(final long permutationId) {
		return ContentUris.withAppendedId(Permutation.CONTENT_URI, permutationId);
	}

	/**
	 * Content uri for an algorithm.
	 * 
	 * @param algorithmId
	 * @return
	 */
	public static Uri algorithm(final long algorithmId) {
		return ContentUris.withAppendedId(Algorithm.CONTENT_URI, algorithmId);
	}

	/**
	 * Content URI for all algorithms.
	 * 
	 * @return
	 */
	public static Uri algorithms() {
		return Algorithm.CONTENT_URI;
	}
}
