package com.zenas.keyboard.shortcuts;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shortcuts")
public class ShortcutItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String keyword;    // e.g. "@@email"
    public String expansion;  // e.g. "zenas@example.com"

    public ShortcutItem(String keyword, String expansion) {
        this.keyword = keyword;
        this.expansion = expansion;
    }
}
