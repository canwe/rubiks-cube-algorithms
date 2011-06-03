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

/**
 * @author Erik Byström
 * 
 */
public enum Side {
	Left("L"), Right("R"), Top("U"), Bottom("D"), Front("F"), Back("B"), InsideLeft("l", "Lw"), InsideRight("r", "Rw"), InsideTop(
			"u", "Uw"), InsideBottom("d", "Dw"), InsideFront("f", "Fw"), InsideBack("b", "Bw"), AxisX("x"), AxisY("y"), AxisZ(
			"z"), Median("M"), Slice("S"), Equator("E");

	private final String singmaster;
	private final String wca;

	private Side(final String notation) {
		this(notation, notation);
	}

	private Side(final String singmaster, final String wca) {
		this.singmaster = singmaster;
		this.wca = wca;

	}

	public String symbol(final NotationType notation) {
		switch (notation) {
		case Singmaster:
			return singmaster;
		case WCA:
			return wca;
		}
		throw new IllegalArgumentException("Unknown notation type " + notation);
	}

	/**
	 * Attempts to parse a character to a Side using the Singmaster notation.
	 * 
	 * @param symbol
	 * @return
	 */
	public static Side fromSymbol(final char symbol) {
		return fromSymbol(String.valueOf(symbol));
	}

	/**
	 * Attempts to parse a string to a Side using the Singmaster notation.
	 * 
	 * @param s
	 * @return
	 */
	public static Side fromSymbol(final String s) {
		for (final Side side : Side.values()) {
			if (side.singmaster.equals(s)) {
				return side;
			}
		}
		throw new IllegalArgumentException("Not a valid side " + s);
	}
}
