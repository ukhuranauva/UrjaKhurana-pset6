package com.example.urja.urjakhurana_pset6;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/*
 * Urja Khurana, 10739947
 * In the MainActivity a user can search for concerts by their favorite artist, regardless of being
 * signed in or not. However, being signed in has its' perks because then you can not only share
 * a concert with your friends, but you can also save the concerts! When a user is not signed in
 * you can share concerts, however you cannot save any concerts.
 */

public class MainActivity extends AppCompatActivity {

    // initialize variables
    private ListView concertView;
    private ConcertAdapter adapter;
    private ArrayList<Concert> concertList;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String lastSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // assign values to initialized variables
        concertList = new ArrayList<>();
        concertView = (ListView) findViewById(R.id.concertView);

        // set adapter
        adapter = new ConcertAdapter(this, R.layout.row_layout, concertList);
        concertView.setAdapter(adapter);

        // add listener for the list view
        setListeners();

        // get the floating context menu for the listview items
        registerForContextMenu(concertView);

        // initialize firebase authentication
        setFirebaseAuthentication();

        // get user id of the current user (will be null if no one is signed in)
        String userId = getCurrentUser();
        // get the right path of the user to perform operations on their own database items
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(userId);
    }

    /*
     * Add listener for authentication of account and reload user's search before exiting the app
     * on start of the activity for user convenience.
     */
    @Override
    public void onStart() {
        super.onStart();
        // add listener for authentication
        mAuth.addAuthStateListener(mAuthListener);

        // get the last search of the user from Shared Preferences and set it on the searchbar
        SharedPreferences prefs = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        lastSearch = prefs.getString("query", "");
        EditText searchBar = (EditText) findViewById(R.id.searchConcert);
        searchBar.setText(lastSearch);

        // execute user search, only if the query is in the valid format
        if(lastSearch.contains(",") && lastSearch.split(",").length > 1) {
            ConcertAsyncTask asyncTask = new ConcertAsyncTask(this);
            asyncTask.execute(lastSearch);
        } else {
            // inform user of invalid search format
            Toast.makeText(getApplicationContext(), "Please give a valid search query!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /** Remove listener for authentication of account and save last query when app is stopped. */
    @Override
    public void onStop() {
        // save last query in sharedpreferences
        SharedPreferences prefs = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("query", lastSearch);
        editor.commit();
        super.onStop();

        // remove listener
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /** Save the last query of the user for the rotation, so search results can be showcased again */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("query", lastSearch);
        super.onSaveInstanceState(outState);
    }

    /** When screen has been rotated, showcase the search results again. */
    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);

        // get query that was last searched for before the rotation
        lastSearch = inState.getString("query");

        /*
         * Executes user search if it was a valid query so if it contains a comma and is not
         * something like: artist,
         */
        if(lastSearch.contains(",") && lastSearch.split(",").length > 1) {
            ConcertAsyncTask asyncTask = new ConcertAsyncTask(this);
            asyncTask.execute(lastSearch);
        } else {
            // inform the user to give proper query
            Toast.makeText(getApplicationContext(), "Please give a valid search query!",
                                                                    Toast.LENGTH_SHORT).show();
        }
    }


    /* Was able to solve this with a bit of help from the following link:
     * http://stackoverflow.com/questions/10692755/how-do-i-hide-a-menu-item-in-the-actionbar
     * Checks if a user is logged in or not, depending on that it displays the right options in
     * the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_items, menu);

        // get all of the items
        MenuItem account = menu.findItem(R.id.action_account);
        MenuItem signout = menu.findItem(R.id.action_signout);
        MenuItem savedConcerts = menu.findItem(R.id.action_saved);

        // if no one is signed in
        if (getCurrentUser().equals("")) {
            // hide sign out and saved concerts and show log in/sign up button
            account.setVisible(true);
            signout.setVisible(false);
            savedConcerts.setVisible(false);
        } else {
            // if user is signed in, show signout button and saved concerts and hide log in/sign up
            account.setVisible(false);
            signout.setVisible(true);
            savedConcerts.setVisible(true);
        }
        return true;
    }

    /** This function is for when one of the items in the toolbar is tapped on by the user */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // log in or make account
            case R.id.action_account:
                // go to activity to sign in or sign up
                Intent goToAccount = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(goToAccount);
                return true;

            // sign out
            case R.id.action_signout:
                FirebaseAuth.getInstance().signOut();
                // reset options because user signed out
                invalidateOptionsMenu();
                return true;

            // display saved concerts
            case R.id.action_saved:
                // go to activity to show saved concerts;
                Intent goToSaved = new Intent(getApplicationContext(), UserSavedActivity.class);
                startActivity(goToSaved);
                finish();
                return true;

            // when user's action is not recognized
            default:
                // invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    /*
     * inflate the floating context menu for the listview items and showcase correct items
     * based on the fact if the user is signed in or not
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);

        MenuItem save = menu.findItem(R.id.action_editDb);

        // if no one is signed in
        if (getCurrentUser().equals("")) {
            // hide save button since user is not signed in
            save.setVisible(false);
        } else {
            // show save button since user is signed in
            save.setVisible(true);
        }

    }

    /** When one of the options of the context menu are tapped on */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // get the proper item that was tapped on
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Concert concert;

        // depending on which options was tapped
        switch (item.getItemId()) {
            // save concert
            case R.id.action_editDb:
                // get which concert it is and save it to database
                concert = concertList.get((int) info.id);
                myRef.push().setValue(concert);
                return true;

            // share option
            case R.id.action_share:
                // get the concert and its url
                concert = concertList.get((int) info.id);
                String url = concert.getUrl();

                // get which app user wants to use for sharing the url and share it there
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Choose an application to share"));
                return true;

            // if user's option is not properly recognized
            default:
                return super.onContextItemSelected(item);
        }
    }

    /** Get user id of the current user. If the user is not logged in, just return "". */
    public String getCurrentUser() {
        // get current firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = "";

        // if someone is signed in
        if (user != null) {
            // get user id of the current user
            uid = user.getUid();
        }
        return uid;
    }

    /** Set the firebase authentication methods */
    private void setFirebaseAuthentication() {
        // get the firebase authentication
        mAuth = FirebaseAuth.getInstance();

        // set the listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(getApplicationContext(), "Welcome!",
                            Toast.LENGTH_SHORT).show();
                    Log.d("logged in", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signing out
                    Toast.makeText(getApplicationContext(), "Bye, see you soon!",
                            Toast.LENGTH_SHORT).show();
                    Log.d("logged out", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    /** Searches for a concert, given the artist and city by the user */
    public void searchConcert(View view) {
        // get query of user and set it as the last search for if the app is closed after
        EditText query = (EditText) findViewById(R.id.searchConcert);
        String searchQuery = query.getText().toString();
        lastSearch = searchQuery;

        // refresh the searchbar for the next search of the user (user convenience)
        query.setText("");

        /*
         * Made use of http://stackoverflow.com/questions/2342620/how-to-hide-keyboard-after-
         * typing-in-edittext-in-android to hide the keyboard after a concert is searched for. this
         * is done for user convenience
         */
        Context context = getApplicationContext();
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // executes user search only if the proper query convention is used
        if(searchQuery.contains(",") && searchQuery.split(",").length > 1) {
            ConcertAsyncTask asyncTask = new ConcertAsyncTask(this);
            asyncTask.execute(searchQuery);
        } else {
            // inform user to give a proper query
            Toast.makeText(context, "Please give a valid search query!", Toast.LENGTH_SHORT).show();
        }
    }

    /** Sets the results of the search in the listview */
    public void setData(ArrayList<Concert> concerts) {
        // delete the old results
        adapter.clear();

        // showcase new results
        adapter.addAll(concerts);
        adapter.notifyDataSetChanged();
    }

    /** Set the click listeners for a normal click */
    public void setListeners() {

        // just a simple toast if short click is done by user, since long click showcases options
        concertView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // get page of movie by clicking on one of the movie names
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Long click for options",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
