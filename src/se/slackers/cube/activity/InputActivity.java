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

package se.slackers.cube.activity;

import java.util.LinkedList;
import java.util.List;

import se.slackers.cube.R;
import se.slackers.cube.config.NotationType;
import se.slackers.cube.model.algorithm.Instruction;
import se.slackers.cube.model.algorithm.Move;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class InputActivity extends Activity implements OnClickListener {
	public static final String ALGORITHM = "algorithm";
	public static final String ALGORITHM_ID = "algorithm.id";
	public static final int REQUEST_CODE_NEW = 2500;
	public static final int REQUEST_CODE_EDIT = 2501;

	private static final char DOUBLE = '2';
	private static final char REVERSE = '\'';

	private TextView algorithmView;
	private final List<Character> list = new LinkedList<Character>();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_input);

		configure(getIntent());

		algorithmView = (TextView) findViewById(R.id.algorithm);

		final Button save = (Button) findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				saveAlgorithm();
			}
		});

		final List<Button> buttons = new LinkedList<Button>();
		buttons.add((Button) findViewById(R.id.turn_b));
		buttons.add((Button) findViewById(R.id.turn_big_b));
		buttons.add((Button) findViewById(R.id.turn_big_d));
		buttons.add((Button) findViewById(R.id.turn_big_f));
		buttons.add((Button) findViewById(R.id.turn_big_l));
		buttons.add((Button) findViewById(R.id.turn_big_r));
		buttons.add((Button) findViewById(R.id.turn_big_u));
		buttons.add((Button) findViewById(R.id.turn_ccw));
		buttons.add((Button) findViewById(R.id.turn_d));
		buttons.add((Button) findViewById(R.id.turn_double));
		buttons.add((Button) findViewById(R.id.turn_e));
		buttons.add((Button) findViewById(R.id.turn_f));
		buttons.add((Button) findViewById(R.id.turn_l));
		buttons.add((Button) findViewById(R.id.turn_m));
		buttons.add((Button) findViewById(R.id.turn_r));
		buttons.add((Button) findViewById(R.id.turn_s));
		buttons.add((Button) findViewById(R.id.turn_u));
		buttons.add((Button) findViewById(R.id.turn_x));
		buttons.add((Button) findViewById(R.id.turn_y));
		buttons.add((Button) findViewById(R.id.turn_z));

		for (final Button button : buttons) {
			button.setOnClickListener(this);
		}

		final ImageButton undo = (ImageButton) findViewById(R.id.turn_backspace);
		undo.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				backspace();
			}
		});
	}

	/**
	 * @param intent
	 */
	private void configure(final Intent intent) {
		final String incomming = intent.getStringExtra(ALGORITHM);
		if (incomming == null) {
			return;
		}
		final NotationType notation = NotationType.Singmaster;
		final Instruction instruction = new Instruction(incomming);
		for (final Move move : instruction.moves()) {
			final char ch = move.side().symbol(notation).charAt(0);

			switch (move.direction()) {
			case CounterClockwise: // 3 adds
				list.add(ch);
			case Double: // 2 adds
				list.add(ch);
			case Clockwise: // 1 add
				list.add(ch);
			}
		}
		render();
	}

	protected void saveAlgorithm() {
		final Intent intent = new Intent();
		intent.putExtra(ALGORITHM, algorithmView.getText());
		intent.putExtra(ALGORITHM_ID, getIntent().getLongExtra(ALGORITHM_ID, 0));
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public void backspace() {
		if (list.isEmpty()) {
			return;
		}
		list.remove(list.size() - 1);
		render();
	}

	public void onClick(final View v) {
		final Button button = (Button) v;
		list.add(button.getText().charAt(0));
		render();
	}

	private void render() {
		final StringBuffer buffer = new StringBuffer();

		final char[] trail = new char[3];
		for (final char ch : list) {
			handle(buffer, trail, ch);
		}

		handle(buffer, trail, (char) 0);
		handle(buffer, trail, (char) 0);

		algorithmView.setText(buffer.toString());

	}

	private void handle(final StringBuffer buffer, final char[] trail, final char ch) {
		trail[2] = trail[1];
		trail[1] = trail[0];
		trail[0] = ch;

		if (trail[0] != 0 && (trail[0] == trail[1] && trail[1] == trail[2])) {
			buffer.append(trail[0]);
			buffer.append(REVERSE);
			trail[0] = trail[1] = trail[2] = 0;
			return;
		}

		if (trail[1] != 0 && (trail[1] == trail[2])) {
			buffer.append(trail[1]);
			buffer.append(DOUBLE);
			trail[1] = trail[2] = 0;
			return;
		}

		if (trail[2] != 0) {
			buffer.append(trail[2]);
		}
	}
}
