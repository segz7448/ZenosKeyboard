package com.zenas.keyboard.clipboard;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ClipboardItem.class}, version = 1, exportSchema = false)
public abstract class ClipboardDatabase extends RoomDatabase {

    private static volatile ClipboardDatabase INSTANCE;

    public abstract ClipboardDao clipboardDao();

    public static ClipboardDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ClipboardDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ClipboardDatabase.class,
                            "zenos_keyboard.db"
                    ).allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
