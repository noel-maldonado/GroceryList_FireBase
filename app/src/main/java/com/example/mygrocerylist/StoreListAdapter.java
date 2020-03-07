package com.example.mygrocerylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreListViewHolder> {

    private ArrayList<Store> storeList;

    public static class StoreListViewHolder extends RecyclerView.ViewHolder {
        public ImageView storeImage;
        public TextView storeName;


        public StoreListViewHolder(@NonNull View itemView) {
            super(itemView);
            storeImage = itemView.findViewById(R.id.storeImageView);
            storeName = itemView.findViewById(R.id.storeNameTextView);
        }

    }
    public StoreListAdapter(ArrayList<Store> StoreList) {

        storeList = StoreList;

    }

    @NonNull
    @Override
    public StoreListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_store, parent, false);
        StoreListViewHolder slvh = new StoreListViewHolder(v);
        return slvh;
    }



    @Override
    public void onBindViewHolder(@NonNull StoreListAdapter.StoreListViewHolder holder, int position) {

        Store currentStoreList = storeList.get(position);

        holder.storeImage.setImageBitmap(currentStoreList.getStorePicture());
        holder.storeName.setText(currentStoreList.getStoreName());
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }


}
