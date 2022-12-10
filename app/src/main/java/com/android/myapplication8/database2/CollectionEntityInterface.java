package com.android.myapplication8.database2;

public interface CollectionEntityInterface {

    int getUid();

    String getCollectionName();

    void setCollectionName(String name);

    int getDeckCount();

    void setDeckCount(int deckCount);

    boolean checkIfSameContentWith(CollectionEntityInterface otherObject);
}
