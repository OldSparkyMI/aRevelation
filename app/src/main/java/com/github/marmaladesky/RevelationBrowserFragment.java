package com.github.marmaladesky;

import java.util.List;

import com.github.marmaladesky.data.Entry;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RevelationBrowserFragment extends Fragment {

	private static final String ARGUMENT_UUID_LIST = "uuidList";

	private String groupUuid;

	public static RevelationBrowserFragment newInstance(String uuidList) {
		RevelationBrowserFragment f = new RevelationBrowserFragment();

		Bundle args = new Bundle();
		args.putString(ARGUMENT_UUID_LIST, uuidList);
		f.setArguments(args);

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
			NodeArrayAdapter itemsAdapter = new NodeArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, group);
			simple.setOnItemClickListener(new ListListener());
			simple.setAdapter(itemsAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		((ARevelation) getActivity()).checkButton();
		return v;

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
