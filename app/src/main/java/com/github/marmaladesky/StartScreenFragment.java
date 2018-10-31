package com.github.marmaladesky;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import de.igloffstein.maik.arevelation.helpers.FabHelper;

public class StartScreenFragment extends Fragment {

    private static final String LOG_TAG = StartScreenFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FabHelper.hideFabIcon(getActivity());

        View v = inflater.inflate(R.layout.start_screen, container, false);

        Button btnNew = v.findViewById(R.id.btnNew);
        Button btnOpen = v.findViewById(R.id.btnOpen);
        Button btnAbout = v.findViewById(R.id.btnAbout);

        btnNew.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity instanceof ARevelation) {
                    ((ARevelation) activity).newFile();
                } else {
                    System.err.println("activity is instanceof: " + activity.toString() + " but should be instanceof: " + ARevelation.class.toString());
                }
            }
        });

        btnOpen.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity instanceof ARevelation) {
                    ((ARevelation) activity).optionItemSelectedOpen();
                } else {
                    System.err.println("activity is instanceof: " + activity.toString() + " but should be instanceof: " + ARevelation.class.toString());
                }
            }
        });

        btnAbout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity instanceof ARevelation) {
                    ((ARevelation) activity).optionItemSelectedAbout();
                } else {
                    System.err.println("activity is instanceof: " + activity.toString() + " but should be instanceof: " + ARevelation.class.toString());
                }
            }
        });

        if (getActivity() instanceof ARevelation) {
            ((ARevelation) getActivity()).checkButton();
        }

        return v;
    }
}
