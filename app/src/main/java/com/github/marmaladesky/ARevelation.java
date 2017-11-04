package com.github.marmaladesky;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.github.marmaladesky.data.RevelationData;
import org.custommonkey.xmlunit.*;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.*;
import java.text.DateFormat;
import java.util.List;

public class ARevelation extends Activity {

    private static final String ARGUMENT_RVLDATA = "rvlData";
    private static final String ARGUMENT_PASSWORD = "password";
    private static final String ARGUMENT_FILE = "file";

    DateFormat dateFormatter;

    RevelationData rvlData;
    String password;
    String currentFile;

    private Button saveButton;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        if(savedInstanceState != null) {
            rvlData = (RevelationData) savedInstanceState.getSerializable(ARGUMENT_RVLDATA);
            password = savedInstanceState.getString(ARGUMENT_PASSWORD);
            currentFile = savedInstanceState.getString(ARGUMENT_FILE);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Only at the startup
        if(getFragmentManager().getBackStackEntryCount() < 1) {
            StartScreenFragment newFragment = new StartScreenFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.mainContainer, newFragment)
                    .commit();
        }

        dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.MEDIUM, getResources().getConfiguration().locale);
        saveButton = (Button)this.findViewById(R.id.saveButton);
    }

    public void saveChanges(View view) throws Exception {
        rvlData.save(currentFile, password, getContentResolver());
        checkButton();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARGUMENT_RVLDATA, rvlData);
        outState.putString(ARGUMENT_PASSWORD, password);
        outState.putString(ARGUMENT_FILE, currentFile);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getFragmentManager().getBackStackEntryCount() < 1) clearState();
        checkButton();
    }

    private void clearState() {
        rvlData = null;
        password = null;
        currentFile = null;
    }

    public void checkButton() {
        if (rvlData != null && rvlData.isEdited())
            saveButton.setVisibility(View.VISIBLE);
        else
            saveButton.setVisibility(View.GONE);
    }

    public static SelfTestingResult testData(String xmlData) throws Exception {
        Serializer serializer1 = new Persister();
        RevelationData data = serializer1.read(RevelationData.class, xmlData, false);

        Serializer serializer2 = new Persister();
        Writer writer = new StringWriter();
        serializer2.write(data, writer);

        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(xmlData, writer.toString());

        if (BuildConfig.DEBUG) {
            System.out.println("Similar? " + diff.similar());
            System.out.println("Identical? " + diff.identical());
            System.out.println(diff);

            DetailedDiff detDiff = new DetailedDiff(diff);
            List differences = detDiff.getAllDifferences();
            System.out.println(differences.size() + " diffs founded");

            int counter = 0;
            for (Object object : differences) {
                Difference difference = (Difference) object;
                System.out.println("***********************");
                System.out.println(difference);
                System.out.println("***********************");
                if (++counter > 100) break;
            }
            System.out.println(differences.size() + " diffs founded");
        }
        if(diff.identical())
            return SelfTestingResult.Identical;
        else if (diff.similar())
            return SelfTestingResult.Similar;
        else
            return SelfTestingResult.Different;
    }
}
