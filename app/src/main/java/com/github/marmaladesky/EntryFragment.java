package com.github.marmaladesky;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TwoLineListItem;
import com.github.marmaladesky.data.Entry;
import com.github.marmaladesky.data.Field;
import com.github.marmaladesky.data.FieldWrapper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EntryFragment extends Fragment {

    private static final String ROW_HEADER_IDENTIFIER = "First Line";
    private static final String ROW_DATA_IDENTIFIER = "Second Line";

    private static final String ARGUMENT_ENTRY_ID = "entryId";


    private Entry entry;

    private ListView simple;

    public static EntryFragment newInstance(String entryId) {
        EntryFragment f = new EntryFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_ENTRY_ID, entryId);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState == null || savedInstanceState.getString(ARGUMENT_ENTRY_ID) == null)
            entry = ((ARevelation) getActivity()).rvlData.getEntryById(getArguments().getString(ARGUMENT_ENTRY_ID));
        else
            entry = ((ARevelation) getActivity()).rvlData.getEntryById(savedInstanceState.getString(ARGUMENT_ENTRY_ID));


        View v = inflater.inflate(R.layout.revelation_entry_browser, container, false);

        simple = (ListView) v.findViewById(R.id.entryList);

        ArrayList<Map<String, Object>> data = new ArrayList<>();

        HashMap<String, Object> values = new HashMap<>();

        values.put(ROW_HEADER_IDENTIFIER, getString(R.string.name));
        values.put(ROW_DATA_IDENTIFIER, entry.getName() == null ? "" : entry.getName());
        data.add(values);
        values = new HashMap<String, Object>();
        values.put(ROW_HEADER_IDENTIFIER, getString(R.string.description));
        values.put(ROW_DATA_IDENTIFIER, entry.getDescription() == null ? "" : entry.getDescription());
        data.add(values);

        for(Field f : entry.fields) {
            values = new HashMap<>();
            values.put(ROW_HEADER_IDENTIFIER, getGenericHeader(f.id));
            values.put(ROW_DATA_IDENTIFIER, f);
            data.add(values);
        }

        values = new HashMap<String, Object>();
        values.put(ROW_HEADER_IDENTIFIER, getString(R.string.notes));
        values.put(ROW_DATA_IDENTIFIER, entry.getNotes() == null ? "" : entry.getNotes());
        data.add(values);

        DateFormat dateFormatter = ((ARevelation)getActivity()).dateFormatter;
        values = new HashMap<String, Object>();
        values.put(ROW_HEADER_IDENTIFIER, getString(R.string.updated));
        values.put(ROW_DATA_IDENTIFIER, dateFormatter.format(new Date(entry.updated*1000L))); // In python world it is seconds
        data.add(values);

        SimpleAdapter itemsAdapter = new SimpleAdapter(
                this.getActivity(), data,
                android.R.layout.simple_list_item_2,
                new String[] {ROW_HEADER_IDENTIFIER, ROW_DATA_IDENTIFIER },
                new int[] {android.R.id.text1, android.R.id.text2 }) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                try {
                    HashMap<String, Object> o = (HashMap<String, Object>) simple.getItemAtPosition(position);
                    if(getString(R.string.generic_password).equals(o.get(ROW_HEADER_IDENTIFIER))) {
                        TwoLineListItem v = (TwoLineListItem)super.getView(position, convertView, parent);
                        v.getText2().setTransformationMethod(PasswordTransformationMethod.getInstance());
                        return v;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return super.getView(position, convertView, parent);
            }
        };

        simple.setAdapter(itemsAdapter);
        simple.setOnItemClickListener(new PasswordOnClickListener());
        ((ARevelation) getActivity()).checkButton();
        return v;
    }

    private String getGenericHeader(String name) {
        String packageName = getActivity().getPackageName();
        int resId = getResources().getIdentifier(name.replace('-', '_'), "string", packageName);
        if(resId == 0) return name;
        else return getString(resId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(entry != null) outState.putString("entryId", entry.getUuid());
    }

    private class PasswordOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                TwoLineListItem item = (TwoLineListItem) view;

                String header = item.getText1().getText().toString();

                FieldWrapper fw;
                Object v = ((HashMap<String, Object>) parent.getAdapter().getItem(position)).get(ROW_DATA_IDENTIFIER);
                String h = (String) ((HashMap<String, Object>) parent.getAdapter().getItem(position)).get(ROW_HEADER_IDENTIFIER);
                if (v instanceof String) {
                    String prop = null;
                    if (h.equals(getString(R.string.name))) {
                        prop = Entry.PROPERTY_NAME;
                    } else if (h.equals(getString(R.string.description))) {
                        prop = Entry.PROPERTY_DESCRIPTION;
                    } else if (h.equals(getString(R.string.notes))) {
                        prop = Entry.PROPERTY_NOTES;
                    }
                    fw = new FieldWrapper(prop, EntryFragment.this.entry);
                } else if (v instanceof Field)
                    fw = new FieldWrapper((Field) v);
                else {
                    throw new Exception("Unknown data in list");
                }
                DialogFragment dial = ItemDialogFragment.newInstance(header, fw.getUuid());
                dial.setTargetFragment(EntryFragment.this, 0); // Amazing piece of shit, but I don't know how to do it in another way
                dial.show(getFragmentManager(), "Tag");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
