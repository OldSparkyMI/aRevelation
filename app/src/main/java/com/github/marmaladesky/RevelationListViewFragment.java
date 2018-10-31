package com.github.marmaladesky;

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

import com.github.marmaladesky.data.Entry;
import com.github.marmaladesky.data.RevelationData;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import de.igloffstein.maik.arevelation.adapters.RevelationStructureBrowserAdapter;
import de.igloffstein.maik.arevelation.helpers.EntryStateHelper;
import de.igloffstein.maik.arevelation.helpers.FabHelper;

public class RevelationListViewFragment extends Fragment {
    private static final String LOG_TAG = RevelationListViewFragment.class.getSimpleName();
    private static final String ARGUMENT_UUID_LIST = "uuidList";
    private static final int oneMinute = 60000;
    private static RevelationStructureBrowserAdapter revelationStructureBrowserAdapter = null;
    private static int minuteCounter;
    private static boolean timerInitialized = false;
    public static final String FRAGMENT_TAG = RevelationEntryFragment.class.getSimpleName();

    private static Timer timer;
    private String groupUuid;

    public static void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timerInitialized = false;
            Log.d(LOG_TAG, "Timer canceled");
        }
    }

    private void newTimer() {
        final ARevelation aRevelation = (ARevelation) getActivity();
        if (!timerInitialized && aRevelation.isLockingSave()) {
            Log.d(LOG_TAG, "Creating new timer ...");
            final int preferenceAutoLock = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("preference_auto_lock", "-1"));
            minuteCounter = 0;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ARevelation aRevelation = (ARevelation) getActivity();
                                int minutesLeft = preferenceAutoLock - ++minuteCounter;
                                Log.d(LOG_TAG, "lock in: " + minutesLeft);
                                if (minutesLeft > 0) {
                                    if (ARevelation.isActivityVisible()) {
                                        Toast.makeText(aRevelation, getResources().getQuantityString(R.plurals.auto_lock_time_left, minutesLeft, minutesLeft), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(aRevelation, getResources().getString(R.string.auto_lock_file_locked), Toast.LENGTH_LONG).show();
                                    try {
                                        aRevelation.clearUI();
                                        aRevelation.openAskPasswordDialog();
                                    } catch (IllegalStateException e) {
                                        Log.e(LOG_TAG, e.getMessage());
                                        Log.getStackTraceString(e);
                                    } finally {
                                        timerInitialized = false;
                                        cancel();
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.v(LOG_TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
            }, oneMinute, oneMinute);
            timerInitialized = true;
        }else{
            Log.d(LOG_TAG, "Won't start automatically file locking.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        cancelTimer();
    }

    public static RevelationListViewFragment newInstance(String uuidList) {
        RevelationListViewFragment f = new RevelationListViewFragment();

        Log.d(LOG_TAG, "uuidList: " + uuidList);

        Bundle args = new Bundle();
        args.putString(ARGUMENT_UUID_LIST, uuidList);
        f.setArguments(args);

        timerInitialized = false;
        return f;
    }

    public static void notifyDataSetChanged(){
        if (revelationStructureBrowserAdapter != null) {
            revelationStructureBrowserAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.revelation_structure_browser, container, false);
        try {
            if (savedInstanceState == null || savedInstanceState.getString(ARGUMENT_UUID_LIST) == null) {
                groupUuid = getArguments().getString(ARGUMENT_UUID_LIST);
            } else {
                groupUuid = savedInstanceState.getString(ARGUMENT_UUID_LIST);
            }
            List<Entry> group = ((ARevelation) getActivity()).rvlData.getEntryGroupById(groupUuid);
            revelationStructureBrowserAdapter = new RevelationStructureBrowserAdapter(this.getActivity(), group);

            ListView simple = v.findViewById(R.id.rootList);
            simple.setOnItemClickListener(new ListListener());
            simple.setAdapter(revelationStructureBrowserAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
        ((ARevelation) getActivity()).checkButton();

        if (Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("preference_auto_lock", "-1")) > 0) {
            newTimer();
        }

        FabHelper.showFabIcon(getActivity());

        // We entered an entry, add to state!
        RevelationData rvlData = ((ARevelation) getActivity()).rvlData;
        if (rvlData != null) {
            // add the current folder element - if there to the entry state
            Entry entry = rvlData.getEntryById(groupUuid);
            if (entry != null) {
                EntryStateHelper.add(entry);
            }
        }

        return v;
    }

    /**
     * Note: Don't disable the fab icon here
     * If the user enters a directory, the fab icon will hide, because we create a new
     * RevelationListViewFragment for every view.
     */
    @Override
    public void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();

    }

    @Override
    public void onDestroy(){
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
        try {
            EntryStateHelper.remove();
        } catch (NoSuchElementException e) {
            // ignore
        }
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (groupUuid != null) outState.putString(ARGUMENT_UUID_LIST, groupUuid);
    }

    private class ListListener implements OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                Entry n = (Entry) parent.getItemAtPosition(position);
                Fragment nextFrag = !n.type.equals(Entry.TYPE_FOLDER) ? RevelationEntryFragment.newInstance(n.getUuid()) : RevelationListViewFragment.newInstance(n.getUuid());
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, nextFrag)
                        .addToBackStack(null).commit();

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
}
