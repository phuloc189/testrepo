package com.android.myapplication8.database2.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.android.myapplication8.database2.entity.DeckEntity;
import com.android.myapplication8.database2.entity.DeckEntityExtra;
import com.android.myapplication8.database2.entity.DeckEntityExtra_CollectionCheckList;

import java.util.List;

@Dao
public interface DeckDao {

    /**
     * @Insert method returned value is a rowId
     *
     * @Update, @Delete method can optionally return an int
     * value indicating the number of rows that were updated/deleted successfully.
     */
    @Insert
    long insert(DeckEntity deck);

    //--------------- queries
    //--------------- read

    @RawQuery(observedEntities = DeckEntity.class)
    LiveData<List<DeckEntityExtra>> getAllDeckEntities_WithExtra_Livedata_RawQuery(SupportSQLiteQuery query);

    @Query("SELECT table_DeckEntity.*, table_CollectionDeckMapping.collectionUid as collectionUid " +
            "FROM table_DeckEntity " +
            "LEFT OUTER JOIN table_CollectionDeckMapping ON table_DeckEntity.uid = table_CollectionDeckMapping.deckUid " +
            "and table_CollectionDeckMapping.collectionUid = :collectionUid ")
    LiveData<List<DeckEntityExtra_CollectionCheckList>> getDeckEntities_WithExtra_Livedata_CollectionChecklist(int collectionUid);

    @Query("SELECT table_DeckEntity.*, COUNT(table_cardentity.deckUid) as cardsCount " +
            "FROM table_DeckEntity LEFT OUTER JOIN table_cardentity ON table_DeckEntity.uid = table_cardentity.deckUid " +
            "WHERE deckName LIKE '%' || :searchString || '%' " +
            "GROUP BY table_DeckEntity.uid")
    List<DeckEntityExtra> findDeckEntities_WithExtra(String searchString);


    @Query("SELECT * FROM table_DeckEntity " +
            "WHERE uid = :deckUid")
    LiveData<DeckEntity> getDeckWithThisId_LiveData(int deckUid);

    //--------------- update

    @Query("UPDATE table_DeckEntity SET visitedDate = :newVisitedDate WHERE uid = :targetUid")
    int updateVisitedDate(int targetUid, long newVisitedDate);

    @Query("UPDATE table_DeckEntity SET visitedDate = :newVisitedDate, deckName = :newDeckName WHERE uid = :targetUid")
    int updateDeckName(int targetUid, String newDeckName, long newVisitedDate);

    //--------------- delete

    @Query("DELETE FROM table_DeckEntity WHERE uid = :targetUid")
    int delete(int targetUid);

}
