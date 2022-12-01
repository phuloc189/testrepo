package com.android.myapplication8.database1;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_ItemClass1")
public class ItemClass1 {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @NonNull
    @ColumnInfo(name = "word")
    private String word;

    /**
     * todo: investigation
     *      - these constructor causes error
     *          magic temporary fix: change method signature from
     *                  ItemClass1(String content, int id)
     *              into:
     *                  ItemClass1(String word, int uid) // notice how parameter name matches with field name
     *          cause of error:
     *              for entity class, either a empty constructor (along with
     *              means to set ALL fields, either with setters or public fields???),
     *              or, a constructor whose signature must
     *              match all fields in BOTH TYPE AND NAME,
     *              must be provided
     *      - private field cause "no setter" error
     */
    public ItemClass1(String word, int uid) {
        this.uid = uid;
        this.word = word;
    }

    @Ignore
    public ItemClass1(String word) {
        /**
         * if this constructor signature was
         * ItemClass1(String word), it has to be tagged @Ignore to
         * avoid conflict with the required (by the code gen) constructor.
         * otherwise, compile error.
         * another solution is to the name the parameter to something that doesnt
         * match with fields name
         */
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public int getUid() {
        return uid;
    }

    public static boolean checkIfSameContent(ItemClass1 item1, ItemClass1 item2) {
        return item1.getWord().equals(item2.getWord())
                && item1.getUid() == item2.getUid();
    }
}
