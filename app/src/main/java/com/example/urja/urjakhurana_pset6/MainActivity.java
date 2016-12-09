package com.example.urja.urjakhurana_pset6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView concertView;
    ConcertAdapter adapter;
    ArrayList<Concert> concertList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        concertList = new ArrayList<>();
        concertView = (ListView) findViewById(R.id.concertView);
        // get the lists from the file and set the taskManager with it
        // get listview and set adapter
        adapter = new ConcertAdapter(this, R.layout.row_layout, concertList);
        concertView.setAdapter(adapter);
        setListeners();
    }



    public void searchEvent(View view) {
        EditText eventName = (EditText) findViewById(R.id.searchEvent);
        String artist = eventName.getText().toString();
        eventName.setText("");
        // gets data of the movie
        EventAsyncTask asyncTask = new EventAsyncTask(this);
        asyncTask.execute(artist);
    }

    public void setData(ArrayList<Concert> concerts) {
//        // sets data of the movie
//        TextView titleView = (TextView) findViewById(R.id.textView);
//        // displays all the corresponding information per view
//        titleView.setText(title);
        concertList = concerts;
        adapter.addAll(concerts);
        adapter.notifyDataSetChanged();
    }

    public ArrayList<Concert> getConcertList() {
        return concertList;
    }

    // set the click listeners for a normal click and a long click
    public void setListeners() {

        // this click listener is to go to the page of the list to showcase and edit the tasks
        concertView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // get page of movie by clicking on one of the movie names
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "hallo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
