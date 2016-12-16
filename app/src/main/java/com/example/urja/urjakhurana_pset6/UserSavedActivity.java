package com.example.urja.urjakhurana_pset6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Arrays;

/*
 * Urja Khurana, 10739947
 * In this activity, the user can look at their saved concerts (the concerts they would like to go
 * or they are actually going to). The user can also share a concert with whoever they want and also
 * delete a concert if they don't want to go there for example.
 */

public class UserSavedActivity extends AppCompatActivity {

    // initialize variables
    private ArrayList<Concert> concertList;
    private ArrayList<String> keyList;
    private ListView concertView;
    private ConcertAdapter adapter;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_saved);

        // set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.user_toolbar);
        setSupportActionBar(myToolbar);

        // assign value to initialized variables
        concertList = new ArrayList<>();
        keyList = new ArrayList<>();
        concertView = (ListView) findViewById(R.id.concertView);

        // set adapter
        adapter = new ConcertAdapter(this, R.layout.row_layout, concertList);
        concertView.setAdapter(adapter);

        // get floating context menu for the listview items
        registerForContextMenu(concertView);

        // set listener for listview
        setListeners();

        String userId = getCurrentUser();
        // initialize database at the proper node for the user to get access to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(userId);

        // get the saved concert from the data
        getDatabaseData();
    }

    /** When the floating context menu is created */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // get inflater
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);

        // set title to delete since concert is already saved
        MenuItem delete = menu.findItem(R.id.action_editDb);
        delete.setTitle("Delete");
    }

    /** When one of the items of the floating context menu is tapped on, handle proper action */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // get which item of the listview has been tapped on
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Concert concert;

        // based on which option was tapped
        switch (item.getItemId()) {
            // delete concert from saved concerts
            case R.id.action_editDb:
                // get concert and the corresponding key
                concert = concertList.get((int) info.id);
                String key = keyList.get((int) info.id);

                // delete from database and update list shown to user
                myRef.child(key).removeValue();
                adapter.remove(concert);
                concertList.remove(concert);
                adapter.notifyDataSetChanged();
                return true;

            // share
            case R.id.action_share:
                // get which concert was tapped on and its url
                concert = concertList.get((int) info.id);
                String url = concert.getUrl();

                // choose which app user wants to share on and share the link of the concert
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Choose an application to share"));
                return true;

            // when user's action is not recognized
            default:
                return super.onContextItemSelected(item);
        }
    }

    /** Create toolbar menu and showcase the proper options */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_items, menu);

        // get the items
        MenuItem signout = menu.findItem(R.id.action_signout);
        MenuItem search = menu.findItem(R.id.action_search);

        // since user has to be signed in to get to this activity, show sign out and search button
        signout.setVisible(true);
        search.setVisible(true);
        return true;
    }

    /** When one of the options in the toolbar is tapped on by the user */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // based on which option has been tapped
        switch (item.getItemId()) {

            // signout
            case R.id.action_signout:
                FirebaseAuth.getInstance().signOut();
                // reset options because user signed out
                invalidateOptionsMenu();
                // close activity since there is no user so no saved data
                finish();
                return true;

            // search for new concerts
            case R.id.action_search:
                // go to search concerts;
                Intent goToSaved = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goToSaved);
                finish();
                return true;

            // user's action not recognized
            default:
                // superclass
                return super.onOptionsItemSelected(item);
        }
    }

    /** Get all of the saved concerts of the user from the firebase database */
    private void getDatabaseData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // this is to prevent the addition of already displayed concerts at each datachange
                concertList.clear();

                // get all of the concerts
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    // get concert and key of concert in database (keys will be used to delete)
                    String key = snapshot.getKey();
                    Concert concert = snapshot.getValue(Concert.class);

                    // add key and concert to the list
                    keyList.add(key);
                    concertList.add(concert);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Error when reading", "Failed to read value.", error.toException());
            }
        });
    }

    /** set listener on listview items */
    public void setListeners() {
        // just a simple toast if short click to inform the user the long click
        concertView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // get page of movie by clicking on one of the movie names
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Long click for options", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**  Get id of current user. If no one's signed in, just return "" */
    public String getCurrentUser() {
        // get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = "";
        if (user != null) {
            // get id of current user
            uid = user.getUid();
        }
        return uid;
    }
}
