package com.android.myapplication8.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.myapplication8.CustomAdapterCollectionList;
import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.ViewModel1;
import com.android.myapplication8.database2.CollectionEntityExtra;
import com.android.myapplication8.database2.Database2Wrapper;
import com.android.myapplication8.database2.DeckEntity;
import com.android.myapplication8.database2.DeckEntityExtra;
import com.android.myapplication8.interfaces.DialogResultCallback;

import java.util.List;

public class FragmentCollectionList extends Fragment implements DialogResultCallback,
        CustomAdapterCollectionList.CustomAdapterCollectionListCallback {

    public static final String TAG = "FragmentCollectionList";

    Database2Wrapper.Database2Callback dbCallback;

    ViewModel1 viewModel;

    RecyclerView recyclerView;

    int itemClickedPosition;

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
        if (newData != null && newData.size() > 0){
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
        return (CustomAdapterCollectionList)recyclerView.getAdapter();
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
                showNewCollectionDialog();
            }
        });
    }

    private void showNewCollectionDialog() {
        DialogFragmentSimpleNameEdit dialog = new DialogFragmentSimpleNameEdit();
        Bundle args = new Bundle();
        args.putString(Util.BUNDLE_KEY_DIALOGTYPE,
                Util.getDialogTypeStringFromDialogType(Util.DialogType.CREATE_COLLECTION));
        dialog.setArguments(args);
        dialog.show(getChildFragmentManager(), DialogFragmentSimpleNameEdit.TAG);
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
        };
    }

    @Override
    public void onDialogResult_NewText(Util.DialogType dialogType, String text) {
        if (dialogType == Util.DialogType.CREATE_COLLECTION) {
            viewModel.createCollection_vm(text, dbCallback);
        }
    }

    @Override
    public void onItemClick(Util.ClickEvent event, int position) {
        /*
            todo: implement
                delete,
                rename,
                confirm dialog
         */
        if (event == Util.ClickEvent.CLICK) {
            itemClickedPosition = position;
            viewModel.setSelectedCollectionUid(
                    recyViewAdapterAlias().getCurrentList().get(position).getUid()
            );
            callback.onCollectionSelected();
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