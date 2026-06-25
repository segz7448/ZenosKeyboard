package com.zenas.keyboard.clipboard;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class ClipboardService extends Service {

    private ClipboardManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = new ClipboardManager(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
