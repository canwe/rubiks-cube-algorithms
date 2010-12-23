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
