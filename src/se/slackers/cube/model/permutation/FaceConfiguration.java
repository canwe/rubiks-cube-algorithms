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

package se.slackers.cube.model.permutation;

import java.io.Serializable;
import java.util.Arrays;

import se.slackers.cube.model.Rotatable;

/**
 * @author Erik Byström
 * 
 */
@SuppressWarnings("serial")
public class FaceConfiguration implements Rotatable<FaceConfiguration>, Comparable<FaceConfiguration>, Serializable {

	private final int[] rotationMap;
	private final int sides;
	private final boolean[] faces;

	public FaceConfiguration(final String config) {
		final String[] part = config.split("#");

		if (config.indexOf('#') < 0) {
			sides = 3;
			faces = new boolean[sides * 4 + sides * sides];
			rotationMap = new int[faces.length];
		} else {
			sides = Integer.parseInt(part[0]);
			faces = new boolean[sides * 4 + sides * sides];
			rotationMap = new int[faces.length];

			if (part[1].length() != faces.length) {
				throw new IllegalArgumentException("Face configuration is incomplete, expecting " + faces.length
						+ " faces!");
			}

			for (int i = 0; i < faces.length; i++) {
				faces[i] = part[1].charAt(i) == '1';
			}
		}

		validate();
		buildRotationMap();
	}

	public FaceConfiguration(final int sides, final long faceConfig) {
		this.sides = sides;
		this.faces = new boolean[sides * 4 + sides * sides];
		this.rotationMap = new int[faces.length];

		long bit = 1;
		for (int i = 0; i < faces.length; i++) {
			faces[i] = 0 != (faceConfig & bit);
			bit <<= 1;
		}

		validate();
		buildRotationMap();
	}

	private FaceConfiguration(final int sides, final boolean[] faces) {
		this.sides = sides;
		this.faces = faces;
		this.rotationMap = new int[faces.length];
		buildRotationMap();
	}

	/**
	 * Validates that the number of active faces in a configuration is equals to the square of the sides.
	 */
	private void validate() {
		int active = 0;
		for (final boolean face : faces) {
			active += face ? 1 : 0;
		}
		if (active != sides * sides) {
			final String msg = String.format("The expected number of active faces are %d but the value is %d!", sides
					* sides, active);
			throw new IllegalArgumentException(msg);
		}
	}

	private void buildRotationMap() {
		// ____0__1__2
		// 3__ 4__5__6 7
		// 8__ 9 10 11 12
		// 13 14 15 16 17
		// ___18 19 20
		// { 13, 8, 3, 18, 14, 9, 4, 0, 19, 15, 10, 5, 1, 20, 16, 11, 6, 2, 17, 12, 7 };

		final int lastRow = sides * 6;
		final int w = sides + 2;
		for (int i = 0; i < sides; i++) {
			final int iside = sides - i - 1;
			rotationMap[i] = sides + iside * w; // top
			rotationMap[lastRow + i] = 2 * sides + iside * w + 1; // bottom
			rotationMap[2 * sides + i * w + 1] = i; // right
			rotationMap[sides + i * w] = lastRow + i; // left

			// center
			for (int j = 0; j < sides; j++) {
				rotationMap[sides + i * w + j + 1] = sides + (sides - j - 1) * w + i + 1;
			}
		}
	}

	public int getSides() {
		return sides;
	}

	public boolean isActive(final int index) {
		return faces[index];
	}

	public FaceConfiguration rotate(final int turns) {
		final int rotation = turns % 4;
		final boolean[] rotated = new boolean[faces.length];
		for (int i = 0; i < faces.length; i++) {
			rotated[i] = faces[rotateIndex(i, rotation)];
		}
		return new FaceConfiguration(sides, rotated);
	}

	/**
	 * @param i
	 * @param turns
	 * @return
	 */
	int rotateIndex(int index, final int turns) {
		for (int i = 0; i < turns; i++) {
			index = rotationMap[index];
		}
		return index;
	}

	public String serialize() {
		return this.toString();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(sides);
		builder.append('#');
		for (final boolean face : faces) {
			builder.append(face ? '1' : '0');
		}
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final FaceConfiguration another) {
		if (sides != another.sides) {
			return sides < another.sides ? -1 : 1;
		}

		for (int i = 0; i < faces.length; i++) {
			if (faces[i] != another.faces[i]) {
				return faces[i] ? 1 : -1;
			}
		}

		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(faces);
		result = prime * result + sides;
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
		final FaceConfiguration other = (FaceConfiguration) obj;
		return 0 == compareTo(other);
	}
}
