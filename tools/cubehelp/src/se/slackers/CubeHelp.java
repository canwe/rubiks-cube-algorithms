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

package se.slackers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import se.slackers.swing.FaceListener;
import se.slackers.swing.LastLayer;

public class CubeHelp {
	public static void main(final String[] args) {
		new CubeHelp();
	}

	private DocumentListener listener = new DocumentListener() {
		@Override
		public void removeUpdate(final DocumentEvent e) {
			updateText();
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			updateText();
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
			updateText();
		}
	};

	private JFrame frame;
	private LastLayer lastLayer;
	private JTextField noteBox;
	private JTextArea algorithmBox;
	private JTextArea text;
	private JButton cleanup;

	public CubeHelp() {
		frame = new JFrame("CubeAlgorithm");

		lastLayer = new LastLayer();
		lastLayer.setFaceListener(new FaceListener() {
			@Override
			public void faceConfigChanged(final int value) {
				updateText();
			}
		});

		cleanup = new JButton("Cleanup");
		cleanup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				cleanupAlgorithm();
			}
		});

		noteBox = new JTextField(15);
		noteBox.getDocument().addDocumentListener(listener);

		algorithmBox = new JTextArea(5, 15);
		algorithmBox.getDocument().addDocumentListener(listener);

		text = new JTextArea();
		text.setEditable(false);

		frame.getContentPane().add(doLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 400);
		frame.setVisible(true);
		updateText();

	}

	protected void cleanupAlgorithm() {
		String text = algorithmBox.getText();
		text = text.replaceAll("OLL", "");
		text = text.replaceAll("PLL", "");
		text = text.replaceAll("[ \t]+", "");
		text = text.replaceAll("\\[X\\]", "");
		algorithmBox.setText(text);
	}

	private Component doLayout() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		final JPanel left = new JPanel(new BorderLayout());
		left.add(noteBox, BorderLayout.NORTH);
		left.add(lastLayer, BorderLayout.CENTER);
		left.add(wrap(algorithmBox), BorderLayout.SOUTH);

		panel.add(left, BorderLayout.WEST);
		panel.add(text, BorderLayout.CENTER);
		panel.add(cleanup, BorderLayout.SOUTH);

		return panel;
	}

	private Component wrap(final JComponent c) {
		final JPanel panel = new JPanel(new FlowLayout());
		panel.add(c);
		return panel;
	}

	protected void updateText() {
		final StringBuilder builder = new StringBuilder();
		final int id = lastLayer.getValue();
		final String type = lastLayer.getType();
		final String arrow = lastLayer.getArrow();
		final String note = noteBox.getText();

		for (String algo : getAlgorithm()) {
			builder.append(String.format("%s, 0x%x, %s, %s, %s\n", type, id, arrow, note, applyTriggers(algo)));
		}
		text.setText(builder.toString());
	}

	private List<String> getAlgorithm() {
		String t = algorithmBox.getText();
		t = t.replace(" ", "").replace("'2", "2'");
		t = t.replace("Rw", "r").replace("Lw", "l").replace("Fw", "f").replace("Bw", "b").replace("Uw", "u")
				.replace("Dw", "d");
		t = t.replaceAll("[^RLFBUDMSErlfbudxyz2'\\(\\)\\r\\n]", "");
		return Arrays.asList(t.split("[\\r\\n]+"));
	}

	private String applyTriggers(String s) {
		final String[] triggers = new String[] { "RUR2'U'", "RU'R'U'", "R'U'RU'", "RUR'U'", "R'FRF'", "R'U'RU",
				"RUR'U", "RU2'R'", "R'U2R'", "R2'UR'", "M'U'M", "RUR2'", "RU'R'", "RU2R'", "R'UR'", "R'U'R", "R'U2R",
				"M2'U2", "RUR'", "RU'R", "R'UR", "R2'U2", "M'U'", "M'U2", "M2'U", "RU2'", "R'U'", "R'U2", "R2U'",
				"R2'U", "M'U", "RU'", "R'U", "RU" };

		final StringBuilder builder = new StringBuilder();
		s = s.replace("(", "").replace(")", "");
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

		// return builder.toString();
		return s;
	}
}
