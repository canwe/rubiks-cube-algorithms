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

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

public class OffsetAdapter implements WrapperListAdapter {
	private final ListAdapter adapter;
	private final int offset;

	public OffsetAdapter(final ListAdapter adapter, final int offset) {
		this.adapter = adapter;
		this.offset = offset;
	}

	public ListAdapter getWrappedAdapter() {
		return adapter;
	}

	public boolean areAllItemsEnabled() {
		return adapter.areAllItemsEnabled();
	}

	public int getCount() {
		return clamp(adapter.getCount() - offset);
	}

	public Object getItem(final int position) {
		return adapter.getItem(position + offset);
	}

	public long getItemId(final int position) {
		return adapter.getItemId(position + offset);
	}

	public int getItemViewType(final int position) {
		return adapter.getItemViewType(position + offset);
	}

	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return adapter.getView(position + offset, convertView, parent);
	}

	public int getViewTypeCount() {
		return adapter.getViewTypeCount();
	}

	public boolean hasStableIds() {
		return adapter.hasStableIds();
	}

	public boolean isEmpty() {
		return adapter.isEmpty();
	}

	public boolean isEnabled(final int position) {
		return adapter.isEnabled(position + offset);
	}

	public void registerDataSetObserver(final DataSetObserver observer) {
		adapter.registerDataSetObserver(observer);
	}

	public void unregisterDataSetObserver(final DataSetObserver observer) {
		adapter.unregisterDataSetObserver(observer);
	}

	private int clamp(final int value) {
		return value < 0 ? 0 : value;
	}
}
