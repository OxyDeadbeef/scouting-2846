/*
 * Copyright  2017  Douglas P Lau
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.firebears.scouting2846;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * A fragment representing a single Team detail screen.
 */
public class TeamDetailFragment extends Fragment {

	/** Argument for team id */
	static public final String ARG_TEAM_KEY = "team_key";

	/** Team detail loader ID */
	static private final int TEAM_DETAIL_LOADER_ID = 40;

	/** Columns to retrieve from the loader */
	static private final String[] COLS = {
		Team.COL_NAME,
		Team.COL_TEAM_NUMBER,
		Team.COL_NICKNAME,
		Team.COL_WEBSITE,
		Team.COL_ROOKIE_YEAR,
		Team.COL_MOTTO,
	};

	/** Required constructor */
	public TeamDetailFragment() { }

	/** Root view */
	private View root_view;

	/** Callbacks for loader */
	private final LoaderCallbacks<Cursor> cb =
		new LoaderCallbacks<Cursor>()
	{
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle b) {
			return (TEAM_DETAIL_LOADER_ID == id)
			      ? createLoader(b)
			      : null;
		}
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
			if (null == root_view || c.getCount() != 1)
				return;
			c.moveToFirst();
			Toolbar bar = (Toolbar) getActivity().findViewById(
				R.id.detail_toolbar);
			if (bar != null) {
				int num = c.getInt(c.getColumnIndex(
					Team.COL_TEAM_NUMBER));
				String nick = c.getString(c.getColumnIndex(
					Team.COL_NICKNAME));
				bar.setTitle("" + num + ' ' + nick);
			}
			TextView tv = setViewText(R.id.team_website, c,
				Team.COL_WEBSITE);
			Linkify.addLinks(tv, Linkify.WEB_URLS);
			tv = (TextView) root_view.findViewById(
				R.id.team_rookie_year);
			String t = c.getString(c.getColumnIndex(
				Team.COL_ROOKIE_YEAR));
			if (t != null) {
				tv.setText(getText(R.string.rookie_year) +
					" " + t);
			}
			initScoutingSpinner();
			setViewText(R.id.team_motto, c, Team.COL_MOTTO);
			setViewText(R.id.team_name, c, Team.COL_NAME);
		}
		@Override
		public void onLoaderReset(Loader<Cursor> loader) { }
	};

	private void initScoutingSpinner() {
		ArrayList<String> items = new ArrayList<String>();
		items.add(getActivity().getString(R.string.pit));
		for (int i = 1; i <= 100; i++)
			items.add("Qm" + i);
		Spinner sp = (Spinner) root_view.findViewById(R.id.scout_spn);
		ArrayAdapter<String> spad = new ArrayAdapter<String>(
			getActivity(), android.R.layout.simple_spinner_item,
			items);
		spad.setDropDownViewResource(
			android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(spad);
	}

	public String getMatch() {
		Spinner sp = (Spinner) root_view.findViewById(R.id.scout_spn);
		return sp.getSelectedItem().toString();
	}

	/** Set text for a TextView from a Cursor column */
	private TextView setViewText(int id, Cursor c, String col) {
		TextView tv = (TextView) root_view.findViewById(id);
		String t = c.getString(c.getColumnIndex(col));
		if (t != null && !t.equals("null"))
			tv.setText(t);
		return tv;
	}

	/** Create a loader for Team details */
	private Loader<Cursor> createLoader(Bundle b) {
		if (b.containsKey(ARG_TEAM_KEY)) {
			String key = b.getString(ARG_TEAM_KEY);
			return new CursorLoader(getContext(),
				Team.CONTENT_URI, COLS, Team.COL_KEY + "='" +
				key + "'", null, null);
		} else
			return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLoaderManager().initLoader(TEAM_DETAIL_LOADER_ID,
			getArguments(), cb);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg,
				 Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.team_detail, vg, false);
		root_view = v;
		return v;
	}
}
