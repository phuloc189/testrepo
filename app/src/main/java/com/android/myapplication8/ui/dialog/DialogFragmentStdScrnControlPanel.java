package com.android.myapplication8.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.interfaces.StdScrnControlPanelCallback;

public class DialogFragmentStdScrnControlPanel extends DialogFragment {

    public static final String TAG = "DialogFragmentStdScrnControlPanel";

    boolean randomMode_org = false;

    boolean randomMode_new = false;

    int limitedMarkingValue_org;

    int limitedMarkingValue_new;

    boolean showBackFirst_org = false;

    boolean showBackFirst_new = false;

    CheckBox checkBoxSelectedMarking;

    CheckBox checkBoxRandomMode;

    CheckBox checkBoxShowBackSideFirst;

    Button buttonCancel;

    Button buttonConfirm;

    SharedPreferences sharedPreferences;

    Util.DialogType dialogType;

    boolean randomMode_changed;
    boolean showBackFirst_changed;
    boolean limitedMarkingValue_changed;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (!processArguments()){
            return super.onCreateDialog(savedInstanceState);
        }
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_study_screen_control_panel, null);
        setupUi(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private boolean processArguments(){
        Bundle args = getArguments();
        if (args == null) {
            return false;
        }
        dialogType = Util.getDialogTypeFromString(args.getString(Util.BUNDLE_KEY_DIALOGTYPE
                , "what the fuck"));
        limitedMarkingValue_org = sharedPreferences.getInt(requireContext().getString(
                R.string.pref_key_limited_marking_value), Util.LIMITED_MARKING_DEFAULT_VALUE);
        limitedMarkingValue_new = limitedMarkingValue_org;
        randomMode_org = sharedPreferences.getBoolean(requireContext().getString(
                R.string.pref_key_random_mode_on), false);
        randomMode_new = randomMode_org;
        showBackFirst_org = sharedPreferences.getBoolean(
                requireContext().getString(R.string.pref_key_show_back_side_first), false);
        showBackFirst_new = showBackFirst_org;
        return true;
    }

    private void setupUi(View view) {
        buttonConfirm = view.findViewById(R.id.button_dialog_confirm);
        buttonCancel = view.findViewById(R.id.button_dialog_cancel);
        checkBoxRandomMode = view.findViewById(R.id.chkBox_studyScrn_CtrlPanel_random_mode);
        checkBoxSelectedMarking = view.findViewById(R.id.chkBox_studyScrn_CtrlPanel_specified_marking);
        checkBoxShowBackSideFirst = view.findViewById(R.id.chkBox_studyScrn_CtrlPanel_showing_back_first);

        checkBoxRandomMode.setChecked(randomMode_org);
        checkBoxSelectedMarking.setChecked(limitedMarkingValue_org < Util.LIMITED_MARKING_NON_LIMITED);
        checkBoxShowBackSideFirst.setChecked(showBackFirst_org);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doConfirmChanges();
                dismiss();
            }
        });

        checkBoxSelectedMarking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAnotherDialog();
            }
        });
        checkBoxRandomMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomMode_new = ((CheckBox) view).isChecked();
            }
        });
        checkBoxShowBackSideFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBackFirst_new = ((CheckBox) view).isChecked();
            }
        });

    }

    private void openAnotherDialog() {
        try {
            StdScrnControlPanelCallback callback = (StdScrnControlPanelCallback) requireParentFragment();
            callback.openMarkingOptionDialog(dialogType, limitedMarkingValue_new);
        } catch (Exception e) {
            Util.logDebug(TAG, "exception happened: " + e);
        }
    }

    public void childDialogClosed(int currentLimitedMarkingValue) {
        Util.logDebug(TAG, "child Dialog returned");
        limitedMarkingValue_new = currentLimitedMarkingValue;
        if (limitedMarkingValue_new < Util.LIMITED_MARKING_NON_LIMITED) {
            checkBoxSelectedMarking.setChecked(true);
        } else if (limitedMarkingValue_new == Util.LIMITED_MARKING_NON_LIMITED){
            checkBoxSelectedMarking.setChecked(false);
        }
    }

    private void doConfirmChanges(){
        sharedPreferences.edit()
                .putBoolean(requireContext().getString(R.string.pref_key_random_mode_on), randomMode_new)
                .putBoolean(requireContext().getString(R.string.pref_key_show_back_side_first), showBackFirst_new)
                .putInt(requireContext().getString(R.string.pref_key_limited_marking_value), limitedMarkingValue_new)
                .commit();
        randomMode_changed = (randomMode_org != randomMode_new);
        limitedMarkingValue_changed = (limitedMarkingValue_org != limitedMarkingValue_new);
        showBackFirst_changed = (showBackFirst_org != showBackFirst_new);
        Util.logDebug(TAG, "change in random mode: " + randomMode_changed);
        Util.logDebug(TAG, "change in marking setting: " + limitedMarkingValue_changed);
        notifyChange();
    }

    private void notifyChange(){
        try {
            StdScrnControlPanelCallback callback = (StdScrnControlPanelCallback) requireParentFragment();
            callback.controlPanelResult(dialogType,
                    randomMode_changed || limitedMarkingValue_changed || showBackFirst_changed,
                    randomMode_changed || limitedMarkingValue_changed);
        } catch (Exception e) {
            Util.logDebug(TAG, "exception happened: " + e);
        }
    }
}
