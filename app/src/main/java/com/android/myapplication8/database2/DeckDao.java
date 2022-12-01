package com.android.myapplication8.database2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DeckDao {

    /**
     * returned value is a rowId
     */
    @Insert
    long insert(DeckEntity deck);

    /**
     * An @Update method can optionally return an int
     * value indicating the number of rows that were updated successfully.
     */
    @Update
    int update(DeckEntity deck);

    /**
     * A @Delete method can optionally return an int
     * value indicating the number of rows that were deleted successfully
     */
    @Delete
    int delete(DeckEntity deck);

    //--------------

    @Query("SELECT * FROM table_DeckEntity")
    List<DeckEntity> getAll();

    @Query("SELECT * FROM table_DeckEntity WHERE deckName LIKE '%' || :searchString || '%'")
    List<DeckEntity> findDecks(String searchString);

    @Query("SELECT * FROM table_DeckEntity WHERE deckName LIKE '%' || :searchString || '%'")
    LiveData<List<DeckEntity>> findDecks_Livedata(String searchString);

    @Query("SELECT * FROM table_deckentity")
    LiveData<List<DeckEntity>> getAllLiveData();

    @Query("SELECT * FROM table_deckentity ORDER BY uid DESC")
    LiveData<List<DeckEntity>> getAllLiveData_Sorted_Uid_Desc();

    @Query("SELECT * FROM table_deckentity ORDER BY visitedDate ASC")
    LiveData<List<DeckEntity>> getAllLiveData_Sorted_VisitDate_Asc();

    @Query("SELECT * FROM table_deckentity ORDER BY visitedDate DESC")
    LiveData<List<DeckEntity>> getAllLiveData_Sorted_VisitDate_Desc();

    @Query("SELECT * FROM table_deckentity ORDER BY deckName ASC")
    LiveData<List<DeckEntity>> getAllLiveData_Sorted_Name_Asc();

    @Query("SELECT * FROM table_deckentity ORDER BY deckName DESC")
    LiveData<List<DeckEntity>> getAllLiveData_Sorted_Name_Desc();

    //---------------

    @Query("UPDATE table_DeckEntity SET visitedDate = :newVisitedDate WHERE uid = :targetUid")
    int updateVisitedDate(int targetUid, long newVisitedDate);

    @Query("UPDATE table_DeckEntity SET visitedDate = :newVisitedDate, deckName = :newDeckName WHERE uid = :targetUid")
    int updateDeckName(int targetUid, String newDeckName, long newVisitedDate);

    @Query("DELETE FROM table_DeckEntity WHERE uid = :targetUid")
    int delete(int targetUid);


}
