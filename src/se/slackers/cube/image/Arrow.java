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

package se.slackers.cube.image;

import android.graphics.RectF;

public class Arrow {
	private int x0, y0;
	private int x1, y1;

	public Arrow(final int from, final int to) {
		x0 = from % 3;
		y0 = from / 3;
		x1 = to % 3;
		y1 = to / 3;
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
}
