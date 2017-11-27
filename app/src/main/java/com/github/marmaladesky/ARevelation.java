package com.github.marmaladesky;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.marmaladesky.data.Entry;
import com.github.marmaladesky.data.RevelationData;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.List;

import de.igloffstein.maik.aRevelation.Fragment.AboutFragment;
import de.igloffstein.maik.aRevelation.EntryType;
import de.igloffstein.maik.aRevelation.Helper.ARevelationHelper;
import de.igloffstein.maik.aRevelation.Helper.EntryHelper;

import static com.github.marmaladesky.R.string.new_folder_currently_not_supported;

public class ARevelation extends AppCompatActivity implements AboutFragment.OnFragmentInteractionListener {

    private static final int REQUEST_FILE_OPEN = 1;
    private static final String ARGUMENT_RVLDATA = "rvlData";
    private static final String ARGUMENT_PASSWORD = "password";
    private static final String ARGUMENT_FILE = "file";

    DateFormat dateFormatter;

    RevelationData rvlData;
    String password;
    String currentFile;
    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            Entry entry = EntryHelper.newEntry(EntryType.getFromPosition(which));

            if (EntryType.getFromPosition(which) == EntryType.FOLDER) {
                // folder is currently not supported
                Toast.makeText(getApplicationContext(), new_folder_currently_not_supported, Toast.LENGTH_LONG).show();
            } else {

                // get id from language values
                int id = getResources().getIdentifier(
                        EntryType.getFromPosition(which).toString().toLowerCase(),
                        "string", getPackageName()
                );
                entry.setName(getString(id));

                rvlData.addEntry(entry);

                EntryFragment nextFrag = EntryFragment.newInstance(entry.getUuid());
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, nextFrag)
                        .addToBackStack(null).commit();
            }
        }
    };
    private Button saveButton;

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
        if (diff.identical())
            return SelfTestingResult.Identical;
        else if (diff.similar())
            return SelfTestingResult.Similar;
        else
            return SelfTestingResult.Different;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            rvlData = (RevelationData) savedInstanceState.getSerializable(ARGUMENT_RVLDATA);
            password = savedInstanceState.getString(ARGUMENT_PASSWORD);
            currentFile = savedInstanceState.getString(ARGUMENT_FILE);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Only at the startup
        if (getFragmentManager().getBackStackEntryCount() < 1) {
            StartScreenFragment newFragment = new StartScreenFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.mainContainer, newFragment)
                    .commit();
        }

        dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.MEDIUM, ARevelationHelper.getLocale(getResources()));
        saveButton = (Button) this.findViewById(R.id.saveButton);
    }

    public void saveChanges(View view) throws Exception {
        rvlData.save(currentFile, password, getContentResolver());
        checkButton();
    }

    public void addEntry(View view) {
        // nice from: https://stackoverflow.com/questions/15762905/how-can-i-display-a-list-view-in-an-android-alert-dialog
        new AlertDialog.Builder(view.getContext())
                .setTitle(R.string.add_entry)
                .setItems(EntryType.getTranslatedEntryTypes(getApplicationContext()), onClickListener)
                .create()
                .show();
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
        if (getFragmentManager().getBackStackEntryCount() < 1) clearState();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_new:
                // currently not implementet
                return true;
            case R.id.menu_open:
                optionItemSelectedOpen();
                return true;
            case R.id.menu_about:
                optionItemSelectedAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void optionItemSelectedOpen() {
        String action = Build.VERSION.SDK_INT >= 19 ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT;
        Intent intent = new Intent(action);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");
        startActivityForResult(intent, REQUEST_FILE_OPEN);
    }

    public void optionItemSelectedAbout() {
        Fragment aboutFragment = AboutFragment.newInstance(null, null);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContainer, aboutFragment); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE_OPEN && data != null) {
            System.out.println("Image opened, result code is " + resultCode
                    + ", file is " + data.getData());
            try {
                (AskPasswordDialog.newInstance(data.getData().toString())).show(getFragmentManager(), "Tag");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static class AskPasswordDialog extends DialogFragment {

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
            LayoutInflater inflater = getActivity().getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.ask_password_dialog, null));
            builder
                    .setPositiveButton(R.string.open,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) { /* See onStart() */ }
                            })

                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                        EditText passwordEdit = ((EditText) getDialog().findViewById(R.id.password));

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
                    DecryptTaskResult res = new DecryptTaskResult();
                    InputStream iStream = AskPasswordDialog.this.getActivity().getContentResolver().openInputStream(Uri.parse(file));
                    byte[] inputData = getBytes(iStream);

                    this.password = password;

                    String result = Cryptographer.decrypt(inputData, password);
                    Serializer serializer = new Persister();
                    res.data = serializer.read(RevelationData.class, result, false);

                    try {
                        SelfTestingResult testing = ARevelation.testData(result);
                        if (testing == SelfTestingResult.Different) {
                            res.toastMessage = R.string.self_testing_super_warning;
                        } else if (testing == SelfTestingResult.Similar) {
                            res.toastMessage = R.string.self_testing_warning;
                        } else if (BuildConfig.DEBUG && testing == SelfTestingResult.Identical) {
                            res.toastMessage = R.string.self_testing_passed_message;
                        } else
                            res.toastMessage = R.string.self_testing_internal_error;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return res;
                } catch (Exception e) {
                    return new DecryptTaskResult(e);
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                TextView t = (TextView) getDialog().findViewById(R.id.message);
                t.setText(R.string.decrypt_label);
            }

            @Override
            protected void onPostExecute(DecryptTaskResult s) {
                super.onPostExecute(s);

                if (!isCancelled()) {
                    if (getActivity() != null) {    // need if user hits back or touches somewhere the screen.
                        if (!s.isFail) {
                            Toast.makeText(context, getActivity().getString(s.toastMessage), Toast.LENGTH_LONG).show();
                            ((ARevelation) getActivity()).rvlData = s.data;

                            RevelationBrowserFragment nextFrag = RevelationBrowserFragment.newInstance(((ARevelation) getActivity()).rvlData.getUuid());
                            ((ARevelation) getActivity()).password = password;
                            ((ARevelation) getActivity()).currentFile = file;

                            getActivity().getFragmentManager().beginTransaction()
                                    .replace(R.id.mainContainer, nextFrag)
                                    .addToBackStack(null).commit();

                            AskPasswordDialog.this.dismiss();
                            getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
                        } else {
                            TextView t = (TextView) getDialog().findViewById(R.id.message);
                            String message = s.exception.getMessage();

                            if (message.endsWith(":BAD_DECRYPT")) {
                                t.setText(R.string.decrypt_invalid_password_label);
                            } else {
                                if (("Could not generate secret key").equals(message)) {
                                    t.setText(R.string.decrypt_empty_passwort_label);
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
}
