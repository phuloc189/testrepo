package com.android.myapplication8.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.myapplication8.R;
import com.android.myapplication8.Util;
import com.android.myapplication8.ViewModel1;
import com.android.myapplication8.ui.fragment.FragmentCardList;
import com.android.myapplication8.ui.fragment.FragmentCollectionList;
import com.android.myapplication8.ui.fragment.FragmentDeckAddRemoveForCollection;
import com.android.myapplication8.ui.fragment.FragmentDeckList;
import com.android.myapplication8.ui.fragment.FragmentStudyScreen;

/**
 * first activity right after start activity
 */
public class ActivityMainContentHost extends AppCompatActivity implements FragmentDeckList.Fragment1Interface,
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
        setupBackstackListener();

        String modeOfOp = getIntent().getStringExtra(Util.INTENT_EXTRA_KEY_MODE_SELECT);

        viewModel = new ViewModelProvider(this).get(ViewModel1.class);

        if (savedInstanceState == null) {
            if (modeOfOp.equals(Util.INTENT_EXTRA_VALUE_MODE_SELECT_COLLECTION_MANAGEMENT)) {
                launchCollectionListFragment();
            } else if (modeOfOp.equals(Util.INTENT_EXTRA_VALUE_MODE_SELECT_DECK_MANAGEMENT)) {
                launchDeckListFragment();
            }
        }
    }

    private void setupBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                handleBackStackChanged();
            }
        });
    }

    private void handleBackStackChanged() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() == 0) {
            finish();
            return;
        }
        Log.d(TAG, "BackStackChanged: currently: " +
                getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName());

        switch (manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName()) {
            case FragmentDeckList.TAG:
                getSupportActionBar().setTitle(R.string.top_bar_text_choose_a_deck);
                break;
            case FragmentCardList.TAG:
                getSupportActionBar().setTitle(R.string.top_bar_text_cards_preview);
                break;
            case FragmentStudyScreen.TAG:
                getSupportActionBar().setTitle(R.string.top_bar_text_study_mode);
                break;
            case FragmentCollectionList.TAG:
                getSupportActionBar().setTitle(R.string.top_bar_text_collection_screen);
                break;
            case FragmentDeckAddRemoveForCollection.TAG:
                getSupportActionBar().setTitle(R.string.top_bar_text_add_remove_deck_for_collection);
                break;
            default:
                Util.logDebug(TAG, "wtf");
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_activity_main2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void launchCollectionListFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_content, FragmentCollectionList.class, null)
                .addToBackStack(FragmentCollectionList.TAG)
                .commit();
    }

    void launchDeckListFragment() {
        // so that we only do it once, and wont do it again in case such as config change
        Log.d(TAG, "onCreate: launching");
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_content, FragmentDeckList.class, null)
                .addToBackStack(FragmentDeckList.TAG)
                .commit();
    }

    @Override
    public void onDeckSelected() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_content, FragmentCardList.class, null)
                .addToBackStack(FragmentCardList.TAG)
                .commit();
    }

    @Override
    public void onAddRemoveDeckTransition() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_content, FragmentDeckAddRemoveForCollection.class, null)
                .addToBackStack(FragmentDeckAddRemoveForCollection.TAG)
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
                .addToBackStack(FragmentStudyScreen.TAG)
                .commit();
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