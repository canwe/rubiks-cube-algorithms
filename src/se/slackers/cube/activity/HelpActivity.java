/*******************************************************************************
 * Copyright (c) 2010 Erik Byström.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package se.slackers.cube.activity;

import java.util.LinkedList;
import java.util.List;

import se.slackers.cube.Config;
import se.slackers.cube.R;
import se.slackers.cube.config.AlgorithmTransform;
import se.slackers.cube.widget.NotationView;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;

public class HelpActivity extends Activity {
	protected Config config;
	protected int id = 20000;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		config = new Config(this);
		setContentView(R.layout.help);

		final AlgorithmTransform transformer = new AlgorithmTransform(config);
		final List<NotationView> views = new LinkedList<NotationView>();
		views.add(new NotationView(this, "U (Up)", R.drawable.u));
		views.add(new NotationView(this, "D (Down)", R.drawable.d));
		views.add(new NotationView(this, "L (Left)", R.drawable.l));
		views.add(new NotationView(this, "R (Right)", R.drawable.r));
		views.add(new NotationView(this, "F (Front)", R.drawable.f));
		views.add(new NotationView(this, "B (Back)", R.drawable.b));
		views.add(new NotationView(this, "M (Middle)", R.drawable.m));
		views.add(new NotationView(this, "E (Equator)", R.drawable.e));
		views.add(new NotationView(this, "S (Side)", R.drawable.s));

		views.add(new NotationView(this, transformer.transform("u") + " (Up)", R.drawable.uw));
		views.add(new NotationView(this, transformer.transform("d") + " (Down)", R.drawable.dw));
		views.add(new NotationView(this, transformer.transform("l") + " (Left)", R.drawable.lw));
		views.add(new NotationView(this, transformer.transform("r") + " (Right)", R.drawable.rw));
		views.add(new NotationView(this, transformer.transform("f") + " (Front)", R.drawable.fw));
		views.add(new NotationView(this, transformer.transform("b") + " (Back)", R.drawable.bw));

		views.add(new NotationView(this, "x", R.drawable.x));
		views.add(new NotationView(this, "y", R.drawable.y));
		views.add(new NotationView(this, "z", R.drawable.z));

		final int imageWidth = (int) (75 * config.getScale());
		final int padding = 5;
		final int margin = padding * 2;
		final int columns = Math.max((config.getDisplayWidth() - 2 * padding) / (imageWidth + 2 * padding), 1);

		final TableLayout helpTable = (TableLayout) findViewById(R.id.helpTable);
		TableRow row = null;
		int nextRow = 0;

		for (NotationView view : views) {
			if (nextRow == 0) {
				row = new TableRow(this);
				row.setId(id++);
				row.setPadding(padding, margin, padding, margin);
				row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				helpTable.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				nextRow = columns;
			}
			view.setPadding(padding, padding, padding, padding);
			row.addView(view);
			nextRow--;
		}
	}
}
