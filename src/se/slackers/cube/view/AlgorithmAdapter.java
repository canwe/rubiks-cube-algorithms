package se.slackers.cube.view;

import java.util.List;

import se.slackers.cube.Config;
import se.slackers.cube.R;
import se.slackers.cube.provider.Algorithm;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class AlgorithmAdapter extends ArrayAdapter<Algorithm> {
	private final Config config;
	private Algorithm favorite;

	public AlgorithmAdapter(final Context context, final Config config, final List<Algorithm> algorithms,
			final Algorithm favorite) {
		super(context, R.layout.algorithm_row, R.id.algorithm, algorithms);
		this.config = config;
		this.favorite = favorite;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.algorithm_row, null);
		}
		final Algorithm algorithm = getItem(position);
		final AlgorithmView algorithmView = (AlgorithmView) convertView.findViewById(R.id.algorithm);
		algorithmView.setAlgorithm(config, algorithm);

		final ImageView image = (ImageView) convertView.findViewById(R.id.favorite);
		if (favorite != null && favorite.equals(algorithm)) {
			image.setImageResource(R.drawable.ic_menu_star_selected);
		} else {
			image.setImageResource(R.drawable.ic_menu_star);
		}

		return convertView;
	}

	public void setFavorite(final Algorithm algorithm) {
		this.favorite = algorithm;
		notifyDataSetChanged();
	}
}
