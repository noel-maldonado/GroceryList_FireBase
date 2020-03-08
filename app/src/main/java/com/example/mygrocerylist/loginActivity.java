package com.example.mygrocerylist;

import android.content.Intent;
import android.nfc.Tag;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

import util.GListApi;

public class loginActivity extends AppCompatActivity {
    //Tag used for Log
    private static final String TAG = "LoginActivity";

    //Connection to FireStore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersReference  = db.collection("Users");

    //FireStore Keys
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    //Declare Connection to Firebase Authentication Server
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initRegisterTextView();
        initLoginButton();

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();



            }
        };


    }


    @Override
    public void onStart() {
        super.onStart();
        //Check if user is signed in and update UI accordingly
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
        if (currentUser != null) {
            //Delays User to first Logged in Screen to send Welcome back message
            Toast.makeText(loginActivity.this, "Welcome Back\n" + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(loginActivity.this, listMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            },1000);
        }
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn: " + email);
        //ensure fields aren't empty
        if(!validateForm()) {
            return;
        }

        // Make the progress bar visible
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.VISIBLE);


        //Start sign in with Email using Firebase Auth
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //Sign in is a success
                            Log.d(TAG, "signInWithEmail: success");
                            currentUser = firebaseAuth.getCurrentUser();

                            assert currentUser != null;
                            final String currentUserId = currentUser.getUid();

                            usersReference
                                    .whereEqualTo("userId", currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.d(TAG, "SnapshotListener Error: " + e.toString());
                                            }
                                            assert queryDocumentSnapshots != null;
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                                                    GListApi gListApi = GListApi.getInstance();
                                                    gListApi.setUsername(snapshot.getString("username"));
                                                    gListApi.setUsername(snapshot.getString("userId"));
                                                    //Send Welcome Message and Go to Main List Activity
                                                    Toast.makeText(loginActivity.this, "Hello " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Intent intent = new Intent(loginActivity.this, listMainActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                        }
                                                    },1000);
                                                }



                                            }


                                        }
                                    });

                        } else {
                            //Sign in Fails
                            Log.w(TAG, "signInWithEmail: failure", task.getException());
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(loginActivity.this, "Authentication failed\nIncorrect Username\\password",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }







    //method used to ensure User has Input'd text into Email & Password
    private boolean validateForm() {
        EditText mEmailField = (EditText) findViewById(R.id.txtEmail);
        EditText mPasswordField = (EditText) findViewById(R.id.txtPwd);
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
        return valid;
    }








    private void initRegisterTextView() {
        TextView tvRegister = (TextView) findViewById(R.id.lnkregisterActivity);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, registerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }




    private void initLoginButton() {


        //btnLogin
        Button btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mEmailField = (EditText) findViewById(R.id.txtEmail);
                EditText mPasswordField = (EditText) findViewById(R.id.txtPwd);
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                signIn(email, password);
            }
        });



    }









}
