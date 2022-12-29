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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database2Wrapper {

    private static final String TAG = "Database2Wrapper";

    private Database2 database2Instance;

    private Database2Callback callback;

    public enum DbTask {
        NONE,
        CARD_DELETE,
        CARD_DELETE_MULTIPLE_CARDS,
        CARD_INSERT,
        CARD_READ_FROM_COLLECTION,
        CARD_UPDATE,
        COLLECTION_CREATE,
        COLLECTION_DELETE,
        COLLECTION_RENAME,
        DECK_DELETE_WITH_UID,
        DECK_INSERT_USING_THIS_NAME,
        DECK_SEARCH,
        DECK_UPDATE_NAME,
        DECK_UPDATE_VISITED_DATE,
        REMOVE_DECKS_FROM_COLLECTION,
        ADD_DECKS_TO_COLLECTION
    }

    public enum DbTaskResult {
        DB_RESULT_OK,
        DB_RESULT_NG,
        DB_RESULT_OTHER
    }

    public interface Database2Callback {
        void onComplete_SimpleResult(DbTask whichTask, DbTaskResult taskResult);

        void onSearchDeckComplete(DbTask whichTask, List<DeckEntity> deckSearchResult);

        void onSearchDeckCompleteExtra(DbTask whichTask, List<DeckEntityExtra> deckSearchResult);

        void onInsertComplete(DbTask whichTask, long newRowId);
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

    //------------ deck
    //------------ deck/read

    public LiveData<DeckEntity> getDeckWithThisId_LiveData(int deckUid) {
        return deckDaoAlias().getDeckWithThisId_LiveData(deckUid);
    }

    public LiveData<List<DeckEntityExtra_CollectionCheckList>> getDecks_WithExtra_LiveData_CollectionChecklist(int collectionUid) {
        return deckDaoAlias().getDeckEntities_WithExtra_Livedata_CollectionChecklist(collectionUid);
    }

    public LiveData<List<DeckEntityExtra>> getDecks_WithExtra_LiveData(Util.SortingOptions optionSortingType
            , boolean optionDescending) {
        return getDecks_WithExtra_LiveData_ForCollection(optionSortingType, optionDescending, 0);
    }

    public LiveData<List<DeckEntityExtra>> getDecks_WithExtra_LiveData_ForCollection(Util.SortingOptions optionSortingType
            , boolean optionDescending, int collectionUid) {
        /*    @Query("SELECT table_DeckEntity.*, COUNT(table_cardentity.deckUid) as cardsCount " +
            "FROM table_DeckEntity LEFT OUTER JOIN table_cardentity ON table_DeckEntity.uid = table_cardentity.deckUid " +
            "GROUP BY table_DeckEntity.uid" )
         */

        StringBuilder sb = new StringBuilder("SELECT table_DeckEntity.*");
        sb.append(", COUNT(table_cardentity.deckUid) as cardsCount")
                .append(" FROM table_DeckEntity")
                .append(" LEFT OUTER JOIN table_cardentity")
                .append(" ON table_DeckEntity.uid = table_cardentity.deckUid");

        if (collectionUid > 0) {
            sb.append(" JOIN table_CollectionDeckMapping")
                    .append(" ON table_DeckEntity.uid = table_CollectionDeckMapping.deckUid")
                    .append(" AND table_CollectionDeckMapping.collectionUid = ")
                    .append(String.valueOf(collectionUid));
        }


        sb.append(" GROUP BY table_DeckEntity.uid");

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

        return deckDaoAlias().getAllDeckEntities_WithExtra_Livedata_RawQuery(new SimpleSQLiteQuery(sb.toString()));
    }

    public void findDecks_WithExtra(String searchString, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<DeckEntityExtra> result = deckDaoAlias().findDeckEntities_WithExtra(searchString);
                callback.onSearchDeckCompleteExtra(DbTask.DECK_SEARCH, result);
            }
        });
    }
    
    //------------ deck/update

    public void updateDeckVisitedDate(int targetUid, long newVisitedDate, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = deckDaoAlias().updateVisitedDate(targetUid, newVisitedDate);
                callback.onComplete_SimpleResult(
                        DbTask.DECK_UPDATE_VISITED_DATE,
                        (result > 0) ? DbTaskResult.DB_RESULT_OK : DbTaskResult.DB_RESULT_NG);
            }
        });
    }

    public void updateDeckName(int targetUid, String newDeckName, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = deckDaoAlias().updateDeckName(targetUid, newDeckName, Calendar.getInstance().getTimeInMillis());

                callback.onComplete_SimpleResult(
                        DbTask.DECK_UPDATE_NAME,
                        (result > 0) ? DbTaskResult.DB_RESULT_OK : DbTaskResult.DB_RESULT_NG);
            }
        });
    }

    //------------ deck/delete

    public void deleteDeck(int targetUid, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = deckDaoAlias().delete(targetUid);
                callback.onComplete_SimpleResult(
                        DbTask.DECK_DELETE_WITH_UID,
                        (result > 0) ? DbTaskResult.DB_RESULT_OK : DbTaskResult.DB_RESULT_NG);
            }
        });
    }

    //------------ deck/create

    public void insertNewDeck(String deckName, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DeckEntity newDeck = new DeckEntity(
                        deckName, Calendar.getInstance().getTimeInMillis());
                long rowId = deckDaoAlias().insert(newDeck);
                Util.logDebug(TAG, "insertNewDeck with result: " + rowId);
                if (rowId < 0) {
                    callback.onComplete_SimpleResult(DbTask.DECK_INSERT_USING_THIS_NAME, DbTaskResult.DB_RESULT_NG);
                } else {
                    callback.onInsertComplete(DbTask.DECK_INSERT_USING_THIS_NAME, rowId);
                }
            }
        });
    }

    //------------ cards
    //------------ cards/create

    public void insertNewCard(CardEntity cardEntity, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long rowId = cardDaoAlias().insert(cardEntity);
                callback.onComplete_SimpleResult(
                        DbTask.CARD_INSERT,
                        (rowId <= 0) ? DbTaskResult.DB_RESULT_NG : DbTaskResult.DB_RESULT_OK);
            }
        });
    }

    //------------ cards/update

    public void updateCard(CardEntity cardEntity, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = cardDaoAlias().update(cardEntity);
                callback.onComplete_SimpleResult(
                        DbTask.CARD_UPDATE,
                        (result <= 0) ? DbTaskResult.DB_RESULT_NG : DbTaskResult.DB_RESULT_OK);
            }
        });
    }

    //------------ cards/delete

    public void deleteCard(CardEntity cardEntity, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = cardDaoAlias().delete(cardEntity);
                callback.onComplete_SimpleResult(
                        DbTask.CARD_DELETE,
                        (result <= 0) ? DbTaskResult.DB_RESULT_NG : DbTaskResult.DB_RESULT_OK);
            }
        });
    }

    public void deleteMultipleCards(Integer[] cardUids, Database2Callback callback) {
        if (cardUids == null || cardUids.length == 0) {
            callback.onComplete_SimpleResult(DbTask.CARD_DELETE_MULTIPLE_CARDS, DbTaskResult.DB_RESULT_OTHER);
            return;
        }
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = cardDaoAlias().deleteMultipleCards(Arrays.asList(cardUids));
                callback.onComplete_SimpleResult(
                        DbTask.CARD_DELETE_MULTIPLE_CARDS,
                        (result == cardUids.length) ? DbTaskResult.DB_RESULT_OK : DbTaskResult.DB_RESULT_NG);
            }
        });
    }

    //------------ cards/read

    public LiveData<List<CardEntity>> getCardsFromDeck_LiveData(int deckUid) {
        return cardDaoAlias().getAllCardsLiveDataFromDeck(deckUid);
    }

    public void getCardsFromCollection(int collectionUid, Database2Callback_CardsEntity callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<CardEntity> result = cardDaoAlias().getAllCardsFromCollection(collectionUid);
                callback.onComplete_FetchingCards(DbTask.CARD_READ_FROM_COLLECTION, result);
            }
        });
    }

    //----------- collection 
    //----------- collection/read

    public LiveData<List<CollectionEntityExtra>> getCollections_WithExtra_Livedata() {
        return collectionDaoAlias().getAllCollectionExtraLivedata2();
    }

    public LiveData<CollectionEntity> getCollectionWithThisUid(int collectionUid) {
        return collectionDaoAlias().getCollectionWithUid(collectionUid);
    }

    //----------- collection/delete

    public void deleteCollection(int targetUid, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = collectionDaoAlias().delete(targetUid);
                callback.onComplete_SimpleResult(DbTask.COLLECTION_DELETE,
                        (result <= 0 ? DbTaskResult.DB_RESULT_NG : DbTaskResult.DB_RESULT_OK));
            }
        });
    }

    //----------- collection/update

    public void renameCollection(int targetUid, String newName, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int result = collectionDaoAlias().updateCollectionName(targetUid, newName);
                callback.onComplete_SimpleResult(DbTask.COLLECTION_RENAME,
                        (result <= 0 ? DbTaskResult.DB_RESULT_NG : DbTaskResult.DB_RESULT_OK));
            }
        });
    }

    //----------- collection/create

    public void createCollection(String name, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CollectionEntity collection = new CollectionEntity(name);
                Util.logDebug(TAG, "new name: " + name);
                long result = collectionDaoAlias().insert(collection);
                callback.onInsertComplete(DbTask.COLLECTION_CREATE,
                        result);
            }
        });
    }

    // ------------  collection-deck map
    // ------------  collection-deck map/create

    public void insertDecksToCollection(int collectionUid, Integer[] deckUidList, Database2Callback callback) {
        if (deckUidList == null || deckUidList.length == 0) {
            callback.onComplete_SimpleResult(DbTask.ADD_DECKS_TO_COLLECTION,
                    DbTaskResult.DB_RESULT_NG);
            return;
        }
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<CollectionToDeckMap> mapList = new ArrayList<>();
                for (Integer deckUid : deckUidList) {
                    mapList.add(new CollectionToDeckMap(collectionUid, deckUid));
                }
                List<Long> results = collectionToDeckMapDaoAlias().insertListOfDecks(mapList);

                callback.onComplete_SimpleResult(
                        DbTask.ADD_DECKS_TO_COLLECTION,
                        (results != null && results.size() > 0) ? DbTaskResult.DB_RESULT_OK : DbTaskResult.DB_RESULT_NG);
            }
        });
    }

    // ------------  collection-deck map/delete

    public void removeDecksFromCollection(int collectionUid, Integer[] deckUidList, Database2Callback callback) {
        if (deckUidList == null || deckUidList.length == 0) {
            callback.onComplete_SimpleResult(DbTask.REMOVE_DECKS_FROM_COLLECTION,
                    DbTaskResult.DB_RESULT_NG);
            return;
        }
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<CollectionToDeckMap> mapList = new ArrayList<>();
                for (Integer deckUid : deckUidList) {
                    mapList.add(new CollectionToDeckMap(collectionUid, deckUid));
                }
                int results = collectionToDeckMapDaoAlias().deleteListOfDecks(mapList);

                callback.onComplete_SimpleResult(
                        DbTask.REMOVE_DECKS_FROM_COLLECTION,
                        (results > 0) ? DbTaskResult.DB_RESULT_OK : DbTaskResult.DB_RESULT_NG);
            }
        });
    }


}
