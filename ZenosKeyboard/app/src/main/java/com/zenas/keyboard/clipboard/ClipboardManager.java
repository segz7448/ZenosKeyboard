package com.zenas.keyboard.clipboard;

import android.content.ClipData;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ClipboardManager {

    private final ClipboardDatabase db;
    private final ClipboardDao dao;
    private final android.content.ClipboardManager systemClipboard;

    public ClipboardManager(Context context) {
        db = ClipboardDatabase.getInstance(context);
        dao = db.clipboardDao();
        systemClipboard = (android.content.ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        registerListener();
    }

    private void registerListener() {
        systemClipboard.addPrimaryClipChangedListener(() -> {
            ClipData data = systemClipboard.getPrimaryClip();
            if (data == null || data.getItemCount() == 0) return;
            String text = data.getItemAt(0).getText() != null
                    ? data.getItemAt(0).getText().toString() : "";
            if (!text.isEmpty()) {
                addItem(text);
            }
        });
    }

    public void addItem(String text) {
        // Avoid duplicates
        List<ClipboardItem> existing = dao.getAll();
        for (ClipboardItem item : existing) {
            if (item.text.equals(text)) {
                // Move to top by updating timestamp
                item.timestamp = System.currentTimeMillis();
                dao.delete(item);
                dao.insert(item);
                return;
            }
        }
        ClipboardItem item = new ClipboardItem(text, System.currentTimeMillis());
        dao.insert(item);
        dao.pruneOld();
    }

    public List<String> getRecentItems(int limit) {
        List<ClipboardItem> items = dao.getRecent(limit);
        List<String> result = new ArrayList<>();
        for (ClipboardItem item : items) result.add(item.text);
        return result;
    }

    public List<ClipboardItem> getAllItems() {
        return dao.getAll();
    }

    public void deleteItem(ClipboardItem item) {
        dao.delete(item);
    }

    public void clearAll() {
        dao.deleteAll();
    }
}
