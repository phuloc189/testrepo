package com.android.myapplication8.database2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CardDao {

    @Insert
    long insert(CardEntity cardEntity);

    @Update
    int update(CardEntity cardEntity);

    @Delete
    int delete(CardEntity cardEntity);

    @Query("SELECT * FROM table_CardEntity")
    LiveData<List<CardEntity>> getAllCardsLiveData();

    @Query("SELECT * FROM table_CardEntity WHERE deckUid = :deckUid")
    LiveData<List<CardEntity>> getAllCardsLiveDataFromADeck(int deckUid);
}
