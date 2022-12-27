package com.android.myapplication8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myapplication8.database2.entity.CollectionEntityExtra;
import com.android.myapplication8.interfaces.ViewHolderOnClick;

public class CustomAdapterCollectionList extends ListAdapter<CollectionEntityExtra,
        CustomAdapterCollectionList.CollectionItemViewHolder> implements ViewHolderOnClick {

    CustomAdapterCollectionListCallback callback;

    public interface CustomAdapterCollectionListCallback {
        void onItemClick(Util.ClickEvent event, int position);
    }

    public CustomAdapterCollectionList(@NonNull DiffUtil.ItemCallback<CollectionEntityExtra> diffCallback,
                                       CustomAdapterCollectionListCallback callback) {
        super(diffCallback);
        this.callback = callback;
    }

    @NonNull
    @Override
    public CollectionItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_ui_collection, parent, false);
        return new CollectionItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionItemViewHolder holder, int position) {
        holder.bindData(getItem(position).getCollectionName(),
                getItem(position).getDeckCount());

        holder.setOnClickListener(this);
    }

    @Override
    public void onItemClick(Util.ClickEvent event, int position) {
        callback.onItemClick(event, position);
    }

    public static class CollectionItemDiff extends DiffUtil.ItemCallback<CollectionEntityExtra> {
        @Override
        public boolean areItemsTheSame(@NonNull CollectionEntityExtra oldItem, @NonNull CollectionEntityExtra newItem) {
            return oldItem.getUid() == newItem.getUid();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CollectionEntityExtra oldItem, @NonNull CollectionEntityExtra newItem) {
            return oldItem.checkIfSameContentWith(newItem);
        }
    }

    static class CollectionItemViewHolder extends RecyclerView.ViewHolder {

        public CollectionItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindData(String collectionName, int deckCount) {
            ((TextView) itemView.findViewById(R.id.tv_collection_item_title)).setText(collectionName);
            ((TextView) itemView.findViewById(R.id.tv_collection_item_deck_count)).setText(
                    friendlyDecksCount(deckCount));
        }

        public void setOnClickListener(ViewHolderOnClick callback) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onItemClick(Util.ClickEvent.CLICK, getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    callback.onItemClick(Util.ClickEvent.LONG_CLICK, getAdapterPosition());
                    return true;
                }
            });
        }

        private String friendlyDecksCount(int decksCount) {
            if (decksCount == 0) {
                return "Empty";
            } else if (decksCount == 1) {
                return "Has one deck";
            } else {
                return "Has " + decksCount + " decks";
            }
        }

    }
}
