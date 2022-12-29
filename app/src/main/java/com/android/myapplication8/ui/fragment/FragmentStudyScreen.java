package com.android.myapplication8.ui.fragment;

import android.graphics.Paint;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.ViewModel1;
import com.android.myapplication8.database2.entity.CardEntity;
import com.android.myapplication8.database2.Database2Wrapper;
import com.android.myapplication8.database2.entity.DeckEntityExtra;
import com.android.myapplication8.interfaces.MarkingEditCallback;
import com.android.myapplication8.interfaces.NewCardDialogCallback;
import com.android.myapplication8.interfaces.StdScrnControlPanelCallback;
import com.android.myapplication8.ui.dialog.DialogFragmentMarkingEditing;
import com.android.myapplication8.ui.dialog.DialogFragmentNewCard;
import com.android.myapplication8.ui.dialog.DialogFragmentStdScrnControlPanel;

import java.util.List;

public class FragmentStudyScreen extends Fragment implements
        MarkingEditCallback, NewCardDialogCallback, StdScrnControlPanelCallback {

    public static final String TAG = "FragmentStudyScreen";

    ViewModel1 viewModel;

    CardEntity displayedCard;

    TextView tvCardContentDisplay;

    TextView tvDisplayedSide;

    TextView tvCardMarkingDisplay;

    TextView tvStudyProgress;

    ScrollView scrollViewCardContent;

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
        fetchCardContent();

        return view;
    }

    private void setupDatabaseCallback() {
        database2Callback = new Database2Wrapper.Database2Callback() {
            @Override
            public void onComplete_SimpleResult(Database2Wrapper.DbTask whichTask, Database2Wrapper.DbTaskResult taskResult) {
                onDbTaskResult(whichTask, taskResult);
            }

            @Override
            public void onSearchDeckCompleteExtra(Database2Wrapper.DbTask whichTask, List<DeckEntityExtra> deckSearchResult) {

            }

            @Override
            public void onInsertComplete(Database2Wrapper.DbTask whichTask, long newRowId) {
            }
        };

        database2Callback_cardsEntity = new Database2Wrapper.Database2Callback_CardsEntity() {
            @Override
            public void onComplete_FetchingCards(Database2Wrapper.DbTask whichTask, List<CardEntity> cardsFetchResult) {
                Util.logDebug(TAG, "onComplete_FetchingCards result: " + cardsFetchResult.size());
                if (whichTask == Database2Wrapper.DbTask.CARD_READ_FROM_COLLECTION) {
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

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel1.class);
    }

    private void setupUi(View view) {
        nextButton = view.findViewById(R.id.button_studyScreen_nextCard);
        previousButton = view.findViewById(R.id.button_studyScreen_previousCard);
        flipButton = view.findViewById(R.id.button_studyScreen_flipCard);
        Button editButton = view.findViewById(R.id.button_studyScreen_editCard);
        Button restartButton = view.findViewById(R.id.button_study_screen_restart);
        tvCardContentDisplay = view.findViewById(R.id.textView_studyScreen_card_content);
        tvCardMarkingDisplay = view.findViewById(R.id.textView_studyScreen_card_marking);
        tvStudyProgress = view.findViewById(R.id.tv_studyScreen_studyProgress);
        tvDisplayedSide = view.findViewById(R.id.textView_studyScreen_cardSide);
        scrollViewCardContent = view.findViewById(R.id.scrollView_studyScreen_card_content);


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
                tvCardContentDisplay.setText(getCardContentToDisplay());
                tvDisplayedSide.setText(getSideInfoToDisplay());
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

        tvCardMarkingDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMarkingEditDialog();
            }
        });
    }

    private void fetchAndCacheCards() {
        if (viewModel.getStudyMode_value() == Util.StudyMode.DECK) {
            viewModel.cacheCardsFromSelectedDeck();
            reloadDeck();
        } else if (viewModel.getStudyMode_value() == Util.StudyMode.COLLECTION &&
                viewModel.isInCollectionMode()) { //todo: do something with this condition expression
            viewModel.getCardsFromCollection_vm(viewModel.getSelectedCollectionUid_Value(), database2Callback_cardsEntity);
        }
    }

    private void fetchCardContent() {
        viewModel.getFilteredStudyingCardsSize().observe(
                getViewLifecycleOwner(),
                new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        Util.logDebug(TAG, "filtered size: " + integer);
                        getView().findViewById(R.id.linearLayout_studyScrn_group_cardNavigation).setVisibility((integer <= 0) ? View.INVISIBLE : View.VISIBLE);
                    }
                }
        );
        viewModel.getStudyingCardsListPointer().observe(
                getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        updateNavigationButton(integer);
                        updateProgressInfo(integer);
                        displayCardContent(integer, true);
                    }
                }
        );
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

    private void showControlPanelDialog() {
        controlPanelDialog = DialogFragmentStdScrnControlPanel.newInstance(Util.DialogType.STUDY_SCREEN_CTRL_PANEL);
        controlPanelDialog.show(getChildFragmentManager(), DialogFragmentStdScrnControlPanel.TAG);
    }

    private void showCardEditDialog() {
        DialogFragmentNewCard dialog = DialogFragmentNewCard.newInstance(
                Util.DialogType.EDIT_CARD, displayedCard.getFrontText(), displayedCard.getBackText()
        );
        dialog.show(getChildFragmentManager(), DialogFragmentNewCard.TAG);
    }

    private void showMarkingEditDialog() {
        DialogFragmentMarkingEditing dialog = DialogFragmentMarkingEditing.newInstance(
                Util.DialogType.CARD_MARKING_EDIT,
                displayedCard.getMarking0());
        dialog.show(getChildFragmentManager(), DialogFragmentMarkingEditing.TAG);
    }

    private void showMarkingSelectDialog(int currentSetting) {
        DialogFragmentMarkingEditing dialog = DialogFragmentMarkingEditing.newInstance(
                Util.DialogType.LIMIT_MARKING_OPTION,
                currentSetting);
        dialog.show(getChildFragmentManager(), DialogFragmentMarkingEditing.TAG);
    }

    private String getCardContentToDisplay() {
        return displayingFront ? displayedCard.getFrontText() : displayedCard.getBackText();
    }

    private String getSideInfoToDisplay() {
        return displayingFront ? "Front: " : "Back: ";
    }

    private void onCardFetchResult(List<CardEntity> cardsFetchResult) {
        viewModel.cacheCards(cardsFetchResult);
        reloadDeck();
    }

    @Override
    public void onMarkingChanged(Util.DialogType dialogType, int newValue) {
        Util.logDebug(TAG, "dialog result from: " + dialogType);
        Util.logDebug(TAG, "dialog result: " + newValue);

        if (dialogType == Util.DialogType.CARD_MARKING_EDIT) {
            // todo: explore more elegant option than this POS
            viewModel.shiftMarkingStat(displayedCard.getMarking0(), newValue);
            displayedCard.setMarking0(newValue);
            updateCard(displayedCard);
            displayCardContent(viewModel.getStudyingCardsListPointer_Value(), false);
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

        updateCard(displayedCard);
        displayCardContent(viewModel.getStudyingCardsListPointer_Value(), true);
    }

    private void updateProgressInfo(int pointerIndex) {
        if (pointerIndex < 0) {
            tvStudyProgress.setText("oops: " + pointerIndex);
            return;
        }
        String progressText = (pointerIndex + 1) + " / " + viewModel.getIndexArray_value().size();
        tvStudyProgress.setText(progressText);
    }

    private void updateNavigationButton(int pointerIndex) {
        if (pointerIndex < 0) {
            return;
        }
        previousButton.setVisibility(
                (pointerIndex == 0) ? View.INVISIBLE : View.VISIBLE);
        nextButton.setVisibility(
                (pointerIndex == (viewModel.getIndexArray_value().size() - 1)) ? View.INVISIBLE : View.VISIBLE);
    }

    private void updateCard(CardEntity card) {
        viewModel.updateCard_vm(card, database2Callback);
        List<CardEntity> newList = viewModel.getStudyingCardsList_Value();
        newList.set(viewModel.getIndexArray_value().get(viewModel.getStudyingCardsListPointer_Value()), card);
        viewModel.getStudyingCardsList().setValue(newList);
    }

    private void displayCardContent(int index, boolean scrollReset) {
        if (index < 0) {
            tvCardContentDisplay.setText("nothing to see here");
            return;
        }
        if (scrollReset) {
            scrollViewCardContent.scrollTo(0, 0);
        }
        displayingFront = !(viewModel.getBackSideFirstSetting());
        displayedCard = viewModel.getStudyingCardsList_Value().get(viewModel.getIndexArray_value().get(index));
        tvCardContentDisplay.setText(getCardContentToDisplay());
        tvDisplayedSide.setText(getSideInfoToDisplay());
        tvCardMarkingDisplay.setText(String.valueOf(displayedCard.getMarking0()));

        if (!viewModel.getMarkingSetting().checkIfMatch(displayedCard.getMarking0())) {
            tvCardContentDisplay.setPaintFlags(tvCardContentDisplay.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvCardContentDisplay.setPaintFlags(tvCardContentDisplay.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

    }

    private void onDbTaskResult(Database2Wrapper.DbTask whichTask, Database2Wrapper.DbTaskResult taskResult) {
        Util.logDebug(TAG, "db task: " + whichTask);
        Util.logDebug(TAG, "result: " + taskResult);
    }

    @Override
    public void controlPanelResult(Util.DialogType dialogType, boolean settingChanged, boolean restartRequested) {
        Util.logDebug(TAG, "dialog result from: " + dialogType);
        Util.logDebug(TAG, "setting changed: " + settingChanged);
        Util.logDebug(TAG, "restart???: " + restartRequested);

        controlPanelDialog = null;

        if (restartRequested) {
            reloadDeck();
        } else if (settingChanged) {
            Util.logDebug(TAG, "load setting without restart: ");
            viewModel.fetchPrefSetting();
        }
    }

    private void reloadDeck() {
        viewModel.fetchPrefSetting();
        viewModel.reloadDeck();
        viewModel.resetStudyingCardsListPointer();
    }

    @Override
    public void openMarkingOptionDialog(Util.DialogType dialogType, int currentSetting) {
        showMarkingSelectDialog(currentSetting);
    }

}