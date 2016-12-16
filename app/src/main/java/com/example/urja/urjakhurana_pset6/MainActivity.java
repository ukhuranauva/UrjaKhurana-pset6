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

/* In the MainActivity a user can search for concerts by their favorite artist, regardless of being
 *  signed in or not. However, being signed in has its' perks because then you can not only share
 *  a concert with your friends, but you can also save the concerts! When a user is not signed in
 *  you can share concerts, however you cannot save any concerts.
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
        adapter = new ConcertAdapter(this, R.layout.row_layout, concertList);
        concertView.setAdapter(adapter);
        // add listener for list view
        setListeners();
        // get the floating context menu for the listview items
        registerForContextMenu(concertView);

        // initialize firebase authentication
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
                // ...
            }
        };

        // get firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // get user id
        String userId = getCurrentUser();
        // get the right path of the user to perform operations on their own database
        myRef = database.getReference("users").child(userId);

        // get last search query
        SharedPreferences prefs = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        String lastQuery = prefs.getString("query", "");
        EditText searchBar = (EditText) findViewById(R.id.searchConcert);
        searchBar.setText(lastQuery);
    }

    // add listener for authentication of account on start
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        SharedPreferences prefs = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        String lastQuery = prefs.getString("query", "");
        lastSearch = lastQuery;
        EditText searchBar = (EditText) findViewById(R.id.searchConcert);
        searchBar.setText(lastQuery);
        // executes user search
        if(lastQuery.contains(",") && lastQuery.split(",").length > 1) {
            ConcertAsyncTask asyncTask = new ConcertAsyncTask(this);
            asyncTask.execute(lastQuery);
        } else {
            Toast.makeText(getApplicationContext(), "Please give a valid search query!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // remove listener for authentication of account when app is stopped
    @Override
    public void onStop() {
        SharedPreferences prefs = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("query", lastSearch);
        editor.commit();
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("query", lastSearch);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle inState) {
        // get saved story back once activity is restarted
        super.onRestoreInstanceState(inState);
        lastSearch = inState.getString("query");
        // sets the right placeholder as hint (otherwise a different placeholder is shown)
        EditText searchBar = (EditText) findViewById(R.id.searchConcert);
        searchBar.setText(lastSearch);
        // executes user search
        if(lastSearch.contains(",") && lastSearch.split(",").length > 1) {
            ConcertAsyncTask asyncTask = new ConcertAsyncTask(this);
            asyncTask.execute(lastSearch);
        } else {
            Toast.makeText(getApplicationContext(), "Please give a valid search query!",
                                                                    Toast.LENGTH_SHORT).show();
        }
    }

    // get user id of the current user
    public String getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = "";
        if (user != null) {
            // get user id of the current user
            uid = user.getUid();
        }
        return uid;
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

    // this function is for when one of the items in the toolbar is tapped on by the user
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // log in or make account
            case R.id.action_account:
                // go to activity to sign in or sign up
                Intent sendIntent = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(sendIntent);
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


    // inflate the floating context menu for the listview items
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

    // when one of the options of the context menu are tapped on
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // get the proper item that was tapped on
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Concert concert;

        // depending on which options was tapped
        switch (item.getItemId()) {
            // save concert
            case R.id.action_editDb:
                Log.d("hello", Long.toString(info.id));
                // get which concert it is and save it to database
                concert = concertList.get((int) info.id);
                myRef.push().setValue(concert);
                Log.d("listen", "i'm pised");
                return true;

            // share option
            case R.id.action_share:
                Log.d("hello", Long.toString(info.id));
                // get the concert and its url on the website
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

    // searches for a concert, given the artist and city by the user
    public void searchConcert(View view) {
        EditText query = (EditText) findViewById(R.id.searchConcert);
        String searchQuery = query.getText().toString();
        lastSearch = searchQuery;

        // refresh the searchbar for the next search of the user
        query.setText("");

        Context context = getApplicationContext();

        /* Made use of http://stackoverflow.com/questions/2342620/how-to-hide-keyboard-after-
         * typing-in-edittext-in-android to hide the keyboard after a concert is searched for. this
         * is done for user convenience
         */
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // executes user search
        if(searchQuery.contains(",") && searchQuery.split(",").length > 1) {
            ConcertAsyncTask asyncTask = new ConcertAsyncTask(this);
            asyncTask.execute(searchQuery);
        } else {
            Toast.makeText(context, "Please give a valid search query!", Toast.LENGTH_SHORT).show();
        }
    }

    // sets the results of the search
    public void setData(ArrayList<Concert> concerts) {
        // delete the old results
        adapter.clear();
        // showcase new results
        adapter.addAll(concerts);
        adapter.notifyDataSetChanged();
    }

    // set the click listeners for a normal click
    public void setListeners() {

        // just a simple toast if short click is done by user
        concertView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // get page of movie by clicking on one of the movie names
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Long click for options", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
