package com.android.myapplication8.database2.entity;

public class CollectionEntityExtra extends CollectionEntity{

    public int deckCount;

    @Override
    public int getDeckCount() {
        return deckCount;
    }

    @Override
    public void setDeckCount(int deckCount) {
        this.deckCount = deckCount;
    }

    @Override
    public boolean checkIfSameContentWith(CollectionEntityInterface otherObject) {
        //todo: re enable this, later
//        return super.checkIfSameContentWith(otherObject)
//                && this.deckCount == otherObject.getDeckCount();
        return super.checkIfSameContentWith(otherObject);
    }
}
