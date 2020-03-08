package ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrocerylist.R;
import com.squareup.picasso.Picasso;

import model.Store;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private Context context;
    private List<Store> storeList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView storeImage;
        public TextView storeName;

        String userId;
        String userName;
        String storeId;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;

            storeImage = itemView.findViewById(R.id.storeImageView);
            storeName = itemView.findViewById(R.id.storeNameTextView);
        }

    }
    public StoreAdapter(Context context, List<Store> StoreList) {
        this.context = context;
        storeList = StoreList;

    }

    @NonNull
    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_store, parent, false);

        return new ViewHolder(view, context);


    }



    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.ViewHolder viewHolder, int position) {

        Store currentStore = storeList.get(position);
        String imageUrl;
        /*
        Use Picasso Library to download and show image

        Use placeholder just in case Image cant be loaded
        use fit() to fit image into viewholder
         */
        viewHolder.storeName.setText(currentStore.getStoreName());
        imageUrl = currentStore.getStorePictureUrl();
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_store).fit().into(viewHolder.storeImage);


    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }


}
