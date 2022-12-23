package com.android.myapplication8.database2;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.android.myapplication8.Util;
import com.android.myapplication8.database2.dao.CardDao;
import com.android.myapplication8.database2.dao.CollectionDao;
import com.android.myapplication8.database2.dao.CollectionToDeckMapDao;
import com.android.myapplication8.database2.dao.DeckDao;
import com.android.myapplication8.database2.entity.CardEntity;
import com.android.myapplication8.database2.entity.CollectionEntity;
import com.android.myapplication8.database2.entity.CollectionEntityExtra;
import com.android.myapplication8.database2.entity.CollectionToDeckMap;
import com.android.myapplication8.database2.entity.DeckEntity;
import com.android.myapplication8.database2.entity.DeckEntityExtra;
import com.android.myapplication8.database2.entity.DeckEntityExtra_CollectionCheckList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database2Wrapper {

    private static final String TAG = "Database2Wrapper";

    private Database2 database2Instance;

    private Database2Callback callback;

    public static enum DbTask {
        DB_TASK_NONE,
        DB_TASK_INSERT_DECK,
        DB_TASK_INSERT_DECK_USING_THIS_NAME,
        DB_TASK_UPDATE_DECK,
        DB_TASK_UPDATE_DECK_VISITED_DATE,
        DB_TASK_UPDATE_DECK_NAME,
        DB_TASK_DELETE_DECK,
        DB_TASK_DELETE_DECK_WITH_UID,
        DB_TASK_READ_DECKS_LIVEDATA,
        DB_TASK_SEARCH_DECKS,
        DB_TASK_GET_DECK_WITH_ID,
        DB_TASK_INSERT_CARD,
        DB_TASK_UPDATE_CARD,
        DB_TASK_DELETE_CARD,
        DB_TASK_READ_CARDS_LIVEDATA,
        DB_TASK_READ_CARDS_FROM_DECK_LIVEDATA,
        DB_TASK_CREATE_COLLECTION,
        DB_TASK_READ_COLLECTION,
        DB_TASK_RENAME_COLLECTION,
        DB_TASK_DELETE_COLLECTION,
        DB_TASK_ADD_DECKS_TO_COLLECTION,
        DB_TASK_REMOVE_DECKS_FROM_COLLECTION,
        DB_TASK_FETCH_CARDS_FROM_COLLECTION
    }

    public static enum DbTaskResult {
        DB_RESULT_OK,
        DB_RESULT_NG
    }

    public interface Database2Callback{
        void onComplete_SimpleResult(DbTask whichTask, DbTaskResult taskResult);

        void onSearchDeckComplete(DbTask whichTask, List<DeckEntity> deckSearchResult);

        void onSearchDeckCompleteExtra(DbTask whichTask, List<DeckEntityExtra> deckSearchResult);

        void onInsertComplete(DbTask whichTask, long newRowId);

        void onGetDeckResult(DbTask whichTask, DeckEntity deck);
    }

    public interface Database2Callback_CardsEntity {
        void onComplete_FetchingCards(DbTask whichTask, List<CardEntity> cardsFetchResult);
    }

//    private static final int NUMBER_OF_THREADS = 4;
//    private static final ExecutorService dbExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    public Database2Wrapper(Context context, Database2Callback callback) {
        database2Instance = Database2.getInstance(context);
        this.callback = callback;
    }

    public DeckDao deckDaoAlias() {
        return database2Instance.deckDao();
    }

    public CardDao cardDaoAlias() {
        return database2Instance.cardDao();
    }

    public CollectionDao collectionDaoAlias() {
        return database2Instance.collectionDao();
    }

    public CollectionToDeckMapDao collectionToDeckMapDaoAlias() {
        return database2Instance.collectionToDeckMapDao();
    }

    //------------

    public void insertNewDeck(DeckEntity deck, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long rowId = deckDaoAlias().insert(deck);
                Util.logDebug(TAG, "insertNewDeck with result: " + rowId);
                if (rowId < 0) {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_INSERT_DECK, DbTaskResult.DB_RESULT_NG);
                } else {
//                    callback.onComplete_SimpleResult(DbTask.DB_TASK_INSERT_DECK, DbTaskResult.DB_RESULT_OK);
                    callback.onInsertComplete(DbTask.DB_TASK_INSERT_DECK, rowId);
                }
            }
        });
    }

    public void updateDeck(DeckEntity deck, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = deckDaoAlias().update(deck);
                if (result <= 0) {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_UPDATE_DECK, DbTaskResult.DB_RESULT_NG);
                } else {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_UPDATE_DECK, DbTaskResult.DB_RESULT_OK);
                }
            }
        });
    }

    public void deleteDeck(DeckEntity deck, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = deckDaoAlias().delete(deck);
                if (result <= 0) {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_DELETE_DECK, DbTaskResult.DB_RESULT_NG);
                } else {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_DELETE_DECK, DbTaskResult.DB_RESULT_OK);
                }
            }
        });
    }

    public LiveData<List<DeckEntity>> readAll() {
        return deckDaoAlias().getAllLiveData();
    }

    public LiveData<List<DeckEntity>> readAll_Sorted_Uid_Desc() {
        return deckDaoAlias().getAllLiveData_Sorted_Uid_Desc();
    }

    public LiveData<List<DeckEntity>> readAll_Sorted_VisitDate_Asc() {
        return deckDaoAlias().getAllLiveData_Sorted_VisitDate_Asc();
    }

    public LiveData<List<DeckEntity>> readAll_Sorted_VisitDate_Desc() {
        return deckDaoAlias().getAllLiveData_Sorted_VisitDate_Desc();
    }

    public LiveData<List<DeckEntity>> readAll_Sorted_Name_Asc() {
        return deckDaoAlias().getAllLiveData_Sorted_Name_Asc();
    }

    public LiveData<List<DeckEntity>> readAll_Sorted_Name_Desc() {
        return deckDaoAlias().getAllLiveData_Sorted_Name_Desc();
    }

    public LiveData<List<DeckEntity>> findDecks_Livedata(String searchString) {
        return deckDaoAlias().findDecks_Livedata(searchString);
    }

    //----------------

    public LiveData<DeckEntity> getDeckWithId_LiveData(int deckUid) {
        return deckDaoAlias().getDeckWithId_LiveData(deckUid);
    }

//    public LiveData<List<DeckEntity>> readAllLiveData_experimental() {
//        return deckDaoAlias().getAllLiveData_experimental();
//    }

    public LiveData<List<DeckEntityExtra>> getAllLiveData_experimental2() {
        return deckDaoAlias().getAllLiveData_experimental2();
    }

    public LiveData<List<DeckEntityExtra>> getAllLiveDataExtra_forCollection(int collectionUid) {
        return deckDaoAlias().getAllLiveDataExtra_forCollection(collectionUid);
    }

    public LiveData<List<DeckEntityExtra_CollectionCheckList>> getAllLiveData_CollectionChecklist(int collectionUid) {
        return deckDaoAlias().getAllLiveData_CollectionChecklist(collectionUid);
    }

    public LiveData<List<DeckEntityExtra>> getAllLiveData_raw_extra(Util.SortingOptions optionSortingType
            , boolean optionDescending) {
        /*    @Query("SELECT table_DeckEntity.*, COUNT(table_cardentity.deckUid) as cardsCount " +
            "FROM table_DeckEntity LEFT OUTER JOIN table_cardentity ON table_DeckEntity.uid = table_cardentity.deckUid " +
            "GROUP BY table_DeckEntity.uid" )
         */
        StringBuilder sb = new StringBuilder("SELECT table_DeckEntity.*");
        sb.append(", COUNT(table_cardentity.deckUid) as cardsCount")
                .append(" FROM table_DeckEntity").append(" LEFT OUTER JOIN table_cardentity")
                .append(" ON table_DeckEntity.uid = table_cardentity.deckUid")
                .append(" GROUP BY table_DeckEntity.uid");

        switch (optionSortingType) {
            case ALPHABET_ORDER:
                sb.append(" ORDER BY").append(" deckName");
                break;
            case VISITED_ORDER:
                sb.append(" ORDER BY").append(" visitedDate");
                break;
            case CREATION_ORDER:
            default:
                sb.append(" ORDER BY").append(" uid");
        }

        if (optionDescending) {
            sb.append(" DESC");
        } else {
            sb.append(" ASC");
        }

        sb.append(";");

        return deckDaoAlias().getAllDeckEntitiesPlus_Livedata_rawQuery(new SimpleSQLiteQuery(sb.toString()));
    }

    public LiveData<List<DeckEntity>> getAllLiveData_raw() {
        //@Query("SELECT * FROM table_deckentity ORDER BY :field DESC")
        StringBuilder sb = new StringBuilder("SELECT * FROM").append(" table_deckentity");
        sb.append(" ORDER BY").append(" uid").append(" DESC;");

        return deckDaoAlias().getAllLiveData_raw(new SimpleSQLiteQuery(sb.toString()));
    }

    public void findDecksExtra(String searchString, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<DeckEntityExtra> result = deckDaoAlias().findDeckEntitiesPlus(searchString);
                callback.onSearchDeckCompleteExtra(DbTask.DB_TASK_SEARCH_DECKS, result);
            }
        });
    }

    public void findDecks(String searchString, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<DeckEntity> result = deckDaoAlias().findDecks(searchString);
                callback.onSearchDeckComplete(DbTask.DB_TASK_SEARCH_DECKS, result);
            }
        });
    }

    public void updateDeckVisitedDate(int targetUid, long newVisitedDate, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = deckDaoAlias().updateVisitedDate(targetUid, newVisitedDate);
                if (result <= 0) {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_UPDATE_DECK_VISITED_DATE, DbTaskResult.DB_RESULT_NG);
                } else {
                    Util.logDebug(TAG, "updated with result: " + result);
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_UPDATE_DECK_VISITED_DATE, DbTaskResult.DB_RESULT_OK);
                }
            }
        });
    }

    public void updateDeckName(int targetUid, String newDeckName, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = deckDaoAlias().updateDeckName(targetUid, newDeckName, Calendar.getInstance().getTimeInMillis());
                if (result <= 0) {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_UPDATE_DECK_NAME, DbTaskResult.DB_RESULT_NG);
                } else {
                    Util.logDebug(TAG, "updated with result: " + result);
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_UPDATE_DECK_NAME, DbTaskResult.DB_RESULT_OK);
                }
            }
        });

    }

    public void deleteDeck(int targetUid, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = deckDaoAlias().delete(targetUid);
                if (result <= 0) {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_DELETE_DECK_WITH_UID, DbTaskResult.DB_RESULT_NG);
                } else {
                    Util.logDebug(TAG, "deleted with result: " + result);
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_DELETE_DECK_WITH_UID, DbTaskResult.DB_RESULT_OK);
                }
            }
        });
    }

    public void insertNewDeck(String deckName, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DeckEntity newDeck = new DeckEntity(
                        deckName, Calendar.getInstance().getTimeInMillis());
                long rowId = deckDaoAlias().insert(newDeck);
                Util.logDebug(TAG, "insertNewDeck with result: " + rowId);
                if (rowId < 0) {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_INSERT_DECK_USING_THIS_NAME, DbTaskResult.DB_RESULT_NG);
                } else {
//                    callback.onComplete_SimpleResult(DbTask.DB_TASK_INSERT_DECK, DbTaskResult.DB_RESULT_OK);
                    callback.onInsertComplete(DbTask.DB_TASK_INSERT_DECK_USING_THIS_NAME, rowId);
                }
            }
        });
    }

    //------------ cards method

    public void insertNewCard(CardEntity cardEntity, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long rowId = cardDaoAlias().insert(cardEntity);
                DbTaskResult dbResult = (rowId == -1)? DbTaskResult.DB_RESULT_NG : DbTaskResult.DB_RESULT_OK;
                callback.onComplete_SimpleResult(DbTask.DB_TASK_INSERT_CARD, dbResult);
            }
        });
    }

    public void updateCard(CardEntity cardEntity, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = cardDaoAlias().update(cardEntity);
                DbTaskResult dbResult = (result == 0)? DbTaskResult.DB_RESULT_NG : DbTaskResult.DB_RESULT_OK;
                callback.onComplete_SimpleResult(DbTask.DB_TASK_UPDATE_CARD, dbResult);
            }
        });
    }

    public void deleteCard(CardEntity cardEntity, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = cardDaoAlias().delete(cardEntity);
                DbTaskResult dbResult = (result == 0)? DbTaskResult.DB_RESULT_NG : DbTaskResult.DB_RESULT_OK;
                callback.onComplete_SimpleResult(DbTask.DB_TASK_DELETE_CARD, dbResult);
            }
        });
    }

    public LiveData<List<CardEntity>> readAllCardsLiveData() {
        return cardDaoAlias().getAllCardsLiveData();
    }

    public LiveData<List<CardEntity>> readAllCardsFromDeckLiveData(int deckUid) {
        return cardDaoAlias().getAllCardsLiveDataFromDeck(deckUid);
    }

    public void getAllCardsFromCollection(int collectionUid, Database2Callback_CardsEntity callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<CardEntity> result = cardDaoAlias().getAllCardsFromCollection(collectionUid);
                callback.onComplete_FetchingCards(DbTask.DB_TASK_FETCH_CARDS_FROM_COLLECTION, result);
            }
        });
    }

    //----------- collection ----------------

    public LiveData<List<CollectionEntityExtra>> getAllCollectionExtraLivedata() {
        return collectionDaoAlias().getAllCollectionExtraLivedata2();
    }

    public LiveData<CollectionEntity> getCollectionWithUid(int collectionUid){
        return collectionDaoAlias().getCollectionWithUid(collectionUid);
    }

    public void deleteCollection (int targetUid, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = collectionDaoAlias().delete(targetUid);
                callback.onComplete_SimpleResult(DbTask.DB_TASK_DELETE_COLLECTION,
                        (result <= 0 ? DbTaskResult.DB_RESULT_NG: DbTaskResult.DB_RESULT_OK));
            }
        });
    }

    public void renameCollection (int targetUid, String newName, Database2Callback callback){
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = collectionDaoAlias().updateCollectionName(targetUid, newName);
                callback.onComplete_SimpleResult(DbTask.DB_TASK_RENAME_COLLECTION,
                        (result <= 0 ? DbTaskResult.DB_RESULT_NG: DbTaskResult.DB_RESULT_OK));
            }
        });
    }

    public void createCollection(String name, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CollectionEntity collection = new CollectionEntity(name);
                Util.logDebug(TAG, "new name: " + name);
                long result = collectionDaoAlias().insert(collection);
                callback.onInsertComplete(DbTask.DB_TASK_CREATE_COLLECTION,
                        result);
            }
        });
    }

    // ------------  collection-deck map  --------------

    public void insertDecksToCollection(int collectionUid, Integer[] deckUidList, Database2Callback callback) {
        if (deckUidList == null || deckUidList.length == 0){
            callback.onComplete_SimpleResult(DbTask.DB_TASK_ADD_DECKS_TO_COLLECTION,
                    DbTaskResult.DB_RESULT_NG);
            return;
        }
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<CollectionToDeckMap> mapList = new ArrayList<>();
                for (Integer deckUid: deckUidList) {
                    mapList.add(new CollectionToDeckMap(collectionUid, deckUid));
                }
                List<Long> results = collectionToDeckMapDaoAlias().insertListOfDecks(mapList);

                if (results != null && results.size()> 0) {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_ADD_DECKS_TO_COLLECTION,
                            DbTaskResult.DB_RESULT_OK);
                } else {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_ADD_DECKS_TO_COLLECTION,
                            DbTaskResult.DB_RESULT_NG);
                }
            }
        });
    }

    public void removeDecksFromCollection(int collectionUid, Integer[] deckUidList, Database2Callback callback) {
        if (deckUidList == null || deckUidList.length == 0){
            callback.onComplete_SimpleResult(DbTask.DB_TASK_REMOVE_DECKS_FROM_COLLECTION,
                    DbTaskResult.DB_RESULT_NG);
            return;
        }
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<CollectionToDeckMap> mapList = new ArrayList<>();
                for (Integer deckUid: deckUidList) {
                    mapList.add(new CollectionToDeckMap(collectionUid, deckUid));
                }
                int results = collectionToDeckMapDaoAlias().deleteListOfDecks(mapList);

                if (results > 0) {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_REMOVE_DECKS_FROM_COLLECTION,
                            DbTaskResult.DB_RESULT_OK);
                } else {
                    callback.onComplete_SimpleResult(DbTask.DB_TASK_REMOVE_DECKS_FROM_COLLECTION,
                            DbTaskResult.DB_RESULT_NG);
                }
            }
        });
    }


}
