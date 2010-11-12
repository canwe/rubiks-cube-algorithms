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

package se.slackers.cube.provider;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class Algorithm implements BaseColumns, Comparable<Algorithm> {
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.slackers.algorithm";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.slackers.algorithm";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AlgorithmProvider.AUTHORITY + "/algorithms");
	public static final String FAVORITE_PATH = "favorite";
	public static final String DEFAULT_SORT_ORDER = "algorithm";

	public static final String PERMUTATION_ID = "permutation";
	public static final String ALGORITHM = "algorithm";
	public static final String RANK = "rank";

	private Long id;
	private Long permutationId;
	private String algorithm;
	private Integer rank;

	public Algorithm(final long permutationId, final String algorithm, final int rank) {
		this.permutationId = permutationId;
		this.algorithm = algorithm;
		this.rank = rank;
	}

	public long getId() {
		return id;
	}

	public long getPermutationId() {
		return permutationId;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public int getRank() {
		return rank;
	}

	public int compareTo(final Algorithm a) {
		if (permutationId != a.permutationId) {
			return permutationId.compareTo(a.permutationId);
		}
		return algorithm.compareTo(a.algorithm);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((permutationId == null) ? 0 : permutationId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Algorithm other = (Algorithm) obj;
		if (algorithm == null) {
			if (other.algorithm != null) {
				return false;
			}
		} else if (!algorithm.equals(other.algorithm)) {
			return false;
		}
		if (permutationId == null) {
			if (other.permutationId != null) {
				return false;
			}
		} else if (!permutationId.equals(other.permutationId)) {
			return false;
		}
		return true;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setPermutationId(final Long permutationId) {
		this.permutationId = permutationId;
	}

	public void setAlgorithm(final String algorithm) {
		this.algorithm = algorithm;
	}

	public void setRank(final Integer rank) {
		this.rank = rank;
	}

	public static Algorithm fromCursor(final Cursor cursor) {
		final long id = cursor.getLong(cursor.getColumnIndex(Algorithm._ID));
		final long pid = cursor.getLong(cursor.getColumnIndex(Algorithm.PERMUTATION_ID));
		final int rank = cursor.getInt(cursor.getColumnIndex(Algorithm.RANK));
		final String algorithm = cursor.getString(cursor.getColumnIndex(Algorithm.ALGORITHM));

		final Algorithm a = new Algorithm(pid, algorithm, rank);
		a.setId(id);
		return a;

	}
}
