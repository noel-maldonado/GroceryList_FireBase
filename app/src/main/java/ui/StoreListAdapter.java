package ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrocerylist.ListStoreItemsActivity;
import com.example.mygrocerylist.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Store;
import util.GListApi;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder>{
    private Context context;
    private List<Store> storeList;
    private String currentUserID;
    private String currentUserName;
    private String listTitle;
    private String glistId;

    public StoreListAdapter(Context context, List<Store> store) {
        this.context = context;
        this.storeList = store;
    }


    @NonNull
    @Override
    public StoreListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_store, viewGroup, false);

        return new ViewHolder(view, context);
    }


    @Override
    public void onBindViewHolder(@NonNull StoreListAdapter.ViewHolder viewHolder, int position) {
        Store store = storeList.get(position);
        String imageUrl;
        imageUrl = store.getStorePictureUrl();

        viewHolder.title.setText(store.getStoreName());
        /*
        Use Picasso Library to download and show image
        Use placeholder just in case Image cant be loaded
        use fit() to fit image into viewholder
         */
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_store).fit().into(viewHolder.image);

    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public ImageView image;



        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemView.setOnClickListener(this);

            title = itemView.findViewById(R.id.storeNameTextView);
            image = itemView.findViewById(R.id.storeImageView);
            image.setEnabled(false);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Store current = storeList.get(position);

            String storeId = current.getStoreId();
            String storeName = current.getStoreName();
            String storePictureUrl = current.getStorePictureUrl();

            if(GListApi.getInstance() != null) {
                currentUserID = GListApi.getInstance().getUserId();
                currentUserName = GListApi.getInstance().getUsername();
                listTitle = GListApi.getInstance().getListTitle();
                glistId = GListApi.getInstance().getGlistId();
            }

            GListApi gListApi = GListApi.getInstance();
            assert gListApi != null;
            gListApi.setUsername(currentUserName);
            gListApi.setUserId(currentUserID);
            gListApi.setGlistId(glistId);
            gListApi.setListTitle(listTitle);
            gListApi.setStoreId(storeId);
            gListApi.setStoreName(storeName);
            gListApi.setStorePictureUrl(storePictureUrl);

            Intent intent = new Intent(context, ListStoreItemsActivity.class);
            context.startActivity(intent);
        }

    }
}
