package com.android.myapplication8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myapplication8.database2.CollectionEntityExtra;

public class CustomAdapterCollectionList extends ListAdapter<CollectionEntityExtra,
        CustomAdapterCollectionList.CollectionItemViewHolder> {


    public CustomAdapterCollectionList(@NonNull DiffUtil.ItemCallback<CollectionEntityExtra> diffCallback) {
        super(diffCallback);
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
        holder.bindData(getItem(position).getCollectionName());
        //todo: callback for item onclick
    }

    public static class CollectionItemDiff extends DiffUtil.ItemCallback<CollectionEntityExtra>{
        @Override
        public boolean areItemsTheSame(@NonNull CollectionEntityExtra oldItem, @NonNull CollectionEntityExtra newItem) {
            return oldItem.getUid() == newItem.getUid();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CollectionEntityExtra oldItem, @NonNull CollectionEntityExtra newItem) {
            return oldItem.checkIfSameContentWith(newItem);
        }
    }

    static class CollectionItemViewHolder extends RecyclerView.ViewHolder{

        public interface CollectionItemVhCallback {
            void onItemClick(Util.ClickEvent event, int position);
        }

        public CollectionItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindData(String collectionName) {
            ((TextView)itemView.findViewById(R.id.tv_collection_item_title)).setText(collectionName);
            //todo: implement deck count
        }

    }
}
