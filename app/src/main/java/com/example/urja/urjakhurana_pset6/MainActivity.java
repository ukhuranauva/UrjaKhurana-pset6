package com.example.urja.urjakhurana_pset6;

import android.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        concertList = new ArrayList<>();
        concertView = (ListView) findViewById(R.id.concertView);
        // get the lists from the file and set the taskManager with it
        // get listview and set adapter
        registerForContextMenu(concertView);
        adapter = new ConcertAdapter(this, R.layout.row_layout, concertList);
        concertView.setAdapter(adapter);
        setListeners();
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

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = getCurrentUser();
        myRef = database.getReference("users").child(userId);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Concert concert = snapshot.getValue(Concert.class);
                    Log.d("heeke", concert.artist);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("sksksk", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public String getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = "";
        if (user != null) {
            // Name, email address, and profile photo Url
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            uid = user.getUid();
        }
        return uid;
    }



    //http://stackoverflow.com/questions/10692755/how-do-i-hide-a-menu-item-in-the-actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_items, menu);
        MenuItem account = menu.findItem(R.id.action_account);
        MenuItem signout = menu.findItem(R.id.action_signout);
        MenuItem savedConcerts = menu.findItem(R.id.action_saved);
        if (getCurrentUser().equals("")) {
            account.setVisible(true);
            signout.setVisible(false);
            savedConcerts.setVisible(false);
        } else {
            account.setVisible(false);
            signout.setVisible(true);
            savedConcerts.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_account:
                // log in or make account
                Intent sendIntent = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(sendIntent);
                return true;

            case R.id.action_signout:
                FirebaseAuth.getInstance().signOut();
                // reset options because user signed out
                invalidateOptionsMenu();
                return true;

            case R.id.action_saved:
                // go to saved concerts;
                Intent goToSaved = new Intent(getApplicationContext(), UserSavedActivity.class);
                startActivity(goToSaved);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Concert concert;
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Log.d("hello", Long.toString(info.id));
                concert = concertList.get((int) info.id);
                myRef.push().setValue(concert);
                Log.d("listen", "i'm pised");
                return true;
            case R.id.action_settings:
                Log.d("hello", Long.toString(info.id));
                concert = concertList.get((int) info.id);
                String url = concert.url;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Choose an application to share"));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // searches for an event, given the name of artist by the user
    public void searchEvent(View view) {
        EditText eventName = (EditText) findViewById(R.id.searchEvent);
        String artist = eventName.getText().toString();
        eventName.setText("");
        Context context = getApplicationContext();
        // hide keyboard: http://stackoverflow.com/questions/2342620/how-to-hide-keyboard-after-typing-in-edittext-in-android
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        // gets data of the search
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

    public ArrayList<Concert> getConcertList() {
        return concertList;
    }

    // set the click listeners for a normal click
    public void setListeners() {

        // just a simple toast if short click
        concertView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // get page of movie by clicking on one of the movie names
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "long click for options", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // go to accountactivity to sign in or sign up (FUNCTIENAAM MOET NOG WORDEN VERANDERD)
    public void signUp(View view) {
        Intent sendIntent = new Intent(getApplicationContext(), AccountActivity.class);
        startActivity(sendIntent);
    }
}
