package com.example.urja.urjakhurana_pset6;

import android.content.Context;
import android.content.Intent;
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
 *  an event with your friends, but you can also save the concerts! When a user is not signed in
 *  you can share events, however you cannot save any concerts.
 */
public class MainActivity extends AppCompatActivity {

    // initialize variables
    ListView concertView;
    ConcertAdapter adapter;
    ArrayList<Concert> concertList;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference myRef;

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
    }

    // add listener for authentication of account on start
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // remove listener for authentication of account when app is stopped
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
        inflater.inflate(R.menu.menu_item, menu);
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
            case R.id.action_favorite:
                Log.d("hello", Long.toString(info.id));
                // get which concert it is and save it to database
                concert = concertList.get((int) info.id);
                myRef.push().setValue(concert);
                Log.d("listen", "i'm pised");
                return true;

            // share option
            case R.id.action_settings:
                Log.d("hello", Long.toString(info.id));
                // get the concert and its url on the website
                concert = concertList.get((int) info.id);
                String url = concert.url;
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

    // searches for an event, given the name of artist by the user
    public void searchEvent(View view) {
        EditText eventName = (EditText) findViewById(R.id.searchEvent);
        String artist = eventName.getText().toString();

        // refresh the searchbar for the next search of the user
        eventName.setText("");

        Context context = getApplicationContext();

        /* Made use of http://stackoverflow.com/questions/2342620/how-to-hide-keyboard-after-
         * typing-in-edittext-in-android to hide the keyboard after an event is searched for. this
         * is done for user convenience
         */
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // executes user search
        EventAsyncTask asyncTask = new EventAsyncTask(this);
        asyncTask.execute(artist);
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
