package com.android.myapplication8.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.android.myapplication8.CustomAdapterCardList;
import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.ViewModel1;
import com.android.myapplication8.database2.entity.CardEntity;
import com.android.myapplication8.database2.Database2Wrapper;
import com.android.myapplication8.database2.entity.DeckEntity;
import com.android.myapplication8.database2.entity.DeckEntityExtra;
import com.android.myapplication8.interfaces.CardListAdapterOnClick;
import com.android.myapplication8.interfaces.ConfirmDialogCallback;
import com.android.myapplication8.interfaces.DialogResultCallback;
import com.android.myapplication8.interfaces.NewCardDialogCallback;
import com.android.myapplication8.ui.dialog.DialogFragmentConfirm;
import com.android.myapplication8.ui.dialog.DialogFragmentNewCard;
import com.android.myapplication8.ui.dialog.DialogFragmentSimpleNameEdit;

import java.util.Calendar;
import java.util.List;

public class FragmentCardList extends Fragment implements NewCardDialogCallback,
        CardListAdapterOnClick, ConfirmDialogCallback, DialogResultCallback {
    public static final String TAG = "FragmentCardList";

    ViewModel1 viewModel;

    RecyclerView recyclerView;

    CardEntity longClickedCard;

    Button studyDeckButton;

    TextView tvCurrentDeckName;

    String currentDeckName;

    Database2Wrapper.Database2Callback database2Callback;

    public interface Fragment2Interface {
        void moveToStudyScreen();
    }

    public FragmentCardList() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupOptionMenu();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_list, container, false);

        setupDatabaseCallback();
        setupViewModel();
        setupList(view);
        setupUi(view);
        readDatabase();
        updateDeckVisitedDate();

        return view;
    }

    private void setupDatabaseCallback() {
        database2Callback = new Database2Wrapper.Database2Callback() {
            @Override
            public void onComplete_SimpleResult(Database2Wrapper.DbTask whichTask, Database2Wrapper.DbTaskResult taskResult) {
                onDbTaskResult(whichTask, taskResult);
            }

            @Override
            public void onSearchDeckComplete(Database2Wrapper.DbTask whichTask, List<DeckEntity> deckSearchResult) {

            }

            @Override
            public void onSearchDeckCompleteExtra(Database2Wrapper.DbTask whichTask, List<DeckEntityExtra> deckSearchResult) {

            }

            @Override
            public void onInsertComplete(Database2Wrapper.DbTask whichTask, long newRowId) {

            }

            //todo: delete this interface method
            @Override
            public void onGetDeckResult(Database2Wrapper.DbTask whichTask, DeckEntity deck) {
            }
        };
    }

    private void setupOptionMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_cards_screen_option, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_item_change_deck_name){
                    showDialog_DeckRename();
                    return true;
                } else {
                    return false;
                }
            }
        }, getViewLifecycleOwner());
    }

    private void updateDeckVisitedDate() {
        viewModel.updateDeckVisitedDate_vm(
                viewModel.getSelectedDeckUid_Value(),
                Calendar.getInstance().getTimeInMillis(),
                database2Callback
        );
    }

    private void setupViewModel(){
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel1.class);
    }

    private void setupList(View view){
        recyclerView = view.findViewById(R.id.recyclerView_card_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new CustomAdapterCardList(new CustomAdapterCardList.CardItemDiff(), this));
    }

    private void setupUi(View view){
        Button createCardButton = view.findViewById(R.id.button_create_new_card);
        tvCurrentDeckName = view.findViewById(R.id.tv_cardList_current_deck);
        createCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewCardDialog();
            }
        });

        studyDeckButton = view.findViewById(R.id.button_study_this_deck);
        studyDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestTransitionToStudyScreen();
            }
        });
    }

    private void requestTransitionToStudyScreen() {
        try {
            viewModel.setStudyMode(Util.StudyMode.DECK);
            Fragment2Interface callback = (Fragment2Interface) requireActivity();
            callback.moveToStudyScreen();
        } catch (Exception e) {
            Util.logDebug(TAG, "exception happened: " + e);
        }
    }

    private CustomAdapterCardList recyViewAdapterAlias() {
        return (CustomAdapterCardList) recyclerView.getAdapter();
    }

    private void readDatabase() {
        Util.logDebug(TAG, "read from deckUid: " + viewModel.getSelectedDeckUid_Value());
        viewModel.readAllCardsFromDeckLiveData_vm(viewModel.getSelectedDeckUid_Value())
                .observe(this.getViewLifecycleOwner(), new Observer<List<CardEntity>>() {
                    @Override
                    public void onChanged(List<CardEntity> cardEntities) {
                        recyViewAdapterAlias().submitList(cardEntities);
                        if (cardEntities.size() > 0) {
                            studyDeckButton.setVisibility(View.VISIBLE);
                        } else {
                            studyDeckButton.setVisibility(View.INVISIBLE);
                        }
                    }
                });

        viewModel.getDeckWithId_LiveData_vm(viewModel.getSelectedDeckUid_Value())
                .observe(this.getViewLifecycleOwner(), new Observer<DeckEntity>() {
                    @Override
                    public void onChanged(DeckEntity deck) {
                        currentDeckName = deck.getDeckName();
                        tvCurrentDeckName.setText(getString(R.string.tv_current_deck_info, deck.getDeckName()));
                    }
                });
    }

    @Override
    public void onNewCardTextDialogResult(Util.DialogType dialogType, String frontText, String backText) {
        Util.logDebug(TAG, "dialog return: " + dialogType);
        Util.logDebug(TAG, "with text: " + frontText + ", " + backText);
        if (dialogType == Util.DialogType.NEW_CARD) {
            addNewCardToDb(frontText, backText);
        } else if (dialogType == Util.DialogType.EDIT_CARD) {
            handleCardEdit(longClickedCard, frontText, backText);
        }
    }

    @Override
    public void onDialogResult_Confirm(Util.DialogType dialogType) {
        if (dialogType == Util.DialogType.CONFIRM_CARD_DELETE) {
            handleCardDelete(longClickedCard);
        }
    }

    @Override
    public void onDialogResult_NewText(Util.DialogType dialogType, String text) {
        if(dialogType == Util.DialogType.DECK_RENAME) {
            viewModel.updateDeckName_vm(
                    viewModel.getSelectedDeckUid_Value(),
                    text, database2Callback);
        }
    }

    private void addNewCardToDb(String frontText, String backText) {
        Util.logDebug(TAG, "add new card to deckuid: " + frontText + ", " + backText);
        CardEntity newCard = new CardEntity(viewModel.getSelectedDeckUid_Value(), frontText, backText);
        viewModel.insertNewCard_vm(newCard, database2Callback);
    }

    private void onDbTaskResult(Database2Wrapper.DbTask whichTask, Database2Wrapper.DbTaskResult taskResult){
        Util.logDebug(TAG, "db task: " + whichTask);
        Util.logDebug(TAG, "result: " + taskResult);
    }

    @Override
    public void cardListAdapterOnItemClick(Util.ClickEvent event, int position, CardEntity card) {
        Util.logDebug(TAG, "adapter on click: ");
        if (event == Util.ClickEvent.LONG_CLICK) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(position);
            if (viewHolder == null) {
                return;
            }
            longClickedCard = card;
            PopupMenu popupMenu = new PopupMenu(requireContext(), viewHolder.itemView);
            popupMenu.inflate(R.menu.card_list_item_option_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    return handlePopUpMenuSelect(menuItem);
                }
            });
            popupMenu.show();
        }
    }

    private boolean handlePopUpMenuSelect(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_item_card_delete) {
            showCardDeleteConfirmDialog();
            return true;
        } else if (menuItem.getItemId() == R.id.menu_item_card_edit) {
            showCardEditDialog();
            return true;
        }
        return false;
    }

    private void showCardDeleteConfirmDialog() {
        DialogFragmentConfirm confirmDialogFragment =
                DialogFragmentConfirm.newInstance(Util.DialogType.CONFIRM_CARD_DELETE, null);
        confirmDialogFragment.show(getChildFragmentManager(), DialogFragmentConfirm.TAG);
    }

    private void showCardEditDialog() {
        DialogFragmentNewCard newCardDialogFragment = new DialogFragmentNewCard();
        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE, Util.BUNDLE_VALUE_DIALOGTYPE_EDIT_CARD);
        args.putString(Util.BUNDLE_KEY_OLD_FRONT_TEXT, longClickedCard.getFrontText());
        args.putString(Util.BUNDLE_KEY_OLD_BACK_TEXT, longClickedCard.getBackText());
        newCardDialogFragment.setArguments(args);
        newCardDialogFragment.show(getChildFragmentManager(), DialogFragmentNewCard.TAG);
    }

    private void showDialog_DeckRename() {
        DialogFragmentSimpleNameEdit newDeckDialogFragment =
                DialogFragmentSimpleNameEdit.newInstance(Util.DialogType.DECK_RENAME, currentDeckName);
        newDeckDialogFragment.show(getChildFragmentManager(), DialogFragmentSimpleNameEdit.TAG);
    }

    private void showNewCardDialog() {
        DialogFragmentNewCard newCardDialogFragment = new DialogFragmentNewCard();
        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE, Util.BUNDLE_VALUE_DIALOGTYPE_NEW_CARD);
        newCardDialogFragment.setArguments(args);
        newCardDialogFragment.show(getChildFragmentManager(), DialogFragmentNewCard.TAG);
    }

    private void handleCardDelete(CardEntity card){
        viewModel.deleteCard_vm(longClickedCard, database2Callback);
    }

    private void handleCardEdit(CardEntity card, String newFrontText, String newBackText) {
        //we don't see any update happen on the list if we do this
//        card.setFrontText(newFrontText);
//        card.setBackText(newBackText);
//        viewModel.updateCard_vm(card, database2Callback);
        //i.e. we need to create new entity object instead of using one from adapter
        CardEntity editedCard = new CardEntity(card.getDeckUid(), newFrontText, newBackText);
        editedCard.setUid(card.getUid());
        editedCard.setMarking0(card.getMarking0());
        viewModel.updateCard_vm(editedCard, database2Callback);
    }

}