package com.android.myapplication8.database2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DeckEntity.class, CardEntity.class}, version = 1)
public abstract class Database2 extends RoomDatabase {

    public abstract DeckDao deckDao();

    public abstract CardDao cardDao();

    private static volatile Database2 instance;

    public static Database2 getInstance(Context context) {
        if (instance == null) {
            synchronized (Database2.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, Database2.class, "database2")
                            .build();
                }
            }
        }
        return instance;
    }
}
