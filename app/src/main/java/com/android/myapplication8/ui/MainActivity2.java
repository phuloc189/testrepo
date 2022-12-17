package com.android.myapplication8.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.ViewModel1;
import com.android.myapplication8.database2.DeckEntity;

/**
 * first activity right after start activity
 */
public class MainActivity2 extends AppCompatActivity implements FragmentDeckList.Fragment1Interface,
        FragmentCardList.Fragment2Interface, FragmentCollectionList.FragmentCollectionListCallback,
        FragmentDeckAddRemoveForCollection.FragmentDeckAddRemoveForCollectionCallback {
    public static String TAG = "MainActivity2";

    ViewModel1 viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main2);
        setupToolbar();

        String modeOfOp = getIntent().getStringExtra(Util.INTENT_EXTRA_KEY_MODE_SELECT);

        viewModel = new ViewModelProvider(this).get(ViewModel1.class);

        if (savedInstanceState == null) {
            if (modeOfOp.equals(Util.INTENT_EXTRA_VALUE_MODE_SELECT_COLLECTION_MANAGEMENT)){
                //todo: how to save status of this shit???
                launchCollectionListFragment();
            } else if (modeOfOp.equals(Util.INTENT_EXTRA_VALUE_MODE_SELECT_DECK_MANAGEMENT)) {
                launchDeckListFragment();
            }
        }
    }

    private void launchCollectionListFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_content, FragmentCollectionList.class, null)
                .addToBackStack("FragmentCollectionList")
                .commit();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_activity_main2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar0, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    void launchDeckListFragment() {
        // so that we only do it once, and wont do it again in case such as config change
        Log.d(TAG, "onCreate: launching");
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_content, FragmentDeckList.class, null)
                .addToBackStack("FragmentDeckList")
                .commit();

        getSupportActionBar().setTitle(getString(R.string.top_bar_text_choose_a_deck));
    }

    @Override
    public void onDeckSelected() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_content, FragmentCardList.class, null)
                .addToBackStack("FragmentCardList")
                .commit();
        getSupportActionBar().setTitle(getString(R.string.top_bar_text_cards_preview));
    }

    @Override
    public void onAddRemoveDeckTransition() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_content, FragmentDeckAddRemoveForCollection.class, null)
                .addToBackStack("FragmentDeckAddRemoveForCollection")
                .commit();
    }

    @Override
    public void moveToStudyMode_ForCollection() {
        loadStudyScreen();
    }

    @Override
    public void moveToStudyScreen() {
        loadStudyScreen();
    }

    private void loadStudyScreen() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_content, FragmentStudyScreen.class, null)
                .addToBackStack("FragmentStudyScreen")
                .commit();
        getSupportActionBar().setTitle(getString(R.string.top_bar_text_study_mode));
    }

    @Override
    public void onCollectionSelected() {
        launchDeckListFragment();
    }

    @Override
    public void settingComplete() {
        getSupportFragmentManager().popBackStack();
    }
}