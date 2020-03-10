package com.example.mygrocerylist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

import model.GroceryList;
import model.Store;
import util.GListApi;

public class TempStoreCreationActivity extends AppCompatActivity {

    //Tag used for Log
    private static final String TAG = "TempStoreCreationActivity";
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
    private CollectionReference storeCollectionReference = db.collection("Store");

    //reference to storage
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_store_creation);

        initAddPhotoBtn();
        initAddStoreBtn();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Checks to see if the Intent is from Add Photo Btn method (GALLERY_CODE) and that is was successful (RESULT_OK)
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            //data received from intent
            if (data != null) {
                imageUri = data.getData(); //GALLERY Code is associated with Uri data being received // path to the image
                ImageView ivImage = (ImageView) findViewById(R.id.create_store_imageView);
                ivImage.setImageURI(imageUri); //show image
            }
        }
    }

    private void initAddPhotoBtn() {
        ImageView ivAddPhotoBtn = (ImageView) findViewById(R.id.postStoreImageButton);
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

    private void initAddStoreBtn() {
        Button btnCreate = (Button) findViewById(R.id.post_save_store_button);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addList();
            }
        });


    }


    //Inserts GroceryList Object into G_L_REF (Reference/Table) to create a new document(Row)
    private void addList() {
        final EditText etStoreName = (EditText) findViewById(R.id.create_store_title_et);
        final String storeName = etStoreName.getText().toString().trim();
        final String storeId;
        if (storeName.equalsIgnoreCase("Kroger")) {
            storeId = "kroger";
        } else if (storeName.equalsIgnoreCase("Publix")) {
            storeId = "publix";
        }else if (storeName.equalsIgnoreCase("Walmart")) {
            storeId = "walmart";
        }else if (storeName.equalsIgnoreCase("Whole Foods Market")) {
            storeId = "wholeFoodsMarket";
        }else {
            return;
        }


        final ProgressBar createProgress = (ProgressBar) findViewById(R.id.store_post_progressBar);
        createProgress.setVisibility(View.VISIBLE);

        //the path to where the Image is saved
        final StorageReference filepath = storageReference
                .child("Store")
                .child("store_image_" + Timestamp.now().getSeconds());

        filepath.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //retrieving image URL
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Store store = new Store();
                                store.setStoreId(storeId);
                                store.setStoreName(storeName);
                                store.setStorePictureUrl(imageUrl);
                                //Collection Reference
                                storeCollectionReference.add(store).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        createProgress.setVisibility(View.INVISIBLE);
                                        Intent intent = new Intent(TempStoreCreationActivity.this, listMainActivity.class);
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








}
