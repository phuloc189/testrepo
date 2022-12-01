package com.android.myapplication8.database2;

public interface DeckEntityInterface {

    int getUid();

    String getDeckName();

    void setDeckName(String name);

    long getVisitedDate();

    void setVisitedDate(long visitedDate);
}
