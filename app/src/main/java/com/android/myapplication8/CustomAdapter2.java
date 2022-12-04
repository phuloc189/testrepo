package com.android.myapplication8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myapplication8.database1.ItemClass1;

//extends ListAdapter<String, >

public class CustomAdapter2 extends ListAdapter<ItemClass1, CustomAdapter2.ListItemViewHolder> {

    public CustomAdapter2(@NonNull DiffUtil.ItemCallback<ItemClass1> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_deck_in_list, parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        ItemClass1 item = getItem(position);
        holder.bind(item.getWord());

    }

    public static class ItemDiff extends DiffUtil.ItemCallback<ItemClass1> {

        @Override
        public boolean areItemsTheSame(@NonNull ItemClass1 oldItem, @NonNull ItemClass1 newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ItemClass1 oldItem, @NonNull ItemClass1 newItem) {
            return ItemClass1.checkIfSameContent(oldItem, newItem);
        }
    }

    static class ListItemViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public ListItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tv_deck_item_deck_title);
        }

        public void bind (String text) { //bind???
            textView.setText(text);
        }
    }


}
