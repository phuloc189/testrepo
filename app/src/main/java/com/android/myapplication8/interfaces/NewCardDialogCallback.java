package com.android.myapplication8.interfaces;

import com.android.myapplication8.Util;

public interface NewCardDialogCallback {
    void onNewCardTextDialogResult(Util.DialogType dialogType, String frontText, String backText);
}
