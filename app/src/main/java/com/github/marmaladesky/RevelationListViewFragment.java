package com.github.marmaladesky;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import com.github.marmaladesky.data.Entry;
import com.github.marmaladesky.data.RevelationData;

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

import de.igloffstein.maik.arevelation.adapters.RevelationStructureBrowserAdapter;

public class RevelationListViewFragment extends Fragment {
    private static final String LOG_TAG = RevelationListViewFragment.class.getSimpleName();
    private static final String ARGUMENT_UUID_LIST = "uuidList";
    private static final int oneMinute = 60000;
    private static int minuteCounter;
    private static boolean timerInitialized = false;

    protected static Timer timer;
    private String groupUuid;

    public static void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timerInitialized = false;
            Log.d(LOG_TAG, "Timer canceled");
        }
    }

    protected void newTimer() {
        if (!timerInitialized) {
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
                                int minutesLeft = preferenceAutoLock - ++minuteCounter;
                                Log.d(LOG_TAG, "lock in: " + minutesLeft);
                                if (minutesLeft > 0) {
                                    Toast.makeText(getActivity(), getResources().getQuantityString(R.plurals.auto_lock_time_left, minutesLeft, minutesLeft), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.auto_lock_file_locked), Toast.LENGTH_LONG).show();
                                    try {
                                        ((ARevelation) getActivity()).clearUI();
                                        ((ARevelation) getActivity()).openAskPasswordDialog();
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
            RevelationStructureBrowserAdapter revelationStructureBrowserAdapter
                    = new RevelationStructureBrowserAdapter(this.getActivity(), group);

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

        return v;
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
        try {
            ((ARevelation) getActivity()).getCurrentEntryState().removeLast();
        } catch (NoSuchElementException e) {
            // ignore
        }
        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();

        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);

        RevelationData rvlData = ((ARevelation) getActivity()).rvlData;
        if (rvlData != null) {
            // add the current folder element - if there to the entry state
            Entry entry = rvlData.getEntryById(groupUuid);
            if (entry != null) {
                ((ARevelation) getActivity()).getCurrentEntryState().add(entry);
            }
        }

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
                if (!n.type.equals(Entry.TYPE_FOLDER)) {
                    RevelationEntryFragment nextFrag = RevelationEntryFragment.newInstance(n.getUuid());
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainContainer, nextFrag)
                            .addToBackStack(null).commit();
                } else {
                    RevelationListViewFragment nextFrag = RevelationListViewFragment.newInstance(n.getUuid());
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainContainer, nextFrag)
                            .addToBackStack(null).commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

    }

}
