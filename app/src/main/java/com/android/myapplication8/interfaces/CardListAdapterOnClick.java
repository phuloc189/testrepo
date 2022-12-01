package com.android.myapplication8.interfaces;

import com.android.myapplication8.Util;
import com.android.myapplication8.database2.CardEntity;

public interface CardListAdapterOnClick {
    void cardListAdapterOnItemClick(Util.ClickEvent event, int position , CardEntity card);
}
