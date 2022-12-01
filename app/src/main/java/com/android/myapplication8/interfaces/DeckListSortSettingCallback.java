package com.android.myapplication8.interfaces;

import com.android.myapplication8.Util;

public interface DeckListSortSettingCallback {

    void onDeckListSortDialogResult(Util.DialogType dialogType, boolean sortSettingChanged);
}
