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

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
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
				i.putExtra(Intent.EXTRA_TEXT, "Android version: " + android.os.Build.VERSION.RELEASE + "\n---\n");

				startActivity(Intent.createChooser(i, getText(R.string.email_chooser)));
			}
		});
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
