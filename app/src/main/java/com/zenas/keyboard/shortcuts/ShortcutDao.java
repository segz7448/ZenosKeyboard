package com.zenas.keyboard.shortcuts;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ShortcutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ShortcutItem item);

    @Update
    void update(ShortcutItem item);

    @Delete
    void delete(ShortcutItem item);

    @Query("SELECT * FROM shortcuts ORDER BY keyword ASC")
    List<ShortcutItem> getAll();

    @Query("SELECT * FROM shortcuts WHERE keyword = :keyword LIMIT 1")
    ShortcutItem findByKeyword(String keyword);

    @Query("DELETE FROM shortcuts")
    void deleteAll();
}
