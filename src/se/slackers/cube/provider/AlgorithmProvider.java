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

package se.slackers.cube.provider;

import java.util.HashMap;

import se.slackers.cube.model.algorithm.Algorithm;
import se.slackers.cube.model.algorithm.PermutationType;
import se.slackers.cube.model.permutation.Permutation;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class AlgorithmProvider extends ContentProvider {
	private static HashMap<String, String> permutationProjectionMap;
	private static HashMap<String, String> algorithmProjectionMap;

	private static final int ALGORITHMS = 1;
	private static final int ALGORITHM_ID = 2;
	private static final int PERMUTATIONS = 3;
	private static final int PERMUTATION_ID = 4;
	private static final int PERMUTATION_ALGORITHMS = 5;
	private static final int PERMUTATION_FAVORITE = 6;
	private static final int PERMUTATIONS_OLL = 7;
	private static final int PERMUTATIONS_PLL = 8;
	private static final int PERMUTATIONS_F2L = 9;

	private static final UriMatcher sUriMatcher;
	public static final String AUTHORITY = "se.slackers.cube.Algorithms";

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs,
			final String sortOrder) {
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String orderBy = Algorithm.DEFAULT_SORT_ORDER;

		switch (sUriMatcher.match(uri)) {
		case ALGORITHMS:
			qb.setTables(DatabaseHelper.ALGORITHM_TABLE);
			qb.setProjectionMap(algorithmProjectionMap);
			break;
		case ALGORITHM_ID:
			qb.setTables(DatabaseHelper.ALGORITHM_TABLE);
			qb.setProjectionMap(algorithmProjectionMap);
			qb.appendWhere(Algorithm._ID + "=" + uri.getPathSegments().get(1));
			break;
		case PERMUTATIONS:
			qb.setTables(DatabaseHelper.PERMUTATION_TABLE);
			qb.setProjectionMap(permutationProjectionMap);
			orderBy = Permutation.DEFAULT_SORT_ORDER;
			break;
		case PERMUTATIONS_OLL:
			qb.setTables(DatabaseHelper.PERMUTATION_TABLE);
			qb.setProjectionMap(permutationProjectionMap);
			qb.appendWhere(Permutation.OLL_FILTER);
			orderBy = Permutation.DEFAULT_SORT_ORDER;
			break;
		case PERMUTATIONS_PLL:
			qb.setTables(DatabaseHelper.PERMUTATION_TABLE);
			qb.setProjectionMap(permutationProjectionMap);
			qb.appendWhere(Permutation.PLL_FILTER);
			orderBy = Permutation.DEFAULT_SORT_ORDER;
			break;
		case PERMUTATIONS_F2L:
			qb.setTables(DatabaseHelper.PERMUTATION_TABLE);
			qb.setProjectionMap(permutationProjectionMap);
			qb.appendWhere(Permutation.TYPE + "='" + PermutationType.F2L.name() + "'");
			orderBy = Permutation.DEFAULT_SORT_ORDER;
			break;
		case PERMUTATION_ID:
			qb.setTables(DatabaseHelper.PERMUTATION_TABLE);
			qb.setProjectionMap(permutationProjectionMap);
			qb.appendWhere(Permutation._ID + "=" + uri.getPathSegments().get(1));
			orderBy = Permutation.DEFAULT_SORT_ORDER;
			break;
		case PERMUTATION_ALGORITHMS:
			qb.setTables(DatabaseHelper.ALGORITHM_TABLE);
			qb.setProjectionMap(algorithmProjectionMap);
			qb.appendWhere(Algorithm.PERMUTATION_ID + "=" + uri.getPathSegments().get(1));
			break;
		case PERMUTATION_FAVORITE:
			qb.setTables(DatabaseHelper.ALGORITHM_TABLE);
			qb.setProjectionMap(algorithmProjectionMap);
			final String id = uri.getPathSegments().get(1);
			final String where = String.format("%s=%s AND %s!=0", Algorithm.PERMUTATION_ID, id, Algorithm.RANK);
			qb.appendWhere(where);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		if (!TextUtils.isEmpty(sortOrder)) {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(final Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case ALGORITHM_ID:
		case PERMUTATION_FAVORITE:
			return Algorithm.CONTENT_ITEM_TYPE;
		case ALGORITHMS:
		case PERMUTATION_ALGORITHMS:
			return Algorithm.CONTENT_TYPE;
		case PERMUTATIONS_OLL:
		case PERMUTATIONS_PLL:
		case PERMUTATIONS_F2L:
		case PERMUTATIONS:
			return Permutation.CONTENT_TYPE;
		case PERMUTATION_ID:
			return Permutation.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case ALGORITHMS:
			final long id = db.insert(DatabaseHelper.ALGORITHM_TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris.withAppendedId(uri, id);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public int delete(final Uri uri, final String where, final String[] whereArgs) {
		throw new UnsupportedOperationException("delete is not implemented");
	}

	@Override
	public int update(final Uri uri, final ContentValues values, final String where, final String[] whereArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case PERMUTATIONS:
			count = db.update(DatabaseHelper.PERMUTATION_TABLE, values, where, whereArgs);
			break;
		case ALGORITHMS:
			count = db.update(DatabaseHelper.ALGORITHM_TABLE, values, where, whereArgs);
			break;
		case ALGORITHM_ID:
			count = db.update(DatabaseHelper.ALGORITHM_TABLE, values, Algorithm._ID + "="
					+ uri.getPathSegments().get(1), null);
			break;
		case PERMUTATION_ALGORITHMS:
			final String permutationWhere = String.format("(%s=%s)", Algorithm.PERMUTATION_ID, uri.getPathSegments()
					.get(1));
			final String extendedWhere = where == null ? permutationWhere : String.format("%s AND (%s)",
					permutationWhere, where);
			count = db.update(DatabaseHelper.ALGORITHM_TABLE, values, extendedWhere, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, "algorithms", ALGORITHMS);
		sUriMatcher.addURI(AUTHORITY, "algorithms/#", ALGORITHM_ID);
		sUriMatcher.addURI(AUTHORITY, "permutations", PERMUTATIONS);
		sUriMatcher.addURI(AUTHORITY, "permutations/#", PERMUTATION_ID);
		sUriMatcher.addURI(AUTHORITY, "permutations/#/favorite", PERMUTATION_FAVORITE);
		sUriMatcher.addURI(AUTHORITY, "permutations/#/algorithms", PERMUTATION_ALGORITHMS);
		sUriMatcher.addURI(AUTHORITY, "permutations/pll", PERMUTATIONS_PLL);
		sUriMatcher.addURI(AUTHORITY, "permutations/oll", PERMUTATIONS_OLL);
		sUriMatcher.addURI(AUTHORITY, "permutations/f2l", PERMUTATIONS_F2L);

		algorithmProjectionMap = new HashMap<String, String>();
		algorithmProjectionMap.put(Algorithm._ID, Algorithm._ID);
		algorithmProjectionMap.put(Algorithm.PERMUTATION_ID, Algorithm.PERMUTATION_ID);
		algorithmProjectionMap.put(Algorithm.ALGORITHM, Algorithm.ALGORITHM);
		algorithmProjectionMap.put(Algorithm.RANK, Algorithm.RANK);

		permutationProjectionMap = new HashMap<String, String>();
		permutationProjectionMap.put(Permutation._ID, Permutation._ID);
		permutationProjectionMap.put(Permutation.TYPE, Permutation.TYPE);
		permutationProjectionMap.put(Permutation.NAME, Permutation.NAME);
		permutationProjectionMap.put(Permutation.FACE_CONFIG, Permutation.FACE_CONFIG);
		permutationProjectionMap.put(Permutation.ARROW_CONFIG, Permutation.ARROW_CONFIG);
		permutationProjectionMap.put(Permutation.ROTATION, Permutation.ROTATION);
		permutationProjectionMap.put(Permutation.VIEWS, Permutation.VIEWS);
		permutationProjectionMap.put(Permutation.QUICKLIST, Permutation.QUICKLIST);
	}
}
