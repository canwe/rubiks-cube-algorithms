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

package se.slackers.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LastLayer extends JPanel implements MouseListener {
	private static final int AREAS = 21;
	private static final int CENTER = 10;
	private static final int SIZE = 125;
	private static final int block = SIZE / 5;
	
	private Rectangle [] areas = new Rectangle[AREAS];
	private boolean [] values = new boolean[AREAS];
	private FaceListener faceListener; 
	private Map<Integer, Integer> arrow = new HashMap<Integer, Integer>();	
	
	private int lastArrow = -1;

	public LastLayer() {
		init(0x400);
	}
	
	public LastLayer(int value) {
		init(value);
	}
	
	private void init(int value) {
		Dimension size = new Dimension(SIZE, SIZE);
		setSize(size);
		setPreferredSize(size);
		setMinimumSize(size);
		
		setValue(value);
		
		for (int i=0;i<3;i++) {
			addArea(i, (i+1)*block, 0, block, block/2);
			addArea(i+18, (i+1)*block, 4*block + block/2, block, block/2);
			addArea(i*5+3, 0, (i+1)*block, block/2, block);
			addArea(i*5+7, 4*block+block/2, (i+1)*block, block/2, block);
			
			for (int j=0;j<3;j++) {
				addArea(4+i+j*5, (i+1)*block, (j+1)*block, block, block);
			}
		}		
		
		addMouseListener(this);
	}

	private int addArea(int index, int x, int y, int w, int h) {
		areas[index] = new Rectangle(x, y, w, h);
		return index+1;
	}
	
	private void addArrow(int from, int to) {
		arrow.put(from, to);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		final int width = getWidth();
		final int height = getHeight();
		
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		
		for (int i=0;i<AREAS;i++) {
			final Rectangle r = areas[i];
			if (values[i]) {
				g.setColor(Color.yellow);
				g.fillRect(r.x, r.y, r.width, r.height);
			}
			g.setColor(Color.black);
			g.drawRect(r.x, r.y, r.width, r.height);
		}
		
		final int S = 3;
		g.setColor(Color.red);
		if (lastArrow >= 0) {
			final Rectangle from = areas[lastArrow];
			int x1 = (int) from.getCenterX() - block/4;
			int y1 = (int) from.getCenterY() - block/4;
			g.drawOval(x1-S, y1-S, 2*S, 2*S);
		}
		
		for (Entry<Integer, Integer> entry : arrow.entrySet()) {
			final Rectangle from = areas[entry.getKey()];
			final Rectangle to = areas[entry.getValue()];
			
			int x1 = (int) from.getCenterX() - block/4; 
			int y1 = (int) from.getCenterY() - block/4;
			int x2 = (int) to.getCenterX();
			int y2 = (int) to.getCenterY();
			
			g.drawLine(x1, y1, x2, y2);
			
			g.drawOval(x1-S, y1-S, 2*S, 2*S);
			g.fillOval(x2-S, y2-S, 2*S, 2*S);
		}
	}

	private void setValue(int value) {
		Arrays.fill(values, false);
		for (int i=0;i<AREAS;i++) {
			values[i] = 0 != (value & (int)Math.pow(2, i));
		}
		values[CENTER] = true;
	}	
	
	public final String getArrow() {
		final List<String> result = new ArrayList<String>();
		for (Entry<Integer, Integer> entry : arrow.entrySet()) {
			final int from = entry.getKey();
			final int to = entry.getValue();
			result.add(String.format("%x%x", transform(from), transform(to)));
		}
		Collections.sort(result);

		final StringBuilder builder = new StringBuilder();
		for (String a : result) {
			builder.append(a);
		}
		return builder.toString();
	}
	
	private int transform(int index) {
		final int x = (index-4) % 5;
		final int y = (index-4) / 5;
		return y*3+x;
	}

	public final int getValue() {
		int value = 0;
		for (int i=0;i<AREAS;i++) {
			if (values[i]) {
				value |= (int)Math.pow(2, i);
			}
		}
		return value;
	}
	
	public String getType() {
		if (getValue() == 0x1ce70) {
			return "PLL";
		}
		return "OLL";
	}
	
	public String toString() {
		return String.format("0x%x", getValue());
	}
	
	private int getIndex(int x, int y) {
		for (int i=0;i<AREAS;i++) {
			final Rectangle r = areas[i];
			if (r.contains(x, y)) {
				return i;
			}
		}		
		return -1;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		final int x = e.getX();
		final int y = e.getY();
		final int index = getIndex(x, y);
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (index >= 0 && index != CENTER) {
				values[index] = !values[index];
				repaint();
				invokeListener();
				return;
			}
			
			if (e.getClickCount() > 1 &&  index == CENTER) {
				setValue(0x1ce70);
				repaint();
				invokeListener();		
			}
		}
		
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (invalidArrow(index)) {
				return;
			}
			if (index >= 0 && lastArrow == index) {
				arrow.remove(index);
				lastArrow = -1;
				invokeListener();
			} else if (lastArrow < 0) {
				lastArrow = index;
			} else if (index >= 0){
				// add an arrow
				addArrow(lastArrow, index);
				lastArrow = -1;
				invokeListener();
			}
			repaint();
		}
	}

	private boolean invalidArrow(int index) {
		switch (index) {
		case 4:
		case 5:
		case 6:
		case 9:
		case 11:
		case 14:
		case 15:
		case 16:
			return false;
		}
		return true;
	}

	private void invokeListener() {
		if (faceListener != null) {
			faceListener.faceConfigChanged(getValue());
		}		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	public void setFaceListener(FaceListener faceListener) {
		this.faceListener = faceListener;
	}
}
