package com.android.myapplication8.database2.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "table_CardEntity",
foreignKeys = {@ForeignKey(entity = DeckEntity.class,
parentColumns = "uid",
childColumns = "deckUid",
onDelete = ForeignKey.CASCADE)})
public class CardEntity {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    private int deckUid;

    private String frontText;

    private String backText;

    private int marking0;

    private long modifiedDate;

    @Ignore
    public CardEntity(int deckUid, String frontText, String backText){
        this.deckUid = deckUid;
        this.frontText = frontText;
        this.backText = backText;

        this.marking0 = 4;
        this.modifiedDate = Calendar.getInstance().getTimeInMillis();
    }

    public CardEntity(){

    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getDeckUid() {
        return deckUid;
    }

    public void setDeckUid(int deckUid) {
        this.deckUid = deckUid;
    }

    public String getFrontText() {
        return frontText;
    }

    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }

    public String getBackText() {
        return backText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public int getMarking0() {
        return marking0;
    }

    public void setMarking0(int marking0) {
        this.marking0 = marking0;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public static boolean checkIfSameContent(CardEntity obj1, CardEntity obj2) {
        return obj1.getFrontText().equals(obj2.getFrontText())
                && obj1.getBackText().equals(obj2.getBackText())
                && obj1.getMarking0() == obj2.getMarking0();
    }
}
