package com.android.myapplication8.database1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemClass1Dao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertAll(ItemClass1 ... items);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(ItemClass1 item);

    @Delete
    void delete(ItemClass1 item);

    @Query("SELECT * FROM table_ItemClass1")
    List<ItemClass1> getAll();

    @Query("SELECT * FROM table_ItemClass1")
    LiveData<List<ItemClass1>> getAllLiveData();
}
