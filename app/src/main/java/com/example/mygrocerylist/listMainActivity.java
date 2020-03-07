package com.example.mygrocerylist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import Util.GroceryListApi;

public class listMainActivity extends AppCompatActivity {
    //Contain our List of Grocery Lists
    private List<GroceryList> groceryLists;



    //Tag used for Log
    private static final String TAG = "listMainActivity";

    private RecyclerView recyclerView;
    private GroceryListAdapter groceryListAdapter;
    private RecyclerView.LayoutManager groceryListLayoutManager;



    //Keys
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE_CREATED = "created";

    //connection to FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Declare Connection to Firebase Authentication Server
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Collection Reference
    private CollectionReference G_L_Ref = db.collection("Grocery List");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLogOut();
        initAdd();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        groceryLists = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected synchronized void onResume() {
        super.onResume();

       FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null) {
//            Toast.makeText(listMainActivity.this, "Welcome Back" + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    protected void onStart() {
        super.onStart();


        G_L_Ref.whereEqualTo("userId", GroceryListApi.getIstance().getUserId())
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
                            groceryListAdapter = new GroceryListAdapter(listMainActivity.this,
                                    groceryLists);
                            recyclerView.setAdapter(groceryListAdapter);
                            //updates itself if something changes
                            groceryListAdapter.notifyDataSetChanged();

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



    private void initAdd() {
        Button btnAdd = (Button) findViewById(R.id.addButton);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               createList();
            }
        });

    }


    private void createList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter List Name");
        //set up the input
        final EditText input = new EditText(this);
        //specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        //places the EditText created in the AlertDialog
        builder.setView(input);
        //Set up AlertDialog Buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String listName = input.getText().toString().trim();
                if (listName.isEmpty()) {
                    Toast.makeText(getBaseContext(), "List Name Can Not Be Empty.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Create List ");
                    //TODO Create GroceryList Object with FireStore

                    addList(listName);



                }


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    //Inserts GroceryList Object into G_L_REF (Reference/Table) to create a new document(Row)
    private void addList(String title) {
        String listTitle = title;

        GroceryList newList = new GroceryList(title);

        G_L_Ref.add(newList);

    }




    private void initLogOut() {
        Button btnLogOut = (Button) findViewById(R.id.logoutButton);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(listMainActivity.this, loginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }






}
