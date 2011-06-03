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
public class InstructionParseException extends RuntimeException {
	private static final long serialVersionUID = 4906746068320423465L;
	private final int column;
	private final String input;
	private final String text;

	public InstructionParseException(final String input, final int column) {
		this.input = input;
		this.column = column;
		this.text = String.format("Unknown symbol '%c' at column %d in %s", input.charAt(column), column, input);
	}

	public String input() {
		return input;
	}

	public int column() {
		return column;
	}

	@Override
	public String toString() {
		return text;
	}
}
