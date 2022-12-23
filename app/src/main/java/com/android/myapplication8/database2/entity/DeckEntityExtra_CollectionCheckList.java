package com.android.myapplication8.database2.entity;

public class DeckEntityExtra_CollectionCheckList extends DeckEntity {
    public int collectionUid;

    public int getCollectionUid() {
        return collectionUid;
    }

    public void setCollectionUid(int collectionUid) {
        this.collectionUid = collectionUid;
    }

    /*
        todo: experimenting: let's see how it fares when we skip comparison of
         check list value (which is a visible property on list)
     */
}
