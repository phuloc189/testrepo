package com.android.myapplication8.database2.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_DeckEntity")
public class DeckEntity implements DeckEntityInterface {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @NonNull
    @ColumnInfo(name = "deckName")
    public String deckName;

    public long visitedDate;

    public DeckEntity(int uid, String deckName, long visitedDate) {
        this.uid = uid;
        this.deckName = deckName;
        this.visitedDate = visitedDate;
    }

    @Ignore
    public DeckEntity(String deckName, long visitedDate) {
        this.deckName = deckName;
        this.visitedDate = visitedDate;
    }

    @Ignore
    public DeckEntity() {

    }

    //----------------------

    @Override
    public int getUid() {
        return this.uid;
    }

    @Override
    public String getDeckName() {
        return this.deckName;
    }

    @Override
    public void setDeckName(String name) {
        this.deckName = name;
    }

    @Override
    public long getVisitedDate() {
        return visitedDate;
    }

    @Override
    public void setVisitedDate(long visitedDate) {
        this.visitedDate = visitedDate;
    }

    @Override
    public boolean checkIfSameContentWith(DeckEntityInterface otherDeck) {
//        return false;
        return this.getDeckName().equals(otherDeck.getDeckName());
    }

    @Override
    public int getCardsCount() {
        return 0;
    }

    //----------------------

    public static boolean checkIfSameContent(DeckEntity deck1, DeckEntity deck2) {
        return deck1.getDeckName().equals(deck2.getDeckName());
    }
}
