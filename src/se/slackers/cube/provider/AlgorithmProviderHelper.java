package se.slackers.cube.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

public class AlgorithmProviderHelper {
	public static boolean save(final ContentResolver resolver, final Permutation permutation) {
		final Uri uri = Permutation.CONTENT_URI;
		final ContentValues values = new ContentValues();
		values.put(Permutation.NAME, permutation.getName());
		values.put(Permutation.VIEWS, permutation.getViews());
		values.put(Permutation.TYPE, permutation.getType().name());

		return 0 != resolver.update(uri, values, Permutation._ID + "=" + permutation.getId(), null);
	}

	public static boolean save(final ContentResolver resolver, final Algorithm algorithm) {
		final Uri uri = Algorithm.CONTENT_URI;
		final ContentValues values = new ContentValues();
		values.put(Algorithm.ALGORITHM, algorithm.getAlgorithm());
		values.put(Algorithm.RANK, algorithm.getRank());

		return 0 != resolver.update(uri, values, Algorithm._ID + "=" + algorithm.getId(), null);
	}
}
