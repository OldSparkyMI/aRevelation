package de.igloffstein.maik.arevelation.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.marmaladesky.ARevelation;
import com.github.marmaladesky.R;

/**
 * A aialog to change the password for:
 *  - saving a new file
 *  - change the password of an existing file
 *
 * Created by OldSparkyMI on 25.01.18.
 */

public class ChangePasswordDialogBuilder extends AlertDialog.Builder {

    private enum State{
        NEW_PASSWORD,CHANGE_PASSWORD
    }

    private final ARevelation aRevelation;
    private final EditText reenterCurrentPassword;
    private final EditText enterNewPassword;
    private final EditText confirmNewPassword;
    private final String oldPassword;
    private final State state;

    private LinearLayout getLayout(Context context) {
        // view of the alert dialog
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        if (state == State.CHANGE_PASSWORD) {
            reenterCurrentPassword.setHint(R.string.current_password);
            reenterCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            layout.addView(reenterCurrentPassword);
        }

        enterNewPassword.setHint(R.string.new_password);
        enterNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(enterNewPassword);

        confirmNewPassword.setHint(R.string.confirm_new_password);
        confirmNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(confirmNewPassword);

        return layout;
    }

    public ChangePasswordDialogBuilder(Context context, final String password) {
        super(context);

        // set the state
        state = (password != null && !"".equals(password)) ? State.CHANGE_PASSWORD : State.NEW_PASSWORD;

        // get the application
        aRevelation = (ARevelation) context;

        // build up layout classes
        reenterCurrentPassword = new EditText(context);
        enterNewPassword = new EditText(context);
        confirmNewPassword = new EditText(context);
        // init it
        LinearLayout layout = getLayout(context);

        // remember the old password
        oldPassword = password;

        this.setTitle(R.string.change_password);
        this.setCancelable(false);
        this.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (state == State.NEW_PASSWORD) {
                    String _enterNewPassword = enterNewPassword.getText().toString();
                    String _confirmNewPassword = confirmNewPassword.getText().toString();
                    if (_enterNewPassword.equals(_confirmNewPassword)){
                        aRevelation.setPassword(_enterNewPassword);

                        try {
                            // save file!
                            aRevelation.saveChanges(aRevelation.getCurrentFocus());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(aRevelation, R.string.passwords_do_not_match, Toast.LENGTH_LONG).show();
                    }
                }

                if (state == State.CHANGE_PASSWORD) {
                    if (!"".equals(password.trim())) {
                        if (password.equals(reenterCurrentPassword.getText().toString())) {
                            if (enterNewPassword.getText() != null
                                    && !"".equals(enterNewPassword.getText().toString())
                                    && enterNewPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
                                try {
                                    aRevelation.setPassword(enterNewPassword.getText().toString());
                                    aRevelation.saveChanges(aRevelation.getCurrentFocus());
                                    Toast.makeText(aRevelation, R.string.password_changed, Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    aRevelation.setPassword(oldPassword);
                                    Toast.makeText(aRevelation, aRevelation.getString(R.string.backup) + ": " + ARevelation.getBackupFile(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(aRevelation, R.string.passwords_do_not_match, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(aRevelation, R.string.decrypt_invalid_password_label, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(aRevelation, R.string.decrypt_empty_password_label, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        this.setNegativeButton(android.R.string.cancel, null);
        this.setView(layout);
    }
}
