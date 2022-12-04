package com.android.myapplication8.database2;


public class DeckEntityExtra extends DeckEntity {

    public int cardsCount;

    public DeckEntityExtra(){
        super();
    }

    @Override
    public int getUid() {
        return super.getUid();
    }

    @Override
    public String getDeckName() {
        return super.getDeckName();
    }

    @Override
    public void setDeckName(String name) {
        super.setDeckName(name);
    }

    @Override
    public long getVisitedDate() {
        return super.getVisitedDate();
    }

    @Override
    public void setVisitedDate(long visitedDate) {
        super.setVisitedDate(visitedDate);
    }

    @Override
    public boolean checkIfSameContentWith(DeckEntityInterface otherDeck) {
//        return super.checkIfSameContentWith(otherDeck);
        return this.getDeckName().equals(otherDeck.getDeckName())
                && this.getCardsCount() == otherDeck.getCardsCount();
    }

    @Override
    public int getCardsCount() {
        return cardsCount;
    }
}
