package com.android.myapplication8.database2.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.android.myapplication8.database2.entity.CollectionEntity;
import com.android.myapplication8.database2.entity.CollectionEntityExtra;

import java.util.List;

@Dao
public interface CollectionDao {

    @Insert
    long insert(CollectionEntity collection);

    //--------------- queries
    //--------------- read

    @Query("SELECT table_CollectionEntity.*, COUNT(table_CollectionDeckMapping.deckUid) as deckCount" +
            " FROM table_CollectionEntity " +
            " left outer JOIN table_CollectionDeckMapping " +
            " ON table_CollectionEntity.uid = table_CollectionDeckMapping.collectionUid" +
            " group by table_CollectionEntity.uid")
    LiveData<List<CollectionEntityExtra>> getAllCollectionExtraLivedata2();

    @Query("SELECT * FROM table_CollectionEntity WHERE uid = :collectionUid")
    LiveData<CollectionEntity> getCollectionWithUid(int collectionUid);

    //--------------- delete

    @Query("DELETE FROM table_collectionentity WHERE uid = :targetUid")
    int delete(int targetUid);

    //--------------- update

    @Query("UPDATE table_CollectionEntity SET collectionName = :newName WHERE uid = :targetUid")
    int updateCollectionName(int targetUid, String newName);

}
