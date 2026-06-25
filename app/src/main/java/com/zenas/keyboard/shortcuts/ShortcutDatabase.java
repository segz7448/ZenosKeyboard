package com.zenas.keyboard.shortcuts;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ShortcutItem.class}, version = 1, exportSchema = false)
public abstract class ShortcutDatabase extends RoomDatabase {

    private static volatile ShortcutDatabase INSTANCE;

    public abstract ShortcutDao shortcutDao();

    public static ShortcutDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ShortcutDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ShortcutDatabase.class,
                            "zenos_shortcuts.db"
                    ).allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
