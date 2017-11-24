package de.igloffstein.maik.aRevelation.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.marmaladesky.R;
import com.github.marmaladesky.data.Entry;

import java.util.List;

import de.igloffstein.maik.aRevelation.EntryType;

/**
 * Created by OldSparkyMI on 24.11.17.
 */

public class RevelationStructureBrowserAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<Entry> data;

    public RevelationStructureBrowserAdapter(Activity context, List<Entry> data) {
        super(context, R.layout.revelation_structure_entry, (List) data);

        this.context = context;
        this.data = data;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.revelation_structure_entry, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageIdentifier);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.headerIdentifier);

        if (data != null && data.get(position) != null) {
            txtTitle.setText((String) data.get(position).getName());
            switch (EntryType.valueOf(data.get(position).type.toUpperCase())) {
                case CREDITCARD:
                    imageView.setImageResource(R.drawable.ic_cc_938);
                    break;
                case CRYPTOKEY:
                    imageView.setImageResource(R.drawable.ic_key_678);
                    break;
                case DATABASE:
                    imageView.setImageResource(R.drawable.ic_database_system_1800);
                    break;
                case DOOR:
                    imageView.setImageResource(R.drawable.ic_door_44);
                    break;
                case EMAIL:
                    imageView.setImageResource(R.drawable.ic_email_1573);
                    break;
                case FTP:
                    imageView.setImageResource(R.drawable.ic_object_connection_round_1096);
                    break;
                case GENERIC:
                    imageView.setImageResource(R.drawable.ic_attachment_1574);
                    break;
                case PHONE:
                    imageView.setImageResource(R.drawable.ic_phone_225);
                    break;
                case SHELL:
                    imageView.setImageResource(R.drawable.ic_window_equal_1465);
                    break;
                case REMOTEDESKTOP:
                    imageView.setImageResource(R.drawable.ic_screen_1111);
                    break;
                case VNC:
                    imageView.setImageResource(R.drawable.ic_showcase_round_726);
                    break;
                case WEBSITE:
                    imageView.setImageResource(R.drawable.ic_file_url_1759);
                    break;
                case FOLDER:
                    imageView.setImageResource(R.drawable.ic_folder_1791);
                    break;
                default:
            }
        }
        return rowView;
    }
}
