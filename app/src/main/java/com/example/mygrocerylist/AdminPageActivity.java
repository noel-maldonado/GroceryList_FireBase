package com.example.mygrocerylist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import model.GroceryList;
import ui.GroceryListAdapter;
import util.GListApi;

public class AdminPageActivity extends AppCompatActivity {

    //Tag used for Log
    private static final String TAG = "AdminPageActivity";


    //connection to FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Declare Connection to Firebase Authentication Server
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private String currentUserID;
    private String currentUserName;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        initAddProductbtn();
        initAddStorebtn();

        mAuth = FirebaseAuth.getInstance();

        if(GListApi.getInstance() != null) {
            currentUserID = GListApi.getInstance().getUserId();
            currentUserName = GListApi.getInstance().getUsername();

        }

    }


    private void initAddStorebtn() {
        Button addStoreBtn = (Button) findViewById(R.id.admin_save_store_button);
        addStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPageActivity.this, TempStoreCreationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }

    private void initAddProductbtn() {
        Button addProductbtn = (Button) findViewById(R.id.admin_save_product_button);
        addProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPageActivity.this, CreateProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });
    }



    @Override
    protected synchronized void onResume() {
        super.onResume();

        currentUser = mAuth.getCurrentUser();

    }


    //adds the custom Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return super.onCreateOptionsMenu(menu);

    }

    // adds the functionality of the buttons in the Menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_signout:
                //Signs Out the User
                if (currentUser != null && mAuth != null) {
                    mAuth.signOut();
                    Intent intent = new Intent(AdminPageActivity.this, loginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                break;
        }


        return super.onOptionsItemSelected(item);
    }






}
