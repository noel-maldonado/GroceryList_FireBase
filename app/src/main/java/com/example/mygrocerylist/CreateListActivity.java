package com.example.mygrocerylist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.List;

import model.GroceryList;
import model.StoreProduct;
import ui.GroceryListAdapter;
import util.GListApi;

public class CreateListActivity extends AppCompatActivity {




    //Tag used for Log
    private static final String TAG = "CreateListActivity";
    //Used to associate data with switch statement
    private static final int GALLERY_CODE = 1;
    private Uri imageUri;

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

    //reference to storage
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);
        initCreateBtn();
        initAddPhotoBtn();
        //Storage reference instantiated (needed to connect)
        storageReference = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        if(GListApi.getInstance() != null) {
            currentUserID = GListApi.getInstance().getUserId();
            currentUserName = GListApi.getInstance().getUsername();
        }


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
    protected synchronized void onResume() {
        super.onResume();

        currentUser = mAuth.getCurrentUser();

    }




    //Inserts GroceryList Object into G_L_REF (Reference/Table) to create a new document(Row)
    private void addList(final String title) {
        if(!validateForm()) {
            return;
        }
        final ProgressBar createProgress = (ProgressBar) findViewById(R.id.post_progressBar);
        createProgress.setVisibility(View.VISIBLE);

        //the path to where the Image is saved
        final StorageReference filepath = storageReference
                .child("GroceryList_Images")
                .child("my_image_" + Timestamp.now().getSeconds());

        filepath.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //retrieving image URL
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                GroceryList newList = new GroceryList(title);
                                newList.setTimeAdded(new Timestamp(new Date()));
                                newList.setUserId(currentUserID);
                                newList.setUsername(currentUserName);
                                newList.setImageUrl(imageUrl);
                                //Collection Reference
                                G_L_Ref.add(newList).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        String listId = documentReference.getId();
                                        documentReference.update("glistId", listId);
                                        StoreProduct storeProduct = new StoreProduct();
                                        documentReference.collection("Store_Product").add(storeProduct);
                                        createProgress.setVisibility(View.INVISIBLE);
                                        Intent intent = new Intent(CreateListActivity.this, listMainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "failed to save: " + e.getMessage());
                                    }
                                });

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }


    private void initCreateBtn() {
        Button btnCreate = (Button) findViewById(R.id.post_save_journal_button);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText etListName = (EditText) findViewById(R.id.create_list_title_et);
                String listName = etListName.getText().toString().trim();

                addList(listName);


            }
        });

    }

    private void initAddPhotoBtn() {
        ImageView ivAddPhotoBtn = (ImageView) findViewById(R.id.postCameraButton);
        ivAddPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get Image from Gallery/phone
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //sets the type of data to look for to anything image related
                intent.setType("image/*");
                //Call method to handle image obtained
                startActivityForResult(intent, GALLERY_CODE);
            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Checks to see if the Intent is from Add Photo Btn method (GALLER_CODE) and that is was successful (RESULT_OK)
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            //data received from intent
            if (data != null) {
                imageUri = data.getData(); //GALLERY Code is associated with Uri data being received // path to the image
                ImageView ivImage = (ImageView) findViewById(R.id.create_list_imageView);
                ivImage.setImageURI(imageUri); //show image
            }
        }
    }

    //ensures the correct data has been inserted and not left blank
    private boolean validateForm() {
        EditText etListName = (EditText) findViewById(R.id.create_list_title_et);
        String listName = etListName.getText().toString().trim();


        boolean valid = true;

        if(TextUtils.isEmpty(listName)) {
            etListName.setError("Required.");
            valid = false;
        } else {
            etListName.setError(null);
        }

        return valid;

    }



}
