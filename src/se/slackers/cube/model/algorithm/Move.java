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

/**
 * @author Erik Byström
 * 
 */
public class Move implements Rotatable<Move>, Comparable<Move> {
	private final Side side;
	private final Direction direction;

	public Move(final Side side, final Direction direction) {
		this.side = side;
		this.direction = direction;
	}

	public Side side() {
		return side;
	}

	public Direction direction() {
		return direction;
	}

	public Move side(final Side side) {
		return new Move(side, direction);
	}

	public Move direction(final Direction direction) {
		return new Move(side, direction);
	}

	public String render(final NotationType notation) {
		return side.symbol(notation) + direction.symbol();
	}

	public static int rotation(final Move move) {
		if (move.side() == Side.AxisY) {
			switch (move.direction) {
			case Clockwise:
				return 1;
			case CounterClockwise:
				return 3;
			case Double:
				return 2;
			}
		}

		return 0;
	}

	public Move rotate(final int turns) {
		Move result = this;
		for (int i = 0; i < turns % 4; i++) {
			result = rotateClockwise(result);
		}
		return result;
	}

	public static Move rotateClockwise(final Move move) {
		switch (move.side()) {
		case Back:
			return move.side(Side.Right);
		case Top:
		case Bottom:
		case Equator:
		case AxisY:
		case InsideBottom:
		case InsideTop:
			return move;
		case Front:
			return move.side(Side.Left);
		case Left:
			return move.side(Side.Back);
		case Median:
			return move.side(Side.Slice);
		case Right:
			return move.side(Side.Front);
		case AxisX:
			return move.side(Side.AxisZ);
		case AxisZ:
			return move.side(Side.AxisX).direction(move.direction().opposite());
		case Slice:
			return move.side(Side.Median).direction(move.direction().opposite());

		case InsideBack:
			return move.side(Side.InsideRight);
		case InsideFront:
			return move.side(Side.InsideLeft);
		case InsideRight:
			return move.side(Side.InsideFront);
		case InsideLeft:
			return move.side(Side.InsideBack);
		}

		throw new IllegalArgumentException("Illegal move " + move);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((side == null) ? 0 : side.hashCode());
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
		final Move other = (Move) obj;
		return 0 == compareTo(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Move another) {
		final int ret = side.compareTo(another.side);
		if (ret == 0) {
			return direction.compareTo(another.direction);
		}
		return ret;
	}

	@Override
	public String toString() {
		return String.valueOf(side) + String.valueOf(direction);
	}
}
