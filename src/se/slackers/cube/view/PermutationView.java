package se.slackers.cube.view;

import se.slackers.cube.provider.Permutation;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class PermutationView extends ImageView {
	private final Permutation permutation;

	public PermutationView(final Context context, final Permutation permutation) {
		super(context);
		this.permutation = permutation;
	}

	public Permutation getPermutation() {
		return permutation;
	}

	public PermutationView image(final Bitmap bitmap) {
		setImageBitmap(bitmap);
		return this;
	}

	public PermutationView padding(final int padding) {
		setPadding(padding, padding, padding, padding);
		return this;
	}
}