package com.github.marmaladesky;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.github.marmaladesky.data.FieldWrapper;
import com.github.marmaladesky.data.RevelationData;

import de.igloffstein.maik.arevelation.helpers.ARevelationHelper;
import de.igloffstein.maik.arevelation.helpers.PasswordGenerationHelper;

public class ItemDialogFragment extends DialogFragment {


    private static final String HEADER_KEY = "header";
    private static final String FIELD_KEY = "field";

    private String header;
    private FieldWrapper field;

    public static ItemDialogFragment newInstance(String header, String fieldUuid) {
        ItemDialogFragment f = new ItemDialogFragment();
        Bundle args = new Bundle();
        args.putString(HEADER_KEY, header);
        args.putString(FIELD_KEY, fieldUuid);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            if (savedInstanceState == null && getArguments() != null) {
                header = getArguments().getString(HEADER_KEY);
                String string = getArguments().getString(FIELD_KEY);
                RevelationData rvlData = ((ARevelation) getActivity()).rvlData;
                field = rvlData.getFieldById(string);
            } else if (savedInstanceState != null) {
                header = savedInstanceState.getString(HEADER_KEY);
                field = ((ARevelation) getActivity()).rvlData.getFieldById(savedInstanceState.getString(FIELD_KEY));
            } else {
                throw new IllegalArgumentException("Need saved state.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Need saved state.", e);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] buttons = (header.equals(getString(R.string.generic_password)))
                ? new String[]{getString(R.string.copy), getString(R.string.edit), getString(R.string.new_password)}
                : new String[]{getString(R.string.copy), getString(R.string.edit)};
        builder.setItems(buttons, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("aRevelation copied data", field.getFieldValue());
                        if (clipboard != null) {
                            clipboard.setPrimaryClip(clip);
                        }
                        break;
                    case 1:
                        try {
                            DialogFragment dial = EditFieldDialog.newInstance(field.getUuid(), header.equals(getString(R.string.generic_password)));
                            dial.setTargetFragment(ItemDialogFragment.this.getTargetFragment(), 0); // Amazing piece of shit, but I don't know how to do it in another way
                            dial.show(getFragmentManager(), "ItemDialogFragment");
                            System.out.println("IDF: "+ getTargetFragment().getTag());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            if (field != null) {
                                int passwordSize = (field.getFieldValue() != null) ? field.getFieldValue().length() : 0;
                                field.setFieldValue(PasswordGenerationHelper.getRandomPassword(getActivity(), passwordSize));
                            }

                            ARevelationHelper.redrawRevelationListViewFragment(getFragmentManager(), getTargetFragment());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(HEADER_KEY, header);
        try {
            outState.putString(FIELD_KEY, field.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
