package com.android.myapplication8.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.myapplication8.interfaces.ConfirmDialogCallback;
import com.android.myapplication8.R;
import com.android.myapplication8.Util;

public class DialogFragmentConfirm extends DialogFragment {

    public static String TAG = "ConfirmDialogFragment";

    Button buttonConfirm;

    Button buttonCancel;

    TextView textView;

    Util.DialogType dialogType;

    String openedDeckName;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (!processArguments()) {
            return super.onCreateDialog(savedInstanceState);
        }

        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_message, null);
        setupDialogUi(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // a way to get context
    }

    private void setupDialogUi(View view) {//textView_dialog_content_text
        textView = view.findViewById(R.id.textView_dialog_descr_text);
        buttonConfirm = view.findViewById(R.id.button_dialog_confirm);
        buttonCancel = view.findViewById(R.id.button_dialog_cancel);

//        textView.setText(Util.getDialogDescriptionTextId(dialogType));
        if (dialogType == Util.DialogType.CONFIRM_OPEN_DECK) {
            textView.setText(getString(Util.getDialogDescriptionResId(dialogType), openedDeckName));
        } else {
            textView.setText(Util.getDialogDescriptionResId(dialogType));
        }


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyResult();
                dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private boolean processArguments() {
        Bundle args = getArguments();
        if (args == null){
            Util.logDebug(TAG, "can't get any argument");
            return false;
        }

        dialogType = Util.getDialogTypeFromString(args.getString(Util.BUNDLE_KEY_DIALOGTYPE, "what the fuck"));
        openedDeckName = args.getString(Util.BUNDLE_KEY_NAME_OF_DECK_TOBE_OPENED, "what the fuck");
        return true;
    }

    private void notifyResult() {
        try {
            ConfirmDialogCallback callback = (ConfirmDialogCallback) requireParentFragment();
            callback.onDialogResult_Confirm(dialogType);
        } catch (Exception e) {
            Util.logError(TAG, "exception happened: " + e);
        }
    }
}
