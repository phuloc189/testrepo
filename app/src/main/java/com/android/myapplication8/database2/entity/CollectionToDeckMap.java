package com.android.myapplication8.database2.entity;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

@Entity(tableName = "table_CollectionDeckMapping",
        primaryKeys = {"collectionUid", "deckUid"},
        foreignKeys = {
                @ForeignKey(entity = CollectionEntity.class,
                        parentColumns = "uid",
                        childColumns = "collectionUid",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = DeckEntity.class,
                        parentColumns = "uid",
                        childColumns = "deckUid",
                        onDelete = ForeignKey.CASCADE)})
public class CollectionToDeckMap {

    public CollectionToDeckMap(){

    }

    @Ignore
    public CollectionToDeckMap(int collectionUid, int deckUid) {
        this.collectionUid = collectionUid;
        this.deckUid = deckUid;
    }

    public int collectionUid;
    public int deckUid;
}
