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
	private final NotationType notationScheme;

	public AlgorithmTransform(final Config config) {
		usingTriggers = config.isTriggers();
		notationScheme = config.getNotationScheme();
	}

	public String transform(final String algorithm) {
		return applyNotation(usingTriggers ? applyTriggers(algorithm) : algorithm);
	}

	private String applyTriggers(final String s) {
		final StringBuilder builder = new StringBuilder();
		final int slen = s.length();
		for (int i = 0; i < slen; i++) {
			final List<String> matches = new LinkedList<String>();

			triggerLoop: for (String trigger : triggers) {
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

	private String applyNotation(String s) {
		switch (notationScheme) {
		case Singmaster:
			break;
		case WCA:
			s = s.replace("u'", "Uw'").replace("u", "Uw");
			s = s.replace("d'", "Dw'").replace("d", "Dw");
			s = s.replace("l'", "Lw'").replace("l", "Lw");
			s = s.replace("r'", "Rw'").replace("r", "Rw");
			s = s.replace("f'", "Fw'").replace("f", "Fw");
			s = s.replace("b'", "Bw'").replace("b", "Bw");
			break;
		}
		return s;
	}

	// public Algorithm rotate() {
	// Algorithm a = new Algorithm(type);
	//
	// String algo = algorithm;
	// algo = algo.replace("R", "Q").replace("B", "R").replace("L", "B").replace("F",
	// "L").replace("Q", "F");
	// algo = algo.replace("r", "q").replace("b", "r").replace("l", "b").replace("f",
	// "l").replace("q", "f");
	// algo = algo.replace("x'", "a").replace("z'", "q").replace("x", "h").replace("z", "j");
	// algo = algo.replace("a", "z'").replace("q", "x").replace("h", "z").replace("j", "x'");
	// algo = algo.replace("x''", "x").replace("z''", "z");
	// algo = algo.replace("M2", "Q2").replace("S2", "M2").replace("Q2", "S2");
	// algo = algo.replace("M", "a").replace("S", "q").replace("M'", "h").replace("S'", "j");
	// algo = algo.replace("a", "S").replace("q", "M'").replace("h", "S'").replace("j", "M");
	// algo = algo.replace("M''", "M").replace("S''", "S");
	// a.setAlgorithm(algo);
	//
	// final int[] amap = new int[] { 2, 5, 8, 1, 4, 7, 0, 3, 6 };
	// // for (Arrow arrow : arrows) {
	// // a.arrows.add(new Arrow(amap[arrow.from], amap[arrow.to], arrow.twoSides));
	// // }
	//
	// final int[] fmap = new int[] { 7, 12, 17, 2, 6, 11, 16, 20, 1, 5, 10, 15, 19, 0, 4, 9, 14,
	// 18, 3, 8, 13 };
	// for (int i = 0; i < faces.length; i++) {
	// a.faces[fmap[i]] = faces[i];
	// }
	//
	// return a;
	// }

}
