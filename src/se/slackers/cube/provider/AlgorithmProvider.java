package se.slackers.cube.provider;

import java.util.HashMap;

import android.content.ContentProvider;
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
	private static final int ALGORITHMS_FOR_PERMUTATION = 2;
	private static final int ALGORITHMS_FAVORITE = 5;
	private static final int PERMUTATIONS = 3;
	private static final int PERMUTATION_ID = 4;

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

		case ALGORITHMS_FAVORITE:
			qb.setTables(DatabaseHelper.ALGORITHM_TABLE);
			qb.setProjectionMap(algorithmProjectionMap);
			final String id = uri.getPathSegments().get(1);
			final String where = String.format("%s=%s AND %s!=0", Algorithm.PERMUTATION_ID, id, Algorithm.RANK);
			qb.appendWhere(where);
			break;
		case ALGORITHMS_FOR_PERMUTATION:
			qb.setTables(DatabaseHelper.ALGORITHM_TABLE);
			qb.setProjectionMap(algorithmProjectionMap);
			qb.appendWhere(Algorithm.PERMUTATION_ID + "=" + uri.getPathSegments().get(1));
			break;

		case PERMUTATIONS:
			qb.setTables(DatabaseHelper.PERMUTATION_TABLE);
			qb.setProjectionMap(permutationProjectionMap);
			orderBy = Permutation.DEFAULT_SORT_ORDER;
			break;

		case PERMUTATION_ID:
			qb.setTables(DatabaseHelper.PERMUTATION_TABLE);
			qb.setProjectionMap(permutationProjectionMap);
			qb.appendWhere(Permutation._ID + "=" + uri.getPathSegments().get(1));
			orderBy = Permutation.DEFAULT_SORT_ORDER;
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
		case ALGORITHMS:
		case ALGORITHMS_FAVORITE:
		case ALGORITHMS_FOR_PERMUTATION:
			return Algorithm.CONTENT_TYPE;
		case PERMUTATIONS:
			return Permutation.CONTENT_TYPE;
		case PERMUTATION_ID:
			return Permutation.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(final Uri uri, final ContentValues initialValues) {
		throw new UnsupportedOperationException("insert is not implemented");
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
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, "algorithms", ALGORITHMS);
		sUriMatcher.addURI(AUTHORITY, "algorithms/#", ALGORITHMS_FOR_PERMUTATION);
		sUriMatcher.addURI(AUTHORITY, "algorithms/#/favorite", ALGORITHMS_FAVORITE);
		sUriMatcher.addURI(AUTHORITY, "permutations", PERMUTATIONS);
		sUriMatcher.addURI(AUTHORITY, "permutations/#", PERMUTATION_ID);

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
		permutationProjectionMap.put(Permutation.VIEWS, Permutation.VIEWS);
	}
}