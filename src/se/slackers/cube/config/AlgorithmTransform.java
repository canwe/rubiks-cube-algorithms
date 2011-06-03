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

package se.slackers.cube.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import se.slackers.cube.Config;

public class AlgorithmTransform {
	private static final String[] triggers = new String[] { "RUR2'U'", "RU'R'U'", "R'U'RU'", "RUR'U'", "R'FRF'",
			"R'U'RU", "RUR'U", "RU2'R'U", "RU2'R'U'", "RU2'R'", "R'U2'R'", "R'U2R'", "R2'UR'", "M'U'M", "MUM'", "MUM",
			"RUR2'", "RU'R'", "RU2R'", "R'UR'", "R'U'R", "R'U2R", "M2'U2", "RUR'", "RU'R", "R'UR", "R2'U2", "M'U'",
			"M'U2", "M2'U", "RU2'", "R'U'", "R'U2", "R2U'", "R2'U", "M'U", "RU'", "R'U", "RU" };

	private final boolean usingTriggers;

	public AlgorithmTransform(final Config config) {
		usingTriggers = config.isTriggers();
	}

	public String transform(final String algorithm) {
		return usingTriggers ? applyTriggers(algorithm) : algorithm;
	}

	private String applyTriggers(final String s) {
		final StringBuilder builder = new StringBuilder();
		final int slen = s.length();
		for (int i = 0; i < slen; i++) {
			final List<String> matches = new LinkedList<String>();

			triggerLoop: for (final String trigger : triggers) {
				final int tlen = trigger.length();
				if (i + tlen > slen) {
					continue;
				}

				for (int j = 0; j < tlen; j++) {
					if (s.charAt(i + j) != trigger.charAt(j)) {
						continue triggerLoop;
					}
				}

				if (i + tlen < slen) {
					final char next = s.charAt(i + tlen);
					if (next == '\'' || next == '2') {
						continue triggerLoop;
					}
				}

				matches.add(trigger);
				break;
			}

			if (matches.isEmpty()) {
				builder.append(s.charAt(i));
			} else {
				Collections.sort(matches);
				final String m = matches.get(matches.size() - 1);
				builder.append("(");
				builder.append(m);
				builder.append(")");
				i += m.length() - 1;
			}
		}

		return builder.toString();
	}
}
