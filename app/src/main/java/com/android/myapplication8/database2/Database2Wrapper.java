package com.android.myapplication8.database2;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.android.myapplication8.Util;

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
        DB_TASK_SEARCH_DECK,
        DB_TASK_INSERT_CARD,
        DB_TASK_UPDATE_CARD,
        DB_TASK_DELETE_CARD,
        DB_TASK_READ_CARDS_LIVEDATA,
        DB_TASK_READ_CARDS_FROM_DECK_LIVEDATA
    }

    public static enum DbTaskResult {
        DB_RESULT_OK,
        DB_RESULT_NG
    }

    public interface Database2Callback{
        void onComplete_SimpleResult(DbTask whichTask, DbTaskResult taskResult);

        void onSearchDeckComplete(DbTask whichTask, List<DeckEntity> deckSearchResult);

        void onInsertComplete(DbTask whichTask, long newRowId);
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

//    public void insertNewDeck(DeckEntity deck) {
//        dbExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                long rowId = deckDaoAlias().insert(deck);
//                if (callback == null){
//                    return;
//                }
//                if (rowId == -1) {
//                    callback.onComplete_SimpleResult(DbTask.DB_TASK_INSERT_DECK, DbTaskResult.DB_RESULT_NG);
//                } else {
//                    callback.onComplete_SimpleResult(DbTask.DB_TASK_INSERT_DECK, DbTaskResult.DB_RESULT_OK);
//                }
//            }
//        });
//    }
//
//    public void updateDeckName(DeckEntity deck) {
//        dbExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                int result = deckDaoAlias().update(deck);
//                if (callback == null){
//                    return;
//                }
//                if (result <= 0) {
//                    callback.onComplete_SimpleResult(DbTask.DB_TASK_UPDATE_DECK, DbTaskResult.DB_RESULT_NG);
//                } else {
//                    callback.onComplete_SimpleResult(DbTask.DB_TASK_UPDATE_DECK, DbTaskResult.DB_RESULT_OK);
//                }
//            }
//        });
//    }
//
//    public void deleteDeck(DeckEntity deck) {
//        dbExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                int result = deckDaoAlias().delete(deck);
//                if (callback == null){
//                    return;
//                }
//                if (result == 0) {
//                    callback.onComplete_SimpleResult(DbTask.DB_TASK_DELETE_DECK, DbTaskResult.DB_RESULT_NG);
//                } else {
//                    callback.onComplete_SimpleResult(DbTask.DB_TASK_DELETE_DECK, DbTaskResult.DB_RESULT_OK);
//                }
//            }
//        });
//    }

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

    public void findDecks(String searchString, Database2Callback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<DeckEntity> result = deckDaoAlias().findDecks(searchString);
                callback.onSearchDeckComplete(DbTask.DB_TASK_SEARCH_DECK, result);
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
        return cardDaoAlias().getAllCardsLiveDataFromADeck(deckUid);
    }

}
