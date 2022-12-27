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
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.interfaces.DeckListSortSettingCallback;

public class DialogFragmentDeckListSortOption extends DialogFragment {

    public static final String TAG = "DialogFragmentDeckListSortOption";

    Button buttonCancel;

    Button buttonConfirm;

    CheckBox chkBoxDescendingOption;

    RadioGroup radioGroupSortingType;

    RadioButton radioButton_CreationOrder;

    RadioButton radioButton_VisitedOrder;

    RadioButton radioButton_AlphabetOrder;

    //-----------

    SharedPreferences sharedPreferences;

    Util.SortingOptions optionSortingType_current;
    Util.SortingOptions optionSortingType_new;

    boolean optionDescending_current;
    boolean optionDescending_new;

    Util.DialogType dialogType;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (!processArguments()) {
            return super.onCreateDialog(savedInstanceState);
        }
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_deck_list_option, null);
        setupDialogUi(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void setupDialogUi(View view) {
        buttonCancel = view.findViewById(R.id.button_dialog_cancel);
        buttonConfirm = view.findViewById(R.id.button_dialog_confirm);
        chkBoxDescendingOption = view.findViewById(R.id.chkBox_listSortingOptions_descending);
        radioGroupSortingType = view.findViewById(R.id.radioGroup_listSortingOptions);
        radioButton_AlphabetOrder = view.findViewById(R.id.radioButton_listSortingOptions_AlphabetOrder);
        radioButton_CreationOrder = view.findViewById(R.id.radioButton_listSortingOptions_creationOrder);
        radioButton_VisitedOrder = view.findViewById(R.id.radioButton_listSortingOptions_VisitedOrder);

        initUi();

        chkBoxDescendingOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                optionDescending_new = b;
            }
        });
        radioGroupSortingType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Util.logDebug(TAG, "check changed: " + i);
                setNewSortTypeOption(i);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSomething();
                dismiss();
            }
        });
    }

    private void setNewSortTypeOption(int viewId) {
        if (viewId == R.id.radioButton_listSortingOptions_AlphabetOrder) {
            optionSortingType_new = Util.SortingOptions.ALPHABET_ORDER;
        } else if (viewId == R.id.radioButton_listSortingOptions_VisitedOrder) {
            optionSortingType_new = Util.SortingOptions.VISITED_ORDER;
        } else if (viewId == R.id.radioButton_listSortingOptions_creationOrder) {
            optionSortingType_new = Util.SortingOptions.CREATION_ORDER;
        }
    }

    private void initUi() {
        chkBoxDescendingOption.setChecked(optionDescending_current);
        switch (optionSortingType_current) {
            case VISITED_ORDER:
                radioButton_VisitedOrder.setChecked(true);
                break;
            case ALPHABET_ORDER:
                radioButton_AlphabetOrder.setChecked(true);
                break;
            case CREATION_ORDER:
            default:
                radioButton_CreationOrder.setChecked(true);
        }
    }

    private boolean processArguments() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return false;
        }
        dialogType = Util.getDialogTypeFromString(
                bundle.getString(Util.BUNDLE_KEY_DIALOGTYPE, "what the fuck"));
        optionSortingType_current = Util.getSortingOption(sharedPreferences.getInt(
                requireContext().getString(R.string.pref_key_deck_list_sorting_type),
                Util.SORTING_TYPE_OPTION_DEFAULT_VALUE
        ));
        optionSortingType_new = optionSortingType_current;
        optionDescending_current = sharedPreferences.getBoolean(
                requireContext().getString(R.string.pref_key_deck_list_sorting_descending),
                Util.SORTING_DESCENDING_OPTION_DEFAULT_VALUE
        );
        optionDescending_new = optionDescending_current;
        return true;
    }

    private void doSomething() {
        if (optionDescending_new == optionDescending_current
                && optionSortingType_new == optionSortingType_current) {
            return;
        }
        sharedPreferences.edit()
                .putBoolean(requireContext().getString(R.string.pref_key_deck_list_sorting_descending), optionDescending_new)
                .putInt(requireContext().getString(R.string.pref_key_deck_list_sorting_type), optionSortingType_new.prefValue)
                .commit();
        notifyResult();
    }

    private void notifyResult() {
        try {
            DeckListSortSettingCallback callback = (DeckListSortSettingCallback) requireParentFragment();
            callback.onDeckListSortDialogResult(dialogType,
                    optionDescending_new != optionDescending_current
                            || optionSortingType_new != optionSortingType_current);
        } catch (Exception e) {
            Util.logError(TAG, "exception happened: " + e);
        }
    }


}
