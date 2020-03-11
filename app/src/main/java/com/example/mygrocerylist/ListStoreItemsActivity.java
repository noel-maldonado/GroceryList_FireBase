package com.example.mygrocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import ui.StoreItemsAdapter;
import util.GListApi;

public class ListStoreItemsActivity extends AppCompatActivity {

    private String listTitle;
    private String glistId;


    //Tag used for Log
    private static final String TAG = "ListStoreItemsActivity";

    private RecyclerView recyclerView;
    private StoreItemsAdapter adapter;

    //connection to FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Declare Connection to Firebase Authentication Server
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;


    private String currentUserID;
    private String currentUserName;

    private CollectionReference productCollectionReference = db.collection("Product");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_items);
        initHomeListButton();

        mAuth = FirebaseAuth.getInstance();

        if(GListApi.getInstance() != null) {
            currentUserID = GListApi.getInstance().getUserId();
            currentUserName = GListApi.getInstance().getUsername();
            listTitle = GListApi.getInstance().getListTitle();
            glistId = GListApi.getInstance().getGlistId();
        }

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




        //Instantiating Authentication Listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {

                }else {

                }
            }
        };

    }


    @Override
    protected void onResume() {
        super.onResume();

        final List<Product> productList = new ArrayList<>();
        currentUser = mAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productCollectionReference.whereEqualTo("productStore", GListApi.getInstance().getStoreId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()) {

                            for(QueryDocumentSnapshot prodList : queryDocumentSnapshots) {
                                Product product = prodList.toObject(Product.class);
                                productList.add(product);

                            }
                            adapter = new StoreItemsAdapter(ListStoreItemsActivity.this, productList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Empty queryDocumentSnapshot");


                        }





                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });




    }

    private void initHomeListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.listImageButtonItems);
        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListStoreItemsActivity.this, listMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }











}
