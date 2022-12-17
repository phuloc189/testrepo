package com.android.myapplication8.database2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

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

    //--------------- for searching with deck name
    @Query("SELECT * FROM table_DeckEntity WHERE deckName LIKE '%' || :searchString || '%'")
    List<DeckEntity> findDecks(String searchString);

    @Query("SELECT * FROM table_DeckEntity WHERE deckName LIKE '%' || :searchString || '%'")
    LiveData<List<DeckEntity>> findDecks_Livedata(String searchString);
    //---------------
    //--------------- for reading with sorted option

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

    @Query("SELECT table_DeckEntity.*, COUNT(table_cardentity.deckUid) as cardsCount " +
            "FROM table_DeckEntity LEFT OUTER JOIN table_cardentity ON table_DeckEntity.uid = table_cardentity.deckUid " +
            "GROUP BY table_DeckEntity.uid" )
    LiveData<List<DeckEntity>> getAllLiveData_experimental();//todo: experimental

    @Query("SELECT table_DeckEntity.*, COUNT(table_cardentity.deckUid) as cardsCount " +
            "FROM table_DeckEntity LEFT OUTER JOIN table_cardentity ON table_DeckEntity.uid = table_cardentity.deckUid " +
            "GROUP BY table_DeckEntity.uid" )
    LiveData<List<DeckEntityExtra>> getAllLiveData_experimental2();//todo: experimental

    @RawQuery(observedEntities = DeckEntity.class)
    LiveData<List<DeckEntity>> getAllLiveData_raw(SupportSQLiteQuery query);

    //---------------
    //--------------- new method for new dataclass

    /*
    todo: query for new data class
        create, read, update, delete???
        create (insert)
            insert: we're doing insert by name, so no need for new method???
            read: definitely need new method for new dataclass, which include:
                read, with sorting
                search method
            update
                currently updating target is identified by uid, so no need???
            delete
                currently deleting target is identified by uid, so no need???

     */

    @RawQuery(observedEntities = DeckEntity.class)
    LiveData<List<DeckEntityExtra>> getAllDeckEntitiesPlus_Livedata_rawQuery(SupportSQLiteQuery query);

    @Query("SELECT table_DeckEntity.*, COUNT(table_cardentity.deckUid) as cardsCount " +
            "FROM table_DeckEntity LEFT OUTER JOIN table_cardentity ON table_DeckEntity.uid = table_cardentity.deckUid " +
            "WHERE deckName LIKE '%' || :searchString || '%' " +
            "GROUP BY table_DeckEntity.uid")
    List<DeckEntityExtra> findDeckEntitiesPlus(String searchString);


    @Query("SELECT table_DeckEntity.*, COUNT(table_cardentity.deckUid) as cardsCount " +
            "FROM table_DeckEntity " +
            "LEFT OUTER JOIN table_cardentity ON table_DeckEntity.uid = table_cardentity.deckUid " +
            "JOIN table_CollectionDeckMapping ON table_DeckEntity.uid = table_CollectionDeckMapping.deckUid and table_CollectionDeckMapping.collectionUid = :collectionUid " +
            "GROUP BY table_DeckEntity.uid" )
    LiveData<List<DeckEntityExtra>> getAllLiveDataExtra_forCollection(int collectionUid);


    @Query("SELECT table_DeckEntity.*, table_CollectionDeckMapping.collectionUid as collectionUid " +
            "FROM table_DeckEntity " +
            "LEFT OUTER JOIN table_CollectionDeckMapping ON table_DeckEntity.uid = table_CollectionDeckMapping.deckUid " +
            "and table_CollectionDeckMapping.collectionUid = :collectionUid ")
    LiveData<List<DeckEntityExtra_CollectionCheckList>> getAllLiveData_CollectionChecklist(int collectionUid);






    //---------------
    //---------------

    @Query("UPDATE table_DeckEntity SET visitedDate = :newVisitedDate WHERE uid = :targetUid")
    int updateVisitedDate(int targetUid, long newVisitedDate);

    @Query("UPDATE table_DeckEntity SET visitedDate = :newVisitedDate, deckName = :newDeckName WHERE uid = :targetUid")
    int updateDeckName(int targetUid, String newDeckName, long newVisitedDate);

    @Query("DELETE FROM table_DeckEntity WHERE uid = :targetUid")
    int delete(int targetUid);


}
