package com.android.myapplication8.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.myapplication8.CustomAdapterDecklist_Extra;
import com.android.myapplication8.database2.entity.CollectionEntity;
import com.android.myapplication8.database2.entity.DeckEntityExtra;
import com.android.myapplication8.interfaces.ConfirmDialogCallback;
import com.android.myapplication8.interfaces.DialogResultCallback;
import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.ViewModel1;
import com.android.myapplication8.database2.Database2Wrapper;
import com.android.myapplication8.database2.entity.DeckEntity;
import com.android.myapplication8.interfaces.DeckListSortSettingCallback;
import com.android.myapplication8.ui.dialog.DialogFragmentConfirm;
import com.android.myapplication8.ui.dialog.DialogFragmentDeckListSortOption;
import com.android.myapplication8.ui.dialog.DialogFragmentSimpleNameEdit;

import java.util.ArrayList;
import java.util.List;

public class FragmentDeckList extends Fragment implements
        DialogResultCallback, ConfirmDialogCallback, DeckListSortSettingCallback {
    public static final String TAG = "FragmentDeckList";

    RecyclerView recyclerView;

    ViewModel1 viewModel;

    int longClickedDeckUid;

    String longClickedDeckName;

    Fragment1Interface callBack = null;

    Database2Wrapper.Database2Callback database2Callback;

    Button buttonCreateNewDeck;

    TextView textView_currentCollectionInfo;

    boolean searchMode = false;

    int longClickedItemPosition;

    int justInsertedDeckUid;

    String currentCollectionName;

    public interface Fragment1Interface {
        void onDeckSelected();
        void onAddRemoveDeckTransition();
        void moveToStudyMode_ForCollection();
    }

    public FragmentDeckList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.logDebug(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Util.logDebug(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_deck_list, container, false);

        setupViewModel();
        setupDatabaseCallback();
        setupListUi(view);
        setupUi(view);
        readDatabaseLiveData();


        return view;
    }

    private void setupViewModel(){
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel1.class);
    }

    private void setupDatabaseCallback() {
        database2Callback = new Database2Wrapper.Database2Callback() {
            @Override
            public void onComplete_SimpleResult(Database2Wrapper.DbTask whichTask, Database2Wrapper.DbTaskResult taskResult) {
                Util.logDebug(TAG, "db task complete: " + whichTask);
                onDbTaskComplete(whichTask, taskResult);
            }

            @Override
            public void onSearchDeckCompleteExtra(Database2Wrapper.DbTask whichTask, List<DeckEntityExtra> deckSearchResult) {
                Util.logDebug(TAG, "db task complete: " + whichTask);
                Util.logDebug(TAG, "onSearchDeckCompleteExtra: " + deckSearchResult.size());
                onNewDeckListFromDatabase2(deckSearchResult);
            }

            @Override
            public void onInsertComplete(Database2Wrapper.DbTask whichTask, long newRowId) {
                Util.logDebug(TAG, "db task complete: " + whichTask);
                //  todo: choose between int or long???
                askUserIfTheyAlsoWantToOpenDeck((int) newRowId);
            }
        };
    }

    private void setupListUi(View view) {
        recyclerView = view.findViewById(R.id.recyclerView_item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(new CustomAdapterDecklist_Extra(
                new CustomAdapterDecklist_Extra.ItemDiff(),
                new CustomAdapterDecklist_Extra.CustomAdapterDecklist_ExtraCallback() {
                    @Override
                    public void onItemClick(Util.ClickEvent event, int position) {
                        handleDeckListItemClick(event, position);
                    }
                }));
    }

    private void setupUi(View view) {
        buttonCreateNewDeck = view.findViewById(R.id.button_add_item_to_decks_list);
        Button buttonAddRemoveDeck = view.findViewById(R.id.button_decks_list_add_remove_existing_deck_for_collection);
        Button buttonCollectionStudy = view.findViewById(R.id.button_decks_list_collection_study);
        SearchView searchView = view.findViewById(R.id.searchView_deck_list);
        textView_currentCollectionInfo = view.findViewById(R.id.tv_deckScreen_currentCollectionInfo);

        buttonCreateNewDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_CreateNewDeck();
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Util.logDebug(TAG, "on search UI Close");
                handleSearchClose();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Util.logDebug(TAG, "onQueryTextSubmit: " + query);
                searchForDecks(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Util.logDebug(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });

        if (!viewModel.isInCollectionMode()){
            view.findViewById(R.id.linearLayout_uiGroup_collection).setVisibility(View.GONE);
            textView_currentCollectionInfo.setVisibility(View.GONE);
        } else {
            searchView.setVisibility(View.GONE);
            buttonAddRemoveDeck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.onAddRemoveDeckTransition();
                }
            });
            buttonCollectionStudy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewModel.setStudyMode(Util.StudyMode.COLLECTION);
                    callBack.moveToStudyMode_ForCollection();
                }
            });
        }
    }

    private void readDatabaseLiveData() {
        Util.logDebug(TAG, "readDatabaseLiveData: " );
        /**
         * "this observer will be notified about modifications of the
         * wrapped data only if the paired LifecycleOwner is in
         * active state. LifecycleOwner is considered as active
         * , if its state is STARTED or RESUMED"
         *  -> ASSUMPTION: no need to manually remove observer
         */
//        viewModel.readAll_vm().observe(
//                getViewLifecycleOwner(),
//                new Observer<List<DeckEntity>>() {
//                    @Override
//                    public void onChanged(List<DeckEntity> deckEntities) {
//                        Util.logDebug(TAG, "auto update from database");
//                        onNewDeckListFromDatabase(deckEntities);
//                    }
//                });

        Util.logDebug(TAG, "selected collection: " + viewModel.getSelectedCollectionUid_Value());
        if (viewModel.isInCollectionMode()) {
            viewModel.getDecks_WithExtra_LiveData_ForCollection_vm(viewModel.getSelectedCollectionUid_Value()).observe(
                    getViewLifecycleOwner(),
                    new Observer<List<DeckEntityExtra>>() {
                        @Override
                        public void onChanged(List<DeckEntityExtra> deckEntityExtras) {
                            onNewDeckListFromDatabase2(deckEntityExtras);
                        }
                    }
            );
            viewModel.getCollectionWithUid_vm(viewModel.getSelectedCollectionUid_Value()).observe(
                    getViewLifecycleOwner(),
                    new Observer<CollectionEntity>() {
                        @Override
                        public void onChanged(CollectionEntity collectionEntity) {
                            currentCollectionName  = collectionEntity.getCollectionName();
                            textView_currentCollectionInfo.setText(getString(R.string.tv_current_collection_info, collectionEntity.getCollectionName()));
                        }
                    }
            );
        } else {
            viewModel.getDecks_WithExtra_LiveData_vm().observe(
                    getViewLifecycleOwner(),
                    new Observer<List<DeckEntityExtra>>() {
                        @Override
                        public void onChanged(List<DeckEntityExtra> deckEntityExtras) {
                            onNewDeckListFromDatabase2(deckEntityExtras);
                        }
                    }
            );
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            callBack = (Fragment1Interface) context;
        } catch (Exception  e) {
            Util.logError(TAG, "exception happened: " + e);
            callBack = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callBack = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupOptionMenu();
    }

    private void setupOptionMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                Util.logDebug(TAG, "on create menu");
                menuInflater.inflate(R.menu.menu_deck_screen_option, menu);
                if (viewModel.isInCollectionMode()) {
                    menuInflater.inflate(R.menu.menu_deck_screen_option_collection_mode, menu);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_item_sorting_option){
                    showDialog_DeckSortOption();
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_item_collection_rename_option_menu) {
                    showDialog_CollectionRename();
                    return true;
                } else {
                    return false;
                }
            }
        }, getViewLifecycleOwner());

    }

    private void askUserIfTheyAlsoWantToOpenDeck(int newRowId) {
        justInsertedDeckUid = newRowId;
        showDialog_ConfirmDialog(Util.DialogType.CONFIRM_OPEN_DECK_JUST_CREATED, null);
    }

    private void onNewDeckListFromDatabase2(List<DeckEntityExtra> deckEntityExtras) {
        if (deckEntityExtras != null && deckEntityExtras.size() > 0){
            Util.logDebug(TAG, "live data list update");
            recyViewAdapterAlias().submitList(deckEntityExtras);
        } else {
            if (deckEntityExtras == null)
                // todo: display error dialog
                Util.logDebug(TAG, "live data list update error: null list");
            else {
                Util.logDebug(TAG, "live data list update error: no item on list");
                recyViewAdapterAlias().submitList(deckEntityExtras);
            }
        }
    }

    private void transitionToSearchMode(){
        searchMode = true;
        buttonCreateNewDeck.setVisibility(View.GONE);
        viewModel.removeDeckListObservers(getViewLifecycleOwner());
    }

    private void transitionToNormalMode() {
        searchMode = false;
        buttonCreateNewDeck.setVisibility(View.VISIBLE);
        readDatabaseLiveData();
    }

    private void handleSearchClose() {
        if (searchMode) {
            transitionToNormalMode();
        }
    }

    private void searchForDecks(String query) {
        if (!searchMode) {
            transitionToSearchMode();
        }
        viewModel.findDecks_WithExtra_vm(query, database2Callback);
    }

    private void handleDeckListItemClick(Util.ClickEvent event, int position) {
        Util.logDebug(TAG, "item clicked " + event);
        if (event == Util.ClickEvent.LONG_CLICK){
            RecyclerView.ViewHolder viewHolder =
                    recyclerView.findViewHolderForLayoutPosition(position);
            if (viewHolder == null) {
                Util.logDebug(TAG, "cannot find view holder");
                return;
            }
            longClickedDeckUid = recyViewAdapterAlias().getCurrentList().get(position).getUid();
            longClickedDeckName = recyViewAdapterAlias().getCurrentList().get(position).getDeckName();
            longClickedItemPosition = position;
            showDeckItemPopupMenu(viewHolder.itemView);
        } else if (event == Util.ClickEvent.CLICK){
            longClickedDeckUid = recyViewAdapterAlias().getCurrentList().get(position).getUid();
            longClickedDeckName = recyViewAdapterAlias().getCurrentList().get(position).getDeckName();
            showDialog_ConfirmDialog(Util.DialogType.CONFIRM_OPEN_DECK, longClickedDeckName);
        }
    }

    private void showDeckItemPopupMenu(View v){
        PopupMenu popMenu = new PopupMenu(requireContext(), v);
        popMenu.inflate(R.menu.deck_item_option_menu);
        popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                /*
                 * return true if the event was handled, false otherwise
                 */
                return handleDeckItemPopupMenuSelected(menuItem);
            }
        });
        popMenu.show();
    }

    private boolean handleDeckItemPopupMenuSelected(MenuItem item) {
        /*
         *      "Resource IDs will be non-final by default in Android Gradle
         *      Plugin version 8.0, avoid using them in switch case statement""
         *
         *  possible answer:
         *      https://stackoverflow.com/questions/64335374/how-to-resolve-resource-ids-will-be-non-final-in-android-gradle-plugin-version
         */
        if (item.getItemId() == R.id.menu_item_deck_delete){
            showDialog_ConfirmDialog(Util.DialogType.CONFIRM_DECK_DELETE, null);
            /*
             * "return true if the event was handled, false otherwise"
             * (interface rule)
             */
            return true;
        }
        else if (item.getItemId() == R.id.menu_item_deck_rename){
            showDialog_DeckRename();
            return true;
        }
        else {
            return false;
        }
    }

    private void showDialog_DeckSortOption() {
        DialogFragmentDeckListSortOption dialog = DialogFragmentDeckListSortOption.newInstance(Util.DialogType.DECK_LIST_SORT_OPTION);
        dialog.show(getChildFragmentManager(), DialogFragmentDeckListSortOption.TAG);
    }

    private void showDialog_CreateNewDeck() {
        DialogFragmentSimpleNameEdit newDeckDialogFragment =
                DialogFragmentSimpleNameEdit.newInstance(Util.DialogType.NEW_DECK_NAME, null);
        newDeckDialogFragment.show(getChildFragmentManager(), DialogFragmentSimpleNameEdit.TAG);
    }

    private void showDialog_DeckRename() {
        DialogFragmentSimpleNameEdit newDeckDialogFragment =
                DialogFragmentSimpleNameEdit.newInstance(Util.DialogType.DECK_RENAME, longClickedDeckName);
        newDeckDialogFragment.show(getChildFragmentManager(), DialogFragmentSimpleNameEdit.TAG);
    }

    private void showDialog_CollectionRename() {
        DialogFragmentSimpleNameEdit dialog =
                DialogFragmentSimpleNameEdit.newInstance(Util.DialogType.COLLECTION_RENAME, currentCollectionName);
        dialog.show(getChildFragmentManager(), DialogFragmentSimpleNameEdit.TAG);
    }

    private void showDialog_ConfirmDialog(Util.DialogType confirmDialogType, String stringParam) {
        DialogFragmentConfirm dialogFragment =
                DialogFragmentConfirm.newInstance(confirmDialogType, stringParam);
        dialogFragment.show(getChildFragmentManager(), DialogFragmentConfirm.TAG);
    }

    private CustomAdapterDecklist_Extra recyViewAdapterAlias() {
        return (CustomAdapterDecklist_Extra) recyclerView.getAdapter();
    }

    @Override
    public void onDialogResult_Confirm(Util.DialogType dialogType) {
        Util.logDebug(TAG, "dialog confirm result");
        Util.logDebug(TAG, "type: " + dialogType);
        if (dialogType == Util.DialogType.CONFIRM_DECK_DELETE) {
            Util.logDebug(TAG, "delete deck");
            deleteDeck(longClickedDeckUid);
        } else if (dialogType == Util.DialogType.CONFIRM_OPEN_DECK) {
            Util.logDebug(TAG, "open deck");
            openDeck(longClickedDeckUid);
        } else if (dialogType == Util.DialogType.CONFIRM_OPEN_DECK_JUST_CREATED) {
            Util.logDebug(TAG, "open just created deck");
            openDeck(justInsertedDeckUid);
        } else {
            Util.logDebug(TAG, "what the hell was that");
        }
    }

    @Override
    public void onDialogResult_NewText(Util.DialogType dialogType, String text) {
        Util.logDebug(TAG, "relay data from dialog");
        Util.logDebug(TAG, "type: " + dialogType);
        Util.logDebug(TAG, "text: " + text);
        if (dialogType == Util.DialogType.NEW_DECK_NAME) {
            insertNewDeck(text);
        } else if (dialogType == Util.DialogType.DECK_RENAME) {
            renameDeck(longClickedDeckUid, text);
        } else if (dialogType == Util.DialogType.COLLECTION_RENAME) {
            renameCollection(text);
        } else {
            Util.logDebug(TAG, "what the hell was that");
        }
    }

    private void deleteDeck(int targetUid) {
        viewModel.deleteDeck_vm(targetUid, database2Callback);
        if (searchMode) {
            List<DeckEntityExtra> currentList = new ArrayList<>(recyViewAdapterAlias().getCurrentList());
            currentList.remove(longClickedItemPosition);
            recyViewAdapterAlias().submitList(currentList);
            recyViewAdapterAlias().notifyItemRemoved(longClickedItemPosition);
        }
    }

    private void openDeck(int deckUid) {
        viewModel.setSelectedDeckUid(deckUid);
        if (callBack != null) {
            callBack.onDeckSelected();
        }
    }

    private void insertNewDeck(String content) {
        viewModel.insertNewDeck_vm(content, database2Callback);
    }

    private void renameDeck(int deckUidInQuestion, String newName) {
        viewModel.updateDeckName_vm(deckUidInQuestion, newName, database2Callback);

        if (searchMode) {
            List<DeckEntityExtra> currentList = new ArrayList<>(recyViewAdapterAlias().getCurrentList());
            currentList.get(longClickedItemPosition).setDeckName(newName);
            recyViewAdapterAlias().submitList(currentList);
            recyViewAdapterAlias().notifyItemChanged(longClickedItemPosition);
        }
    }

    private void renameCollection (String newName) {
        viewModel.renameCollection_vm(viewModel.getSelectedCollectionUid_Value(),
                newName, database2Callback);
    }

    private void onDbTaskComplete(Database2Wrapper.DbTask whichTask
            , Database2Wrapper.DbTaskResult taskResult){
        /**
         *  todo:
         *      display dialog in case of error???
         *      ...what else???
         */
        Util.logDebug(TAG, "completed database task: " + whichTask +
                ", with result: " + taskResult);
    }

    @Override
    public void onDeckListSortDialogResult(Util.DialogType dialogType, boolean sortSettingChanged) {
        Util.logDebug(TAG, "onDeckListSortDialogResult: " + sortSettingChanged);
        if (sortSettingChanged) {
            readDatabaseLiveData();
        }
    }

    //////////////////////////////////

    @Override
    public void onStart() {
        super.onStart();
        Util.logDebug(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Util.logDebug(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Util.logDebug(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Util.logDebug(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Util.logDebug(TAG, "onDestroy: ");
    }
}


