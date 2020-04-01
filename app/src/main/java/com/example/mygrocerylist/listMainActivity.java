package com.example.mygrocerylist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.GListApi;
import model.GroceryList;
import ui.GroceryListAdapter;

public class listMainActivity extends AppCompatActivity {
    //Contain our List of Grocery Lists
    private List<GroceryList> groceryLists;

    //Tag used for Log
    private static final String TAG = "listMainActivity";

    private RecyclerView recyclerView;
    private GroceryListAdapter adapter;

    //connection to FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Declare Connection to Firebase Authentication Server
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private String currentUserID;
    private String currentUserName;

    //Collection Reference
    private CollectionReference G_L_Ref = db.collection("Grocery List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();

        if(GListApi.getInstance() != null) {
            currentUserID = GListApi.getInstance().getUserId();
            currentUserName = GListApi.getInstance().getUsername();
        }

        groceryLists = new ArrayList<>();

        //Will start animation with users name
        initNameTextView();
        //Takes you to the next layout
        initAddBtn();
        //Logs out the use
        initLogoutBtn();

        recyclerView = findViewById(R.id.recyclerView);
        //ensures that the size is fixed
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

       currentUser = mAuth.getCurrentUser();

        final List<GroceryList> groceryLists = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        //ensures that the size is fixed
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initNameTextView();

        G_L_Ref.whereEqualTo("userId", GListApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            //FireStore Query using For Loop to add grocerylist in database to local list<GroceryList>
                            for(QueryDocumentSnapshot glists : queryDocumentSnapshots) {
                                GroceryList grocList = glists.toObject(GroceryList.class);
                                groceryLists.add(grocList);
                            }
                            //invoke RecyclerView
                            adapter = new GroceryListAdapter(listMainActivity.this,
                                    groceryLists);
                            recyclerView.setAdapter(adapter);
                            //updates itself if something changes
                            adapter.notifyDataSetChanged();

                        }else {

                            TextView noListMessage = (TextView) findViewById(R.id.noListMessage);
                            noListMessage.setVisibility(View.VISIBLE);

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();

        final List<GroceryList> groceryLists = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        //ensures that the size is fixed
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        G_L_Ref.whereEqualTo("userId", GListApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            //FireStore Query using For Loop to add grocerylist in database to local list<GroceryList>
                            for(QueryDocumentSnapshot glists : queryDocumentSnapshots) {
                                GroceryList grocList = glists.toObject(GroceryList.class);
                                groceryLists.add(grocList);
                            }
                            //invoke RecyclerView
                            adapter = new GroceryListAdapter(listMainActivity.this,
                                    groceryLists);
                            new ItemTouchHelper(itemTouchHelpCallback).attachToRecyclerView(recyclerView);
                            recyclerView.setAdapter(adapter);
                            //updates itself if something changes
                            adapter.notifyDataSetChanged();

                        }else {

                            TextView noListMessage = (TextView) findViewById(R.id.noListMessage);
                            noListMessage.setVisibility(View.VISIBLE);

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    //adds the custom Menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//
//    }



    // adds the functionality of the buttons in the Menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_signout_main:
                //Signs Out the User
                if (currentUser != null && mAuth != null) {
                    mAuth.signOut();
                    Intent intent = new Intent(listMainActivity.this, loginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
            case R.id.action_settings_main:


                break;
            case R.id.action_add:
                Intent intent = new Intent(listMainActivity.this, CreateListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;


        }


        return super.onOptionsItemSelected(item);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelpCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            adapter.deleteItem();
            Log.d(TAG,"Item Swiped");
        }
    };

    private void initAddBtn(){
        ImageButton addBtn = (ImageButton) findViewById(R.id.addButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(listMainActivity.this, CreateListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initLogoutBtn(){
        Button logoutBtn = (Button) findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null && mAuth != null) {
                    mAuth.signOut();
                    Intent intent = new Intent(listMainActivity.this, loginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    private void initNameTextView(){
        TextView name = (TextView) findViewById(R.id.name);
        final Animation slideRight = AnimationUtils.loadAnimation(this, R.anim.slide_to_right);
        name.startAnimation(slideRight);
    }



}
