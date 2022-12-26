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

import com.android.myapplication8.database2.entity.CardEntity;
import com.android.myapplication8.interfaces.CardListAdapterOnClick;
import com.android.myapplication8.interfaces.ViewHolderCheckBoxInterface;
import com.android.myapplication8.interfaces.ViewHolderOnClick;

import java.util.Arrays;
import java.util.TreeSet;

public class CustomAdapterCardList extends ListAdapter<CardEntity, CustomAdapterCardList.CardItemViewHolder>
        implements ViewHolderOnClick, ViewHolderCheckBoxInterface {

    public static final String TAG = "CustomAdapterCardList";

    CardListAdapterOnClick callback;

    private static final int VIEW_TYPE_NORMAL = 0;

    private static final int VIEW_TYPE_SELECT_MODE = 1;

    TreeSet<Integer> selectedUid;

    public CustomAdapterCardList(@NonNull DiffUtil.ItemCallback<CardEntity> diffCallback,
                                 CardListAdapterOnClick callback) {
        super(diffCallback);
        this.callback = callback;
        selectedUid = new TreeSet<>();
    }

    @NonNull
    @Override
    public CardItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ui_card, parent, false);
        return new CardItemViewHolder(view, selectMode);
    }

    @Override
    public void onBindViewHolder(@NonNull CardItemViewHolder holder, int position) {
        CardEntity cardEntity = getItem(position);
        holder.bindHolderWithContent(cardEntity.getFrontText(), cardEntity.getBackText()
                , cardEntity.getMarking0());

        if (selectMode && holder.getItemViewType() == VIEW_TYPE_SELECT_MODE) {
            holder.setSelected(selectedUid.contains(getItem(position).getUid()));
            holder.setCheckBoxListener(this);
        } else if (!selectMode && holder.getItemViewType() == VIEW_TYPE_NORMAL) {
            holder.setOnClickListener(this);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Util.logDebug(TAG, "getItemViewType: " + (selectMode ? VIEW_TYPE_SELECT_MODE : VIEW_TYPE_NORMAL));
        return selectMode ? VIEW_TYPE_SELECT_MODE : VIEW_TYPE_NORMAL;
    }

    private boolean selectMode = false;

    public void enableSelectMode() {
        this.selectMode = true;
        this.notifyDataSetChanged();
    }

    public void disableSelectMode() {
        this.selectMode = false;
        selectedUid.clear();
        this.notifyDataSetChanged();
    }

    public Integer[] getSelectedUids() {
        return Arrays.copyOf(selectedUid.toArray(), selectedUid.size(), Integer[].class);
    }

    @Override
    public void onItemClick(Util.ClickEvent event, int position) {
        Util.logDebug(TAG, "onItemClick");
        callback.cardListAdapterOnItemClick(event, position, getItem(position));
    }

    @Override
    public void onCheckbox(int position, boolean value) {
        if (value && selectedUid.contains(getItem(position).getUid())) {
            Util.logDebug(TAG, "oncheckbox: something is wrong: about to include it's already there");
            return;
        } else if (!value && !selectedUid.contains(getItem(position).getUid())) {
            Util.logDebug(TAG, "oncheckbox: something is wrong: about to remove it's not there");
            return;
        }
        if (value) {
            selectedUid.add(getItem(position).getUid());
        } else {
            selectedUid.remove(getItem(position).getUid());
        }
    }

    static class CardItemViewHolder extends RecyclerView.ViewHolder {
        private TextView frontText;
        private TextView backText;
        private TextView marking;
        private CheckBox checkBoxItemSelected;
        private boolean selectMode = false;

        public CardItemViewHolder(@NonNull View itemView, boolean selectModeOn) {
            super(itemView);

            frontText = itemView.findViewById(R.id.textView_cardList_cardItem_frontText);
            backText = itemView.findViewById(R.id.textView_cardList_cardItem_backText);
            marking = itemView.findViewById(R.id.textView_cardList_cardItem_marking);
            checkBoxItemSelected = itemView.findViewById(R.id.chkBox_cardListItem_itemSelected);
            selectMode = selectModeOn;
            if (!selectModeOn) {
                checkBoxItemSelected.setVisibility(View.GONE);
            }
        }

        public void bindHolderWithContent(String frontText, String backText, int marking) {
            this.frontText.setText(frontText);
            this.backText.setText(backText);
            this.marking.setText(String.valueOf(marking));
        }

        public void setOnClickListener(ViewHolderOnClick callback) {

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    callback.onItemClick(Util.ClickEvent.LONG_CLICK, getLayoutPosition());
                    return true;
                }
            });
        }

        public void setCheckBoxListener(ViewHolderCheckBoxInterface callback) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onCheckbox(getAdapterPosition(), !checkBoxItemSelected.isChecked());
                    checkBoxItemSelected.setChecked(!checkBoxItemSelected.isChecked());
                }
            });
        }

        public void setSelected(boolean value) {
            checkBoxItemSelected.setChecked(value);
        }
    }

    public static class CardItemDiff extends DiffUtil.ItemCallback<CardEntity> {
        /*

        # DiffUtil.ItemCallback
        https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil.ItemCallback
            areContentsTheSame
                "Called to check whether two items have the same data.
                This information is used to detect if the contents of an item have changed.
                This method to check equality instead of equals so
                that you can change its behavior depending on your UI.
                For example, if you are using DiffUtil with a RecyclerView.Adapter,
                you should return whether the items' visual representations are the same.
                This method is called only if areItemsTheSame returns true for these items."

            areItemsTheSame
                "Called to check whether two objects represent the same item.
                For example, if your items have unique ids,
                this method should check their id equality."
         */

        @Override
        public boolean areItemsTheSame(@NonNull CardEntity oldItem, @NonNull CardEntity newItem) {
            return oldItem.getUid() == newItem.getUid();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CardEntity oldItem, @NonNull CardEntity newItem) {
            return CardEntity.checkIfSameContent(oldItem, newItem);
        }
    }
}
