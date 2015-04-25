package com.example.utkarshgagrg.sustarch;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MarketPlace extends ListActivity {

    String[] itemName ={
            "Ashford Art Gallery",
            "Nature Museum",
            "Global Trade Center",
            "Google Imaginative Headquarters",
            "Georgia Tech 2050 Concept",
            "UIUC Main Auditorium",
            "MIT Kresger Auditorium",
            "IBM Design Building 50",
            "Amazon Building 67",
            "Some Other Building"
    };

    Integer[] imageID = {
            R.drawable.img_1,
            R.drawable.img_2,
            R.drawable.img_3,
            R.drawable.img_4,
            R.drawable.img_5,
            R.drawable.img_6,
            R.drawable.img_7,
            R.drawable.img_8,
            R.drawable.img_9,
            R.drawable.img_10
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_place);

        CustomListAdapter adapter=new CustomListAdapter(this, itemName, imageID);

        this.setListAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_market_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(MarketPlace.this, UserProfilePage.class));
        finish();

    }
}
