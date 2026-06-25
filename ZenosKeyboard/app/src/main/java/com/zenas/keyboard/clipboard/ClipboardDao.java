package com.zenas.keyboard.clipboard;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClipboardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ClipboardItem item);

    @Query("SELECT * FROM clipboard_items ORDER BY timestamp DESC LIMIT :limit")
    List<ClipboardItem> getRecent(int limit);

    @Query("SELECT * FROM clipboard_items ORDER BY timestamp DESC")
    List<ClipboardItem> getAll();

    @Delete
    void delete(ClipboardItem item);

    @Query("DELETE FROM clipboard_items")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM clipboard_items")
    int count();

    // Avoid duplicates - delete oldest when > 50 items
    @Query("DELETE FROM clipboard_items WHERE id IN (SELECT id FROM clipboard_items ORDER BY timestamp ASC LIMIT MAX(0, (SELECT COUNT(*) FROM clipboard_items) - 50))")
    void pruneOld();
}
