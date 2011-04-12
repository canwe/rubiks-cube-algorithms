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

package se.slackers.cube.render;

import se.slackers.cube.Config;
import se.slackers.cube.image.CubeMetric;
import se.slackers.cube.image.CubeMetric.Metric;
import se.slackers.cube.model.permutation.Arrow;
import se.slackers.cube.model.permutation.ArrowConfiguration;
import se.slackers.cube.model.permutation.FaceConfiguration;
import se.slackers.cube.model.permutation.Permutation;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class PermutationRenderer {
	private final CubeMetric metric;
	private final Paint background;
	private final Paint border;
	private final Paint activeFace;
	private final Paint red = new Paint();
	private final Paint blue = new Paint();
	private final Paint unknownFace = new Paint();
	private final Paint sideFace = new Paint();

	public PermutationRenderer(final Config config, final boolean list) {
		metric = new CubeMetric(list ? config.getListCubeSize() : config.getViewCubeSize());

		background = new Paint();
		border = config.getBorderColor();
		background.setColor(config.getBackgroundColor());

		red.setColor(config.getEdgeColor());
		blue.setColor(config.getCornerColor());

		activeFace = config.getImportantFaceColor();
		unknownFace.setColor(config.getUnknownFaceColor());
		sideFace.setColor(config.getSideFaceColor());
	}

	public Bitmap render(final Permutation permutation) {
		final int size = metric.size;

		final Bitmap bitmap = Bitmap.createBitmap(size + 1, size + 1, Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(bitmap);
		canvas.drawRect(0, 0, size, size, background);
		canvas.drawRect(metric.getOffset(), border);
		drawFaces(canvas, permutation.getFaceConfiguration());
		drawArrows(canvas, permutation.getArrowConfiguration());

		return bitmap;
	}

	private void drawArrows(final Canvas canvas, final ArrowConfiguration arrowConfig) {
		if (arrowConfig == null) {
			return;
		}

		for (final Arrow arrow : arrowConfig.getArrows()) {
			final RectF from = arrow.getFrom(metric);
			final RectF to = arrow.getTo(metric);
			final Paint paint = arrow.usingCorner() ? blue : red;
			final int width = (int) (to.right - to.left);

			final float x0 = from.centerX();
			final float y0 = from.centerY();
			final float x1 = to.centerX();
			final float y1 = to.centerY();

			canvas.drawLine(x0, y0, x1, y1, paint);
			addArrowHead(canvas, x0, y0, x1, y1, arrow, width / 3, paint);
		}
	}

	private void addArrowHead(final Canvas canvas, final float fx, final float fy, final float tx, final float ty,
			final Arrow arrow, final int size, final Paint paint) {
		final Path p = new Path();
		final float t = Math.max(size / 4, 1);
		p.moveTo(t, 0);
		p.lineTo(t, size);
		p.lineTo(t + size, 0);
		p.lineTo(t, -size);
		p.close();

		if (arrow.vertical()) {
			if (ty > fy) {
				p.transform(rotate(90));
			} else {
				p.transform(rotate(-90));
			}
		} else if (arrow.horizontal()) {
			if (tx > fx) {
				// normal
			} else {
				p.transform(rotate(180));
			}
		} else if (tx > fx) {
			if (ty > fy) {
				p.transform(rotate(45));
			} else {
				p.transform(rotate(-45));
			}
		} else {
			if (ty > fy) {
				p.transform(rotate(135));
			} else {
				p.transform(rotate(-135));
			}
		}
		p.offset(tx, ty);
		canvas.drawPath(p, paint);

		final float ilen = (float) (1.0 / Math.sqrt((fx - tx) * (fx - tx) + (fy - ty) * (fy - ty)));
		final float dx = (tx - fx) * ilen;
		final float dy = (ty - fy) * ilen;
		canvas.drawLine(tx, ty, tx + dx * t, ty + dy * t, paint);
	}

	private Matrix rotate(final float degrees) {
		final Matrix m = new Matrix();
		m.preRotate(degrees);
		return m;
	}

	private void drawFaces(final Canvas canvas, final FaceConfiguration faceConfig) {
		for (int i = 0; i < 21; i++) {
			final RectF rect = metric.rect(i);
			final boolean active = faceConfig.isActive(i);

			if (metric.type(i) == Metric.Top) {
				final Paint paint = active ? activeFace : unknownFace;
				canvas.drawRect(rect, paint);
			} else if (active) {
				canvas.drawRect(rect, sideFace);
			}
		}
	}
}
