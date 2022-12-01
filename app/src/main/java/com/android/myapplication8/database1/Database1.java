package com.android.myapplication8.database1;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ItemClass1.class}, version = 1)
public abstract class Database1 extends RoomDatabase {

    private static volatile Database1 instance;
    public abstract ItemClass1Dao getItemClass1Dao();

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService dbWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static Database1 getInstance(Context context) {
        if (instance == null){
            synchronized (Database1.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, Database1.class, "database1").build();
                }
            }
        }

        return instance;
    }
}
