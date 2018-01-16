package com.github.marmaladesky;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.marmaladesky.data.Entry;
import com.github.marmaladesky.data.RevelationData;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;

import de.igloffstein.maik.arevelation.activities.ARevelationSettingsActivity;
import de.igloffstein.maik.arevelation.fragments.AboutFragment;
import de.igloffstein.maik.arevelation.enums.EntryType;
import de.igloffstein.maik.arevelation.helpers.ARevelationHelper;
import de.igloffstein.maik.arevelation.helpers.EntryHelper;
import de.igloffstein.maik.arevelation.dialogs.AskPasswordDialog;
import lombok.Getter;
import lombok.Setter;

public class ARevelation extends AppCompatActivity implements AboutFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = ARevelation.class.getSimpleName();
    private static final int REQUEST_FILE_OPEN = 1;
    public static final String ARGUMENT_RVLDATA = "rvlData";
    public static final String ARGUMENT_PASSWORD = "password";
    public static final String ARGUMENT_FILE = "file";
    public static final String BACKUP_FILE_ENDING = ".arvlbak";
    @Getter
    private LinkedList<Entry> currentEntryState = new LinkedList<>();
    private static int sessionDepth = 0;

    DateFormat dateFormatter;

    @Setter
    @Getter
    protected RevelationData rvlData;
    @Setter
    @Getter
    protected String password;
    @Setter
    @Getter
    protected String currentFile;
    @Getter
    protected Button saveButton;
    protected long onPauseSystemMillis = 0;
    protected DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            Entry entry = EntryHelper.newEntry(EntryType.getFromPosition(which));
            // get id from language values
            int id = getResources()
                    .getIdentifier(EntryType.getFromPosition(which).toString().toLowerCase(),"string", getPackageName());
            entry.setName(getString(id));

            // add new element
            if (currentEntryState.size() <= 0) {
                rvlData.addEntry(entry);
            }else{
                currentEntryState.getLast().list.add(entry);
            }

            // go directly in detail view mode for editing
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, RevelationEntryFragment.newInstance(entry.getUuid()))
                    .addToBackStack(null).commit();
        }
    };

    /**
     * After https://developer.android.com/guide/components/activities/activity-lifecycle.html this
     * is the latest call before the acitivity is displayed to the user
     * <p>
     * lets check, if the user has to enter his password again
     */
    @Override
    public void onResume() {
        super.onResume();

        // we have user input
        if (currentFile != null){
            // do we already displayed something?
            if (((ViewGroup) findViewById(R.id.mainContainer)).getChildCount() <= 0){
                // empty view
                openAskPasswordDialog();
            }else{
                // full view, shall we hide the view?
                int preferenceLockInBackground = ARevelationHelper.preferenceLockInBackground(getApplicationContext());
                long preferenceLockInBackgroundNextSeconds = onPauseSystemMillis + preferenceLockInBackground * 60 * 1000;

                if (preferenceLockInBackground == 0 || preferenceLockInBackgroundNextSeconds <= System.currentTimeMillis()) {
                    clearUI();
                    openAskPasswordDialog();
                }
            }
        } else {
            // we have nothing, start!
            openStartScreenFragment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.onPauseSystemMillis = System.currentTimeMillis();
    }

    @Override
    public void onStop() {
        super.onStop();
        int preferenceLockInBackground = ARevelationHelper.preferenceLockInBackground(getApplicationContext());
        if (preferenceLockInBackground == 0) {
            Log.d(LOG_TAG, "clearing state in: ARevelation.onStop()");
            clearUI();
        }
    }

    private void restoreRvlDataStateFromBundle(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "restoreRvlDataStateFromBundle() ...");
            rvlData = (RevelationData) savedInstanceState.getSerializable(ARGUMENT_RVLDATA);
            password = savedInstanceState.getString(ARGUMENT_PASSWORD);
            currentFile = savedInstanceState.getString(ARGUMENT_FILE);
        }
    }

    public static SelfTestingResult testData(String xmlData) throws Exception {
        Serializer serializer1 = new Persister();
        RevelationData data = serializer1.read(RevelationData.class, xmlData, false);

        Serializer serializer2 = new Persister();
        Writer writer = new StringWriter();
        serializer2.write(data, writer);

        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(xmlData, writer.toString());

        if (BuildConfig.DEBUG) {
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // restore state!
        restoreRvlDataStateFromBundle(savedInstanceState);

        dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.MEDIUM, ARevelationHelper.getLocale(getResources()));
        saveButton = this.findViewById(R.id.saveButton);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARGUMENT_RVLDATA, rvlData);
        outState.putString(ARGUMENT_PASSWORD, password);
        outState.putString(ARGUMENT_FILE, currentFile);

        // call superclass to save any view hierarchy
        // this should be the last call: https://developer.android.com/guide/components/activities/activity-lifecycle.html#oncreate
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void saveChanges(View view) throws Exception {

        String backupFile = null;

        try {
            // backup old file
            backupFile = ARevelationHelper.backupFile(getApplicationContext(), currentFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (backupFile != null && !"".equals(backupFile)) {
            try {
                // save file
                rvlData.save(getApplicationContext(), currentFile, password);
                checkButton();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.error_cannot_save, Toast.LENGTH_LONG).show();

                try {
                    // can't save / error during save, restore old file
                    ARevelationHelper.restoreFile(getApplicationContext(), currentFile, backupFile);
                    Toast.makeText(getApplicationContext(), R.string.backup_restored, Toast.LENGTH_LONG).show();
                } catch (IOException e1) {
                    Toast.makeText(getApplicationContext(), R.string.error_cannot_restore, Toast.LENGTH_LONG).show();
                    e1.printStackTrace();
                }

                throw e;
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_cannot_backup, Toast.LENGTH_LONG).show();
            throw new IOException(getString(R.string.error_cannot_backup));
        }

    }

    /**
     * FAB Handler
     * @param view
     */
    public void addEntry(View view) {
        // nice from: https://stackoverflow.com/questions/15762905/how-can-i-display-a-list-view-in-an-android-alert-dialog
        new AlertDialog.Builder(view.getContext())
                .setTitle(R.string.add_entry)
                .setItems(EntryType.getTranslatedEntryTypes(getApplicationContext()), onClickListener)
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkButton();
        if (getFragmentManager().getBackStackEntryCount() < 1) {
            clearState(true);
            clearUI();
            openStartScreenFragment();
        }
    }

    /**
     * Just hides the view. This functions won't destroy the rvlData object.
     * But you have to redraw the whole view.
     *
     * This is only allowed, when we show the revelation data!
     */
    public void clearUI() {
        Log.d(LOG_TAG, "clearing UI state ...");
        // cancel the timer, because the view is destroyed
        RevelationListViewFragment.cancelTimer();
        // destroy the whole view, remove every child!
        ((ViewGroup) findViewById(R.id.mainContainer)).removeAllViews();
    }

    /**
     * Resets the rvlData state to null, but won't change the UI
     * @param resetFile clear the user input?
     */
    public void clearState(boolean resetFile) {
        Log.d(LOG_TAG, "clearing state ...");
        rvlData = null;
        password = null;
        currentFile = resetFile ? null : currentFile;
        RevelationListViewFragment.cancelTimer();
    }

    protected void clearState() {
        clearState(true);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentFile != null && !"".equals(currentFile)) {
            menu.findItem(R.id.menu_change_password).setEnabled(true);
        } else {
            menu.findItem(R.id.menu_change_password).setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_new:
                // currently not implementet
                break;
            case R.id.menu_open:
                optionItemSelectedOpen();
                break;
            case R.id.menu_change_password:
                changePassword();
                break;
            case R.id.menu_settings:
                Intent intent = new Intent(this, ARevelationSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_about:
                optionItemSelectedAbout();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     * This works, but this will save the changes too.
     * Because we have only one instance of rvlData
     * Better is to store all changes in an other rvlData variable
     *
     * ToDo: Fix this some day
     */
    private void changePassword() {
        // reenter old password
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // view of the alert dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText reenterCurrentPassword = new EditText(this);
        reenterCurrentPassword.setHint(R.string.current_password);
        reenterCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(reenterCurrentPassword);

        final EditText enterNewPassword = new EditText(this);
        enterNewPassword.setHint(R.string.new_password);
        enterNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(enterNewPassword);

        final EditText confirmNewPassword = new EditText(this);
        confirmNewPassword.setHint(R.string.confirm_new_password);
        confirmNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(confirmNewPassword);

        final String oldPassword = password;

        builder.setTitle(R.string.change_password);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (password != null && !"".equals(password.trim())) {
                    if (password.equals(reenterCurrentPassword.getText().toString())) {
                        if ( enterNewPassword.getText() != null
                                && !"".equals(enterNewPassword.getText().toString())
                                && enterNewPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
                            try {
                                password = enterNewPassword.getText().toString();
                                saveChanges(getCurrentFocus());
                                Toast.makeText(getApplicationContext(), R.string.password_changed, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                password = oldPassword;
                                Toast.makeText(getApplicationContext(), getString(R.string.backup) + ": " + ARevelationHelper.backupFile, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.passwords_do_not_match, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.decrypt_invalid_password_label, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.decrypt_empty_password_label, Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);


        builder.setView(layout);
        builder.create().show();
    }

    public void optionItemSelectedOpen() {
        clearState(true);
        String action = Build.VERSION.SDK_INT >= 19 ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT;
        Intent intent = new Intent(action);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");
        startActivityForResult(intent, REQUEST_FILE_OPEN);
    }

    public void optionItemSelectedAbout() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContainer, AboutFragment.newInstance()); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE_OPEN && data != null) {
            System.out.println("Revelation file opened, result code is " + resultCode
                    + ", file is " + data.getData());
            try {
                Log.d(LOG_TAG, "openAskPasswordDialog on onActivityResult()");
                // clear state needed because of 2 open password dialogs
                // D/ARevelation:: openAskPasswordDialog on onActivityResult() and
                // openAskPasswordDialog on onResume()
                // when screen goes on
                // <cancel> on password dialog
                // open new file
                // --> both event get triggered
                clearState();
                openAskPasswordDialog(data.getData().toString());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    protected boolean isAskPasswordDialogOpen(String file){
        return AskPasswordDialog.getInstance(file).isVisible();
    }

    /**
     * opens the ask password dialog
     */
    protected void openAskPasswordDialog() {
        openAskPasswordDialog(this.currentFile);
    }

    protected void openAskPasswordDialog(String file) {
        final String tag = "Tag";

        AskPasswordDialog askPasswordDialog = AskPasswordDialog.getInstance(file);
        if (!isAskPasswordDialogOpen(file) && file != null && !"".equals(file)){
            if (getFragmentManager().findFragmentByTag(tag) == null) {
                // only show / put on the fragment stack, if not already shown
                // turn phone off when askPasswordDialog is shown and:
                // java.lang.RuntimeException: Unable to resume activity {de.igloffstein.maik.aRevelation/com.github.marmaladesky.ARevelation}: java.lang.IllegalStateException: Fragment already added: AskPasswordDialog{af66224 #2 Tag}
                // so beware!
                askPasswordDialog.show(getFragmentManager(), tag);
            }
        }
    }

    public void openStartScreenFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, new StartScreenFragment())
                .commit();
    }
}
