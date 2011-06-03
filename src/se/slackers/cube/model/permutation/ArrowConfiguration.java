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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import se.slackers.cube.model.Rotatable;

/**
 * @author Erik Byström
 * 
 */
@SuppressWarnings("serial")
public class ArrowConfiguration implements Rotatable<ArrowConfiguration>, Serializable {
	private final int sides;
	private final Set<Arrow> arrows = new HashSet<Arrow>();

	public ArrowConfiguration(final String configuration) {
		final String[] parts = configuration.split("#");
		if (configuration.indexOf('#') < 0) {
			// old format of arrows
			this.sides = 3;
			for (int i = 0; i < configuration.length(); i += 2) {
				arrows.add(new Arrow(configuration.charAt(i + 0) - '0', configuration.charAt(i + 1) - '0'));
			}
		} else {
			// parse new format
			sides = Integer.parseInt(parts[0]);
			if (parts.length > 1) {
				for (final String arrow : parts[1].split(";")) {
					arrows.add(parseArrow(arrow));
				}
			}
		}
	}

	public Collection<Arrow> getArrows() {
		return arrows;
	}

	private ArrowConfiguration(final int sides, final Collection<Arrow> arrows) {
		this.sides = sides;
		this.arrows.addAll(arrows);
	}

	/**
	 * @param arrow
	 * @return
	 */
	private Arrow parseArrow(final String arrow) {
		final String[] coords = arrow.split("->|,");

		final int x0 = Integer.parseInt(coords[0]);
		final int y0 = Integer.parseInt(coords[1]);
		final int x1 = Integer.parseInt(coords[2]);
		final int y1 = Integer.parseInt(coords[3]);

		return new Arrow(x0, y0, x1, y1);
	}

	public ArrowConfiguration rotate(int turns) {
		turns = turns % 4;
		final List<Arrow> rotated = new LinkedList<Arrow>();
		for (final Arrow arrow : arrows) {
			rotated.add(arrow.rotate(sides, turns));
		}

		return new ArrowConfiguration(sides, rotated);
	}

	public String serialize() {
		return this.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrows == null) ? 0 : arrows.hashCode());
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
		final ArrowConfiguration other = (ArrowConfiguration) obj;
		if (sides != other.sides) {
			return false;
		}
		if (arrows == null) {
			if (other.arrows != null) {
				return false;
			}
		} else if (!arrows.equals(other.arrows)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(sides);
		builder.append('#');
		for (final Arrow arrow : arrows) {
			builder.append(arrow.serialize());
			builder.append(';');
		}
		return builder.toString();
	}
}
