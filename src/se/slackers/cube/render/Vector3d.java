package se.slackers.cube.render;

public class Vector3d {
	public float x, y, z;

	public Vector3d() {
		x = y = z = 0;
	}

	public Vector3d(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d(final Vector3d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
}