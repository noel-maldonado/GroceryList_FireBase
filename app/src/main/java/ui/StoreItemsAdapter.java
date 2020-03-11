package ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrocerylist.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Product;

public class StoreItemsAdapter extends RecyclerView.Adapter<StoreItemsAdapter.ViewHolder> {
    private Context context;
    private List<Product> productList;


    public StoreItemsAdapter(Context context, List<Product> product) {
        this.context = context;
        this.productList = product;
    }


    @NonNull
    @Override
    public StoreItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_products, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemsAdapter.ViewHolder viewHolder, int position) {
        Product product = productList.get(position);
        String imageUrl = product.getProductImageUrl();

        Double itemPrice = product.getItemPrice();
        String itemPriceText = "" + itemPrice;
        int quantity = product.getQuantity();
        String quantityText = "" + quantity;
        viewHolder.productPriceTextView.setText(itemPriceText);
        viewHolder.productNameTextView.setText(product.getItemName());
        viewHolder.quantityTextView.setText(quantityText);
        Double total = ((double) quantity) * itemPrice;
        String totalText = "" + total;
        viewHolder.productsTotalTextView.setText(totalText);

        /*
        Use Picasso Library to download and show image
        Use placeholder just in case Image cant be loaded
        use fit() to fit image into viewholder
         */
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_store).fit().into(viewHolder.imageViewProduct);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView productNameTextView, priceTextView, productPriceTextView, quantityTextView, totalTextView, productsTotalTextView;
        public ImageView imageViewProduct;
        public Button buttonAddQuantity, buttonSubtractQuantity;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemView.setOnClickListener(this);

            //Image
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            imageViewProduct.setEnabled(false);
            //TextViews
            priceTextView = itemView.findViewById(R.id.priceTextView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            totalTextView = itemView.findViewById(R.id.totalTextView);
            productsTotalTextView = itemView.findViewById(R.id.productsTotalTextView);
            //Buttons
            buttonAddQuantity = itemView.findViewById(R.id.buttonAddQuantity);
            buttonSubtractQuantity = itemView.findViewById(R.id.buttonSubtractQuantity);

            buttonSubtractQuantity.setOnClickListener(this);
            buttonAddQuantity.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            Product product = productList.get(position);
            int quantity = product.getQuantity();
            switch (v.getId()) {

                case R.id.buttonAddQuantity:
                    product.setQuantity(quantity + 1);
                    break;

                case R.id.buttonSubtractQuantity:
                    product.setQuantity(quantity - 1);
            }



        }
    }


}
