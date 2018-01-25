package de.igloffstein.maik.arevelation.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.marmaladesky.ARevelation;
import com.github.marmaladesky.R;

/**
 *
 * ToDo: 0. Locking der Datei verhindern, wenn ich neu mache und auf speichern klicke
 * ToDo: 1. Dateinameneingabe
 * ToDo: 2. Passworteingabe (inkl. Wiederholen, kannst da changePasswordDialog überarbeiten?
 * ToDo: 3. dismiss changes Implementieren
 * ToDo: 4. wenn ich die Datei gespeichert habe, muss ich PW eingeben, wenn Sie neu ist - vielleicht gar nicht verkehrt ;)
 * ToDo: 5. Neu im Startfragment sollte auch gehen!
 * ToDo: 6. Check for wirte permissions: https://stackoverflow.com/a/26765884 & https://stackoverflow.com/questions/26744842/how-to-use-the-new-sd-card-access-api-presented-for-android-5-0-lollipop
 * ToDo: 7. is MimeType correct?
 *
 * Created by OldSparkyMI on 25.01.18.
 */

public class NewFilenameDialogBuilder extends AlertDialog.Builder {

    final ARevelation aRevelation;
    final EditText filename;

    private LinearLayout getLayout(Context context){

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
                if (filename != null && filename.getText() != null && filename.getText().toString() != null) {

                    Uri data1 = data.getData();
                    String dataString = data.getDataString();
                    Bundle extras = data.getExtras();

                    DocumentFile pickedDir = DocumentFile.fromTreeUri(aRevelation, data1);
                    DocumentFile newFile = pickedDir.createFile("text/application", filename.getText().toString());
                    aRevelation.setCurrentFile(newFile.getUri().toString());

                    new ChangePasswordDialogBuilder(context, null).create().show();
                }

            }
        });
        this.setNegativeButton(android.R.string.cancel, null);
        this.setView(layout);
    }
}
