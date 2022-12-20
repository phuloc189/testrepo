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

    @Query("SELECT table_CollectionEntity.*, COUNT(table_CollectionDeckMapping.deckUid) as deckCount" +
            " FROM table_CollectionEntity " +
            " left outer JOIN table_CollectionDeckMapping " +
            " ON table_CollectionEntity.uid = table_CollectionDeckMapping.collectionUid" +
            " group by table_CollectionEntity.uid")
    LiveData<List<CollectionEntityExtra>> getAllCollectionExtraLivedata2();

    @Query("SELECT * FROM table_CollectionEntity WHERE uid = :collectionUid")
    LiveData<CollectionEntity> getCollectionWithUid(int collectionUid);

}
