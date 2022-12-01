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

import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.interfaces.NewCardDialogCallback;

public class DialogFragmentNewCard extends DialogFragment {

    public static String TAG = "NewCardDialogFragment";

    Button buttonConfirm;

    Button buttonCancel;

    TextView textViewDescription;

    EditText editTextInputField1;

    EditText editTextInputField2;

    Util.DialogType dialogType;

    String oldFrontText;

    String oldBackText;

    String frontText;

    String backText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (!processArguments()){
            return super.onCreateDialog(savedInstanceState);
        }

        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_card, null);
        setupUi(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void setupUi(View view) {
        //todo: this dialog can also be used for editing card
        textViewDescription = view.findViewById(R.id.textView_dialog_descr_text);
        editTextInputField1 = view.findViewById(R.id.editText_dialog_field1);
        editTextInputField2 = view.findViewById(R.id.editText_dialog_field2);
        buttonConfirm = view.findViewById(R.id.button_dialog_confirm);
        buttonCancel = view.findViewById(R.id.button_dialog_cancel);

        textViewDescription.setText(Util.getDialogDescriptionResId(dialogType));
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processUserInput();
            }
        });
        if (dialogType == Util.DialogType.EDIT_CARD) {
            editTextInputField1.setText(oldFrontText);
            editTextInputField2.setText(oldBackText);
        }
    }

    private boolean processArguments() {
        Bundle args = getArguments();
        if (args == null) {
            Util.logDebug(TAG, "can't get any argument");
            return false;
        }
        dialogType = Util.getDialogTypeFromString(args.getString(Util.BUNDLE_KEY_DIALOGTYPE, "what the fuck"));
        if (dialogType == Util.DialogType.EDIT_CARD) {
            oldFrontText = args.getString(Util.BUNDLE_KEY_OLD_FRONT_TEXT, "what the fuck");
            oldBackText = args.getString(Util.BUNDLE_KEY_OLD_BACK_TEXT, "what the fuck");
            //todo: should i do something about weird case???
        }
        return true;
    }

    private void processUserInput() {
        frontText = editTextInputField1.getText().toString();
        backText = editTextInputField2.getText().toString();
        if (frontText.equals("") || backText.equals("")) {
            editTextInputField1.setError("enter both fields bruh");
            return;
        } else if (dialogType == Util.DialogType.EDIT_CARD &&
        frontText.equals(oldFrontText) && backText.equals(oldBackText)) {
            editTextInputField1.setError("these are the same bruh");
            return;
        }

        notifyResult();
        dismiss();
    }

    private void notifyResult(){
        try {
            NewCardDialogCallback callback = (NewCardDialogCallback) requireParentFragment();
            callback.onNewCardTextDialogResult(dialogType, frontText, backText);
        } catch (Exception e) {
            Util.logError(TAG, "exception happened: " + e);
        }
    }
}
