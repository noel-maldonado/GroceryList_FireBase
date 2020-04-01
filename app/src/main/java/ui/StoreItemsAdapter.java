package ui;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrocerylist.R;
import com.example.mygrocerylist.listMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Product;
import model.StoreProduct;
import util.GListApi;

public class StoreItemsAdapter extends RecyclerView.Adapter<StoreItemsAdapter.ViewHolder> {


    private Context context;
    private List<Product> productList;
    private String currentUserID;
    private String currentUserName;
    private String listTitle;
    private String glistId;
    private String storeId;
    private String storeName;
    private String storePictureUrl;

    //connection to FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Grocery List Collection Reference
    private CollectionReference groceryListProductCollectionReference = db.collection("Grocery List");

    private String TAG = "StoreItemsAdapter";


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
        viewHolder.quantityTextView.setText("" + product.getQuantity());
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
            //adding on click listeners to buttons
            buttonSubtractQuantity.setOnClickListener(this);
            buttonAddQuantity.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            //get item position
            int position = getAdapterPosition();
            //create a Product Object from the Product List Array using the items position
            Product product = productList.get(position);
            //get the quantity of the object
            int quantity = product.getQuantity();

            switch (v.getId()) {

                case R.id.buttonAddQuantity:
                    product.setQuantity(product.getQuantity() + 1);
                    notifyItemChanged(position);
                    Log.d(TAG, "Add Button: " +getAdapterPosition());


                    break;

                case R.id.buttonSubtractQuantity:
                    if (product.getQuantity() > 0) {
                        product.setQuantity(product.getQuantity() - 1);
                        notifyItemChanged(position);
                        Log.d(TAG, "Subtract Button: " + getAdapterPosition());



                    }
                    break;
            }

        }
    }


    public void saveBtn() {

        Log.d(TAG, "saveBtn method ran");
        if(GListApi.getInstance() != null) {
            currentUserID = GListApi.getInstance().getUserId();
            currentUserName = GListApi.getInstance().getUsername();
            listTitle = GListApi.getInstance().getListTitle();
            glistId = GListApi.getInstance().getGlistId();
            storeName = GListApi.getInstance().getStoreName();
            storeId = GListApi.getInstance().getStoreId();
            storePictureUrl = GListApi.getInstance().getStorePictureUrl();
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

        final String document = gListApi.getGlistId();

        //Using for loop to look through each Product in the Array List
        for(int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            if(product.getQuantity() > 0) {

                StoreProduct storeProduct = new StoreProduct();
                storeProduct.setItemId(product.getItemId());
                storeProduct.setItemName(product.getItemName());
                storeProduct.setItemPrice(product.getItemPrice());
                storeProduct.setProductImageUrl(product.getProductImageUrl());
                storeProduct.setQuantity(product.getQuantity());
                storeProduct.setStoreId(gListApi.getStoreId());
                storeProduct.setStoreName(gListApi.getStoreName());
                storeProduct.setStorePictureUrl(gListApi.getStorePictureUrl());
                groceryListProductCollectionReference.document("" + document)
                        .collection("Store_Product")
                        .add(storeProduct).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(final DocumentReference documentReference) {
                        Log.d(TAG, "Successfully Added Sub Collection Document");
                        String listId = documentReference.getId();
                        documentReference.update("documentReference", listId);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to add Sub Collection");

                    }
                });
            }

        }
        Intent intent = new Intent(context, listMainActivity.class);
        context.startActivity(intent);

    }

}
