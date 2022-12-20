package com.android.myapplication8.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.myapplication8.interfaces.DialogResultCallback;
import com.android.myapplication8.R;
import com.android.myapplication8.Util;

public class DialogFragmentSimpleNameEdit extends DialogFragment {

    public static String TAG = "DialogFragmentSimpleNameEdit";

    Button buttonConfirm;

    Button buttonCancel;

    TextView textViewDescription;

    EditText editTextInputField1;

    Util.DialogType dialogType;

    String oldName;

    boolean renaming = false;

    public static DialogFragmentSimpleNameEdit newInstance(Util.DialogType dialogType,
                                                           String stringParam1) {

        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE, Util.getDialogTypeStringFromDialogType(dialogType));

        if (dialogType == Util.DialogType.DECK_RENAME || dialogType == Util.DialogType.COLLECTION_RENAME) {
            args.putString(Util.BUNDLE_KEY_OLD_NAME, stringParam1);
        }

        DialogFragmentSimpleNameEdit fragment = new DialogFragmentSimpleNameEdit();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (!processArguments()){
            return super.onCreateDialog(savedInstanceState);
        }

        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_deck_name, null);
        setupUi(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // a way to get context
    }

    private boolean processArguments() {
        Bundle args = getArguments();
        if (args == null) {
            Util.logDebug(TAG, "can't get any argument");
            return false;
        }
        dialogType = Util.getDialogTypeFromString(args.getString(Util.BUNDLE_KEY_DIALOGTYPE, "what the fuck"));
        if (dialogType == Util.DialogType.DECK_RENAME ||
        dialogType == Util.DialogType.COLLECTION_RENAME) {
            renaming = true;
            oldName = args.getString(Util.BUNDLE_KEY_OLD_NAME, "");
            if (oldName.length() == 0) {
                //todo: should i do something about this case???
                return false;
            }
        }
        return true;
    }

    private void setupUi(View view) {
        editTextInputField1 = view.findViewById(R.id.editText_dialog_field1);
        textViewDescription = view.findViewById(R.id.textView_dialog_descr_text);
        buttonConfirm = view.findViewById(R.id.button_dialog_confirm);
        buttonCancel = view.findViewById(R.id.button_dialog_cancel);

        textViewDescription.setText(Util.getDialogDescriptionResId(dialogType));

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.logDebug(TAG, "confirm");
                handleButtonConfirm();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.logDebug(TAG, "cancel");
                dismiss();
            }
        });

        if (renaming) {
            editTextInputField1.setText(oldName);
        }

    }

    private void handleButtonConfirm() {
        String inputText = editTextInputField1.getText().toString();
        if (inputText.equals("")) {
            editTextInputField1.setError("there's nothin here bruh");
            return;
        }
        if (renaming && inputText.equals(oldName)) {
            editTextInputField1.setError("dude it's the same name");
            return;
        }
        notifyResult();
        dismiss();
    }

    private void notifyResult() {
        DialogResultCallback callback;
        try {
            callback = (DialogResultCallback) requireParentFragment();
        } catch (Exception e) {
            Util.logError(TAG, "exception happened: " + e);
            return;
        }
        callback.onDialogResult_NewText(dialogType, editTextInputField1.getText().toString());
    }

}
