package de.igloffstein.maik.arevelation.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.provider.DocumentFile;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.marmaladesky.ARevelation;
import com.github.marmaladesky.R;
import com.github.marmaladesky.RevelationListViewFragment;

/**
 * Dialog to get a name for the file which stores the data
 *
 * Created by OldSparkyMI on 25.01.18.
 */

public class NewFilenameDialogBuilder extends AlertDialog.Builder {

    private static final String LOG_TAG = NewFilenameDialogBuilder.class.getSimpleName();
    private final ARevelation aRevelation;
    private final EditText filename;

    private LinearLayout getLayout(Context context) {

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        filename.setHint(R.string.new_filename);
        filename.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        layout.addView(filename);

        return layout;
    }

    public NewFilenameDialogBuilder(final Context context, final Intent data) {
        super(context);

        // get the application
        aRevelation = (ARevelation) context;

        // build up layout classes
        filename = new EditText(context);

        // init it
        LinearLayout layout = getLayout(context);

        this.setTitle(R.string.new_filename);
        this.setCancelable(false);
        this.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DocumentFile pickedDir = DocumentFile.fromTreeUri(aRevelation, data.getData());
                DocumentFile newFile = pickedDir.createFile("application/octet-stream", filename.getText().toString());

                try {
                    if (newFile.canWrite()) {
                        // set current file
                        aRevelation.setCurrentFile(newFile.getUri().toString());
                        aRevelation.saveChanges(aRevelation.getCurrentFocus());
                    } else {
                        Toast.makeText(context, R.string.cannot_save_file, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        this.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("D:", "test");
                RevelationListViewFragment.notifyDataSetChanged();
            }
        });
        this.setView(layout);
    }
}
