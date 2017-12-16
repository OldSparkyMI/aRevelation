package com.github.marmaladesky;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.github.marmaladesky.data.Entry;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import de.igloffstein.maik.aRevelation.Adapter.RevelationStructureBrowserAdapter;

public class RevelationBrowserFragment extends Fragment {
	private static final String LOG_TAG = RevelationBrowserFragment.class.getSimpleName();
	private static final String ARGUMENT_UUID_LIST = "uuidList";
	private static final int oneMinute = 60000;
	private static int minuteCounter;
	private static boolean timerInitialized = false;

	protected static Timer timer;
	private String groupUuid;

	protected static void cancelTimer(){
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timerInitialized=false;
			Log.d(LOG_TAG, "Timer canceled");
		}
	}

	protected void newTimer(){
		if (!timerInitialized) {
			Log.d(LOG_TAG, "Creating new timer ...");
			final int preferenceAutoLock = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("preference_auto_lock", "-1"));
			minuteCounter = 0;
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							int minutesLeft = preferenceAutoLock - ++minuteCounter;
							Log.d(LOG_TAG, "lock in: " + minutesLeft);
							if (minutesLeft > 0) {
								Toast.makeText(getActivity(), getResources().getQuantityString(R.plurals.auto_lock_time_left, minutesLeft, minutesLeft), Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(getActivity(), getResources().getString(R.string.auto_lock_file_locked), Toast.LENGTH_LONG).show();
								try {
									((ARevelation) getActivity()).clearStateResetUi(false);
								} catch (IllegalStateException e) {
									Log.e(LOG_TAG, e.getMessage());
									Log.getStackTraceString(e);
								} finally {
									timerInitialized=false;
									cancel();
								}
							}
						}
					});
				}
			}, oneMinute, oneMinute);
			timerInitialized = true;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		cancelTimer();
	}

	public static RevelationBrowserFragment newInstance(String uuidList) {
		RevelationBrowserFragment f = new RevelationBrowserFragment();

		Bundle args = new Bundle();
		args.putString(ARGUMENT_UUID_LIST, uuidList);
		f.setArguments(args);

		cancelTimer();
		timerInitialized = false;
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		List<Entry> group;
		View v = inflater.inflate(R.layout.revelation_structure_browser, container, false);
		try {
			if (savedInstanceState == null || savedInstanceState.getString(ARGUMENT_UUID_LIST) == null) {
				groupUuid = getArguments().getString(ARGUMENT_UUID_LIST);
			} else {
				groupUuid = savedInstanceState.getString(ARGUMENT_UUID_LIST);
			}
			group = ((ARevelation) getActivity()).rvlData.getEntryGroupById(groupUuid);

			ListView simple = (ListView) v.findViewById(R.id.rootList);
			//NodeArrayAdapter itemsAdapter = new NodeArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, group);
			RevelationStructureBrowserAdapter revelationStructureBrowserAdapter
					= new RevelationStructureBrowserAdapter(this.getActivity(), group);
			simple.setOnItemClickListener(new ListListener());
			simple.setAdapter(revelationStructureBrowserAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		((ARevelation) getActivity()).checkButton();

		if (Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("preference_auto_lock", "-1")) > 0) {
			newTimer();
		}

        return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (((ARevelation) getActivity()).rvlData == null){
			// if no data, just go back to the start fragment
			((ARevelation) getActivity()).resetUI();
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(groupUuid != null) outState.putString(ARGUMENT_UUID_LIST, groupUuid);
	}

	private class ListListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                Entry n = (Entry) parent.getItemAtPosition(position);
                if (!n.type.equals(Entry.TYPE_FOLDER)) {
                    EntryFragment nextFrag = EntryFragment.newInstance(n.getUuid());
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainContainer, nextFrag)
                            .addToBackStack(null).commit();
                } else {
					RevelationBrowserFragment nextFrag = RevelationBrowserFragment.newInstance(n.getUuid());
					getFragmentManager()
							.beginTransaction()
							.replace(R.id.mainContainer, nextFrag)
							.addToBackStack(null).commit();
				}
            } catch(Exception e) {
                e.printStackTrace();
                throw e;
            }
		}

	}

}
