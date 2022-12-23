package com.android.myapplication8.database2.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.android.myapplication8.database2.entity.CollectionToDeckMap;

import java.util.List;

@Dao
public interface CollectionToDeckMapDao {

    @Insert
    List<Long> insertListOfDecks(List<CollectionToDeckMap> map);

    @Delete
    int deleteListOfDecks(List<CollectionToDeckMap> map);

}
