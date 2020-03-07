package com.example.mygrocerylist;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class registerActivity extends AppCompatActivity {
    //Tag used for Log
    private static final String TAG = "registerActivity";

    //FireStore Keys
    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    //Connecting to Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Firebase Authentication instance declared
    private FirebaseAuth mAuth;

    //Input Text Fields
    private EditText mFullNameField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initLoginTextView();
        initRegisterButton();


        mAuth = FirebaseAuth.getInstance();
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

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount: " + email);
        if(!validateForm()) {
            return;
        }


        // Start  create user with email
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()) {
                           //sign up is successful
                           Log.d(TAG, "createUserWithEmail: success");
                           FirebaseUser user = mAuth.getCurrentUser();
                           sendEmailVerification();

                           EditText mFullNameField = (EditText) findViewById(R.id.txtName);
                           final String fullName = mFullNameField.getText().toString().trim();
                           //creates profile change request to place Full Name as Display Name
                           UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                   .setDisplayName(fullName).build();

                           user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()) {
                                       Log.d(TAG, "Display Name updated: " + fullName);
                                   }
                               }
                           });


                           Intent intent = new Intent(registerActivity.this, listMainActivity.class);
                           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                           startActivity(intent);
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
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(registerActivity.this,
                            "Verification email sent to" + user.getEmail(),
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

                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                createAccount(email, password);



//                //need to create hashmap in order to pass data into FireStore db
//                Map<String, Object> data = new HashMap<>();
//                data.put(KEY_FULLNAME, fullName);
//                data.put(KEY_EMAIL, EMail);
//                data.put(KEY_PASSWORD, Pass);

//                db.collection("Users")
//                        .document(EMail).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        //Tells the User the Account was Created
//                        Toast.makeText(registerActivity.this, "Success", Toast.LENGTH_LONG);
//                        //Delays switch of Activity by 2 seconds in order for the User to read the Success Toast Message
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent intent = new Intent(registerActivity.this, loginActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                            }
//                        }, 2000);
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //to tell us what the error is
//                        Log.d(TAG, "OnFailure: " + e.toString());
//                    }
//                });

            }
        });


    }



}
