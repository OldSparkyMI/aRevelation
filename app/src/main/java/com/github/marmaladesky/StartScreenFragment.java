package com.github.marmaladesky;

import android.annotation.TargetApi;
import android.app.*;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.Button;

public class StartScreenFragment extends Fragment {

    @TargetApi(19)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.start_screen, container, false);

        Button btnOpen = v.findViewById(R.id.btnOpen);
        Button btnAbout = v.findViewById(R.id.btnAbout);

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
