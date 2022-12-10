package com.android.myapplication8.database2;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_CollectionEntity")
public class CollectionEntity implements CollectionEntityInterface{
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @NonNull
    public String collectionName;

    public CollectionEntity() {

    }

    @Ignore CollectionEntity(String name) {
        collectionName = name;
    }

    @Override
    public int getUid() {
        return uid;
    }

    @Override
    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public void setCollectionName(String name) {
        collectionName = name;
    }

    @Override
    public int getDeckCount() {
        return 0;
    }

    @Override
    public void setDeckCount(int deckCount) {

    }

    @Override
    public boolean checkIfSameContentWith(CollectionEntityInterface otherObject) {
        return this.collectionName.equals(otherObject.getCollectionName());
    }
}
