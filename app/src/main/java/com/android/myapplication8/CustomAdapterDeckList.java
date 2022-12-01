package com.android.myapplication8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myapplication8.database2.DeckEntity;

public class CustomAdapterDeckList extends ListAdapter<DeckEntity, CustomAdapterDeckList.DeckItemViewHolder>{

    private static final String TAG = "CustomAdapterDeckList";

    private CustomAdapter3Callback callback;

    public interface CustomAdapter3Callback {
        void customAdapter3OnItemClick(Util.ClickEvent event, int position);
//        void customAdapter3OnItemClick(Util.ClickEvent event, int position ,DeckEntity deck);
    }

    public CustomAdapterDeckList(@NonNull DiffUtil.ItemCallback<DeckEntity> diffCallback,
                                 CustomAdapter3Callback callback) {
        super(diffCallback);
        this.callback = callback;
    }

    @NonNull
    @Override
    public DeckItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_layout, parent, false);
        return new DeckItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckItemViewHolder holder, int position) {
        Util.logDebug(TAG, "onBindViewHolder called: " + position);
        DeckEntity deck = getItem(position);
        holder.bind(deck.getDeckName());
        // need position then holder.getAdapterPosition() is the way
        holder.setOnClickListener(new DeckItemViewHolder.DeckItemViewHolderCallback() {
            @Override
            public void onItemClick(Util.ClickEvent event, int position) {
                Util.logDebug(TAG, "item clicked at: " + position +
                        ", click type: " + event);
                processItemClick(event, position);
            }
        });
    }

    public void processItemClick(Util.ClickEvent event, int position) {
        callback.customAdapter3OnItemClick(event, position);
    }

    static class DeckItemViewHolder extends RecyclerView.ViewHolder {
//        View itemView; // we don't need this, the base class already have one
        TextView textView;

        public interface DeckItemViewHolderCallback {
            void onItemClick(Util.ClickEvent event, int position);
        }

        public DeckItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.tv_item_text);
        }

        public void setOnClickListener(DeckItemViewHolderCallback callback) {
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onItemClick(Util.ClickEvent.CLICK,
                            DeckItemViewHolder.this.getLayoutPosition());
                }
            });

            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    callback.onItemClick(Util.ClickEvent.LONG_CLICK,
                            DeckItemViewHolder.this.getLayoutPosition());
                    //todo: which one???
//                    DeckItemViewHolder.this.getLayoutPosition();
//                    DeckItemViewHolder.this.getLayoutPosition();
                    return true;// "true" in order to not trigger onclick
                }
            });
        }

        public void bind(String text) {
            this.textView.setText(text);
        }
    }

    public static class DeckItemDiff extends DiffUtil.ItemCallback<DeckEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull DeckEntity oldItem, @NonNull DeckEntity newItem) {
            //mimicking card list adapter
//            return oldItem == newItem;
            return oldItem.getUid() == newItem.getUid();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DeckEntity oldItem, @NonNull DeckEntity newItem) {
            return DeckEntity.checkIfSameContent(oldItem, newItem);
        }
    }
}
