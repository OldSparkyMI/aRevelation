package de.igloffstein.maik.arevelation.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.marmaladesky.ARevelation;
import com.github.marmaladesky.Cryptographer;
import com.github.marmaladesky.R;
import com.github.marmaladesky.RevelationListViewFragment;
import com.github.marmaladesky.SelfTestingResult;
import com.github.marmaladesky.data.RevelationData;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Dialog for password inputs and decrypting revelation files
 *
 * Created by OldSparkyMI on 07.01.18.
 */
public class AskPasswordDialog extends DialogFragment {

    public String file;

    public static AskPasswordDialog newInstance(String file) {
        AskPasswordDialog f = new AskPasswordDialog();
        Bundle args = new Bundle();
        args.putString("file", file);
        f.setArguments(args);
        f.setCancelable(false);
        return f;
    }

    @SuppressLint("InflateParams") // Passing null is normal for dialogs
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getString("file") != null)
            file = savedInstanceState.getString("file");
        else
            file = getArguments().getString("file");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.ask_password_dialog, null));
        builder
                .setPositiveButton(R.string.open,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) { /* See onStart() */ }
                        })

                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    EditText passwordEdit = getDialog().findViewById(R.id.password);

                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordEdit.getWindowToken(), 0);

                    (new DecryptTask(v.getContext())).execute(passwordEdit.getText().toString(), file, v.getContext());
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file", file);
    }

    private class DecryptTask extends AsyncTask<Object, Void, DecryptTask.DecryptTaskResult> {

        private Context context;
        private String password;

        DecryptTask(Context context) {
            this.context = context;
        }

        @Override
        protected DecryptTask.DecryptTaskResult doInBackground(Object... params) {
            final String password = (String) params[0];
            final String file = (String) params[1];
            try {
                DecryptTask.DecryptTaskResult res = new DecryptTask.DecryptTaskResult();
                InputStream iStream = AskPasswordDialog.this.getActivity().getContentResolver().openInputStream(Uri.parse(file));
                byte[] inputData = getBytes(iStream);

                this.password = password;

                String result = Cryptographer.decrypt(inputData, password);
                Serializer serializer = new Persister();
                res.data = serializer.read(RevelationData.class, result, false);

                try {
                    SelfTestingResult testing = ARevelation.testData(result);
                    switch (testing) {
                        case Different:
                            res.toastMessage = R.string.self_testing_super_warning;
                            break;
                        case Similar:
                            res.toastMessage = R.string.self_testing_warning;
                            break;
                        case Identical:
                            res.toastMessage = R.string.self_testing_passed_message;
                            break;
                        default:
                            // a "this should never ever happen" case ;)
                            res.toastMessage = R.string.self_testing_internal_error;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            } catch (Exception e) {
                return new DecryptTask.DecryptTaskResult(e);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView t = getDialog().findViewById(R.id.message);
            t.setText(R.string.decrypt_label);
        }

        @Override
        protected void onPostExecute(DecryptTask.DecryptTaskResult s) {
            super.onPostExecute(s);

            if (!isCancelled()) {
                if (getActivity() != null) {    // need if user hits back or touches somewhere the screen.
                    if (!s.isFail) {
                        Toast.makeText(context, getActivity().getString(s.toastMessage), Toast.LENGTH_LONG).show();
                        ((ARevelation) getActivity()).setRvlData(s.data);

                        RevelationListViewFragment nextFrag = RevelationListViewFragment.newInstance(((ARevelation) getActivity()).getRvlData().getUuid());
                        ((ARevelation) getActivity()).setPassword(password);
                        ((ARevelation) getActivity()).setCurrentFile(file);

                        getActivity().getFragmentManager().beginTransaction()
                                .replace(R.id.mainContainer, nextFrag)
                                .addToBackStack(null).commit();

                        AskPasswordDialog.this.dismiss();
                        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);

                        int preferenceAutoLock = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("preference_auto_lock", "-1"));
                        if (preferenceAutoLock > 0) {
                            Toast.makeText(getActivity(), getResources().getQuantityString(R.plurals.auto_lock_time_left, preferenceAutoLock, preferenceAutoLock), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        TextView t = getDialog().findViewById(R.id.message);
                        String message = s.exception.getMessage();

                        if (message.endsWith(":BAD_DECRYPT")) {
                            t.setText(R.string.decrypt_invalid_password_label);
                        } else {
                            if (("Could not generate secret key").equals(message)) {
                                t.setText(R.string.decrypt_empty_password_label);
                            } else {
                                t.setText(s.exception.getMessage());
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, R.string.decrypt_canceled_label, Toast.LENGTH_LONG).show();
                }
            }
        }

        private byte[] getBytes(InputStream inputStream) throws IOException {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        }

        class DecryptTaskResult {

            Integer toastMessage;
            RevelationData data;
            boolean isFail;
            Throwable exception;

            DecryptTaskResult() {
            }

            DecryptTaskResult(Throwable e) {
                exception = e;
                isFail = true;
            }
        }
    }
}
