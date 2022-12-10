package com.android.myapplication8.database2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CollectionDao {

    @Query("DELETE FROM table_collectionentity WHERE uid = :targetUid")
    int delete(int targetUid);

    @Insert
    long insert(CollectionEntity collection);

    @Query("UPDATE table_CollectionEntity SET collectionName = :newName WHERE uid = :targetUid")
    int updateCollectionName(int targetUid, String newName);

    @Query("SELECT * FROM table_CollectionEntity")
    public LiveData<CollectionEntity> getAllCollectionLivedata();

    @Query("SELECT * FROM table_CollectionEntity")
    public LiveData<List<CollectionEntityExtra>> getAllCollectionExtraLivedata();

    //todo: a get method with deck number in it

}
