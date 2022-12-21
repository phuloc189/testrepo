package com.android.myapplication8.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.ViewModel1;
import com.android.myapplication8.database2.CardEntity;
import com.android.myapplication8.database2.Database2Wrapper;
import com.android.myapplication8.database2.DeckEntity;
import com.android.myapplication8.database2.DeckEntityExtra;
import com.android.myapplication8.interfaces.MarkingEditCallback;
import com.android.myapplication8.interfaces.NewCardDialogCallback;
import com.android.myapplication8.interfaces.StdScrnControlPanelCallback;

import java.util.List;

public class FragmentStudyScreen extends Fragment implements
        MarkingEditCallback, NewCardDialogCallback, StdScrnControlPanelCallback {

    public static final String TAG = "FragmentStudyScreen";

    ViewModel1 viewModel;

    CardEntity displayedCard;

    TextView cardContentDisplay;

    TextView cardMarkingDisplay;

    DialogFragmentStdScrnControlPanel controlPanelDialog;

    Database2Wrapper.Database2Callback database2Callback;

    Database2Wrapper.Database2Callback_CardsEntity database2Callback_cardsEntity;

    Button nextButton;
    Button previousButton;
    Button flipButton;

    boolean displayingFront = true;

    public FragmentStudyScreen() {
        // Required empty public constructor
    }

//    public static Fragment3 newInstance(String param1, String param2) {
//        Fragment3 fragment = new Fragment3();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study_screen, container, false);

        setupDatabaseCallback();
        setupViewModel();
        setupUi(view);
        fetchAndCacheCards();
        displayCardContent();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupOptionsMenu();
    }

    private void setupOptionsMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_study_screen_option, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_item_sorting) {
                    showControlPanelDialog();
                    return true;
                } else {
                    return false;
                }
            }
        }, getViewLifecycleOwner());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel1.class);
    }

    private void setupUi(View view) {
        nextButton = view.findViewById(R.id.button_study_screen_next_card);
        previousButton = view.findViewById(R.id.button_study_screen_previous_card);
        flipButton = view.findViewById(R.id.button_study_screen_flip_card);
        Button editButton = view.findViewById(R.id.button_study_screen_edit_card);
        Button restartButton = view.findViewById(R.id.button_study_screen_restart);
        cardContentDisplay = view.findViewById(R.id.textView_studyScreen_card_content);
        cardMarkingDisplay = view.findViewById(R.id.textView_studyScreen_card_marking);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.incrementStudyingCardsListPointer();
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.decrementStudyingCardsListPointer();
            }
        });
        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayingFront = !displayingFront;
                cardContentDisplay.setText(getCardContentToDisplay());
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCardEditDialog();
            }
        });
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadDeck();
            }
        });

        cardMarkingDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMarkingEditDialog();
            }
        });
    }

    private void showControlPanelDialog() {
        controlPanelDialog = new DialogFragmentStdScrnControlPanel();
        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE,
                Util.getDialogTypeStringFromDialogType(Util.DialogType.STUDY_SCREEN_CTRL_PANEL));
        controlPanelDialog.setArguments(args);
        controlPanelDialog.show(getChildFragmentManager(), DialogFragmentStdScrnControlPanel.TAG);
    }

    private void showCardEditDialog() {
        DialogFragmentNewCard dialog = new DialogFragmentNewCard();
        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE, Util.BUNDLE_VALUE_DIALOGTYPE_EDIT_CARD);
        args.putString(Util.BUNDLE_KEY_OLD_FRONT_TEXT, displayedCard.getFrontText());
        args.putString(Util.BUNDLE_KEY_OLD_BACK_TEXT, displayedCard.getBackText());
        dialog.setArguments(args);
        dialog.show(getChildFragmentManager(), DialogFragmentNewCard.TAG);
    }

    private void showMarkingEditDialog(){
        DialogFragmentMarkingEditing dialogFragment = new DialogFragmentMarkingEditing();
        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE, Util.BUNDLE_VALUE_DIALOGTYPE_CARD_MARKING_EDIT);
        args.putInt(Util.BUNDLE_KEY_OLD_MARKING_VALUE, displayedCard.getMarking0());
        dialogFragment.setArguments(args);
        dialogFragment.show(getChildFragmentManager(), DialogFragmentMarkingEditing.TAG);
    }

    private String getCardContentToDisplay() { //todo: should i put this in viewmodel???
        if (displayingFront) {
            return displayedCard.getFrontText();
        } else {
            return displayedCard.getBackText();
        }
    }

    private void fetchAndCacheCards() {
        if (viewModel.getStudyMode_value() == Util.StudyMode.DECK) {
            viewModel.cacheCardsFromSelectedDeck();
            reloadDeck();
        } else if (viewModel.getStudyMode_value() == Util.StudyMode.COLLECTION &&
                viewModel.isInCollectionMode()) { //todo: do something with this condition expression
            viewModel.getAllCardsFromCollection_vm(viewModel.getSelectedCollectionUid_Value(), database2Callback_cardsEntity);
        }
    }

    private void onCardFetchResult(List<CardEntity> cardsFetchResult) {
        viewModel.cacheCards(cardsFetchResult);
        reloadDeck();
    }

    private void displayCardContent() {
        viewModel.getStudyingCardsListPointer().observe(
                getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (integer < 0){
                            nextButton.setVisibility(View.INVISIBLE);
                            previousButton.setVisibility(View.INVISIBLE);
                            flipButton.setVisibility(View.INVISIBLE);
                            return;
                        }
                        ontoTheDisplay(integer);
                        //todo: enable/disable next/previous button
                    }
                }
        );
    }

    @Override
    public void onMarkingChanged(Util.DialogType dialogType, int newValue) {
        Util.logDebug(TAG, "dialog result from: " + dialogType);
        Util.logDebug(TAG, "dialog result: " + newValue);

        if (dialogType == Util.DialogType.CARD_MARKING_EDIT) {
            // todo: explore more elegant option than this POS
            viewModel.shiftMarkingStat(displayedCard.getMarking0(), newValue);
            displayedCard.setMarking0(newValue);
            updateDisplayedCard(displayedCard);
        } else if (dialogType == Util.DialogType.LIMIT_MARKING_OPTION) {
            try {
                controlPanelDialog.childDialogClosed(newValue);
            } catch (Exception e) {
                Util.logDebug(TAG, "exception happened: " + e);
            }
        }
    }

    @Override
    public void onNewCardTextDialogResult(Util.DialogType dialogType, String frontText, String backText) {
        Util.logDebug(TAG, "dialog result from: " + dialogType);
        if (dialogType != Util.DialogType.EDIT_CARD) {
            return;
        }
        displayedCard.setFrontText(frontText);
        displayedCard.setBackText(backText);

        updateDisplayedCard(displayedCard);
    }

    private void updateDisplayedCard(CardEntity card) {
        viewModel.updateCard_vm(card, database2Callback);
        List<CardEntity> newList = viewModel.getStudyingCardsList_Value();
        newList.set(viewModel.getIndexArrays().get(viewModel.getStudyingCardsListPointer_Value()), card);
        viewModel.getStudyingCardsList().setValue(newList);
        ontoTheDisplay(viewModel.getStudyingCardsListPointer_Value());
    }

    private void ontoTheDisplay(int index) {
        displayingFront = !(viewModel.getBackSideFirstSetting());
        displayedCard = viewModel.getStudyingCardsList_Value().get(viewModel.getIndexArrays().get(index));
        cardContentDisplay.setText(getCardContentToDisplay());
        cardMarkingDisplay.setText(String.valueOf(displayedCard.getMarking0()));
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

            @Override
            public void onGetDeckResult(Database2Wrapper.DbTask whichTask, DeckEntity deck) {

            }
        };

        database2Callback_cardsEntity = new Database2Wrapper.Database2Callback_CardsEntity() {
            @Override
            public void onComplete_FetchingCards(Database2Wrapper.DbTask whichTask, List<CardEntity> cardsFetchResult) {
                Util.logDebug(TAG, "onComplete_FetchingCards result: " + cardsFetchResult.size());
                if (whichTask == Database2Wrapper.DbTask.DB_TASK_FETCH_CARDS_FROM_COLLECTION) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onCardFetchResult(cardsFetchResult);
                        }
                    });

                }

            }
        };
    }

    private void onDbTaskResult(Database2Wrapper.DbTask whichTask, Database2Wrapper.DbTaskResult taskResult){
        Util.logDebug(TAG, "db task: " + whichTask);
        Util.logDebug(TAG, "result: " + taskResult);
    }

    @Override
    public void controlPanelResult(Util.DialogType dialogType, boolean settingChanged, boolean restartRequested) {
        Util.logDebug(TAG, "dialog result from: " + dialogType);
        Util.logDebug(TAG, "setting changed: " + settingChanged);
        Util.logDebug(TAG, "restart???: " + restartRequested);

        controlPanelDialog = null;

        /**
         *  todo:
         *      when "show back side" change
         *          show back side/front side upon next card
         *
         */

        if (restartRequested) {
            /**
             *  todo: handle restart request
             *      ???call the same function that the restart button use
              */
            reloadDeck();
        } else if (settingChanged) {
            Util.logDebug(TAG, "load setting without restart: ");
            viewModel.fetchPrefSetting();
        }
        /**
         *  todo: how to handle restart
         *      start from the beginning index
         *      shuffle the list again if random mode is on
         *      what about selected marking:
         *          should already be filtered before hand???
         *          what if user change card marking during learning session???
         *
         */
    }

    private void reloadDeck() {
        viewModel.fetchPrefSetting();
        viewModel.reloadDeck();
        viewModel.resetCardListPointer();
    }

    @Override
    public void openMarkingOptionDialog(Util.DialogType dialogType, int currentSetting) {
        openMarkingSelectDialog(currentSetting);
    }

    private void openMarkingSelectDialog(int currentSetting) {
        DialogFragmentMarkingEditing limitedMarkingDialog = new DialogFragmentMarkingEditing();
        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE, Util.getDialogTypeStringFromDialogType(Util.DialogType.LIMIT_MARKING_OPTION));
        args.putInt(Util.BUNDLE_KEY_CURRENT_LIMITED_MARKING_VALUE_SETTING, currentSetting);
        limitedMarkingDialog.setArguments(args);
        limitedMarkingDialog.show(getChildFragmentManager(), DialogFragmentMarkingEditing.TAG);
    }

}