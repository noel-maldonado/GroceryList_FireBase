package com.example.mygrocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.Arrays;
import java.util.List;

import model.Store;
import model.StoreProduct;
import ui.StoreListAdapter;
import util.GListApi;

public class ListStoreActivity extends AppCompatActivity {

    private List<Store> storeList;
    //Name and Id of (Parent) GroceryList
    private String listTitle;
    private String glistId;



    //Tag used for Log
    private static final String TAG = "ListStoreActivity";

    private RecyclerView recyclerView;
    private StoreListAdapter adapter;

    //connection to FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Declare Connection to Firebase Authentication Server
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private String currentUserID;
    private String currentUserName;

    //Collection Reference
    private CollectionReference storeCollectionReference = db.collection("Store");

    //Collection Reference
    private CollectionReference G_L_Ref = db.collection("Grocery List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        mAuth = FirebaseAuth.getInstance();

        if(GListApi.getInstance() != null) {
            currentUserID = GListApi.getInstance().getUserId();
            currentUserName = GListApi.getInstance().getUsername();
            listTitle = GListApi.getInstance().getListTitle();
            glistId = GListApi.getInstance().getGlistId();
        }

        storeList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewStore);
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

    //adds the custom Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // adds the functionality of the buttons in the Menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_signout_store:
                //Signs Out the User
                if (currentUser != null && mAuth != null) {
                    mAuth.signOut();
                    Intent intent = new Intent(ListStoreActivity.this, loginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
            case R.id.action_settings_store_items:


                break;

            case R.id.action_menu_store:
                Intent intent = new Intent(ListStoreActivity.this, listMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }


        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onResume() {
        super.onResume();
        final List<Store> storeList =  new ArrayList<>();
        currentUser = mAuth.getCurrentUser();

        setAdapter();

    }

    //Adapter set to show All stores if no items have been saved or only the Items that have been saved;
    public void setAdapter() {
        final List<Store> storeList =  new ArrayList<>();
        currentUser = mAuth.getCurrentUser();


        recyclerView = findViewById(R.id.recyclerViewStore);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        storeCollectionReference.whereIn("storeName", Arrays.asList("Walmart", "Kroger", "Publix"))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    //FireStore Query using For Loop to add store documents in database to local list<Store>
                    for (QueryDocumentSnapshot storelist : queryDocumentSnapshots) {
                        Store stolist = storelist.toObject(Store.class);
                        storeList.add(stolist);
                    }
                    //invoke RecyclerView
                    adapter = new StoreListAdapter(ListStoreActivity.this, storeList);
                    recyclerView.setAdapter(adapter);
                    //updates itself if something changes
                    adapter.notifyDataSetChanged();
                }
                else {
                    Log.d(TAG, "failed at onsuccess querry");

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed To Retrieve Stores: " + e.getMessage());
            }
        });


    }








}