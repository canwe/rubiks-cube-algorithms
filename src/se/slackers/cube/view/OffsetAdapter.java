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