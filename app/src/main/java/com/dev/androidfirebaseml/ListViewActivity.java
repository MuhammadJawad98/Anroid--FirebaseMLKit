package com.dev.androidfirebaseml;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {
    ArrayList<Item> events = new ArrayList<Item>();
    FirebaseRealtimeDbStorage firebase = new FirebaseRealtimeDbStorage();
private Button btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        getSupportActionBar().setTitle("View All Analyzed Images");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnAdd= findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        ItemAdapter cbrEventAdapter = new ItemAdapter(
                this, R.layout.list_item, events);

        firebase.downloadDataFromRealtimeDB(cbrEventAdapter);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(cbrEventAdapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Item cbrevent = events.get(position);
//                        Intent intent = new Intent(view.getContext(), EventActivity.class);
//                        intent.putExtra("title", cbrevent.getTitle());
//                        intent.putExtra("imageResource", cbrevent.getImageResource());
//                        intent.putExtra("dates", cbrevent.getDates());
//                        ListViewActivity.this.startActivity(intent);
                    }
                });
    }
}
