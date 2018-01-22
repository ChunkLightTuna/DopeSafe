package com.uniting.android.msic

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem

/**
 * Created by Chris.Oelerich on 1/20/2018.
 *
 * Displays text in a title - body format. needs a "titles" and bodies" string array.
 */
class InfoTextActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_text)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }


        val recyclerView = findViewById<RecyclerView>(R.id.texty_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(false)

        val titles = intent.extras.getStringArray("titles")
        val bodies = intent.extras.getStringArray("bodies")
        recyclerView.adapter = InfoTextAdapter(titles, bodies)
    }
}