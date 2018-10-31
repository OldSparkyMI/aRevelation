package de.igloffstein.maik.arevelation.helpers;

import android.app.Activity;
import android.view.View;

import com.github.marmaladesky.R;

public class FabHelper {

    public static void showFabIcon(Activity activity) {
        if (activity != null) {
            activity.findViewById(R.id.fab).setVisibility(View.VISIBLE);
        }
    }

    public static void hideFabIcon(Activity activity) {
        if (activity != null) {
            activity.findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        }
    }



//    public static DialogInterface.OnClickListener getFabOnClickListener(Activity activity) {
//        if (onClickListener == null) {
//            if (activity instanceof ARevelation) {
//                final ARevelation aRevelation = (ARevelation) activity;
//
//                onClickListener = new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Entry entry = EntryHelper.newEntry(Objects.requireNonNull(EntryType.getFromPosition(which)));
//                        // get id from language values
//                        int id = aRevelation.getResources()
//                                .getIdentifier(EntryType.getFromPosition(which).toString().toLowerCase(), "string", aRevelation.getPackageName());
//                        entry.setName(aRevelation.getString(id));
//
//                        // add new element
//                        if (FabHelper.currentEntryState.size() <= 0) {
//                            aRevelation.getRvlData().addEntry(entry);
//                        } else {
//                            FabHelper.currentEntryState.getLast().list.add(entry);
//                        }
//
//                        RevelationListViewFragment.notifyDataSetChanged();
//
//                        // go directly in detail view mode for editing
//                        aRevelation.getFragmentManager()
//                                .beginTransaction()
//                                .replace(R.id.mainContainer, RevelationEntryFragment.newInstance(entry.getUuid()), FRAGMENT_TAG)
//                                .addToBackStack(null).commit();
//                    }
//
//                };
//            }
//        }
//        return onClickListener;
//    }
}
