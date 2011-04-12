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

package se.slackers.cube.model.permutation;

import se.slackers.cube.model.Rotatable;
import se.slackers.cube.model.algorithm.PermutationType;
import se.slackers.cube.provider.AlgorithmProvider;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class Permutation implements BaseColumns, Comparable<Permutation>, Rotatable<Permutation> {
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.slackers.permutation";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.slackers.permutation";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AlgorithmProvider.AUTHORITY + "/permutations");
	public static final Uri CONTENT_URI_OLL = Uri.parse("content://" + AlgorithmProvider.AUTHORITY
			+ "/permutations/oll");
	public static final Uri CONTENT_URI_PLL = Uri.parse("content://" + AlgorithmProvider.AUTHORITY
			+ "/permutations/pll");
	public static final Uri CONTENT_URI_F2L = Uri.parse("content://" + AlgorithmProvider.AUTHORITY
			+ "/permutations/f2l");

	public static final String DEFAULT_SORT_ORDER = "type, name";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String FACE_CONFIG = "faces";
	public static final String ARROW_CONFIG = "arrows";
	public static final String ROTATION = "rotation";
	public static final String VIEWS = "views";
	public static final String QUICKLIST = "quicklist";

	public static final String PLL_FILTER = TYPE + "=\"" + PermutationType.PLL.name() + "\"";
	public static final String OLL_FILTER = TYPE + "='" + PermutationType.OLL.name() + "'";

	private long id;
	private PermutationType type;
	private String name;
	private FaceConfiguration faceConfiguration;
	private ArrowConfiguration arrowConfiguration;
	private int views;
	private int rotation;
	private boolean quicklist = false;

	public Permutation(final long id, final PermutationType type, final String name,
			final FaceConfiguration faceConfiguration, final ArrowConfiguration arrowConfig, final int rotation,
			final int views, final boolean quicklist) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.faceConfiguration = faceConfiguration;
		this.arrowConfiguration = arrowConfig;
		this.rotation = rotation;
		this.views = views;
		this.quicklist = quicklist;
	}

	public Permutation rotate(final int turns) {
		final int newRotation = (rotation + turns) % 4;
		final FaceConfiguration faces = faceConfiguration.rotate(turns);
		final ArrowConfiguration arrows = arrowConfiguration.rotate(turns);
		return new Permutation(id, type, name, faces, arrows, newRotation, views, quicklist);
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public PermutationType getType() {
		return type;
	}

	public void setType(final PermutationType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public FaceConfiguration getFaceConfiguration() {
		return faceConfiguration;
	}

	public void setFaceConfig(final FaceConfiguration faceConfig) {
		this.faceConfiguration = faceConfig;
	}

	public ArrowConfiguration getArrowConfiguration() {
		return arrowConfiguration;
	}

	public void setArrowConfig(final ArrowConfiguration arrowConfig) {
		this.arrowConfiguration = arrowConfig;
	}

	public void setRotation(final int rotation) {
		this.rotation = rotation;
	}

	public int getRotation() {
		return rotation;
	}

	public int getViews() {
		return views;
	}

	public void setViews(final int views) {
		this.views = views;
	}

	public boolean getQuickList() {
		return quicklist;
	}

	public void setQuickList(final boolean quicklist) {
		this.quicklist = quicklist;
	}

	public int compareTo(final Permutation p) {
		if (type != p.type) {
			return type.compareTo(p.type);
		}
		if (type == PermutationType.OLL) {
			final int len = name.length();
			final int plen = p.name.length();

			if (len != plen) {
				return len < plen ? -1 : 1;
			}
		}
		return name.compareTo(p.name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrowConfiguration == null) ? 0 : arrowConfiguration.hashCode());
		result = prime * result + ((faceConfiguration == null) ? 0 : faceConfiguration.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + rotation;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Permutation other = (Permutation) obj;
		if (arrowConfiguration == null) {
			if (other.arrowConfiguration != null) {
				return false;
			}
		} else if (!arrowConfiguration.equals(other.arrowConfiguration)) {
			return false;
		}
		if (faceConfiguration == null) {
			if (other.faceConfiguration != null) {
				return false;
			}
		} else if (!faceConfiguration.equals(other.faceConfiguration)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (rotation != other.rotation) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	private static int iId = -1;
	private static int iType = -1;
	private static int iName = -1;
	private static int iFace = -1;
	private static int iArrow = -1;
	private static int iRotation = -1;
	private static int iViews = -1;
	private static int iQuickList = -1;

	public static Permutation fromCursor(final Cursor cursor) {
		if (iId < 0) {
			iId = cursor.getColumnIndex(Permutation._ID);
			iType = cursor.getColumnIndex(Permutation.TYPE);
			iName = cursor.getColumnIndex(Permutation.NAME);
			iFace = cursor.getColumnIndex(Permutation.FACE_CONFIG);
			iArrow = cursor.getColumnIndex(Permutation.ARROW_CONFIG);
			iRotation = cursor.getColumnIndex(Permutation.ROTATION);
			iViews = cursor.getColumnIndex(Permutation.VIEWS);
			iQuickList = cursor.getColumnIndex(Permutation.QUICKLIST);
		}

		final long id = cursor.getLong(iId);
		final PermutationType type = PermutationType.valueOf(cursor.getString(iType));
		final String name = cursor.getString(iName);
		final String faceConfig = cursor.getString(iFace);
		final String arrowConfig = cursor.getString(iArrow);
		final int rotation = cursor.getInt(iRotation);
		final int views = cursor.getInt(iViews);
		final boolean quicklist = cursor.getInt(iQuickList) == 1;

		// Note(erik.b): This creation ignores rotation
		return new Permutation(id, type, name, new FaceConfiguration(faceConfig), new ArrowConfiguration(arrowConfig),
				rotation, views, quicklist);
	}
}
