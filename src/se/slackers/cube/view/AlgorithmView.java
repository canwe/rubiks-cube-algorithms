package se.slackers.cube.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.slackers.cube.Config;
import se.slackers.cube.config.AlgorithmTransform;
import se.slackers.cube.config.DoubleNotation;
import se.slackers.cube.provider.Algorithm;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class AlgorithmView extends TextView {
	private Algorithm algorithm;
	private AlgorithmTransform transform;

	private boolean isColoredReverse;
	private DoubleNotation doubleNotation = DoubleNotation.Normal;

	public AlgorithmView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	public AlgorithmView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public AlgorithmView(final Context context, final Config config, final Algorithm algorithm) {
		super(context);
		setAlgorithm(config, algorithm);
	}

	public void setAlgorithm(final Config config, final Algorithm algorithm) {
		this.isColoredReverse = config.isColoredReverse();
		this.doubleNotation = config.getDoubleTurns();
		this.algorithm = algorithm;
		this.transform = new AlgorithmTransform(config);
		adjust(algorithm.getAlgorithm());
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	private void adjust(final String algorithm) {
		final String text = transform.transform(algorithm);
		final SpannableStringBuilder span = new SpannableStringBuilder(text);

		span.setSpan(new ForegroundColorSpan(Config.COLOR), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		if (doubleNotation != DoubleNotation.Normal) {
			final Matcher matcher = Pattern.compile("2'?").matcher(text);
			int index = 0;

			while (matcher.find(index)) {
				final int start = matcher.start();
				final int end = matcher.end();
				final CharacterStyle style = (doubleNotation == DoubleNotation.SuperScript) ? new SuperscriptSpan()
						: new SubscriptSpan();
				span.setSpan(style, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				span.setSpan(new RelativeSizeSpan(0.8f), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				index = end;
			}
		}

		if (isColoredReverse) {
			final Matcher matcher = Pattern.compile(".2?w?'").matcher(text);
			int index = 0;

			while (matcher.find(index)) {
				final int start = matcher.start();
				final int end = matcher.end();
				span.setSpan(new ForegroundColorSpan(Config.REVERSE_COLOR), start, end,
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				index = end;
			}
		}

		setText(span, TextView.BufferType.SPANNABLE);
	}
}
