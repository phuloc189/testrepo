package com.android.myapplication8.database2;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_DeckEntity")
public class DeckEntity {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @NonNull
    @ColumnInfo(name = "deckName")
    public String deckName;

    public long visitedDate;

    public DeckEntity(int uid, String deckName, long visitedDate){
        this.uid = uid;
        this.deckName = deckName;
        this.visitedDate = visitedDate;
    }

    @Ignore
    public DeckEntity(String deckName, long visitedDate) {
        this.deckName = deckName;
        this.visitedDate = visitedDate;
    }

    //----------------------

    public int getUid(){
        return this.uid;
    }

    public String getDeckName() {
        return this.deckName;
    }

    public void setDeckName(String name) {
        this.deckName = name;
    }

    public long getVisitedDate() {
        return visitedDate;
    }

    public void setVisitedDate(long visitedDate) {
        this.visitedDate = visitedDate;
    }

    //----------------------

    public static boolean checkIfSameContent(DeckEntity deck1, DeckEntity deck2) {
//        return
//                deck1.getUid() == deck2.getUid()
//                && deck1.getDeckName().equals(deck2.getDeckName());
        return deck1.getDeckName().equals(deck2.getDeckName());
    }

    //----------------------

//    public DeckEntity(int uid, String deckName){
//        this.uid = uid;
//        this.deckName = deckName;
//    }

    //todo: delete
//    @Ignore
//    public DeckEntity(String deckName) {
//        this.deckName = deckName;
//    }
}
