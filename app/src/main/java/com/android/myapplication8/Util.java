package com.android.myapplication8;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Util {

    public static final boolean TEST_MODE = true;

    //------------

    public static final String BUNDLE_KEY_DIALOGTYPE = "bundle_key_dialogtype";

    public static final String BUNDLE_VALUE_DIALOGTYPE_DECK_DELETE_CONFIRM = "bundle_value_dialogtype_deck_delete_confirm";

    public static final String BUNDLE_VALUE_DIALOGTYPE_OPEN_DECK_CONFIRM = "bundle_value_dialogtype_open_deck_confirm";

    public static final String BUNDLE_VALUE_DIALOGTYPE_CREATE_NEW_DECK = "bundle_value_dialogtype_create_new_deck";

    public static final String BUNDLE_VALUE_DIALOGTYPE_RENAME_DECK = "bundle_value_dialogtype_rename_deck";

    public static final String BUNDLE_VALUE_DIALOGTYPE_DELETE_CARD_CONFIRM = "bundle_value_dialogtype_delete_card_confirm";

    public static final String BUNDLE_VALUE_DIALOGTYPE_NEW_CARD = "bundle_value_dialogtype_new_card";

    public static final String BUNDLE_VALUE_DIALOGTYPE_EDIT_CARD = "bundle_value_dialogtype_edit_card";

    public static final String BUNDLE_VALUE_DIALOGTYPE_CARD_MARKING_EDIT = "bundle_value_dialogtype_card_marking_edit";

    public static final String BUNDLE_VALUE_DIALOGTYPE_STUDY_SCREEN_CTRL_PANEL = "bundle_value_dialogtype_study_screen_ctrl_panel";

    public static final String BUNDLE_VALUE_DIALOGTYPE_LIMIT_MARKING_OPTION = "bundle_value_dialogtype_limit_marking_option";

    public static final String BUNDLE_VALUE_DIALOGTYPE_CONFIRM_OPEN_DECK_JUST_CREATED = "bundle_value_dialogtype_confirm_open_deck_just_created";

    public static final String BUNDLE_VALUE_DIALOGTYPE_DECK_LIST_SORT_OPTION = "bundle_value_dialogtype_deck_list_sort_option";

    public static final String BUNDLE_VALUE_DIALOGTYPE_CREATE_COLLECTION = "bundle_value_dialogtype_create_collection";

    public static final String BUNDLE_VALUE_DIALOGTYPE_CONFIRM_COLLECTION_DELETE = "bundle_value_dialogtype_confirm_collection_delete";

    public static final String BUNDLE_VALUE_DIALOGTYPE_COLLECTION_RENAME = "bundle_value_dialogtype_collection_rename";

    public static final String BUNDLE_VALUE_DIALOGTYPE_CONFIRM_MULTIPLE_CARDS_DELETE = "bundle_value_dialogtype_confirm_multiple_cards_delete";

    //////////////

    public static final String BUNDLE_KEY_OLD_NAME = "bundle_key_old_name";
    public static final String BUNDLE_KEY_OLD_FRONT_TEXT = "bundle_key_old_front_text";
    public static final String BUNDLE_KEY_OLD_BACK_TEXT = "bundle_key_old_back_text";

    public static final String BUNDLE_KEY_OLD_MARKING_VALUE = "bundle_key_old_name";
    public static final String BUNDLE_KEY_CURRENT_LIMITED_MARKING_VALUE_SETTING = "bundle_key_current_limited_marking_value_setting";
    public static final String BUNDLE_KEY_NAME_OF_DECK_TOBE_OPENED = "bundle_key_name_of_deck_tobe_opened";

    public static final String INTENT_EXTRA_KEY_MODE_SELECT = "intent_extra_mode_select";

    public static final String INTENT_EXTRA_VALUE_MODE_SELECT_DECK_MANAGEMENT = "intent_extra_value_mode_select_deck_management";
    public static final String INTENT_EXTRA_VALUE_MODE_SELECT_COLLECTION_MANAGEMENT = "intent_extra_value_mode_select_collection_management";

    //////////////

    public static final int CARD_MARKING_MAX_NUMBER_OF_VALUES = 10;

    public static final int LIMITED_MARKING_DEFAULT_VALUE = 0b1111111111;

    public static final int LIMITED_MARKING_NON_LIMITED = 0b1111111111;

    public static final int SORTING_TYPE_OPTION_DEFAULT_VALUE = SortingOptions.CREATION_ORDER.prefValue;

    public static final boolean SORTING_DESCENDING_OPTION_DEFAULT_VALUE = false;

    //////////////

    public enum StudyMode {
        DECK,
        COLLECTION
    }

    public enum ClickEvent {
        NONE,
        CLICK,
        LONG_CLICK
    }

    public enum DialogType {
        NONE,
        NEW_DECK_NAME,
        DECK_RENAME,
        COLLECTION_RENAME,
        NEW_CARD,
        EDIT_CARD,
        CARD_MARKING_EDIT,
        LIMIT_MARKING_OPTION,
        STUDY_SCREEN_CTRL_PANEL,
        DECK_LIST_SORT_OPTION,
        CONFIRM_DECK_DELETE,
        CONFIRM_OPEN_DECK,
        CONFIRM_CARD_DELETE,
        CONFIRM_MULTIPLE_CARDS_DELETE,
        CONFIRM_OPEN_DECK_JUST_CREATED,
        CONFIRM_COLLECTION_DELETE,
        CREATE_COLLECTION

    }

    public enum SortingOptions {
        NONE(0),
        CREATION_ORDER(1),
        VISITED_ORDER(2),
        ALPHABET_ORDER(3);

        public final int prefValue;

        SortingOptions(int prefValue) {
            this.prefValue = prefValue;
        }
    }

    public static SortingOptions getSortingOption(int prefValue) {
        switch (prefValue) {
            case 1:
                return SortingOptions.CREATION_ORDER;
            case 2:
                return SortingOptions.VISITED_ORDER;
            case 3:
                return SortingOptions.ALPHABET_ORDER;
            default:
                return SortingOptions.NONE;
        }
    }


    public static void toast(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void toastFromBackground(AppCompatActivity activity, CharSequence text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Util.toast(activity, text);
            }
        });
    }

    public static void logDebug(String TAG, String msg) {
        Log.d(TAG, msg);
    }

    public static void logError(String TAG, String msg) {
        Log.e(TAG, msg);
    }

    public static int getDialogDescriptionResId(DialogType dialogType) {
        switch (dialogType) {
            case CONFIRM_DECK_DELETE:
                return R.string.dialog_descr_txt_deck_delete_confirm;
            case NEW_DECK_NAME:
                return R.string.dialog_descr_txt_create_new_deck;
            case DECK_RENAME:
                return R.string.dialog_descr_txt_rename_deck;
            case CONFIRM_OPEN_DECK:
                return R.string.dialog_descr_txt_open_deck_confirm;
            case CONFIRM_CARD_DELETE:
                return R.string.dialog_descr_txt_card_delete_confirm;
            case CONFIRM_OPEN_DECK_JUST_CREATED:
                return R.string.dialog_descr_txt_open_new_deck_confirm;
            case NEW_CARD:
                return R.string.dialog_descr_txt_create_new_card;
            case EDIT_CARD:
                return R.string.dialog_descr_txt_edit_card;
            case CREATE_COLLECTION:
                return R.string.dialog_descr_txt_create_collection;
            case CONFIRM_COLLECTION_DELETE:
                return R.string.dialog_descr_txt_collection_delete_confirm;
            case COLLECTION_RENAME:
                return R.string.dialog_descr_txt_collection_rename;
            case CONFIRM_MULTIPLE_CARDS_DELETE:
                return R.string.dialog_descr_txt_multiple_cards_delete_confirm;
            default:
                return R.string.dialog_descr_txt_default_text;
        }
    }

    public static String getDialogTypeStringFromDialogType(Util.DialogType dialogType) {
        switch (dialogType) {
            case CONFIRM_DECK_DELETE:
                return BUNDLE_VALUE_DIALOGTYPE_DECK_DELETE_CONFIRM;
            case NEW_DECK_NAME:
                return BUNDLE_VALUE_DIALOGTYPE_CREATE_NEW_DECK;
            case DECK_RENAME:
                return BUNDLE_VALUE_DIALOGTYPE_RENAME_DECK;
            case CONFIRM_OPEN_DECK:
                return BUNDLE_VALUE_DIALOGTYPE_OPEN_DECK_CONFIRM;
            case CONFIRM_CARD_DELETE:
                return BUNDLE_VALUE_DIALOGTYPE_DELETE_CARD_CONFIRM;
            case CONFIRM_OPEN_DECK_JUST_CREATED:
                return BUNDLE_VALUE_DIALOGTYPE_CONFIRM_OPEN_DECK_JUST_CREATED;
            case NEW_CARD:
                return BUNDLE_VALUE_DIALOGTYPE_NEW_CARD;
            case EDIT_CARD:
                return BUNDLE_VALUE_DIALOGTYPE_EDIT_CARD;
            case CARD_MARKING_EDIT:
                return BUNDLE_VALUE_DIALOGTYPE_CARD_MARKING_EDIT;
            case STUDY_SCREEN_CTRL_PANEL:
                return BUNDLE_VALUE_DIALOGTYPE_STUDY_SCREEN_CTRL_PANEL;
            case LIMIT_MARKING_OPTION:
                return BUNDLE_VALUE_DIALOGTYPE_LIMIT_MARKING_OPTION;
            case DECK_LIST_SORT_OPTION:
                return BUNDLE_VALUE_DIALOGTYPE_DECK_LIST_SORT_OPTION;
            case CREATE_COLLECTION:
                return BUNDLE_VALUE_DIALOGTYPE_CREATE_COLLECTION;
            case CONFIRM_COLLECTION_DELETE:
                return BUNDLE_VALUE_DIALOGTYPE_CONFIRM_COLLECTION_DELETE;
            case COLLECTION_RENAME:
                return BUNDLE_VALUE_DIALOGTYPE_COLLECTION_RENAME;
            case CONFIRM_MULTIPLE_CARDS_DELETE:
                return BUNDLE_VALUE_DIALOGTYPE_CONFIRM_MULTIPLE_CARDS_DELETE;
            default:
                return "WUT???";
        }
    }

    public static Util.DialogType getDialogTypeFromString(String dialogType) {
        switch (dialogType) {
            case BUNDLE_VALUE_DIALOGTYPE_DECK_DELETE_CONFIRM:
                return Util.DialogType.CONFIRM_DECK_DELETE;
            case BUNDLE_VALUE_DIALOGTYPE_CREATE_NEW_DECK:
                return Util.DialogType.NEW_DECK_NAME;
            case BUNDLE_VALUE_DIALOGTYPE_RENAME_DECK:
                return Util.DialogType.DECK_RENAME;
            case BUNDLE_VALUE_DIALOGTYPE_OPEN_DECK_CONFIRM:
                return DialogType.CONFIRM_OPEN_DECK;
            case BUNDLE_VALUE_DIALOGTYPE_DELETE_CARD_CONFIRM:
                return DialogType.CONFIRM_CARD_DELETE;
            case BUNDLE_VALUE_DIALOGTYPE_CONFIRM_OPEN_DECK_JUST_CREATED:
                return DialogType.CONFIRM_OPEN_DECK_JUST_CREATED;
            case BUNDLE_VALUE_DIALOGTYPE_NEW_CARD:
                return DialogType.NEW_CARD;
            case BUNDLE_VALUE_DIALOGTYPE_EDIT_CARD:
                return DialogType.EDIT_CARD;
            case BUNDLE_VALUE_DIALOGTYPE_CARD_MARKING_EDIT:
                return DialogType.CARD_MARKING_EDIT;
            case BUNDLE_VALUE_DIALOGTYPE_LIMIT_MARKING_OPTION:
                return DialogType.LIMIT_MARKING_OPTION;
            case BUNDLE_VALUE_DIALOGTYPE_DECK_LIST_SORT_OPTION:
                return DialogType.DECK_LIST_SORT_OPTION;
            case BUNDLE_VALUE_DIALOGTYPE_CREATE_COLLECTION:
                return DialogType.CREATE_COLLECTION;
            case BUNDLE_VALUE_DIALOGTYPE_CONFIRM_COLLECTION_DELETE:
                return DialogType.CONFIRM_COLLECTION_DELETE;
            case BUNDLE_VALUE_DIALOGTYPE_COLLECTION_RENAME:
                return DialogType.COLLECTION_RENAME;
            case BUNDLE_VALUE_DIALOGTYPE_CONFIRM_MULTIPLE_CARDS_DELETE:
                return DialogType.CONFIRM_MULTIPLE_CARDS_DELETE;
            default:
                return DialogType.NONE;
        }
    }
}
