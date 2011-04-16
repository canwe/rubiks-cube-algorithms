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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.slackers.cube.R;
import se.slackers.cube.config.NotationType;
import se.slackers.cube.model.algorithm.Algorithm;
import se.slackers.cube.model.algorithm.Instruction;
import se.slackers.cube.model.algorithm.PermutationType;
import se.slackers.cube.model.permutation.ArrowConfiguration;
import se.slackers.cube.model.permutation.FaceConfiguration;
import se.slackers.cube.model.permutation.Permutation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "algorithms.db";
	private static final int DATABASE_VERSION = 12;

	public static final String LOG_TAG = DatabaseHelper.class.getSimpleName();
	public static final String ALGORITHM_TABLE = "algorithms";
	public static final String PERMUTATION_TABLE = "permutation";
	private final Context context;

	DatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + ALGORITHM_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + PERMUTATION_TABLE);

		db.execSQL("CREATE TABLE " + ALGORITHM_TABLE + " (" + Algorithm._ID + " INTEGER PRIMARY KEY,"
				+ Algorithm.PERMUTATION_ID + " INTEGER," + Algorithm.ALGORITHM + " TEXT," + Algorithm.RANK
				+ " INTEGER," + Algorithm.BUILTIN + " INTEGER);");

		db.execSQL("CREATE TABLE " + PERMUTATION_TABLE + " (" + Permutation._ID + " INTEGER PRIMARY KEY,"
				+ Permutation.TYPE + " TEXT," + Permutation.NAME + " INTEGER," + Permutation.FACE_CONFIG + " TEXT,"
				+ Permutation.ARROW_CONFIG + " TEXT," + Permutation.ROTATION + " INTEGER," + Permutation.VIEWS
				+ " INTEGER," + Permutation.QUICKLIST + " INTEGER);");

		final List<String> algorithms = new LinkedList<String>();
		algorithms.addAll(Arrays.asList(context.getResources().getStringArray(R.array.oll_algorithms)));
		algorithms.addAll(Arrays.asList(context.getResources().getStringArray(R.array.pll_algorithms)));
		insertAlgorithms(db, algorithms);
	}

	@Override
	public void onOpen(final SQLiteDatabase db) {
		super.onOpen(db);

		if (db.isReadOnly() || db.getVersion() != 6) {
			return;
		}
		// Check if the database is empty
		final String[] columns = new String[] { Permutation._ID };
		final Cursor cursor = db.query(PERMUTATION_TABLE, columns, "", null, null, null, null);
		try {
			if (!cursor.moveToFirst()) {
				onCreate(db);
			}
		} finally {
			cursor.close();
		}
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
		switch (oldVersion) {
		default:
			recreateButSaveFavorites(db);
		case 11:
			updateTo12(db);
		}
	}

	/**
	 * @param db
	 */
	private void updateTo12(final SQLiteDatabase db) {
		db.execSQL(String.format("ALTER TABLE %s ADD %s INTEGER;", ALGORITHM_TABLE, Algorithm.BUILTIN));
		final ContentValues values = new ContentValues();
		values.put(Algorithm.BUILTIN, Algorithm.BUILTIN_ALGORITHM);
		db.update(ALGORITHM_TABLE, values, null, null);
	}

	/**
	 * @param db
	 */
	private void recreateButSaveFavorites(final SQLiteDatabase db) {
		final Map<String, Integer> favorites = storeFavorites(db);
		onCreate(db);
		restoreFavorites(db, favorites);
	}

	/**
	 * @param db
	 * @param favorites
	 */
	private void restoreFavorites(final SQLiteDatabase db, final Map<String, Integer> favorites) {
		for (final Entry<String, Integer> entry : favorites.entrySet()) {
			final ContentValues values = new ContentValues();
			values.put(Algorithm.RANK, entry.getValue());
			db.update(ALGORITHM_TABLE, values, Algorithm.ALGORITHM + "=?", new String[] { entry.getKey() });
		}
	}

	/**
	 * 
	 * @param db
	 * @return
	 */
	private Map<String, Integer> storeFavorites(final SQLiteDatabase db) {
		final Map<String, Integer> ator = new HashMap<String, Integer>();
		final Cursor cursor = db.query(ALGORITHM_TABLE, new String[] { Algorithm.ALGORITHM, Algorithm.RANK }, null,
				null, null, null, null);
		// store favorites
		try {
			if (cursor.moveToFirst()) {
				do {
					ator.put(cursor.getString(0), cursor.getInt(1));
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		return ator;
	}

	private void insertAlgorithms(final SQLiteDatabase db, final List<String> algorithms) {
		final Map<Permutation, Permutation> lookup = new HashMap<Permutation, Permutation>();
		for (final String algorithm : algorithms) {
			final String[] part = algorithm.split("\\s*,\\s*");

			Permutation permutation = toPermutation(part);
			if (!lookup.containsKey(permutation)) {
				lookup.put(permutation, permutation);
				insertPermutation(db, permutation);
			} else {
				permutation = lookup.get(permutation);
			}
			final int rank = Integer.parseInt(part[0]);
			insertAlgorithm(db, new Algorithm(permutation.getId(), new Instruction(part[5]), rank, 1));
		}
	}

	private void insertAlgorithm(final SQLiteDatabase db, final Algorithm algorithm) {
		final ContentValues values = new ContentValues();
		values.put(Algorithm.PERMUTATION_ID, algorithm.getPermutationId());
		values.put(Algorithm.ALGORITHM, algorithm.getInstruction().render(NotationType.Singmaster));
		values.put(Algorithm.RANK, algorithm.getRank());
		algorithm.setId(db.insert(ALGORITHM_TABLE, null, values));
	}

	private void insertPermutation(final SQLiteDatabase db, final Permutation permutation) {
		final ContentValues values = new ContentValues();
		values.put(Permutation.TYPE, permutation.getType().name());
		values.put(Permutation.NAME, permutation.getName());
		values.put(Permutation.VIEWS, 0);
		values.put(Permutation.FACE_CONFIG, permutation.getFaceConfiguration().serialize());
		values.put(Permutation.ARROW_CONFIG, permutation.getArrowConfiguration().serialize());
		values.put(Permutation.ROTATION, permutation.getRotation());
		values.put(Permutation.QUICKLIST, permutation.getQuickList() ? 1 : 0);
		permutation.setId(db.insert(PERMUTATION_TABLE, null, values));
	}

	private Permutation toPermutation(final String[] part) {
		final PermutationType type = PermutationType.valueOf(part[1]);
		final FaceConfiguration faceConfig = new FaceConfiguration(3, Long.parseLong(part[2], 16));
		final ArrowConfiguration arrowConfig = new ArrowConfiguration(part[3]);
		final String name = part[4];
		return new Permutation(0, type, name, faceConfig, arrowConfig, 0, 0, false);
	}
}
