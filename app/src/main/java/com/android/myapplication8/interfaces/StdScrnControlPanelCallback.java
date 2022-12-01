package com.android.myapplication8.interfaces;

import com.android.myapplication8.Util;

public interface StdScrnControlPanelCallback {

    void controlPanelResult(Util.DialogType dialogType, boolean settingChanged, boolean restartRequired);

    void openMarkingOptionDialog(Util.DialogType dialogType, int currentSetting);
}
