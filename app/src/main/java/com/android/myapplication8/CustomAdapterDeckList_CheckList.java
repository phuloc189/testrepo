package com.android.myapplication8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myapplication8.database2.entity.DeckEntityExtra_CollectionCheckList;
import com.android.myapplication8.interfaces.ViewHolderOnClick_CheckList;

import java.util.Arrays;
import java.util.TreeSet;

public class CustomAdapterDeckList_CheckList
        extends ListAdapter<DeckEntityExtra_CollectionCheckList, CustomAdapterDeckList_CheckList.DeckItemViewHolder>
implements ViewHolderOnClick_CheckList {
    public static final String TAG = "CustomAdapterDeckList_CheckList";
    TreeSet<Integer> removeList;
    TreeSet<Integer> addList;

    public CustomAdapterDeckList_CheckList(@NonNull DiffUtil.ItemCallback<DeckEntityExtra_CollectionCheckList> diffCallback) {
        super(diffCallback);
        removeList = new TreeSet<>();
        addList = new TreeSet<>();
    }

    @NonNull
    @Override
    public DeckItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_ui_deck_selectable, parent, false);
        return new DeckItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckItemViewHolder holder, int position) {
        holder.bindData(getItem(position).getDeckName(),
                (getItem(position).getCollectionUid() > 0)? (!removeList.contains(getItem(position).getUid())) : (addList.contains(getItem(position).getUid())));
        holder.setOnItemClickListener(this);
    }

    @Override
    public void onItemCheckChange(int position, boolean newValue) {
        int uid = getItem(position).getUid();
        if (newValue) {
            if (removeList.contains(uid)) {
                Util.logDebug(TAG, "undoing remove for: " + uid);
                removeList.remove(uid);
            } else {
                Util.logDebug(TAG, "registering on add list for: " + uid);
                addList.add(uid);
            }

        } else {
            if (addList.contains(uid)) {
                Util.logDebug(TAG, "undoing add for: " + uid);
                addList.remove(uid);
            } else {
                Util.logDebug(TAG, "registering on remove list for: " + uid);
                removeList.add(uid);
            }
        }
    }

    public Integer[] getAddList() {
        return Arrays.copyOf(addList.toArray(), addList.size(), Integer[].class);
    }

    public Integer[] getRemoveList() {
        return Arrays.copyOf(removeList.toArray(), removeList.size(), Integer[].class);
    }

    static class DeckItemViewHolder extends RecyclerView.ViewHolder {

        CheckBox chkBox;

        public DeckItemViewHolder(@NonNull View itemView) {
            super(itemView);
            chkBox = itemView.findViewById(R.id.chkBox_deckListItem_included);
        }

        public void bindData(String deckName, boolean isInCollection) {
            ((TextView)itemView.findViewById(R.id.tv_deck_item_deck_title)).setText(deckName);
            ((CheckBox)itemView.findViewById(R.id.chkBox_deckListItem_included)).setChecked(isInCollection);
        }

        public void setOnItemClickListener(ViewHolderOnClick_CheckList callback) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onItemCheckChange(getAdapterPosition(), !chkBox.isChecked());
                    chkBox.setChecked(!chkBox.isChecked());
                }
            });
        }
    }

    public static class DeckItemDiff extends DiffUtil.ItemCallback<DeckEntityExtra_CollectionCheckList> {
        @Override
        public boolean areItemsTheSame(@NonNull DeckEntityExtra_CollectionCheckList oldItem, @NonNull DeckEntityExtra_CollectionCheckList newItem) {
            return oldItem.getUid() == newItem.getUid();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DeckEntityExtra_CollectionCheckList oldItem, @NonNull DeckEntityExtra_CollectionCheckList newItem) {
            return oldItem.checkIfSameContentWith(newItem);
        }
    }
}
