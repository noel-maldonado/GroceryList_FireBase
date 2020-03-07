package com.example.mygrocerylist;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.ViewHolder> {
    private Context context;
    private List<GroceryList> groceryList;

    public GroceryListAdapter(Context context, List<GroceryList> groceryList) {
        this.context = context;
        this.groceryList = groceryList;
    }

    @NonNull
    @Override
    public GroceryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);


        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryListAdapter.ViewHolder viewHolder, int position) {

        GroceryList glist = groceryList.get(position);

        viewHolder.title.setText(glist.getListTitle());

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(glist.getTimeAdded()
                .getSeconds() * 1000);

        viewHolder.dateCreated.setText(timeAgo);

        if(position % 2 == 0) {
            viewHolder.image.setImageResource(R.drawable.ic_store);
        }else {
            viewHolder.image.setImageResource(R.drawable.ic_store_purp);
        }

    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title,
        dateCreated;
        public ImageView image;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.line1TextView);
            dateCreated = itemView.findViewById(R.id.line2TextView);
            image = itemView.findViewById(R.id.imageView);




        }
    }
}
