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

import se.slackers.cube.image.CubeMetric;
import android.graphics.RectF;

@SuppressWarnings("serial")
public class Arrow implements Comparable<Arrow>, Serializable {
	private final int x0, y0;
	private final int x1, y1;

	public Arrow(final int from, final int to) {
		x0 = from % 3;
		y0 = from / 3;
		x1 = to % 3;
		y1 = to / 3;
		if (x0 < 0 || x0 > 2 || y0 < 0 || y0 > 2 || x1 < 0 || x1 > 2 || y1 < 0 || y1 > 2) {
			throw new IllegalArgumentException();
		}
	}

	public Arrow(final int x0, final int y0, final int x1, final int y1) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;

		if (x0 < 0 || x0 > 2 || y0 < 0 || y0 > 2 || x1 < 0 || x1 > 2 || y1 < 0 || y1 > 2) {
			throw new IllegalArgumentException();
		}
	}

	public boolean horizontal() {
		return y0 == y1;
	}

	public boolean vertical() {
		return x0 == x1;
	}

	public RectF getFrom(final CubeMetric metric) {
		return metric.rect(4 + x0 + y0 * 5);
	}

	public RectF getTo(final CubeMetric metric) {
		return metric.rect(4 + x1 + y1 * 5);
	}

	public boolean usingCorner() {
		switch (x0 + y0 * 3) {
		case 0:
		case 2:
		case 6:
		case 8:
			return true;
		default:
			return false;
		}
	}

	public String serialize() {
		return this.toString();
	}

	@Override
	public String toString() {
		return String.format("%d,%d->%d,%d", x0, y0, x1, y1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Arrow another) {
		if (x0 != another.x0) {
			return x0 < x0 ? -1 : 1;
		}
		if (y0 != another.y0) {
			return y0 < y0 ? -1 : 1;
		}
		if (x1 != another.x1) {
			return x1 < x1 ? -1 : 1;
		}
		if (y1 != another.y1) {
			return y1 < y1 ? -1 : 1;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x0;
		result = prime * result + x1;
		result = prime * result + y0;
		result = prime * result + y1;
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
		final Arrow other = (Arrow) obj;
		return 0 == compareTo(other);
	}

	/**
	 * @param turns
	 * @return
	 */
	public Arrow rotate(final int sides, final int turns) {
		final int m = sides / 2;

		int nx0 = x0;
		int ny0 = y0;
		int nx1 = x1;
		int ny1 = y1;

		for (int i = 0; i < turns % 4; i++) {
			final int tmp0 = 2 * m - ny0;
			ny0 = nx0;
			nx0 = tmp0;

			final int tmp1 = 2 * m - ny1;
			ny1 = nx1;
			nx1 = tmp1;
		}

		return new Arrow(nx0, ny0, nx1, ny1);
	}
}
