package ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import model.GroceryList;
import com.example.mygrocerylist.R;
import com.squareup.picasso.Picasso;

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
    public GroceryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item, viewGroup, false);


        return new ViewHolder(view, context);
    }

    @Override       //binds widgets with the data
    public void onBindViewHolder(@NonNull GroceryListAdapter.ViewHolder viewHolder, int position) {

        GroceryList glist = groceryList.get(position);
        String imageUrl;
        imageUrl = glist.getImageUrl();

        viewHolder.title.setText(glist.getListTitle());
        //method used to get time ago like "20 minutes ago, or 5 hours ago"
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(glist.getTimeAdded()
                .getSeconds() * 1000);
        viewHolder.dateCreated.setText(timeAgo);
        /*
        Use Picasso Library to download and show image
        Use placeholder just in case Image cant be loaded
        use fit() to fit image into viewholder
         */
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_store).fit().into(viewHolder.image);


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
        public Button deleteList;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.line1TextView);
            dateCreated = itemView.findViewById(R.id.line2TextView);
            image = itemView.findViewById(R.id.imageView);
            deleteList = itemView.findViewById(R.id.buttonDeleteList);




        }
    }
}
