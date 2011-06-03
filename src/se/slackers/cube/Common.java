/*******************************************************************************
 * Copyright (c) 2011 Erik Byström.
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
package se.slackers.cube;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author Erik Byström
 * 
 */
public class Common {
	public static void showMessages(final Context context, final Config config) {
		// fetch version to see if the welcome/update dialog needs to be shown
		final PackageInfo info = getPackageInfo(context);
		final int version = info.versionCode;

		if (config.isFirstStart()) {
			final Dialog dialog = createFirstStartDialog(context);
			config.setMessageDialogVersion(version);
			dialog.show();
		} else if (config.getMessageDialogVersion() < version) {
			final Dialog dialog = createMessageDialog(context);
			config.setMessageDialogVersion(version);
			dialog.show();
		}
	}

	public static PackageInfo getPackageInfo(final Context context) {
		final PackageManager manager = context.getPackageManager();
		try {
			return manager.getPackageInfo(context.getPackageName(), 0);
		} catch (final NameNotFoundException e) {
			return null;
		}
	}

	private static Dialog createFirstStartDialog(final Context context) {
		final Dialog dialog = new Dialog(context);
		final PackageInfo info = getPackageInfo(context);

		final OnClickListener firstDismissListener = new OnClickListener() {
			public void onClick(final View v) {
				dialog.dismiss();
			}
		};

		dialog.setContentView(R.layout.dialog_firststart);
		dialog.setTitle(context.getString(R.string.firststart_title) + String.format(" v%s", info.versionName));
		dialog.findViewById(R.id.text).setOnClickListener(firstDismissListener);
		dialog.findViewById(R.id.dialogRoot).setOnClickListener(firstDismissListener);

		return dialog;
	}

	private static Dialog createMessageDialog(final Context context) {
		final Dialog dialog = new Dialog(context);
		final PackageInfo info = getPackageInfo(context);

		final OnClickListener dismissListener = new OnClickListener() {
			public void onClick(final View v) {
				dialog.dismiss();
			}
		};

		dialog.setContentView(R.layout.dialog_message);
		dialog.setTitle(context.getString(R.string.messageTitle) + String.format(" v%s", info.versionName));
		dialog.findViewById(R.id.text).setOnClickListener(dismissListener);
		dialog.findViewById(R.id.dialogRoot).setOnClickListener(dismissListener);
		return dialog;
	}
}
