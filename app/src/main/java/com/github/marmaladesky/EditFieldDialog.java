package com.github.marmaladesky;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.github.marmaladesky.data.FieldWrapper;

import de.igloffstein.maik.arevelation.helpers.ARevelationHelper;
import de.igloffstein.maik.arevelation.helpers.PasswordGenerationHelper;

public class EditFieldDialog extends DialogFragment {

    private static final String ARGUMENT_FIELD_UUID = "fieldUuid";
    private static final String ARGUMENT_FIELD_PASSWORD = "isPassword";

    private EditText value;
    private FieldWrapper field;
    private boolean isPassword = false;

    public static EditFieldDialog newInstance(String fieldUuid) {
        return newInstance(fieldUuid, false);
    }

    public static EditFieldDialog newInstance(String fieldUuid, boolean isPassword) {
        EditFieldDialog d = new EditFieldDialog();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_FIELD_UUID, fieldUuid);
        args.putBoolean(ARGUMENT_FIELD_PASSWORD, isPassword);
        d.setArguments(args);
        return d;
    }

    @SuppressLint("InflateParams") // Passing null is normal for dialogs
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        try {
            if (savedInstanceState == null && getArguments() != null) {
                field = ((ARevelation) getActivity()).rvlData.getFieldById(getArguments().getString(ARGUMENT_FIELD_UUID));
                isPassword = getArguments().getBoolean(ARGUMENT_FIELD_PASSWORD);
            } else if (savedInstanceState != null) {
                field = ((ARevelation) getActivity()).rvlData.getFieldById(savedInstanceState.getString(ARGUMENT_FIELD_UUID));
                isPassword = savedInstanceState.getBoolean(ARGUMENT_FIELD_PASSWORD);
            } else {
                throw new IllegalArgumentException("Need saved state.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Need saved state.", e);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.edit_field_dialog, null);
        builder.setView(v);
        value = v.findViewById(R.id.edit_field_value);
        value.setText(field.getFieldValue());

        builder
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    field.setFieldValue(value.getText().toString());
                                    ARevelationHelper.redrawRevelationListViewFragment(getFragmentManager(), getTargetFragment());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })

                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        if (isPassword) {
            builder.setNeutralButton(R.string.new_password,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                int passwordSize = (field != null && field.getFieldValue() != null) ? field.getFieldValue().length() : 0;
                                field.setFieldValue(PasswordGenerationHelper.getRandomPassword(getActivity(), passwordSize));
                                System.out.println("EFD: "+ getTargetFragment().getTag());
                                ARevelationHelper.redrawRevelationListViewFragment(getFragmentManager(), getTargetFragment());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putString(ARGUMENT_FIELD_UUID, field.getUuid());
            outState.putBoolean(ARGUMENT_FIELD_PASSWORD, isPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
