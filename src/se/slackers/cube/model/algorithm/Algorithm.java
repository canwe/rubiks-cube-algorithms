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

package se.slackers.cube.model.algorithm;

import se.slackers.cube.config.NotationType;
import se.slackers.cube.model.Rotatable;
import se.slackers.cube.model.permutation.Permutation;
import se.slackers.cube.provider.AlgorithmProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class Algorithm implements BaseColumns, Comparable<Algorithm>, Rotatable<Algorithm> {
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.slackers.algorithm";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.slackers.algorithm";
	public static final int BUILTIN_ALGORITHM = 1;

	public static final Uri CONTENT_URI = Uri.parse("content://" + AlgorithmProvider.AUTHORITY + "/algorithms");
	public static final String DEFAULT_SORT_ORDER = "algorithm";

	public static final String PERMUTATION_ID = "permutation";
	public static final String ALGORITHM = "algorithm";
	public static final String RANK = "rank";
	public static final String BUILTIN = "builtin";

	private Long id;
	private Long permutationId;
	private final Instruction instruction;
	private Integer rank;
	private final int builtin;

	public Algorithm(final long permutationId, final Instruction instruction, final int rank, final int builtin) {
		this.permutationId = permutationId;
		this.instruction = instruction;
		this.rank = rank;
		this.builtin = builtin;
	}

	public Algorithm rotate(final int turns) {
		final Algorithm algorithm = new Algorithm(permutationId, instruction.rotate(turns), rank, builtin);
		algorithm.setId(id);
		return algorithm;
	}

	public long getId() {
		return id;
	}

	public long getPermutationId() {
		return permutationId;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public int getRank() {
		return rank;
	}

	public int getBuiltIn() {
		return builtin;
	}

	public boolean isFavorite() {
		return rank != 0;
	}

	public int compareTo(final Algorithm a) {
		if (permutationId != a.permutationId) {
			return permutationId.compareTo(a.permutationId);
		}
		return instruction.compareTo(a.instruction);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instruction == null) ? 0 : instruction.hashCode());
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
		if (instruction == null) {
			if (other.instruction != null) {
				return false;
			}
		} else if (!instruction.equals(other.instruction)) {
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

	public void setRank(final Integer rank) {
		this.rank = rank;
	}

	public static Algorithm fromCursor(final Cursor cursor, final Permutation permutation) {
		final long id = cursor.getLong(cursor.getColumnIndex(Algorithm._ID));
		final long pid = cursor.getLong(cursor.getColumnIndex(Algorithm.PERMUTATION_ID));
		final int rank = cursor.getInt(cursor.getColumnIndex(Algorithm.RANK));
		final int builtin = cursor.getInt(cursor.getColumnIndex(Algorithm.BUILTIN));
		final String algorithm = cursor.getString(cursor.getColumnIndex(Algorithm.ALGORITHM));

		if (permutation == null) {
			final Algorithm a = new Algorithm(pid, new Instruction(algorithm), rank, builtin);
			a.setId(id);
			return a;
		}
		final Algorithm a = new Algorithm(pid, new Instruction(algorithm, permutation.getRotation()), rank, builtin);
		a.setId(id);
		return a;

	}

	public ContentValues toContentValues() {
		final ContentValues values = new ContentValues();
		values.put(Algorithm._ID, id);
		values.put(Algorithm.PERMUTATION_ID, permutationId);
		values.put(Algorithm.ALGORITHM, instruction.render(NotationType.Singmaster));
		values.put(Algorithm.RANK, rank);
		values.put(Algorithm.BUILTIN, builtin);
		return values;
	}
}
