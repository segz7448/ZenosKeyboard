package com.zenas.keyboard.clipboard;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clipboard_items")
public class ClipboardItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String text;
    public long timestamp;

    public ClipboardItem(String text, long timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }
}
