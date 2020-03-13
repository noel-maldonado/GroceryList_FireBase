package ui;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import model.GroceryList;
import model.StoreProduct;
import util.GListApi;

import com.example.mygrocerylist.ListItemsSavedActivity;
import com.example.mygrocerylist.ListStoreActivity;
import com.example.mygrocerylist.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.ViewHolder> {

    //Tag used for Log
    private static final String TAG = "GroceryListAdapter";

    private Context context;
    private List<GroceryList> groceryList;
    private String currentUserID;
    private String currentUserName;
    private String listTitle;
    private String glistId;
    private int position;

    //connection to FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Collection Reference
    private CollectionReference G_L_Ref = db.collection("Grocery List");


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


//        if(position % 2 == 0) {
//            viewHolder.image.setImageResource(R.drawable.ic_store);
//        }else {
//            viewHolder.image.setImageResource(R.drawable.ic_store_purp);
//        }

    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title,
        dateCreated;
        public ImageView image;
        public Button deleteList;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemView.setOnClickListener(this);

            title = itemView.findViewById(R.id.line1TextView);
            dateCreated = itemView.findViewById(R.id.line2TextView);
            image = itemView.findViewById(R.id.imageView);
            deleteList = itemView.findViewById(R.id.buttonDeleteList);




        }

        @Override
        public void onClick(View v) {
            position = getAdapterPosition();
            GroceryList current = groceryList.get(position);
            listTitle = current.getListTitle();
            glistId = current.getGlistId();


            if(GListApi.getInstance() != null) {
                currentUserID = GListApi.getInstance().getUserId();
                currentUserName = GListApi.getInstance().getUsername();
            } else {
                currentUserID = current.getUserId();
                currentUserName = current.getUsername();
            }

            GListApi gListApi = GListApi.getInstance();
            gListApi.setUsername(currentUserName);
            gListApi.setUserId(currentUserID);
            gListApi.setGlistId(glistId);
            gListApi.setListTitle(listTitle);
            DocumentReference documentReference = G_L_Ref.document("" + current.getGlistId());



            G_L_Ref.document("" + glistId).collection("Store_Product").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int size = 0;
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                size++;
                            }
                            if(size  > 1) {
                                Intent intent = new Intent(context, ListItemsSavedActivity.class);
                                context.startActivity(intent);

                            }else {
                                Intent intent = new Intent(context, ListStoreActivity.class);
                                context.startActivity(intent);


                            }
                        }
                    });



        }


    }







    public void deleteItem() {
        GroceryList current = groceryList.get(position);
        final String document = current.getGlistId();

        G_L_Ref.document("" + document).collection("Store_Product").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Get the a snapshot for evey Document in the Store_Product Subcollection
                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                    StoreProduct storeProduct = documentSnapshot.toObject(StoreProduct.class);
                    final String docId = storeProduct.getDocumentReference();
                    G_L_Ref.document("" + document).collection("Store_Product")
                            .document("" + docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "SubCollection Document delete: " + docId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Failed to Delete Sub Collection: " + docId +"\n " +e.getMessage() );
                        }
                    });


                }




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        G_L_Ref.document("" + document).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Succesfully delete" + document);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to Delete" + e.getMessage());
                    }
                });


    }

}
