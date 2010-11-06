package se.slackers.cube.image;

import android.graphics.RectF;

public class CubeMetric {
	public static enum Metric {
		Back, Front, Left, Right, Top
	}

	public final int size;
	private final RectF offset;
	private final RectF[] faces = new RectF[21];

	public CubeMetric(final int size) {
		//
		// Ext | L | 2x Border | T | Border | T | Border | T | 2x Border | R | Ext
		//
		this.size = size;
		final int border = size * 2 / 100;
		final int block = size * 26 / 100;
		final int face = size * 4 / 100;
		final int external = size * 4 / 100;

		final int leftX = external;
		final int topX = leftX + face + 2 * border;
		final int rightX = topX + 3 * (block + border) + border;
		final int backY = external;
		final int topY = backY + face + 2 * border;
		final int frontY = topY + 3 * (block + border) + border;

		for (int i = 0; i < 3; i++) {
			final int pos = i * (block + border);
			addFace(i, topX + pos, backY, block, face);
			addFace(i + 18, topX + pos, frontY, block, face);
			addFace(i * 5 + 3, leftX, topY + pos, face, block);
			addFace(i * 5 + 7, rightX, topY + pos, face, block);

			for (int j = 0; j < 3; j++) {
				addFace(4 + j + i * 5, topX + j * (block + border), topY + pos, block, block);
			}
		}

		final int o = face + external - border;

		offset = new RectF(o, o, size - o, size - o);
	}

	private void addFace(final int index, final int x, final int y, final int w, final int h) {
		faces[index] = new RectF(x, y, x + w, y + h);
	}

	public RectF rect(final int index) {
		return faces[index];
	}

	public Metric type(final int index) {
		switch (index) {
		case 0:
		case 1:
		case 2:
			return Metric.Back;
		case 3:
		case 8:
		case 13:
			return Metric.Left;
		case 7:
		case 12:
		case 17:
			return Metric.Right;
		case 18:
		case 19:
		case 20:
			return Metric.Front;
		default:
			return Metric.Top;
		}
	}

	public RectF getOffset() {
		return offset;
	}
}
