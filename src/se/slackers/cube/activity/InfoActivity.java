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

import java.util.Random;

import se.slackers.cube.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InfoActivity extends Activity {

	private static String[] persons = { "girlfriend", "dog", "roommate", "boss", "I" };
	private static String[] verbs = { "ate", "smoked", "cursed", "stole" };
	private static String[] nouns = { "my credit card", "my soul", "my pay check", "my shoes", "all of my food" };

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_info);
		final TextView appName = (TextView) findViewById(R.id.infoAppName);
		final String version = appName.getText() + " v" + getCurrentVersion();
		appName.setText(version);

		final Button feedback = (Button) findViewById(R.id.feedback);
		feedback.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				final Intent i = new Intent(Intent.ACTION_SEND);
				// i.setType("text/plain"); // use this line for testing in the emulator
				final String email = "erik.bystrom@gmail.com";
				final String subject = "Feedback (" + version + ")";

				i.setType("message/rfc822"); // use from live device
				i.putExtra(Intent.EXTRA_EMAIL, new String[] { email, "" });
				i.putExtra(Intent.EXTRA_SUBJECT, subject);
				final String reason = generateReason();
				i.putExtra(Intent.EXTRA_TEXT, "Hey, your app is awesome!\nI was going to give you a donation but "
						+ reason + "\nAnyway, have a nice day!\n");

				startActivity(Intent.createChooser(i, getText(R.string.email_chooser)));
			}
		});
	}

	protected String generateReason() {
		final Random random = new Random();
		final String person = persons[random.nextInt(persons.length)];
		final String verb = verbs[random.nextInt(verbs.length)];
		final String noun = nouns[random.nextInt(nouns.length)];

		return String.format("my %s %s %s.", person, verb, noun);
	}

	private String getCurrentVersion() {
		final PackageManager manager = getPackageManager();
		try {
			final PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (final NameNotFoundException e) {
			return "";
		}
	}
}
