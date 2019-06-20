package de.igloffstein.maik.arevelation.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.marmaladesky.ARevelation;
import com.github.marmaladesky.R;
import com.github.marmaladesky.data.Entry;

import java.util.List;

import de.igloffstein.maik.arevelation.enums.EntryType;

/**
 * Class for an advance list view of the password list
 *
 * Created by OldSparkyMI on 24.11.17.
 */

public class RevelationStructureBrowserAdapter extends ArrayAdapter<String> {

    private static final String LOG_TAG = RevelationStructureBrowserAdapter.class.getSimpleName();
    private final ARevelation aRevelation;
    private final List<Entry> data;

    public RevelationStructureBrowserAdapter(Activity activity, List<Entry> data) {
        //noinspection unchecked
        super(activity, R.layout.revelation_structure_entry, (List) data);

        this.aRevelation = (ARevelation) activity;
        this.data = data;
    }

    /**
     * ToDo: optimize via https://developer.android.com/training/improving-layouts/smooth-scrolling.html
     * @param position
     * @param view
     * @param parent
     * @return
     */
    @Override
    @NonNull
    public View getView(final int position, View view, @NonNull ViewGroup parent) {

        LayoutInflater inflater = aRevelation.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.revelation_structure_entry, parent, false);

        ImageView entryTypeIcon = rowView.findViewById(R.id.imageIdentifier);
        TextView entryName = rowView.findViewById(R.id.headerIdentifier);
        ImageView entryDeleteIcon = rowView.findViewById(R.id.deleteImageView);

        if (data != null) {
            final Entry entry = data.get(position);
            if (entry != null) {

                //[TypeIcon] [EntryName]       [EntryDeleteIcon]

                // set the icon to display
                entryTypeIcon.setImageResource(getIconForView(EntryType.valueOf(entry.type.toUpperCase())));
               
                // read name of item and display it (text)
                entryName.setText(entry.getName());

                // set the delete function
                entryDeleteIcon.setOnClickListener(new View.OnClickListener() {

                    private  void delete() {
                        Log.d(LOG_TAG, "Deleting: " + entry.getUuid());
                        // remove from the main data storage
                        aRevelation.getRvlData().removeEntryById(entry.getUuid());
                        // remove from the adapter view
                        data.remove(entry);
                        // notify data changes
                        notifyDataSetChanged();
                        // display save button
                        aRevelation.getSaveButton().setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onClick(View v) {

                        if (PreferenceManager.getDefaultSharedPreferences(rowView.getContext()).getBoolean("preference_enable_entry_deletion_confirmation_dialog", true)) {
                            AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
                            adb.setTitle(R.string.confirm_entry_deletion);
                            adb.setIcon(android.R.drawable.ic_dialog_alert);
                            adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    delete();
                                } });
                            adb.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                } });
                            adb.show();
                        } else {
                            delete();
                        }
                    }
                });
            }
        }
        return rowView;
    }
    
    private Integer getIconForView(EntryType entryType){
        // witch icon shall we display
        switch (entryType) {
            case CREDITCARD:
                return R.drawable.ic_cc_938;
            case CRYPTOKEY:
                return R.drawable.ic_key_678;
            case DATABASE:
                return R.drawable.ic_database_system_1800;
            case DOOR:
                return R.drawable.ic_door_44;
            case EMAIL:
                return R.drawable.ic_email_1573;
            case FTP:
                return R.drawable.ic_object_connection_round_1096;
            case GENERIC:
                return R.drawable.ic_attachment_1574;
            case PHONE:
                return R.drawable.ic_phone_225;
            case SHELL:
                return R.drawable.ic_window_equal_1465;
            case REMOTEDESKTOP:
                return R.drawable.ic_screen_1111;
            case VNC:
                return R.drawable.ic_showcase_round_726;
            case WEBSITE:
                return R.drawable.ic_file_url_1759;
            case FOLDER:
                return R.drawable.ic_folder_1791;
            case UNKNOWN:
                return R.drawable.ic_question_1445;
            default:
                return null;
        }
    }
}
