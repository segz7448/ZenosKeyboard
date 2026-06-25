package com.zenas.keyboard.shortcuts;

import android.content.Context;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ShortcutManager {

    private final ShortcutDatabase db;
    private final ShortcutDao dao;

    public ShortcutManager(Context context) {
        db = ShortcutDatabase.getInstance(context);
        dao = db.shortcutDao();
        seedDefaults();
    }

    private void seedDefaults() {
        if (dao.getAll().isEmpty()) {
            Calendar cal = Calendar.getInstance();
            String today = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));
            dao.insert(new ShortcutItem("@@date", today));
            dao.insert(new ShortcutItem("@@brb", "Be right back"));
            dao.insert(new ShortcutItem("@@ty", "Thank you!"));
            dao.insert(new ShortcutItem("@@np", "No problem!"));
            dao.insert(new ShortcutItem("@@omw", "On my way!"));
        }
    }

    /** Returns expanded string or null if no match */
    public String expand(String word) {
        if (word == null || word.isEmpty()) return null;
        ShortcutItem item = dao.findByKeyword(word.trim());
        if (item != null) return item.expansion;
        return null;
    }

    public List<ShortcutItem> getAll() {
        return dao.getAll();
    }

    public void add(String keyword, String expansion) {
        dao.insert(new ShortcutItem(keyword.trim(), expansion));
    }

    public void update(ShortcutItem item) {
        dao.update(item);
    }

    public void delete(ShortcutItem item) {
        dao.delete(item);
    }
}
