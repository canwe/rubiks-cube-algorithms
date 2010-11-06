package se.slackers.cube.render;

public class Face {
	private Vector3d[] points;

	public Face(final Vector3d... v) {
		points = v;
	}

	public Vector3d[] getPoints() {
		return points;
	}

	public void setPoints(final Vector3d[] points) {
		this.points = points;
	}
}
