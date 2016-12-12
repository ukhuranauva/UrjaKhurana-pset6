package com.example.urja.urjakhurana_pset6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/* In this activity, a user can sign in or make a new account. The option is given to the user by
 * giving both of the buttons. Suppose an account is already made, it is not possible to create
 * an account again. If a user tries to sign in when their account has not been made yet, it is
 * not able to log in.
 */

public class AccountActivity extends AppCompatActivity {

    // initialize variables used throughout
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        // get the firebase authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("k", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("n", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    // creates new account
    public void createAccount(View view) {
        // get email and password typed in by user
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText passField = (EditText) findViewById(R.id.password);
        String email = emailField.getText().toString();
        String password = passField.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("k", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication Succesful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent sendIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(sendIntent);
                            finish();
                        }
                    }
                });
    }

    // here, a user can sign in
    public void signIn(View view) {
        // get email and password of user
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText passField = (EditText) findViewById(R.id.password);
        String email = emailField.getText().toString();
        String password = passField.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("dd", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("ddd", "signInWithEmail", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Authentication Succesful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent sendIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(sendIntent);
                            finish();
                        }
                    }
                });
    }
}
