package se.slackers.cube.provider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import se.slackers.cube.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "algorithms.db";
	private static final int DATABASE_VERSION = 9;

	public static final String LOG_TAG = DatabaseHelper.class.getSimpleName();
	public static final String ALGORITHM_TABLE = "algorithms";
	public static final String PERMUTATION_TABLE = "permutation";
	private final List<String> algorithms;

	DatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		algorithms = new LinkedList<String>();
		algorithms.addAll(Arrays.asList(context.getResources().getStringArray(R.array.oll_algorithms)));
		algorithms.addAll(Arrays.asList(context.getResources().getStringArray(R.array.pll_algorithms)));
	}

	public void cleanup() {
		algorithms.clear();
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		if (algorithms.isEmpty()) {
			throw new RuntimeException("onCreate is called twice or no algorithms can be found");
		}

		db.execSQL("DROP TABLE IF EXISTS " + ALGORITHM_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + PERMUTATION_TABLE);

		db.execSQL("CREATE TABLE " + ALGORITHM_TABLE + " (" + Algorithm._ID + " INTEGER PRIMARY KEY,"
				+ Algorithm.PERMUTATION_ID + " INTEGER," + Algorithm.ALGORITHM + " TEXT," + Algorithm.RANK
				+ " INTEGER);");

		db.execSQL("CREATE TABLE " + PERMUTATION_TABLE + " (" + Permutation._ID + " INTEGER PRIMARY KEY,"
				+ Permutation.TYPE + " TEXT, " + Permutation.NAME + " INTEGER," + Permutation.FACE_CONFIG + " INTEGER,"
				+ Permutation.ARROW_CONFIG + " TEXT," + Permutation.VIEWS + " INTEGER);");

		insertAlgorithms(db, algorithms);
		cleanup();
	}

	@Override
	public void onOpen(final SQLiteDatabase db) {
		super.onOpen(db);

		if (db.isReadOnly() || db.getVersion() != 6) {
			return;
		}
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
		case 4:
		case 5:
		case 6:
			// dummy or old upgrades
		case 7:
			onCreate(db);
		case 8:
			upgradeTo9(db);
		}

		cleanup();
	}

	private void upgradeTo9(final SQLiteDatabase db) {
		// Change face config for OLL 36 and OLL 38
		// OLL 38: 59692 -> 10cd4a
		// OLL 36: 59612 -> 59692

		final String sql = "UPDATE " + PERMUTATION_TABLE + " SET " + Permutation.FACE_CONFIG + "=%d WHERE "
				+ Permutation.NAME + "='%s' AND " + Permutation.FACE_CONFIG + "=%d";
		db.execSQL(String.format(sql, 0x10cd4a, "OLL 38", 0x59692));
		db.execSQL(String.format(sql, 0x59692, "OLL 36", 0x59612));
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
			insertAlgorithm(db, new Algorithm(permutation.getId(), part[5], rank));
		}
	}

	private void insertAlgorithm(final SQLiteDatabase db, final Algorithm algorithm) {
		final ContentValues values = new ContentValues();
		values.put(Algorithm.PERMUTATION_ID, algorithm.getPermutationId());
		values.put(Algorithm.ALGORITHM, algorithm.getAlgorithm());
		values.put(Algorithm.RANK, algorithm.getRank());
		algorithm.setId(db.insert(ALGORITHM_TABLE, null, values));
	}

	private void insertPermutation(final SQLiteDatabase db, final Permutation permutation) {
		final ContentValues values = new ContentValues();
		values.put(Permutation.TYPE, permutation.getType().name());
		values.put(Permutation.NAME, permutation.getName());
		values.put(Permutation.VIEWS, 0);
		values.put(Permutation.FACE_CONFIG, permutation.getFaceConfig());
		values.put(Permutation.ARROW_CONFIG, permutation.getArrowConfig());
		permutation.setId(db.insert(PERMUTATION_TABLE, null, values));
	}

	private Permutation toPermutation(final String[] part) {
		final AlgorithmType type = AlgorithmType.valueOf(part[1]);
		final long faceConfig = Long.parseLong(part[2], 16);
		final String arrowConfig = part[3];
		final String name = part[4];
		return new Permutation(0, type, name, faceConfig, arrowConfig, 0);
	}
}