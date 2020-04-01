package com.example.mygrocerylist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import model.Product;
import model.StoreProduct;
import ui.StoreItemsSavedAdapter;
import util.GListApi;

public class ListItemsSavedActivity extends AppCompatActivity {

    //Contains our List of Saved Products
    private List<StoreProduct> storeProducts;

    //Tag used for Log
    private static final String TAG = "ListItemsSavedActivity";

    private RecyclerView recyclerView;
    private StoreItemsSavedAdapter adapter;

    //connection to FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Declare Connection to Firebase Authentication Server
    private FirebaseAuth mAuth;


    private FirebaseUser currentUser;
    private String currentUserID;
    private String currentUserName;

    //GroceryList Reference
    private CollectionReference groceryListReference = db.collection("Grocerly List");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items_saved);

        mAuth = FirebaseAuth.getInstance();

        if(GListApi.getInstance() != null) {

            currentUserID = GListApi.getInstance().getUserId();

            currentUserName = GListApi.getInstance().getUsername();
        }

        storeProducts = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewSavedItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onResume() {
        super.onResume();

        currentUser = mAuth.getCurrentUser();

        final List<StoreProduct> storeProducts = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewSavedItems);
        //gets the current documents ID
        String documentId = GListApi.getInstance().getGlistId();

        groceryListReference.document("" + documentId)
                .collection("Store_Product").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot savedList : queryDocumentSnapshots) {
                                StoreProduct storeProduct = savedList.toObject(StoreProduct.class);
                                storeProducts.add(storeProduct);
                            }
                            adapter = new StoreItemsSavedAdapter(ListItemsSavedActivity.this, storeProducts);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Empty queryDocumentSnapshot");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to Retrieve Document: " + e.getMessage());
            }
        });


    }
}
