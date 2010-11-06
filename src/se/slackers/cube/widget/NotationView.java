package se.slackers.cube.widget;

import se.slackers.cube.R;
import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotationView extends LinearLayout {
	private final TextView textView;
	private final ImageView imageView;

	public NotationView(final Context context, final String text, final int resource) {
		super(context);
		setOrientation(VERTICAL);

		textView = new TextView(context);
		imageView = new ImageView(context);

		textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setText(text);
		imageView.setImageResource(resource);
		addView(imageView);
		addView(textView);

		textView.setTextSize(getResources().getDimension(R.dimen.medium));
	}

	public TextView getTextView() {
		return textView;
	}
}