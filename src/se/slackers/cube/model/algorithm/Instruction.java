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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import se.slackers.cube.config.NotationType;
import se.slackers.cube.model.Rotatable;

/**
 * @author Erik Byström
 * 
 */
public class Instruction implements Rotatable<Instruction>, Comparable<Instruction> {
	private final List<Move> moves = new LinkedList<Move>();
	private int rotation = 0;
	private int hashCache = -1;

	public Instruction(final String instruction) {
		this(instruction, 0);
	}

	/**
	 * 
	 */
	public Instruction(final String instruction, final int rotation) {
		this.rotation = rotation % 4;
		parse(instruction);
		rotate(moves, this.rotation);
	}

	private Instruction(final Collection<Move> moves, final int rotation) {
		this.rotation = rotation % 4;
		this.moves.addAll(moves);
		rotate(this.moves, this.rotation);
	}

	public Instruction rotate(final int turns) {
		return new Instruction(moves, rotation + turns);
	}

	/**
	 * @param moves
	 * @param rotation
	 */
	private void rotate(final List<Move> moves, final int rotation) {
		if (rotation == 0) {
			return;
		}

		if (initialCancelation(moves, rotation)) {
			return;
		}

		final List<Move> rotated = new LinkedList<Move>();
		for (final Move move : moves) {
			rotated.add(move.rotate(rotation));
		}
		moves.clear();
		moves.addAll(rotated);
	}

	private boolean initialCancelation(final List<Move> moves, final int rotation) {
		if (moves.isEmpty()) {
			return false;
		}

		final Move first = moves.get(0);
		if (first.side() != Side.AxisY) {
			return false;
		}
		final int firstRotation = Move.rotation(first);
		final Direction direction = Direction.fromRotation(firstRotation + rotation);

		moves.remove(0);
		if (direction == null) {
			return true;
		}
		moves.add(0, first.direction(direction));

		return true;
	}

	/**
	 * 
	 * @param notation
	 * @return
	 */
	public String render(final NotationType notation) {
		final StringBuilder builder = new StringBuilder();
		for (final Move move : moves) {
			builder.append(move.render(notation));
		}
		return builder.toString();
	}

	/**
	 * @param instruction
	 */
	private void parse(String instruction) {
		if (instruction == null) {
			return;
		}

		instruction = instruction.replace("2'", "2").replace("'2", "2");

		final int length = instruction.length();
		for (int i = 0; i < length; i++) {
			try {
				final Side side = Side.fromSymbol(instruction.charAt(i));
				final Direction direction = getDirection(instruction, i + 1, length);
				if (direction != Direction.Clockwise) {
					i++;
				}

				moves.add(new Move(side, direction));
			} catch (final IllegalArgumentException e) {
				throw new InstructionParseException(instruction, i);
			}
		}
	}

	/**
	 * @param instruction
	 * @param i
	 * @param length
	 * @return
	 */
	private Direction getDirection(final String instruction, final int index, final int length) {
		if (index >= length) {
			return Direction.Clockwise;
		}
		switch (instruction.charAt(index)) {
		case '\'':
			return Direction.CounterClockwise;
		case '2':
			return Direction.Double;
		}
		return Direction.Clockwise;
	}

	@Override
	public int hashCode() {
		// optimize for several hash calculations, works because Instruction is immutable
		if (hashCache < 0) {
			final int prime = 31;
			hashCache = 1;
			hashCache = prime * hashCache + ((moves == null) ? 0 : moves.hashCode());
			hashCache = prime * hashCache + rotation;
		}
		return hashCache;
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
		final Instruction other = (Instruction) obj;
		return 0 == compareTo(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Instruction another) {
		final Iterator<Move> ti = moves.iterator();
		final Iterator<Move> ai = another.moves.iterator();

		while (ti.hasNext() && ai.hasNext()) {
			final int ret = ti.next().compareTo(ai.next());
			if (ret != 0) {
				return ret;
			}
		}

		if (!ti.hasNext() && !ai.hasNext()) {
			return 0;
		}

		if (!ti.hasNext()) {
			return -1;
		}

		return 1;
	}
}
