package com.example.mygrocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import util.GListApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class registerActivity extends AppCompatActivity {
    //Tag used for Log
    private static final String TAG = "registerActivity";


    //Connecting to FireBase FireStore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Firebase Authentication instance declared
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    //Users Collection Reference
    private CollectionReference userRefercence = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initLoginTextView();
        initRegisterButton();


        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    //user is already logged in so they are sent to the entrance
                    Intent intent = new Intent(registerActivity.this, listMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    //no user yet; dont really need an else but I created it so what

                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void initLoginTextView() {
        TextView tvRegister = (TextView) findViewById(R.id.lnkLoginActivity);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(registerActivity.this, loginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    private boolean validateForm() {
        EditText mEmailField = (EditText) findViewById(R.id.txtEmailReg);
        EditText mPasswordField = (EditText) findViewById(R.id.txtPwdReg);
        EditText mUsername = (EditText) findViewById(R.id.username_edittext_register);
        EditText mFullName = (EditText) findViewById(R.id.txtName);

        boolean valid = true;

        String email = mEmailField.getText().toString();
        if(TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if(TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        String username = mPasswordField.getText().toString();
        if(TextUtils.isEmpty(username)) {
            mUsername.setError("Required.");
            valid = false;
        } else {
            mUsername.setError(null);
        }

        String fullName = mFullName.getText().toString();
        if(TextUtils.isEmpty(password)) {
            mFullName.setError("Required.");
            valid = false;
        } else {
            mFullName.setError(null);
        }


        return valid;
    }

    private void createAccount(String email, String password, final String username) {
        Log.d(TAG, "createAccount: " + email);
        if(!validateForm()) {
            return;
        }

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.create_acct_progress);

        progressBar.setVisibility(View.VISIBLE);

        // Start  create user with email
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()) {
                           //sign up is successful
                           Log.d(TAG, "createUserWithEmail: success");
                           currentUser = firebaseAuth.getCurrentUser();
                           sendEmailVerification();

                           EditText mFullNameField = (EditText) findViewById(R.id.txtName);
                           final String fullName = mFullNameField.getText().toString().trim();
                           //creates profile change request to place Full Name as Display Name
                           UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                   .setDisplayName(fullName).build();

                           currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()) {
                                       Log.d(TAG, "Display Name updated: " + fullName);
                                   }
                               }
                           });


                           //getUid needs to assert that it is not null
                           assert currentUser != null;
                           //Create unique Id for User
                           final String currentUserID = currentUser.getUid();

                           //Create a user Map we can create a user in the User Collection
                           Map<String, String> userObj = new HashMap<>();
                           userObj.put("userId", currentUserID);
                           userObj.put("username", username);

                           userRefercence.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                               @Override
                               public void onSuccess(DocumentReference documentReference) {
                                   documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                           if (Objects.requireNonNull(task.getResult().exists())) {
                                               progressBar.setVisibility(View.INVISIBLE);
                                               String name = task.getResult()
                                                       .getString("username");

                                               GListApi gListApi = GListApi.getInstance(); //Global API; accessible by the all activities
                                               gListApi.setUserId(currentUserID);
                                               gListApi.setUsername(name);

                                               Intent intent = new Intent(registerActivity.this, listMainActivity.class);
                                               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                               intent.putExtra("username", name);
                                               intent.putExtra("userId", currentUserID);
                                               startActivity(intent);


                                           }else {
                                               Log.d(TAG, "Failed to create GListApi ");
                                               progressBar.setVisibility(View.INVISIBLE);
                                           }
                                       }
                                   });
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Log.d(TAG, "Failed userReference.add:" + e.toString());

                               }
                           });



                       } else {
                           //sign up fails
                           Log.w(TAG, "createUserWithEmail: failure", task.getException());
                           Toast.makeText(registerActivity.this, "Authentication failed.",
                                   Toast.LENGTH_SHORT).show();
                       }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "createUserWithEmail: method error" + e.toString());
            }
        });




    }


    private void sendEmailVerification() {

        //send verification email
        //start send email verification
        currentUser.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(registerActivity.this,
                            "Verification email sent to" + currentUser.getEmail(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "sendEmailVerification", task.getException());
                    Toast.makeText(registerActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "sendEmailVerification: method error" + e.toString());
            }
        });


    }





    private void initRegisterButton() {
        Button butRegister = (Button) findViewById(R.id.btnRegister);
        butRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mEmailField = (EditText) findViewById(R.id.txtEmailReg);
                EditText mPasswordField = (EditText) findViewById(R.id.txtPwdReg);
                EditText mUsername = (EditText) findViewById(R.id.username_edittext_register);
                String email = mEmailField.getText().toString().trim();
                String password = mPasswordField.getText().toString();
                String username = mUsername.getText().toString().trim();
                createAccount(email, password, username);


            }
        });


    }



}
