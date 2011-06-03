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

package se.slackers.cube.image;

import java.util.Arrays;

import se.slackers.cube.render.Face;
import se.slackers.cube.render.Vector3d;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class RotationBuilder {
	private int topFace[] = new int[9];
	private int rightFace[] = new int[9];
	private int frontFace[] = new int[9];
	private Paint top;
	private Paint front;
	private Paint right;
	private Paint background;
	private Paint border;

	public RotationBuilder() {
		Arrays.fill(topFace, 0);
		Arrays.fill(rightFace, 0);
		Arrays.fill(frontFace, 0);

		top = new Paint();
		front = new Paint();
		right = new Paint();
		background = new Paint();
		border = new Paint();

		top.setColor(0xffffffff);
		front.setColor(0xff00cc00);
		right.setColor(0xffcc0000);
		background.setColor(0xff000000);
		border.setColor(0xff404040);
	}

	public Bitmap build(final int size) {
		final Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(bitmap);

		// canvas.drawRect(0, 0, size, size, background);
		canvas.drawRect(0, 0, size, size, border);
		drawTop(canvas, size, size);

		return bitmap;
	}

	private void drawTop(final Canvas canvas, final int w, final int h) {
		final int x0 = 3;
		final int y0 = 0;
		final float dx = 2;
		final float dy = 0.25f;

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				final Face faceT = new Face(new Vector3d(x, y0, y), new Vector3d(x, y0, y + 1), new Vector3d(x + 1, y0,
						y + 1), new Vector3d(x + 1, y0, y));
				final Face faceR = new Face(new Vector3d(x0, y, x), new Vector3d(x0, y, x + 1), new Vector3d(x0, y + 1,
						x + 1), new Vector3d(x0, y + 1, x));
				drawFace(canvas, faceT, w, h, top, dx, dy);
				drawFace(canvas, faceR, w, h, right, dx, dy);
			}
		}
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				final Face faceF = new Face(new Vector3d(x, y, 0), new Vector3d(x, y + 1, 0), new Vector3d(x + 1,
						y + 1, 0), new Vector3d(x + 1, y, 0));
				drawFace(canvas, faceF, w, h, front, dx, dy);
			}
		}
	}

	private void drawFace(final Canvas canvas, final Face face, final int w, final int h, final Paint paint,
			final float x0, final float y0) {

		final Vector3d t = new Vector3d(1.5f + 5, 1.5f - 5, -3);
		final Path path = new Path();
		boolean first = true;
		for (Vector3d p : face.getPoints()) {
			final float z = p.z - t.z;
			final float x = (w / 2) * ((p.x - t.x) / z + x0);
			final float y = (h / 2) * ((p.y - t.y) / z + y0);

			if (first) {
				first = false;
				path.moveTo(x, y);
			} else {
				path.lineTo(x, y);
			}
		}
		path.close();
		canvas.drawPath(path, paint);
	}
}
