package com.android.myapplication8.database2.entity;

public interface DeckEntityInterface {

    int getUid();

    String getDeckName();

    void setDeckName(String name);

    long getVisitedDate();

    void setVisitedDate(long visitedDate);

    boolean checkIfSameContentWith(DeckEntityInterface otherDeck);

    int getCardsCount();

//    static boolean checkIfSameContent(DeckEntityInterface deck1, DeckEntityInterface deck2) {
//        return deck1.getDeckName().equals(deck2.getDeckName());
//    }
}
