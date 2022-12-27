package com.android.myapplication8.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.android.myapplication8.CustomAdapterCollectionList;
import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.ViewModel1;
import com.android.myapplication8.database2.entity.CollectionEntityExtra;
import com.android.myapplication8.database2.Database2Wrapper;
import com.android.myapplication8.database2.entity.DeckEntity;
import com.android.myapplication8.database2.entity.DeckEntityExtra;
import com.android.myapplication8.interfaces.ConfirmDialogCallback;
import com.android.myapplication8.interfaces.DialogResultCallback;
import com.android.myapplication8.ui.dialog.DialogFragmentConfirm;
import com.android.myapplication8.ui.dialog.DialogFragmentSimpleNameEdit;

import java.util.List;

public class FragmentCollectionList extends Fragment implements DialogResultCallback,
        CustomAdapterCollectionList.CustomAdapterCollectionListCallback, ConfirmDialogCallback {

    public static final String TAG = "FragmentCollectionList";

    Database2Wrapper.Database2Callback dbCallback;

    ViewModel1 viewModel;

    RecyclerView recyclerView;

    int itemClickedPosition;

    String longClickedItemName;

    int longClickedItemUid;

    FragmentCollectionListCallback callback;

    public interface FragmentCollectionListCallback {
        void onCollectionSelected();
    }

    public FragmentCollectionList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_list, container, false);

        setupDatabaseCallback();
        setupList(view);
        setupButton(view);
        setupViewModel();
        readDatabase();

        return view;
    }

    private void readDatabase() {
        viewModel.getAllCollectionExtraLivedata_vm().observe(
                getViewLifecycleOwner(),
                new Observer<List<CollectionEntityExtra>>() {
                    @Override
                    public void onChanged(List<CollectionEntityExtra> collectionEntityExtras) {
                        onListUpdateFromDatabase(collectionEntityExtras);
                    }
                }
        );
    }

    private void onListUpdateFromDatabase(List<CollectionEntityExtra> newData) {
        if (newData != null && newData.size() > 0) {
//            for (CollectionEntityExtra entityExtra: newData) {
//                Util.logDebug(TAG, "item uid: " + entityExtra.getUid());
//                Util.logDebug(TAG, "item uid: " + entityExtra.getUid());
//            }
            recyViewAdapterAlias().submitList(newData);
        } else {
            if (newData == null)
                // todo: display error dialog
                Util.logDebug(TAG, "live data list update error: null list");
            else {
                Util.logDebug(TAG, "live data list update error: no item on list");
                recyViewAdapterAlias().submitList(newData);
            }
        }
    }

    private CustomAdapterCollectionList recyViewAdapterAlias() {
        return (CustomAdapterCollectionList) recyclerView.getAdapter();
    }

    private void setupList(View view) {
        recyclerView = view.findViewById(R.id.recyView_CollectionListScrn_CollectionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(new CustomAdapterCollectionList(
                new CustomAdapterCollectionList.CollectionItemDiff(), this
        ));
    }

    private void setupViewModel() {
        viewModel = (new ViewModelProvider(requireActivity())).get(ViewModel1.class);
    }

    private void setupButton(View view) {
        view.findViewById(R.id.button_collectionList_createNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_NewCollection();
            }
        });
    }

    private void showDialog_NewCollection() {
        DialogFragmentSimpleNameEdit dialog =
                DialogFragmentSimpleNameEdit.newInstance(Util.DialogType.CREATE_COLLECTION, null);
        dialog.show(getChildFragmentManager(), DialogFragmentSimpleNameEdit.TAG);
    }

    private void showDialog_ConfirmDialog(Util.DialogType dialogType) {
        DialogFragmentConfirm dialogFragment = DialogFragmentConfirm.newInstance(dialogType, null);
        dialogFragment.show(getChildFragmentManager(), DialogFragmentConfirm.TAG);
    }

    private void showDialog_Rename(String oldName) {
        Util.logDebug(TAG, "renaming");
        DialogFragmentSimpleNameEdit dialogFragment =
                DialogFragmentSimpleNameEdit.newInstance(Util.DialogType.COLLECTION_RENAME, oldName);
        dialogFragment.show(getChildFragmentManager(), DialogFragmentSimpleNameEdit.TAG);
    }


    private void setupDatabaseCallback() {
        //todo: put more logic in here
        dbCallback = new Database2Wrapper.Database2Callback() {
            @Override
            public void onComplete_SimpleResult(Database2Wrapper.DbTask whichTask, Database2Wrapper.DbTaskResult taskResult) {
                Util.logDebug(TAG, "db task complete: " + whichTask);
            }

            @Override
            public void onSearchDeckComplete(Database2Wrapper.DbTask whichTask, List<DeckEntity> deckSearchResult) {
                Util.logDebug(TAG, "db task complete: " + whichTask);
            }

            @Override
            public void onSearchDeckCompleteExtra(Database2Wrapper.DbTask whichTask, List<DeckEntityExtra> deckSearchResult) {
                Util.logDebug(TAG, "db task complete: " + whichTask);
            }

            @Override
            public void onInsertComplete(Database2Wrapper.DbTask whichTask, long newRowId) {
                Util.logDebug(TAG, "db task complete: " + whichTask);
            }

            @Override
            public void onGetDeckResult(Database2Wrapper.DbTask whichTask, DeckEntity deck) {

            }
        };
    }

    @Override
    public void onDialogResult_NewText(Util.DialogType dialogType, String text) {
        if (dialogType == Util.DialogType.CREATE_COLLECTION) {
            viewModel.createCollection_vm(text, dbCallback);
        } else if (dialogType == Util.DialogType.COLLECTION_RENAME) {
            viewModel.renameCollection_vm(longClickedItemUid, text, dbCallback);
        }
    }

    @Override
    public void onDialogResult_Confirm(Util.DialogType dialogType) {
        if (dialogType == Util.DialogType.CONFIRM_COLLECTION_DELETE) {
            viewModel.deleteCollection_vm(longClickedItemUid, dbCallback);
        }
    }

    private void showPopUpMenu(View view) {
        PopupMenu menu = new PopupMenu(requireContext(), view);
        menu.getMenuInflater().inflate(R.menu.collection_list_item_option_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_item_collection_delete) {
                    showDialog_ConfirmDialog(Util.DialogType.CONFIRM_COLLECTION_DELETE);
                } else if (menuItem.getItemId() == R.id.menu_item_collection_rename) {
                    showDialog_Rename(longClickedItemName);
                }
                return false;
            }
        });
        menu.show();
    }


    @Override
    public void onItemClick(Util.ClickEvent event, int position) {
        if (event == Util.ClickEvent.CLICK) {
            itemClickedPosition = position;
            viewModel.setSelectedCollectionUid(
                    recyViewAdapterAlias().getCurrentList().get(position).getUid()
            );
            callback.onCollectionSelected();
        } else if (event == Util.ClickEvent.LONG_CLICK) {
            itemClickedPosition = position;
            longClickedItemName = recyViewAdapterAlias().getCurrentList().get(position).getCollectionName();
            longClickedItemUid = recyViewAdapterAlias().getCurrentList().get(position).getUid();
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
            showPopUpMenu(viewHolder.itemView);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (FragmentCollectionListCallback) context;
        } catch (Exception e) {
            Util.logError(TAG, "exception happened: " + e);
            callback = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}