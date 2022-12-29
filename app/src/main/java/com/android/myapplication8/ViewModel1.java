package com.android.myapplication8;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.android.myapplication8.database2.entity.CardEntity;
import com.android.myapplication8.database2.entity.CollectionEntity;
import com.android.myapplication8.database2.entity.CollectionEntityExtra;
import com.android.myapplication8.database2.Database2Wrapper;
import com.android.myapplication8.database2.entity.DeckEntity;
import com.android.myapplication8.database2.entity.DeckEntityExtra;
import com.android.myapplication8.database2.entity.DeckEntityExtra_CollectionCheckList;

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

    MutableLiveData<Util.StudyMode> studyMode;

    LiveData<List<CardEntity>> cardsList;

    // it's actually cardsList but we need this to do stuff like persisting changes for ui data,
    // and persisting change for randomized list, cuz supposedly
    // random list should not be updated on database trigger??? (
    // like how can we even keep the randomized position???)
    MutableLiveData<List<CardEntity>> studyingCardsList;

    MutableLiveData<List<Integer>> indexArray = new MutableLiveData<>();

    LiveData<Integer> filteredStudyingCardsSize;
//    somehow this shit wouldn't trigger if i do this right here instead
//    LiveData<Integer> filteredStudyingCardsSize = Transformations.map(indexArray, this::transforming);

    MutableLiveData<Integer> studyingCardsListPointer;

    SharedPreferences sharedPreferences;

    boolean randomSetting;

    boolean backSideFirstSetting;

    MarkingSettingHelper markingSetting;

    int[] markingStat;

    /**
     * default constructor (when this class was created)
     */
    public ViewModel1(@NonNull Application application) {
        super(application);
        Util.logDebug(TAG, "default constructor called");
        database2 = new Database2Wrapper(application, null);
        init();
    }

    public ViewModel1(@NonNull Application application, Database2Wrapper.Database2Callback callback) {
        super(application);
        Util.logDebug(TAG, "my constructor 2 called");
        database2 = new Database2Wrapper(application, callback);
        init();
    }

    private void init() {
        deckList = new MutableLiveData<>();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        studyingCardsListPointer = new MutableLiveData<>();
        studyingCardsList = new MutableLiveData<>();
        indexArray = new MutableLiveData<>(new ArrayList<>());
        selectedDeckUid = new MutableLiveData<>();
        selectedCollectionUid = new MutableLiveData<>(-1);
        studyMode = new MutableLiveData<>();
        filteredStudyingCardsSize = Transformations.map(indexArray, filteredList -> (filteredList != null) ? filteredList.size() : -1);
    }

    public void setSelectedDeckUid(int uid) {
        this.selectedDeckUid.setValue(uid);
    }

    public int getSelectedDeckUid_Value() {
        return this.selectedDeckUid.getValue();
    }

    //----------- decks
    //----------- decks/create

    public void insertNewDeck_vm(String deckName, Database2Wrapper.Database2Callback callback) {
        database2.insertNewDeck(deckName, callback);
    }
    //----------- decks/read

    public LiveData<DeckEntity> getDeckWithThisId_LiveData_vm(int deckUid) {
        return database2.getDeckWithThisId_LiveData(deckUid);
    }

    public LiveData<List<DeckEntityExtra>> getDecks_WithExtra_LiveData_vm() {
        deckList_withExtra = database2.getDecks_WithExtra_LiveData(
                getSortingType(),
                getOptionDescending());
        return deckList_withExtra;
    }

    public LiveData<List<DeckEntityExtra>> getDecks_WithExtra_LiveData_ForCollection_vm(int collectionUid) {
        deckList_withExtra = database2.getDecks_WithExtra_LiveData_ForCollection(
                getSortingType(),
                getOptionDescending(),
                collectionUid);
        return deckList_withExtra;
    }

    public LiveData<List<DeckEntityExtra_CollectionCheckList>> getDecks_WithExtra_LiveData_CollectionChecklist_vm(int collectionUid) {
        return database2.getDecks_WithExtra_LiveData_CollectionChecklist(collectionUid);
    }

    public void findDecks_WithExtra_vm(String searchString, Database2Wrapper.Database2Callback callback) {
        database2.findDecks_WithExtra(searchString, callback);
    }

    //----------- decks/update

    public void updateDeckVisitedDate_vm(int targetUid, long newVisitedDate, Database2Wrapper.Database2Callback callback) {
        database2.updateDeckVisitedDate(targetUid, newVisitedDate, callback);
    }

    public void updateDeckName_vm(int targetUid, String newDeckName, Database2Wrapper.Database2Callback callback) {
        database2.updateDeckName(targetUid, newDeckName, callback);
    }

    //----------- decks/delete

    public void deleteDeck_vm(int targetUid, Database2Wrapper.Database2Callback callback) {
        database2.deleteDeck(targetUid, callback);
    }

    //----------- decks/other

    public void removeDeckListObservers(LifecycleOwner lifecycleOwner) {
        if (deckList != null) {
            deckList.removeObservers(lifecycleOwner);
        }
        if (deckList_withExtra != null) {
            deckList_withExtra.removeObservers(lifecycleOwner);
        }
    }

    private Util.SortingOptions getSortingType() {
        return Util.getSortingOption(sharedPreferences.getInt(
                getApplication().getString(R.string.pref_key_deck_list_sorting_type),
                Util.SORTING_TYPE_OPTION_DEFAULT_VALUE
        ));
    }

    private boolean getOptionDescending() {
        return sharedPreferences.getBoolean(
                getApplication().getString(R.string.pref_key_deck_list_sorting_descending),
                Util.SORTING_DESCENDING_OPTION_DEFAULT_VALUE
        );
    }

    //----------- cards
    //----------- cards/create

    public void insertNewCard_vm(CardEntity cardEntity, Database2Wrapper.Database2Callback callback) {
        database2.insertNewCard(cardEntity, callback);
    }

    //----------- cards/read

    public LiveData<List<CardEntity>> getCardsFromDeck_LiveData_vm(int deckUid) {
        cardsList = database2.getCardsFromDeck_LiveData(deckUid);
        return cardsList;
    }

    public void getCardsFromCollection_vm(int collectionUid,
                                          Database2Wrapper.Database2Callback_CardsEntity callback) {
        database2.getCardsFromCollection(collectionUid, callback);
    }

    //----------- cards/update

    public void updateCard_vm(CardEntity cardEntity, Database2Wrapper.Database2Callback callback) {
        database2.updateCard(cardEntity, callback);
    }

    //----------- cards/delete

    public void deleteCard_vm(CardEntity cardEntity, Database2Wrapper.Database2Callback callback) {
        database2.deleteCard(cardEntity, callback);
    }

    public void deleteMultipleCards_vm(Integer[] cardUids, Database2Wrapper.Database2Callback callback) {
        database2.deleteMultipleCards(cardUids, callback);
    }

    //--------- collection
    //--------- collection/create

    public void createCollection_vm(String name, Database2Wrapper.Database2Callback callback) {
        database2.createCollection(name, callback);
    }

    //--------- collection/read

    public LiveData<List<CollectionEntityExtra>> getCollections_WithExtra_Livedata_vm() {
        return database2.getCollections_WithExtra_Livedata();
    }

    public LiveData<CollectionEntity> getCollectionWithUid_vm(int collectionUid) {
        return database2.getCollectionWithThisUid(collectionUid);
    }

    //--------- collection/update

    public void renameCollection_vm(int targetUid, String newName, Database2Wrapper.Database2Callback callback) {
        database2.renameCollection(targetUid, newName, callback);
    }

    //--------- collection/delete

    public void deleteCollection_vm(int targetUid, Database2Wrapper.Database2Callback callback) {
        database2.deleteCollection(targetUid, callback);
    }

    //----------- deck-collection-relation
    //----------- deck-collection-relation/create

    public void insertDecksToCollection_vm(int collectionUid, Integer[] deckUidList, Database2Wrapper.Database2Callback callback) {
        database2.insertDecksToCollection(collectionUid, deckUidList, callback);
    }

    //----------- deck-collection-relation/delete

    public void removeDecksFromCollection_vm(int collectionUid, Integer[] deckUidList, Database2Wrapper.Database2Callback callback) {
        database2.removeDecksFromCollection(collectionUid, deckUidList, callback);
    }

    //----------- studying cards

    public void cacheCardsFromSelectedDeck() {
        studyingCardsList.setValue(cardsList.getValue());
    }

    public void cacheCards(List<CardEntity> cardsForStudying) {
        studyingCardsList.setValue(cardsForStudying);
    }

    public void fetchPrefSetting() {
        randomSetting = sharedPreferences.getBoolean(
                getApplication().getString(R.string.pref_key_random_mode_on), false);
        backSideFirstSetting = sharedPreferences.getBoolean(
                getApplication().getString(R.string.pref_key_show_back_side_first), false);
        int markingSetting_int = sharedPreferences.getInt(
                getApplication().getString(R.string.pref_key_limited_marking_value), Util.LIMITED_MARKING_DEFAULT_VALUE);
        markingSetting = new MarkingSettingHelper(markingSetting_int);
    }

    public MarkingSettingHelper getMarkingSetting() {
        return markingSetting;
    }

    /**
     * filter and sort deck
     */
    public void reloadDeck() {
        List<Integer> newIndexArray = new ArrayList<>();
        markingStat = new int[Util.CARD_MARKING_MAX_NUMBER_OF_VALUES];
        List<CardEntity> studyingCardsList_value = studyingCardsList.getValue();
        if (studyingCardsList_value == null || studyingCardsList_value.size() < 1) {
            indexArray.setValue(newIndexArray);
            return;
        }
        int cardsListSize = studyingCardsList_value.size();

        for (int i = 0; i < cardsListSize; i++) {
            if (markingSetting.checkIfMatch(studyingCardsList_value.get(i).getMarking0())) {
                newIndexArray.add(i);
            }
            markingStat[studyingCardsList_value.get(i).getMarking0()]++;
        }
        if (randomSetting && newIndexArray.size() > 0) {
            Collections.shuffle(newIndexArray);
        }

        indexArray.setValue(newIndexArray);
    }

    public List<CardEntity> getStudyingCardsList_Value() {
        return studyingCardsList.getValue();
    }

    public MutableLiveData<List<CardEntity>> getStudyingCardsList() {
        return studyingCardsList;
    }

    //----------- studying cards list pointer

    public void resetStudyingCardsListPointer() {
        studyingCardsListPointer.setValue((getIndexArray_value().size() > 0) ? 0 : -1);
    }

    public MutableLiveData<Integer> getStudyingCardsListPointer() {
        return studyingCardsListPointer;
    }

    public int getStudyingCardsListPointer_Value() {
        return studyingCardsListPointer.getValue();
    }

    public void incrementStudyingCardsListPointer() {
        if (getIndexArray_value().size() == 0) {
            return;
        }
        int pointerValue = studyingCardsListPointer.getValue();
        if (pointerValue < getIndexArray_value().size() - 1) {
            studyingCardsListPointer.setValue(pointerValue + 1);
        }
    }

    public void decrementStudyingCardsListPointer() {
        if (getIndexArray_value().size() == 0) {
            return;
        }
        int pointerValue = studyingCardsListPointer.getValue();
        if (pointerValue > 0) {
            studyingCardsListPointer.setValue(pointerValue - 1);
        }
    }

    //----------------- filtered studying cards list size

    public LiveData<Integer> getFilteredStudyingCardsSize() {
        return filteredStudyingCardsSize;
    }

    //------------- studying cards indexes array

    public List<Integer> getIndexArray_value() {
        return indexArray.getValue();
    }

    public boolean getBackSideFirstSetting() {
        return backSideFirstSetting;
    }

    public int[] getMarkingStat() {
        return markingStat;
    }

    public void shiftMarkingStat(int from, int to) {
        markingStat[from]--;
        markingStat[to]++;
    }

    // ---------- selected deck

    public void setSelectedCollectionUid(int uid) {
        selectedCollectionUid.setValue(uid);
    }

    public int getSelectedCollectionUid_Value() {
        return selectedCollectionUid.getValue();
    }

    public boolean isInCollectionMode() {
        return (selectedCollectionUid.getValue() > 0);
    }

    // ---------- study mode

    public void setStudyMode(Util.StudyMode mode) {
        studyMode.setValue(mode);
    }

    public Util.StudyMode getStudyMode_value() {
        return studyMode.getValue();
    }

    //----------- factory

    public static class MyVmFactory3 extends ViewModelProvider.AndroidViewModelFactory {
        Application application;
        Database2Wrapper.Database2Callback callback;

        public MyVmFactory3(Application application, Database2Wrapper.Database2Callback callback) {
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
