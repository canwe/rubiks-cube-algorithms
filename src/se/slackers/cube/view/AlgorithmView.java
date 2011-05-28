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

package se.slackers.cube.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.slackers.cube.Config;
import se.slackers.cube.R;
import se.slackers.cube.config.AlgorithmTransform;
import se.slackers.cube.config.DoubleNotation;
import se.slackers.cube.model.algorithm.Algorithm;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
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
	private final int fontSize;

	public AlgorithmView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		fontSize = context.getResources().getDimensionPixelOffset(R.dimen.font_size_quicklist);
	}

	public AlgorithmView(final Context context, final Config config, final Algorithm algorithm) {
		super(context);
		setAlgorithm(config, algorithm);

		fontSize = context.getResources().getDimensionPixelOffset(R.dimen.font_size_quicklist);
	}

	public void setAlgorithm(final Config config, final Algorithm algorithm) {
		this.isColoredReverse = config.isColoredReverse();
		this.doubleNotation = config.getDoubleTurns();
		this.transform = new AlgorithmTransform(config);
		this.algorithm = algorithm;
		if (algorithm != null) {
			adjust(algorithm.getInstruction().render(config.getNotationScheme()));
		}
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	private void adjust(final String algorithm) {
		final String text = transform.transform(algorithm);
		final SpannableStringBuilder span = new SpannableStringBuilder(text);

		span.setSpan(new ForegroundColorSpan(Config.COLOR), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		span.setSpan(new AbsoluteSizeSpan(fontSize), 0, span.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

		if (doubleNotation != DoubleNotation.Normal) {
			final Matcher matcher = Pattern.compile("2'?").matcher(text);
			int index = 0;

			while (matcher.find(index)) {
				final int start = matcher.start();
				final int end = matcher.end();
				final CharacterStyle style = (doubleNotation == DoubleNotation.Superscript) ? new SuperscriptSpan()
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
