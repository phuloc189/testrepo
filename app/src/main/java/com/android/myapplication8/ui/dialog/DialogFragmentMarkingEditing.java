package com.android.myapplication8.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.myapplication8.MarkingSettingHelperType;
import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.ViewModel1;
import com.android.myapplication8.interfaces.MarkingEditCallback;

public class DialogFragmentMarkingEditing extends DialogFragment {

    public static final String TAG = "MarkingEditingDialogFragment";

    private int previousChoice;

    private int newChoice;

    int limitedMarkingValue_received;

    MarkingSettingHelperType limitedMarkingValue_current;

    Util.DialogType dialogType;

    ViewModel1 viewModel;

    int[] markingStat;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (!processArguments()) {
            return super.onCreateDialog(savedInstanceState);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        if (dialogType == Util.DialogType.CARD_MARKING_EDIT) {
            builder.setTitle("Select difficulty mark")
                    .setSingleChoiceItems(initListOfItems(), previousChoice, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            newChoice = i;
                        }
                    })
                    .setPositiveButton(R.string.button_dialog_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkResult();
                        }
                    })
                    .setNegativeButton(R.string.button_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    });
        } else if (dialogType == Util.DialogType.LIMIT_MARKING_OPTION) {
            builder.setTitle("Select difficulty mark")
                    .setMultiChoiceItems(initListOfItems(), limitedMarkingValue_current.getByteArray(), new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            limitedMarkingValue_current.setMarking(i, b);
                        }
                    })
                    .setPositiveButton(R.string.button_dialog_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkResult();
                        }
                    })
                    .setNegativeButton(R.string.button_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            abort2ndDialog();
                            dismiss();
                        }
                    });
        }
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private boolean processArguments() {
        Bundle args = getArguments();
        if (args == null) {
            return false;
        }
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel1.class);
        markingStat = viewModel.getMarkingStat();

        dialogType = Util.getDialogTypeFromString(args.getString(Util.BUNDLE_KEY_DIALOGTYPE, ""));
        if (dialogType == Util.DialogType.CARD_MARKING_EDIT) {
            previousChoice = args.getInt(Util.BUNDLE_KEY_OLD_MARKING_VALUE);
            newChoice = previousChoice;
        } else if (dialogType == Util.DialogType.LIMIT_MARKING_OPTION) {
            limitedMarkingValue_received = args.getInt(Util.BUNDLE_KEY_CURRENT_LIMITED_MARKING_VALUE_SETTING, Util.LIMITED_MARKING_DEFAULT_VALUE);
            limitedMarkingValue_current = new MarkingSettingHelperType(limitedMarkingValue_received);
        }
        return true;
    }

    private void checkResult() {
        if (dialogType == Util.DialogType.CARD_MARKING_EDIT) {
            if (newChoice != previousChoice) {
                notifyResult();
            }
        } else if (dialogType == Util.DialogType.LIMIT_MARKING_OPTION) {
            notifyResult();
        }
        dismiss();
    }

    private CharSequence[] initListOfItems(){
        CharSequence[] items = new CharSequence[Util.CARD_MARKING_MAX_NUMBER_OF_VALUES];
        for (int i = 0; i< items.length; i++ ){
            String infoTxt;
            if (markingStat[i] == 0) {
                infoTxt = " - none";
            } else if (markingStat[i] == 1) {
                infoTxt = " - only one";
            } else {
                infoTxt =  " - " + markingStat[i] + " cards in total";
            }
            items[i] = (String.valueOf(i) + infoTxt);
        }
        return items;
    }

    private void abort2ndDialog() {
        try {
            MarkingEditCallback callback = (MarkingEditCallback) requireParentFragment();
            callback.onMarkingChanged(dialogType, limitedMarkingValue_received);
        } catch (Exception e) {
            Util.logDebug(TAG, "exception happened: " + e);
        }

    }

    private void notifyResult() {
        try {
            MarkingEditCallback callback = (MarkingEditCallback) requireParentFragment();
            if (dialogType == Util.DialogType.CARD_MARKING_EDIT) {
                callback.onMarkingChanged(dialogType, newChoice);
            } else if (dialogType == Util.DialogType.LIMIT_MARKING_OPTION) {
                callback.onMarkingChanged(dialogType, limitedMarkingValue_current.getMarkingValue_int());
            }
        } catch (Exception e) {
            Util.logDebug(TAG, "exception happened: " + e);
        }
    }
}
