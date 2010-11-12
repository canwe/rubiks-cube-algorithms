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

package se.slackers.cube.provider;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class Permutation implements BaseColumns, Comparable<Permutation> {
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.slackers.permutation";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.slackers.permutation";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AlgorithmProvider.AUTHORITY + "/permutations");
	public static final String DEFAULT_SORT_ORDER = "type, name";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String FACE_CONFIG = "faces";
	public static final String ARROW_CONFIG = "arrows";
	public static final String VIEWS = "views";

	private long id;
	private AlgorithmType type;
	private String name;
	private long faceConfig;
	private String arrowConfig;
	private int views;

	public Permutation(final long id, final AlgorithmType type, final String name, final long faceConfig,
			final String arrowConfig, final int views) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.faceConfig = faceConfig;
		this.arrowConfig = arrowConfig;
		this.views = views;
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public AlgorithmType getType() {
		return type;
	}

	public void setType(final AlgorithmType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public long getFaceConfig() {
		return faceConfig;
	}

	public void setFaceConfig(final long faceConfig) {
		this.faceConfig = faceConfig;
	}

	public String getArrowConfig() {
		return arrowConfig;
	}

	public void setArrowConfig(final String arrowConfig) {
		this.arrowConfig = arrowConfig;
	}

	public int getViews() {
		return views;
	}

	public void setViews(final int views) {
		this.views = views;
	}

	public int compareTo(final Permutation p) {
		if (type != p.type) {
			return type.compareTo(p.type);
		}
		if (type == AlgorithmType.OLL) {
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
		result = prime * result + ((arrowConfig == null) ? 0 : arrowConfig.hashCode());
		result = prime * result + (int) (faceConfig ^ (faceConfig >>> 32));
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
		if (arrowConfig == null) {
			if (other.arrowConfig != null) {
				return false;
			}
		} else if (!arrowConfig.equals(other.arrowConfig)) {
			return false;
		}
		if (faceConfig != other.faceConfig) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	public static Permutation fromCursor(final Cursor cursor) {
		final long id = cursor.getLong(cursor.getColumnIndex(Permutation._ID));
		final AlgorithmType type = AlgorithmType.valueOf(cursor.getString(cursor.getColumnIndex(Permutation.TYPE)));
		final String name = cursor.getString(cursor.getColumnIndex(Permutation.NAME));
		final long faceConfig = cursor.getLong(cursor.getColumnIndex(Permutation.FACE_CONFIG));
		final String arrowConfig = cursor.getString(cursor.getColumnIndex(Permutation.ARROW_CONFIG));
		final int views = cursor.getInt(cursor.getColumnIndex(Permutation.VIEWS));

		return new Permutation(id, type, name, faceConfig, arrowConfig, views);
	}
}
