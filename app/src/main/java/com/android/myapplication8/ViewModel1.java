package com.android.myapplication8;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.android.myapplication8.database2.CardEntity;
import com.android.myapplication8.database2.CollectionEntityExtra;
import com.android.myapplication8.database2.Database2Wrapper;
import com.android.myapplication8.database2.DeckEntity;
import com.android.myapplication8.database2.DeckEntityExtra;
import com.android.myapplication8.database2.DeckEntityExtra_CollectionCheckList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewModel1 extends AndroidViewModel {

    public static final String TAG = "ViewModel1";

    private Database2Wrapper database2;

    LiveData<List<DeckEntity>> deckList;

    LiveData<List<DeckEntityExtra>> deckList_withExtra;

    MutableLiveData<Integer> selectedDeckUid;

    MutableLiveData<Integer> selectedCollectionUid;

    LiveData<List<CardEntity>> cardsList;

    // it's actually cardsList but we need this to do stuff like persisting changes for ui data,
    // and persisting change for randomized list, cuz supposedly random list should not be updated (with changes) from database??? (
    // like how can we even keep the randomized position???)
    MutableLiveData<List<CardEntity>> studyingCardsList;

    List<Integer> indexArrays;

    MutableLiveData<Integer> studyingCardsListPointer;

    SharedPreferences sharedPreferences;

    boolean randomSetting;

    boolean backSideFirstSetting;

    MarkingSettingHelperType markingSetting;

    int [] markingStat;

    /**
     * default constructor (when this class was created)
     */
    public ViewModel1(@NonNull Application application) {
        super(application);
        Util.logDebug(TAG, "default constructor called");
        database2 = new Database2Wrapper(application, null);
        init();
    }

    public ViewModel1(@NonNull Application application, Database2Wrapper.Database2Callback callback){
        super(application);
        Util.logDebug(TAG, "my constructor 2 called");
        database2 = new Database2Wrapper(application, callback);
        init();
    }

    private void init() {
        deckList = new MutableLiveData<>();//todo: experimental
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        studyingCardsListPointer = new MutableLiveData<>();
        studyingCardsList = new MutableLiveData<>();
        indexArrays = new ArrayList<>();
        selectedDeckUid = new MutableLiveData<>();
        selectedCollectionUid = new MutableLiveData<>(-1);
    }

    public void setSelectedDeckUid(int uid) {
        this.selectedDeckUid.setValue(uid);
    }

    public int getSelectedDeckUid_Value() {
        return this.selectedDeckUid.getValue();
    }

    //----------- decks

    public void insertNewDeck_vm(String deckName, Database2Wrapper.Database2Callback callback) {
        database2.insertNewDeck(deckName, callback);
    }

    public void deleteDeck_vm(int targetUid, Database2Wrapper.Database2Callback callback) {
        database2.deleteDeck(targetUid, callback);
    }

    public LiveData<List<DeckEntity>> readAll_vm() {
        //--------
        Util.SortingOptions optionSortingType = Util.getSortingOption(sharedPreferences.getInt(
                getApplication().getString(R.string.pref_key_deck_list_sorting_type),
                Util.SORTING_TYPE_OPTION_DEFAULT_VALUE
        ));
        boolean optionDescending = sharedPreferences.getBoolean(
                getApplication().getString(R.string.pref_key_deck_list_sorting_descending),
                Util.SORTING_DESCENDING_OPTION_DEFAULT_VALUE
        );
//        if (optionDescending) {
//            switch (optionSortingType) {
//                case ALPHABET_ORDER:
//                    deckList = database2.readAll_Sorted_Name_Desc();
//                    break;
//                case VISITED_ORDER:
//                    deckList = database2.readAll_Sorted_VisitDate_Desc();
//                    break;
//                case CREATION_ORDER:
//                default:
//                    deckList = database2.readAll_Sorted_Uid_Desc();
//            }
//        } else {
//            switch (optionSortingType) {
//                case ALPHABET_ORDER:
//                    deckList = database2.readAll_Sorted_Name_Asc();
//                    break;
//                case VISITED_ORDER:
//                    deckList = database2.readAll_Sorted_VisitDate_Asc();
//                    break;
//                case CREATION_ORDER:
//                default:
//                    deckList = database2.readAll();
//            }
//        }
        //todo: experimenting
//        deckList = database2.readAllLiveData_experimental();
        deckList = database2.getAllLiveData_raw();

        //don't enable this, not part of experiment
//        deckList = database2.readAll();
        return deckList;
    }

    public LiveData<List<DeckEntityExtra>> getAllLiveData_experimental2_vm() {
//        return database2.getAllLiveData_experimental2();

        Util.SortingOptions optionSortingType = Util.getSortingOption(sharedPreferences.getInt(
                getApplication().getString(R.string.pref_key_deck_list_sorting_type),
                Util.SORTING_TYPE_OPTION_DEFAULT_VALUE
        ));
        boolean optionDescending = sharedPreferences.getBoolean(
                getApplication().getString(R.string.pref_key_deck_list_sorting_descending),
                Util.SORTING_DESCENDING_OPTION_DEFAULT_VALUE
        );
        deckList_withExtra = database2.getAllLiveData_raw_extra(optionSortingType, optionDescending);
        return deckList_withExtra;
    }

    public LiveData<List<DeckEntityExtra>> getAllLiveDataExtra_forCollection(int collectionUid) {
        //todo: decklist_withextra assignment
        return database2.getAllLiveDataExtra_forCollection(collectionUid);
    }

    public LiveData<List<DeckEntityExtra_CollectionCheckList>> getAllLiveData_CollectionChecklist_vm(int collectionUid) {
        return database2.getAllLiveData_CollectionChecklist(collectionUid);
    }

    public void insertDecksToCollection_vm(int collectionUid, Integer[] deckUidList, Database2Wrapper.Database2Callback callback) {
        database2.insertDecksToCollection(collectionUid, deckUidList, callback);
    }

    public void removeDecksFromCollection_vm(int collectionUid, Integer[] deckUidList, Database2Wrapper.Database2Callback callback) {
        database2.removeDecksFromCollection(collectionUid, deckUidList, callback);
    }

    public void removeDeckListObservers(LifecycleOwner lifecycleOwner) {
        if (deckList != null) {
            deckList.removeObservers(lifecycleOwner);
        }
    }

    public void findDecksExtra_vm (String searchString, Database2Wrapper.Database2Callback callback) {
        database2.findDecksExtra(searchString, callback);
    }

    public void findDecks_vm (String searchString, Database2Wrapper.Database2Callback callback) {
        database2.findDecks(searchString, callback);
    }

    public void updateDeckVisitedDate_vm(int targetUid, long newVisitedDate, Database2Wrapper.Database2Callback callback) {
        database2.updateDeckVisitedDate(targetUid, newVisitedDate, callback);
    }

    public void updateDeckName_vm(int targetUid, String newDeckName, Database2Wrapper.Database2Callback callback) {
        database2.updateDeckName(targetUid, newDeckName, callback);
    }

    //----------- cards

    public void insertNewCard_vm(CardEntity cardEntity, Database2Wrapper.Database2Callback callback) {
        database2.insertNewCard(cardEntity, callback);
    }

    public void updateCard_vm(CardEntity cardEntity, Database2Wrapper.Database2Callback callback) {
        database2.updateCard(cardEntity, callback);
    }

    public void deleteCard_vm(CardEntity cardEntity, Database2Wrapper.Database2Callback callback) {
        database2.deleteCard(cardEntity, callback);
    }

    public LiveData<List<CardEntity>> readAllCardsLiveData_vm() {
        cardsList = database2.readAllCardsLiveData();
        return cardsList;
    }

    public LiveData<List<CardEntity>> readAllCardsFromDeckLiveData_vm(int deckUid) {
        cardsList = database2.readAllCardsFromDeckLiveData(deckUid);
        return cardsList;
    }

    public LiveData<List<CardEntity>> getCardsList_vm() {
        return cardsList;
    }

    //-----------

    public void cacheSelectedDeck() {
        List<CardEntity> studyingCardsList_value = cardsList.getValue();
        studyingCardsList.setValue(studyingCardsList_value);

        //todo: handle case where there are no card to display
//        int cardsListSize = studyingCardsList_value.size();
//
//        indexArrays = new ArrayList<>();
//        for (int i = 0; i < cardsListSize; i++ ) {
//            if (markingSetting.checkIfMarkingEnabled(studyingCardsList_value.get(i).getMarking0())){
//                indexArrays.add(i);
//            }
//        }
//        if (randomSetting && indexArrays.size() > 0) {
//            Collections.shuffle(indexArrays);
//        }
    }

    public void fetchPrefSetting() {
        randomSetting = sharedPreferences.getBoolean(
                getApplication().getString(R.string.pref_key_random_mode_on), false);
        backSideFirstSetting = sharedPreferences.getBoolean(
                getApplication().getString(R.string.pref_key_show_back_side_first), false);
        int markingSetting_int = sharedPreferences.getInt(
                getApplication().getString(R.string.pref_key_limited_marking_value), Util.LIMITED_MARKING_DEFAULT_VALUE);
        markingSetting = new MarkingSettingHelperType(markingSetting_int);
    }

    /**
     * filter and sort deck
     */
    public void reloadDeck() {
        indexArrays.clear();
        markingStat = new int[Util.CARD_MARKING_MAX_NUMBER_OF_VALUES];
        List<CardEntity> studyingCardsList_value = cardsList.getValue();
        if (studyingCardsList_value == null || studyingCardsList_value.size() == 0) {
            return;
        }
        int cardsListSize = studyingCardsList_value.size();

        for (int i = 0; i < cardsListSize; i++ ) {
            if (markingSetting.checkIfMarkingEnabled(studyingCardsList_value.get(i).getMarking0())){
                indexArrays.add(i);
            }
            markingStat[studyingCardsList_value.get(i).getMarking0()]++;
        }
        if (randomSetting && indexArrays.size() > 0) {
            Collections.shuffle(indexArrays);
        }
    }

    public List<CardEntity> getStudyingCardsList_Value() {
        return studyingCardsList.getValue();
    }

    public MutableLiveData<List<CardEntity>> getStudyingCardsList() {
        return studyingCardsList;
    }

    //-----------

    public void resetCardListPointer() {
        studyingCardsListPointer.setValue((indexArrays.size() > 0)?0:-1);
    }

    public MutableLiveData<Integer> getStudyingCardsListPointer() {
        return studyingCardsListPointer;
    }

    public int getStudyingCardsListPointer_Value() {
        return studyingCardsListPointer.getValue();
    }

    public void incrementStudyingCardsListPointer() {
        if (indexArrays.size() == 0){
            return;
        }
        int pointerValue = studyingCardsListPointer.getValue();
        if (pointerValue < indexArrays.size() - 1) {
            studyingCardsListPointer.setValue(pointerValue + 1);
        }
    }

    public void decrementStudyingCardsListPointer() {
        if (indexArrays.size() == 0){
            return;
        }
        int pointerValue = studyingCardsListPointer.getValue();
        if (pointerValue > 0) {
            studyingCardsListPointer.setValue(pointerValue - 1);
        }
    }

    //-------------

    public List<Integer> getIndexArrays() {
        return indexArrays;
    }

    public boolean getBackSideFirstSetting() {
        return backSideFirstSetting;
    }

    public int[] getMarkingStat() {
        return markingStat;
    }

    public void shiftMarkingStat(int from, int to){
        markingStat[from]--;
        markingStat[to]++;
    }

    //--------- collection

    public LiveData<List<CollectionEntityExtra>> getAllCollectionExtraLivedata_vm() {
        return database2.getAllCollectionExtraLivedata();
    }

    public void deleteCollection_vm (int targetUid, Database2Wrapper.Database2Callback callback){
        database2.deleteCollection(targetUid, callback);
    }

    public void renameCollection_vm (int targetUid, String newName, Database2Wrapper.Database2Callback callback) {
        database2.renameCollection(targetUid, newName, callback);
    }

    public void  createCollection_vm (String name, Database2Wrapper.Database2Callback callback) {
        database2.createCollection(name, callback);
    }

    // ---------------

    public void setSelectedCollectionUid(int uid) {
        selectedCollectionUid.setValue(uid);
    }

    public int getSelectedCollectionUid_Value() {
        return selectedCollectionUid.getValue();
    }

    //----------- factory

    public static class MyVmFactory3 extends ViewModelProvider.AndroidViewModelFactory{
        Application application;
        Database2Wrapper.Database2Callback callback;

        public MyVmFactory3 (Application application, Database2Wrapper.Database2Callback callback) {
            this.application = application;
            this.callback = callback;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ViewModel1(this.application, this.callback);
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass, @NonNull CreationExtras extras) {
            /**
             * somehow platform will call this "create" method,
             * but we aren't using this feature, so we reroute it
             */
            return MyVmFactory3.this.create(modelClass);
        }
    }

}
