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
import android.widget.Button;

import com.android.myapplication8.CustomAdapterDeckList_CheckList;
import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.ViewModel1;
import com.android.myapplication8.database2.Database2Wrapper;
import com.android.myapplication8.database2.DeckEntity;
import com.android.myapplication8.database2.DeckEntityExtra;
import com.android.myapplication8.database2.DeckEntityExtra_CollectionCheckList;

import java.util.List;

public class FragmentDeckAddRemoveForCollection extends Fragment {

    public static final String TAG = "FragmentDeckAddRemoveForCollection";

    ViewModel1 viewModel;

    Database2Wrapper.Database2Callback databaseCallback;

    RecyclerView recyclerView;

    FragmentDeckAddRemoveForCollectionCallback callback;

    int dbResultCount = 0;

    public interface FragmentDeckAddRemoveForCollectionCallback{
        void settingComplete();
    }

    public FragmentDeckAddRemoveForCollection() {
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_deck_add_remove_for_collection, container, false);

        setupDatabaseCallback();
        setupListUi(view);
        setupButton(view);
        setupViewModel();
        readDatabase();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (FragmentDeckAddRemoveForCollectionCallback) context;
        } catch (Exception e ) {
            Util.logDebug(TAG, "exception: " + e);
            callback = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel1.class);
    }

    private void setupDatabaseCallback() {
        databaseCallback = new Database2Wrapper.Database2Callback() {
            @Override
            public void onComplete_SimpleResult(Database2Wrapper.DbTask whichTask, Database2Wrapper.DbTaskResult taskResult) {
                dbResultCount++;
                if (dbResultCount ==2) {
                    callback.settingComplete();
                }
            }

            @Override
            public void onSearchDeckComplete(Database2Wrapper.DbTask whichTask, List<DeckEntity> deckSearchResult) {
                //todo: implement shit here
            }

            @Override
            public void onSearchDeckCompleteExtra(Database2Wrapper.DbTask whichTask, List<DeckEntityExtra> deckSearchResult) {
                //todo: implement shit here
            }

            @Override
            public void onInsertComplete(Database2Wrapper.DbTask whichTask, long newRowId) {
                //todo: implement shit here
            }
        };
    }

    private void setupListUi(View view) {
        recyclerView = view.findViewById(R.id.recyclerView_item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new CustomAdapterDeckList_CheckList(new CustomAdapterDeckList_CheckList.DeckItemDiff()));
    }


    private CustomAdapterDeckList_CheckList recyViewAdapterAlias() {
        return (CustomAdapterDeckList_CheckList)recyclerView.getAdapter();
    }


    private void setupButton(View view) {
        Button confirmButton = view.findViewById(R.id.button_add_remove_screen_confirm);
        Button cancelButton = view.findViewById(R.id.button_add_remove_screen_cancel);
        // todo: do something with these button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.insertDecksToCollection_vm(
                        viewModel.getSelectedCollectionUid_Value(),
                        recyViewAdapterAlias().getAddList(),
                        databaseCallback);
                viewModel.removeDecksFromCollection_vm(
                        viewModel.getSelectedCollectionUid_Value(),
                        recyViewAdapterAlias().getRemoveList(),
                        databaseCallback);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.settingComplete();
            }
        });
    }

    private void readDatabase() {
        viewModel.getAllLiveData_CollectionChecklist_vm(viewModel.getSelectedCollectionUid_Value())
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<List<DeckEntityExtra_CollectionCheckList>>() {
                            @Override
                            public void onChanged(List<DeckEntityExtra_CollectionCheckList> deckEntityExtra_collectionCheckLists) {
                                onListDataUpdated(deckEntityExtra_collectionCheckLists);
                            }
                        }
                );
    }

    private void onListDataUpdated(List<DeckEntityExtra_CollectionCheckList> newList) {
        if (newList != null && newList.size() > 0) {
            recyViewAdapterAlias().submitList(newList);
        } else {
            Util.logDebug(TAG, "something is wrong with the list");
        }
    }






}