package com.android.myapplication8.database2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.android.myapplication8.database2.dao.CardDao;
import com.android.myapplication8.database2.dao.CollectionDao;
import com.android.myapplication8.database2.dao.CollectionToDeckMapDao;
import com.android.myapplication8.database2.dao.DeckDao;
import com.android.myapplication8.database2.entity.CardEntity;
import com.android.myapplication8.database2.entity.CollectionEntity;
import com.android.myapplication8.database2.entity.CollectionToDeckMap;
import com.android.myapplication8.database2.entity.DeckEntity;

@Database(entities = {DeckEntity.class, CardEntity.class, CollectionEntity.class,
        CollectionToDeckMap.class}, version = 1)
public abstract class Database2 extends RoomDatabase {

    public abstract DeckDao deckDao();

    public abstract CardDao cardDao();

    public abstract CollectionDao collectionDao();

    public abstract CollectionToDeckMapDao collectionToDeckMapDao();

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
