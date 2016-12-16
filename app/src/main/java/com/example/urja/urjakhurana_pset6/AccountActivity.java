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

/*
 * Urja Khurana, 10739947
 * In this activity, a user can sign in or make a new account. The option is given to the user by
 * giving both of the buttons. Suppose an account is already made, it is not possible to create
 * an account again. If a user tries to sign in when their account has not been made yet, it is
 * not able to log in.
 */

public class AccountActivity extends AppCompatActivity {

    /** initialize variables used throughout */
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // get the firebase authentication
        mAuth = FirebaseAuth.getInstance();
    }



    /** Creates new account */
    public void createAccount(View view) {
        // get email and password typed in by user
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText passField = (EditText) findViewById(R.id.password);
        String email = emailField.getText().toString();
        String password = passField.getText().toString();

        // create the new account with the firebase authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                          // if creating an account is not succesful
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // if creating an account is succesful, go back to the main activity
                            Toast.makeText(getApplicationContext(), "Authentication Succesful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent sendIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(sendIntent);
                            finish();
                        }
                    }
                });
    }

    /** With this function, user can sign in */
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
                        // if sign in  failed
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } // if sign in is succesful go back to the main activity
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
