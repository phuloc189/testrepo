package com.android.myapplication8.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.android.myapplication8.R;
import com.android.myapplication8.database2.DeckEntity;

/**
 * first activity right after start activity
 */
public class MainActivity2 extends AppCompatActivity implements FragmentDeckList.Fragment1Interface,
        FragmentCardList.Fragment2Interface {
    public static String TAG = "MainActivity2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main2);

        setupToolbar();

        launchDeckListFragment(savedInstanceState);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_activity_main2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("wait wut???");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar0, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    void launchDeckListFragment(Bundle savedInstanceState) {
        if(savedInstanceState == null ) { // so that we only do it once, and wont do it again in case such as config change
            Log.d(TAG, "onCreate: launching");
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_content, FragmentDeckList.class, null)
                    .addToBackStack("Fragment1")
                    .commit();
        }
    }

    @Override
    public void onDeckSelected() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_content, FragmentCardList.class, null)
                .addToBackStack("Fragment2")
                .commit();
    }

    @Override
    public void moveToStudyScreen() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_content, FragmentStudyScreen.class, null)
                .addToBackStack("Fragment3")
                .commit();
    }
}