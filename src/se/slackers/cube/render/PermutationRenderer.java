package se.slackers.cube.render;

import se.slackers.cube.Config;
import se.slackers.cube.image.Arrow;
import se.slackers.cube.image.CubeMetric;
import se.slackers.cube.image.CubeMetric.Metric;
import se.slackers.cube.provider.Permutation;
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
		final int size = list ? config.getListWidth() : config.getViewWidth();
		metric = new CubeMetric(size);

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
		drawFaces(canvas, permutation.getFaceConfig());
		drawArrows(canvas, permutation.getArrowConfig());

		return bitmap;
	}

	private void drawArrows(final Canvas canvas, final String arrowConfig) {
		if (arrowConfig == null || arrowConfig.length() == 0) {
			return;
		}

		for (int i = 0; i < arrowConfig.length(); i += 2) {
			final Arrow arrow = new Arrow(arrowConfig.charAt(i + 0) - '0', arrowConfig.charAt(i + 1) - '0');
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

	private void drawFaces(final Canvas canvas, final long faceConfig) {
		int bit = 1;
		for (int i = 0; i < 21; i++) {
			final RectF rect = metric.rect(i);
			final boolean active = (faceConfig & bit) != 0;

			if (metric.type(i) == Metric.Top) {
				final Paint paint = active ? activeFace : unknownFace;
				canvas.drawRect(rect, paint);
			} else if (active) {
				canvas.drawRect(rect, sideFace);
			}

			bit <<= 1;
		}
	}
}