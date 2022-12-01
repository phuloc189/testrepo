package com.android.myapplication8.database1;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.android.myapplication8.Util;

import java.util.List;

public class Database1Wrapper {

    private static final String TAG = "Database1Wrapper";

    ItemClass1Dao itemClass1DaoRef;

    Db1WrapperInterface callback;

    public interface Db1WrapperInterface {
        void onReadResult(List<ItemClass1> result);

        void onWriteComplete(long writeId);
    }

    public Database1Wrapper (Application application, Db1WrapperInterface callback) {
        itemClass1DaoRef = Database1.getInstance(application).getItemClass1Dao();
        this.callback = callback;
    }

    /**
     * todo: explore other option: use livedata on reading
     *     question: when will it be updated???
     *          does it update when underlying db is updated??? (which mean we only need to call read method once)
     *
     *     UPDATE: calling read on main thread is also a NO NO,
     *     so this call from main thread == crash
     */
//    public List<ItemClass1> readAll() { // this is fucked
//        return itemClass1DaoRef.getAll();
//    }

    public LiveData<List<ItemClass1>> readAllLiveData() { // this is ok, because of livedata
        return itemClass1DaoRef.getAllLiveData();
    }

    public void readAllAsync() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<ItemClass1> list = itemClass1DaoRef.getAll(); // this will block operation
                callback.onReadResult(list);
                Util.logDebug(TAG,"list size: " + list.size());
            }
        });
    }

    public void insert (ItemClass1 item) {
        Database1.dbWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long id = itemClass1DaoRef.insert(item);
                Util.logDebug(TAG, "insert result: " + id);
                callback.onWriteComplete(id);
            }
        });
    }

    /**
     * test methods
     */
    private void testIterate(List<ItemClass1> list) {
        for (int i = 0; i < list.size(); i++ ) {
            Log.d(TAG, "item information, uid: " + list.get(i).getUid() + ", content: " + list.get(i).getWord());
        }
    }
}
