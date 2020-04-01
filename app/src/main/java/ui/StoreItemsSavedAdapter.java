package ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrocerylist.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.StoreProduct;

public class StoreItemsSavedAdapter extends RecyclerView.Adapter<StoreItemsSavedAdapter.ViewHolder> {


    private static final String TAG = "StoreItemsSavedAdapter";
    private List<StoreProduct> storeProducts;
    private int position;
    private Context context;

    //connection to FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Collection Reference


    public StoreItemsSavedAdapter(Context context, List<StoreProduct> storeProducts) {

        this.context = context;
        this.storeProducts = storeProducts;

    }

    @NonNull
    @Override
    public StoreItemsSavedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_saved_products, viewGroup, false);

        return new StoreItemsSavedAdapter.ViewHolder(view, context);
    }


    //String strDouble = String.format("%.2f", 1.23456);

    @Override
    public void onBindViewHolder(@NonNull StoreItemsSavedAdapter.ViewHolder viewHolder, int position) {

        StoreProduct storeProduct = storeProducts.get(position);
        String imageUrl;
        imageUrl = storeProduct.getProductImageUrl();

        Double itemPrice = storeProduct.getItemPrice();
        String itemPriceText = String.format("%.2f", itemPrice);
        int quantity = storeProduct.getQuantity();
        Double total = ((double) quantity) * itemPrice;
        String totalText = String.format("%.2f", total);
        //TextViews
        viewHolder.productPriceTotal.setText(totalText);
        viewHolder.productPrice.setText(itemPriceText);
        viewHolder.productQuantity.setText("" + quantity);
        viewHolder.productName.setText(storeProduct.getItemName());
        //ImageView
        /*
        Use Picasso Library to download and show image
        Use placeholder just in case Image cant be loaded
        use fit() to fit image into viewholder
         */
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_store).fit().into(viewHolder.productImage);


    }

    @Override
    public int getItemCount() {
        return storeProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Add items in the Layout here
        public TextView productName, productPrice, productQuantity, productPriceTotal;
        public ImageView productImage;
        public CheckBox productCheckBox;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            //Image
            productImage = itemView.findViewById(R.id.imageViewProductSaved);
            //TextViews
            productName = itemView.findViewById(R.id.productSavedNameTextView);
            productPrice = itemView.findViewById(R.id.productSavedPriceTextView);
            productQuantity = itemView.findViewById(R.id.quantityTextViewSaved);
            productPriceTotal = itemView.findViewById(R.id.productsSavedTotalTextView);

            productCheckBox = itemView.findViewById(R.id.checkBox);
            productCheckBox.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            //get item position
            int position = getAdapterPosition();


        }
    }



}
