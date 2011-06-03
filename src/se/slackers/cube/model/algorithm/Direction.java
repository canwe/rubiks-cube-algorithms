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

/**
 * @author Erik Byström
 * 
 */
public enum Direction {
	Clockwise(""), CounterClockwise("'"), Double("2");

	private final String symbol;

	Direction(final String symbol) {
		this.symbol = symbol;

	}

	public String symbol() {
		return symbol;
	}

	public Direction opposite() {
		return opposite(this);
	}

	public int rotation() {
		return rotation(this);
	}

	public static Direction fromRotation(final int rotation) {
		switch (rotation % 4) {
		case 1:
			return Direction.Clockwise;
		case 2:
			return Direction.Double;
		case 3:
			return Direction.CounterClockwise;
		default:
			return null;
		}
	}

	public static int rotation(final Direction direction) {
		switch (direction) {
		case Clockwise:
			return 1;
		case CounterClockwise:
			return 3;
		case Double:
			return 2;
		}

		return 0;
	}

	public static Direction opposite(final Direction direction) {
		switch (direction) {
		case Clockwise:
			return Direction.CounterClockwise;
		case CounterClockwise:
			return Direction.Clockwise;
		}
		return direction;
	}
}
