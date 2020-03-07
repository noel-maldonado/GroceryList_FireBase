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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class loginActivity extends AppCompatActivity {
    //Tag used for Log
    private static final String TAG = "LoginActivity";

    //Connection to FireStore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef  = db.collection("Users");

    //FireStore Keys
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    //Declare Connection to Firebase Authentication Server
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initRegisterTextView();
        initLoginButton();

        mAuth = FirebaseAuth.getInstance();


    }


    @Override
    public void onStart() {
        super.onStart();
        //Check if user is signed in and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
        if(!validateForm()) {
            return;
        }


        //Start sign in with Email using Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //Sign in is a success
                            Log.d(TAG, "signInWithEmail: success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(loginActivity.this, "Hello " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(loginActivity.this, listMainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            },1000);
                        } else {
                            //Sign in Fails
                            Log.w(TAG, "signInWithEmail: failure", task.getException());
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






//        DocumentReference docRef = db.collection("users").document(inputedEmail);
//
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    String eMail = documentSnapshot.getString(KEY_EMAIL);
//                    String pass = documentSnapshot.getString(KEY_PASSWORD);
//                    if(inputedEmail.equals(eMail) && inputedPass.equals(pass)) {
//                        Intent intent = new Intent(loginActivity.this, listMainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                    }else {
//                        Toast.makeText(loginActivity.this, "EMAIL/PASSWORD INCORECT",
//                                Toast.LENGTH_LONG).show();
//                    }
//
//                }else {
//                    Toast.makeText(loginActivity.this, "EMAIL/PASSWORD INCORECT",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: " + e.toString());
//            }
//        });
    }









}
