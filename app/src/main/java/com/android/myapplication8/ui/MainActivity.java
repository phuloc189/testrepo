package com.android.myapplication8.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.myapplication8.R;
import com.android.myapplication8.Util;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonManageDeck = findViewById(R.id.button_startScrn_manageDeck);
        buttonManageDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                intent.putExtra(Util.INTENT_EXTRA_KEY_MODE_SELECT, Util.INTENT_EXTRA_VALUE_MODE_SELECT_DECK_MANAGEMENT);
                startActivity(intent);
                finish();
            }
        });

        Button buttonManageCollection = findViewById(R.id.button_startScrn_manageCollection);
        buttonManageCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                intent.putExtra(Util.INTENT_EXTRA_KEY_MODE_SELECT, Util.INTENT_EXTRA_VALUE_MODE_SELECT_COLLECTION_MANAGEMENT);
                startActivity(intent);
                finish();
            }
        });
    }
}