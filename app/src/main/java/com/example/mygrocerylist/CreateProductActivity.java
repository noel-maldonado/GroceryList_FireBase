package com.example.mygrocerylist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

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


import model.Product;
import util.GListApi;

public class CreateProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //Tag used for Log
    private static final String TAG = "CreateProductActivity";
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

    private String storeSelected;

    //Collection Reference
    private CollectionReference productCollectionReference = db.collection("Product");

    //reference to storage
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        initCreateProductBtn();
        initAddPhotoBtn();
        spinnerLayout();

        //Storage reference instantiated (needed to connect)
        storageReference = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        if (GListApi.getInstance() != null) {
            currentUserID = GListApi.getInstance().getUserId();
            currentUserName = GListApi.getInstance().getUsername();

        }

        //Instantiating Authentication Listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {

                } else {

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
                ImageView ivImage = (ImageView) findViewById(R.id.create_product_imageView);
                ivImage.setImageURI(imageUri); //show image
            }
        }
    }

    private void initAddPhotoBtn() {
        ImageView ivAddPhotoBtn = (ImageView) findViewById(R.id.postProductImageButton);
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


    private void initCreateProductBtn() {
        Button btnProduct = (Button) findViewById(R.id.post_save_product_button);
        btnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

    }



    //Inserts Product Object into productCollectionReference (Reference/Table) to create a new document(Row)
    private void addProduct() {
        final EditText etProductName = (EditText) findViewById(R.id.create_product_title_et);
        final EditText etProductId = (EditText) findViewById(R.id.create_product_id_et);
        final EditText etProductPrice = (EditText) findViewById(R.id.create_product_price_et);

        final String productName = etProductName.getText().toString().trim();
        final String productId = etProductId.getText().toString().trim();



        final ProgressBar createProgress = (ProgressBar) findViewById(R.id.product_post_progressBar);
        createProgress.setVisibility(View.VISIBLE);


        //the path to where the Image is saved
        final StorageReference filepath = storageReference
                .child("Product")
                .child("product_image_" + Timestamp.now().getSeconds());


        filepath.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //retrieving image URL
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Product product = new Product();
                                product.setItemId(productId);
                                product.setItemName(productName);
                                product.setProductImageUrl(imageUrl);
                                product.setProductStore(storeSelected);
                                //Try Catch just in case formatting is incorrect
                                try {
                                    Double productPrice = Double.parseDouble(etProductPrice.getText().toString());
                                    product.setItemPrice(productPrice);
                                }catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "Price Formatting Error: " + e.getMessage());
                                    return;
                                }
                                //Collection Reference
                                productCollectionReference.add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        createProgress.setVisibility(View.INVISIBLE);
                                        Intent intent = new Intent(CreateProductActivity.this, AdminPageActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "failed to save: " + e.getMessage());
                                        createProgress.setVisibility(View.INVISIBLE);

                                    }
                                });

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                createProgress.setVisibility(View.INVISIBLE);

            }
        });


    }

    private void spinnerLayout() {
        Spinner storeSpinner = findViewById(R.id.create_product_store_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.stores, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeSpinner.setAdapter(adapter);
        storeSpinner.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
        switch (selected) {
            case "Kroger":
                storeSelected = "kroger";
                break;
            case "Walmart":
                storeSelected = "walmart";
                break;
            case "Publix":
                storeSelected = "publix";
                break;
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
