package com.android.myapplication8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myapplication8.database2.DeckEntityExtra;
import com.android.myapplication8.database2.DeckEntityInterface;

public class CustomAdapterDecklist_Extra extends ListAdapter<DeckEntityExtra, CustomAdapterDecklist_Extra.DeckItemViewHolder> {

    private static final String TAG = "CustomAdapterDecklist_Extra";

    private CustomAdapterDecklist_ExtraCallback callback;

    public interface CustomAdapterDecklist_ExtraCallback {
        void onItemClick(Util.ClickEvent event, int position);
    }

    public CustomAdapterDecklist_Extra(@NonNull DiffUtil.ItemCallback<DeckEntityExtra> diffCallback,
                                          CustomAdapterDecklist_ExtraCallback callback) {
        super(diffCallback);
        this.callback = callback;
    }

    @NonNull
    @Override
    public DeckItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_deck_in_list, parent, false);
        return new DeckItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckItemViewHolder holder, int position) {
        DeckEntityExtra item = getItem(position);
        holder.bind(item.getDeckName(), item.getCardsCount(), item.getVisitedDate());
        holder.setOnClickListener(new CustomAdapterDeckList.DeckItemViewHolder.DeckItemViewHolderCallback() {
            @Override
            public void onItemClick(Util.ClickEvent event, int position) {
                processItemClick(event, position);
            }
        });
    }

    public void processItemClick(Util.ClickEvent event, int position) {
        callback.onItemClick(event, position);
    }

    static class DeckItemViewHolder extends RecyclerView.ViewHolder {

        public interface DeckItemViewHolderCallback {
            void onItemClick(Util.ClickEvent event, int position);
        }

        public DeckItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setOnClickListener(CustomAdapterDeckList.DeckItemViewHolder.DeckItemViewHolderCallback callback) {
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onItemClick(Util.ClickEvent.CLICK,
                            CustomAdapterDecklist_Extra.DeckItemViewHolder.this.getLayoutPosition());
                }
            });

            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    callback.onItemClick(Util.ClickEvent.LONG_CLICK,
                            CustomAdapterDecklist_Extra.DeckItemViewHolder.this.getAdapterPosition());
                    //todo: which one???
//                    DeckItemViewHolder.this.getAdapterPosition();
//                    DeckItemViewHolder.this.getLayoutPosition();
                    return true;// "true" in order to not trigger onclick
                }
            });
        }

        public void bind(String deckName, int cardsCount, long visitedDate) {
            ((TextView)this.itemView.findViewById(R.id.tv_deck_item_deck_title)).setText(deckName);
            ((TextView)this.itemView.findViewById(R.id.tv_deck_item_cards_count)).setText(String.valueOf(cardsCount));
            ((TextView)this.itemView.findViewById(R.id.tv_deck_item_last_visited_date)).setText(String.valueOf(visitedDate));
        }
    }

    public static class ItemDiff extends DiffUtil.ItemCallback<DeckEntityExtra> {
        @Override
        public boolean areItemsTheSame(@NonNull DeckEntityExtra oldItem, @NonNull DeckEntityExtra newItem) {
            return oldItem.getUid() == newItem.getUid();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DeckEntityExtra oldItem, @NonNull DeckEntityExtra newItem) {
            return oldItem.checkIfSameContentWith(newItem);
        }
    }
}
