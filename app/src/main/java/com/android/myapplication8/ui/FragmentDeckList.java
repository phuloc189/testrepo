package com.android.myapplication8.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

import com.android.myapplication8.CustomAdapterDeckList;
import com.android.myapplication8.interfaces.ConfirmDialogCallback;
import com.android.myapplication8.interfaces.DialogResultCallback;
import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.ViewModel1;
import com.android.myapplication8.database2.Database2Wrapper;
import com.android.myapplication8.database2.DeckEntity;
import com.android.myapplication8.interfaces.DeckListSortSettingCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentDeckList extends Fragment implements
        DialogResultCallback, ConfirmDialogCallback, DeckListSortSettingCallback {
    public static String TAG = "FragmentDeckList";

    RecyclerView recyclerView;

    ViewModel1 viewModel;

    DeckEntity longClickedDeck;

    Fragment1Interface callBack = null;

    Database2Wrapper.Database2Callback database2Callback;

    Button buttonCreateNewDeck;

    Button buttonSortingOption;

    boolean searchMode = false;

    int longClickedItemPosition;

    int justInsertedDeckUid;

    public interface Fragment1Interface {
        void onDeckSelected();
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

        //todo: handle back key to and from this screen
        setupDatabaseCallback();
        setupListUi(view);
        setupViewModel();
        readDatabaseLiveData();
        setupButton(view);


        return view;
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

    private void setupDatabaseCallback() {
        database2Callback = new Database2Wrapper.Database2Callback() {
            @Override
            public void onComplete_SimpleResult(Database2Wrapper.DbTask whichTask, Database2Wrapper.DbTaskResult taskResult) {
                Util.logDebug(TAG, "db task complete: " + whichTask);
                onDbTaskComplete(whichTask, taskResult);
            }

            @Override
            public void onSearchDeckComplete(Database2Wrapper.DbTask whichTask, List<DeckEntity> deckSearchResult) {
                Util.logDebug(TAG, "db task complete: " + whichTask);
                Util.logDebug(TAG, "onSearchDeckComplete: " + deckSearchResult.size());
                onNewDeckListFromDatabase(deckSearchResult);

            }

            @Override
            public void onInsertComplete(Database2Wrapper.DbTask whichTask, long newRowId) {
                Util.logDebug(TAG, "db task complete onInsertComplete: " + whichTask);
                //  todo: choose between int or long???
                askUserIfTheyAlsoWantToOpenDeck((int) newRowId);
            }
        };
    }

    private void askUserIfTheyAlsoWantToOpenDeck(int newRowId) {
        justInsertedDeckUid = newRowId;
        showDialog_ConfirmDialog(Util.DialogType.CONFIRM_OPEN_DECK_JUST_CREATED);
    }

    private void setupViewModel(){
        /**
         *  todo: problem: should i pass callback like this???
         */
//        ViewModelProvider.Factory factory = (ViewModelProvider.Factory) new ViewModel1.MyVmFactory3(
//                requireActivity().getApplication(),
//                new Database2Wrapper.Database2Callback() {
//                    @Override
//                    public void onComplete(Database2Wrapper.DbTask whichTask,
//                                           Database2Wrapper.DbTaskResult taskResult) {
//                        Util.logDebug(TAG, "completed database task: " + whichTask +
//                                ", with result: " + taskResult);
//                    }
//                }
//        );
//        viewModel = new ViewModelProvider(
//                requireActivity(),
//                factory).get(ViewModel1.class);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel1.class);
        viewModel.setMark("i was here");
    }

    private void readDatabaseLiveData() {
        /**
         * "this observer will be notified about modifications of the
         * wrapped data only if the paired LifecycleOwner is in
         * active state. LifecycleOwner is considered as active
         * , if its state is STARTED or RESUMED"
         *  -> ASSUMPTION: no need to manually remove observer
         */
        viewModel.readAll_vm().observe(
                getViewLifecycleOwner(),
                new Observer<List<DeckEntity>>() {
                    @Override
                    public void onChanged(List<DeckEntity> deckEntities) {
                        Util.logDebug(TAG, "auto update from database");
                        onNewDeckListFromDatabase(deckEntities);
                    }
                });
    }

    private void onNewDeckListFromDatabase(List<DeckEntity> deckEntities) {
        if (deckEntities != null && deckEntities.size() > 0){
            Util.logDebug(TAG, "live data list update");
            recyViewAdapterAlias().submitList(deckEntities);
        } else {
            if (deckEntities == null)
                // todo: display error dialog
                Util.logDebug(TAG, "live data list update error: null list");
            else {
                Util.logDebug(TAG, "live data list update error: no item on list");
                recyViewAdapterAlias().submitList(deckEntities);
            }
        }
    }

    private void setupButton(View view) {
        buttonCreateNewDeck = view.findViewById(R.id.button_add_item_to_decks_list);
        buttonSortingOption = view.findViewById(R.id.button_decks_list_list_sort_option);
        SearchView searchView = view.findViewById(R.id.searchView_deck_list);

        buttonCreateNewDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_CreateNewDeck();
            }
        });
        buttonSortingOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_DeckSortOption();
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
    }

    private void transitionToSearchMode(){
        searchMode = true;
        buttonCreateNewDeck.setVisibility(View.GONE);
        LiveData list = viewModel.getCurrentDeckList_LiveData();
        if (list != null) {
            list.removeObservers(getViewLifecycleOwner());
        }
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
        viewModel.findDecks_vm(query, database2Callback);
    }

    private void setupListUi(View view) {
        recyclerView = view.findViewById(R.id.recyclerView_item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(new CustomAdapterDeckList(new CustomAdapterDeckList.DeckItemDiff(),
                new CustomAdapterDeckList.CustomAdapter3Callback() {
                    @Override
                    public void customAdapter3OnItemClick(Util.ClickEvent event, int position) {
                        handleDeckListItemClick(event, position);
                    }
                }));
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
//            longClickedDeck = deck;
            longClickedDeck = recyViewAdapterAlias().getCurrentList().get(position);
            longClickedItemPosition = position;
            showDeckItemPopupMenu(viewHolder.itemView);
        } else if (event == Util.ClickEvent.CLICK){
//            longClickedDeck = deck;
            longClickedDeck = recyViewAdapterAlias().getCurrentList().get(position);
//            showDialog_OpenDeckConfirm();
            showDialog_ConfirmDialog(Util.DialogType.CONFIRM_OPEN_DECK);
        }
    }

    private void showDeckItemPopupMenu(View v){
        PopupMenu popMenu = new PopupMenu(requireContext(), v);
        popMenu.inflate(R.menu.deck_item_option_menu);
        popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                /**
                 * return true if the event was handled, false otherwise
                 */
                return handleDeckItemPopupMenuSelected(menuItem);
            }
        });
        popMenu.show();
    }

    private boolean handleDeckItemPopupMenuSelected(MenuItem item) {
        /**
         *  todo done: investigate this
         *      "Resource IDs will be non-final by default in Android Gradle
         *      Plugin version 8.0, avoid using them in switch case statement""
         *
         *  possible answer:
         *      https://stackoverflow.com/questions/64335374/how-to-resolve-resource-ids-will-be-non-final-in-android-gradle-plugin-version
         */
        if (item.getItemId() == R.id.menu_item_deck_delete){
//            showDialog_DeckDeleteConfirm();
            showDialog_ConfirmDialog(Util.DialogType.CONFIRM_DECK_DELETE);
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
        DialogFragmentDeckListSortOption dialog = new DialogFragmentDeckListSortOption();
        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE,
                Util.getStringFromDialogType(Util.DialogType.DECK_LIST_SORT_OPTION));
        dialog.setArguments(args);
        dialog.show(getChildFragmentManager(), DialogFragmentDeckListSortOption.TAG);
    }

    private void showDialog_CreateNewDeck() {
        DialogFragmentNewDeck newDeckDialogFragment = new DialogFragmentNewDeck();
        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE,
                Util.getStringFromDialogType(Util.DialogType.NEW_DECK_NAME));
        newDeckDialogFragment.setArguments(args);
        newDeckDialogFragment.show(getChildFragmentManager(), DialogFragmentNewDeck.TAG);
    }

    private void showDialog_DeckRename() {
        DialogFragmentNewDeck newDeckDialogFragment = new DialogFragmentNewDeck();
        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE,
                Util.getStringFromDialogType(Util.DialogType.DECK_RENAME));
        args.putString(Util.BUNDLE_KEY_OLD_NAME, longClickedDeck.getDeckName());
        newDeckDialogFragment.setArguments(args);
        newDeckDialogFragment.show(getChildFragmentManager(), DialogFragmentNewDeck.TAG);
    }

//    private void showDialog_DeckDeleteConfirm() {
//        ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment();
//        Bundle args = new Bundle();
//        args.putString(Util.BUNDLE_KEY_DIALOGTYPE,
//                Util.getStringFromDialogType(Util.DialogType.CONFIRM_DECK_DELETE));
//        dialogFragment.setArguments(args);
//        dialogFragment.show(getChildFragmentManager(), ConfirmDialogFragment.TAG);
//    }
//
//    private void showDialog_OpenDeckConfirm() {
//        ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment();
//        Bundle args = new Bundle();
//        args.putString(Util.BUNDLE_KEY_DIALOGTYPE,
//                Util.getStringFromDialogType(Util.DialogType.CONFIRM_OPEN_DECK));
//        dialogFragment.setArguments(args);
//        dialogFragment.show(getChildFragmentManager(), ConfirmDialogFragment.TAG);
//    }

    private void showDialog_ConfirmDialog(Util.DialogType confirmDialogType) {
        DialogFragmentConfirm dialogFragment = new DialogFragmentConfirm();
        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE,
                Util.getStringFromDialogType(confirmDialogType));
        if (confirmDialogType == Util.DialogType.CONFIRM_OPEN_DECK) {
            args.putString(Util.BUNDLE_KEY_NAME_OF_DECK_TOBE_OPENED, longClickedDeck.getDeckName());
        }
        dialogFragment.setArguments(args);
        dialogFragment.show(getChildFragmentManager(), DialogFragmentConfirm.TAG);
    }

    private CustomAdapterDeckList recyViewAdapterAlias() {
        return (CustomAdapterDeckList) recyclerView.getAdapter();
    }

    @Override
    public void onDialogResult_Confirm(Util.DialogType dialogType) {
        Util.logDebug(TAG, "dialog confirm result");
        Util.logDebug(TAG, "type: " + dialogType);
        if (dialogType == Util.DialogType.CONFIRM_DECK_DELETE) {
            Util.logDebug(TAG, "delete deck");
//            deleteDeck(longClickedDeck);
            deleteDeck(longClickedDeck.getUid());
        } else if (dialogType == Util.DialogType.CONFIRM_OPEN_DECK) {
            Util.logDebug(TAG, "open deck");
            openDeck(longClickedDeck.getUid());
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
//            renameDeck(longClickedDeck, text);
            renameDeck(longClickedDeck.getUid(), text);
        } else {
            Util.logDebug(TAG, "what the hell was that");
        }
    }

//    private void deleteDeck(DeckEntity deck) {
//        viewModel.deleteDeck_vm(deck, database2Callback);
//        if (searchMode) {
//            List<DeckEntity> currentList = new ArrayList<>(recyViewAdapterAlias().getCurrentList());
//            currentList.remove(longClickedItemPosition);
//            recyViewAdapterAlias().submitList(currentList);
//            recyViewAdapterAlias().notifyItemRemoved(longClickedItemPosition);
//        }
//    }

    private void deleteDeck(int targetUid) {
        viewModel.deleteDeck_vm(targetUid, database2Callback);
        if (searchMode) {
            List<DeckEntity> currentList = new ArrayList<>(recyViewAdapterAlias().getCurrentList());
            currentList.remove(longClickedItemPosition);
            recyViewAdapterAlias().submitList(currentList);
            recyViewAdapterAlias().notifyItemRemoved(longClickedItemPosition);
        }
    }

//    private void openDeck(DeckEntity deck) {
//        viewModel.setSelectedDeckUid(deck.getUid());
//        if (callBack != null) {
//            callBack.onDeckSelected();
//        }
//    }

    private void openDeck(int deckUid) {
        viewModel.setSelectedDeckUid(deckUid);
        if (callBack != null) {
            callBack.onDeckSelected();
        }
    }

    private void insertNewDeck(String content) {
//        viewModel.insertNewDeck_vm(new DeckEntity(
//                content, Calendar.getInstance().getTimeInMillis()), database2Callback);
        viewModel.insertNewDeck_vm(content, database2Callback);
    }

//    private void renameDeck(DeckEntity deckInQuestion, String newName) {
//        /**
//         *  todo: doing basic stuff for now
//         *      when entity get complicated, this would get ugly
//         */
//        viewModel.updateDeck_vm(
//                new DeckEntity(
//                        deckInQuestion.getUid(),
//                        newName,
//                        Calendar.getInstance().getTimeInMillis()),
//                database2Callback);
//
//        //todo: switch to new function
//
//        if (searchMode) {
//            List<DeckEntity> currentList = new ArrayList<>(recyViewAdapterAlias().getCurrentList());
//            currentList.get(longClickedItemPosition).setDeckName(newName);
//            recyViewAdapterAlias().submitList(currentList);
//            recyViewAdapterAlias().notifyItemChanged(longClickedItemPosition);
//        }
//    }

    private void renameDeck(int deckUidInQuestion, String newName) {
        viewModel.updateDeckName_vm(deckUidInQuestion, newName, database2Callback);

        if (searchMode) {
            List<DeckEntity> currentList = new ArrayList<>(recyViewAdapterAlias().getCurrentList());
            currentList.get(longClickedItemPosition).setDeckName(newName);
            recyViewAdapterAlias().submitList(currentList);
            recyViewAdapterAlias().notifyItemChanged(longClickedItemPosition);
        }
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


