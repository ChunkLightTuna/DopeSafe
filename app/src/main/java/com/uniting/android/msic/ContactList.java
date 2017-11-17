package com.uniting.android.msic;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class ContactList extends AppCompatActivity {

    RecyclerView contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = findViewById(R.id.contact_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            //add contact
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactList = findViewById(R.id.contact_recycler_view);
        contactList.setHasFixedSize(true);
        contactList.setLayoutManager(new LinearLayoutManager(this));


        contactList.setAdapter(new ContactListAdapter());

    }
}
