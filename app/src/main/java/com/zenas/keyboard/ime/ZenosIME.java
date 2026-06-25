package com.zenas.keyboard.ime;

import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zenas.keyboard.R;
import com.zenas.keyboard.admin.LayoutAdminFragment;
import com.zenas.keyboard.clipboard.ClipboardManager;
import com.zenas.keyboard.clipboard.ClipboardPanel;
import com.zenas.keyboard.shortcuts.ShortcutManager;

import java.util.List;

public class ZenosIME extends InputMethodService {

    private KeyboardLayoutBuilder layoutBuilder;
    private ClipboardManager clipboardManager;
    private ShortcutManager shortcutManager;
    private Vibrator vibrator;

    private View keyboardView;
    private ClipboardPanel clipboardPanel;

    private boolean shiftOn = false;
    private boolean capsLock = false;
    private boolean numMode = false;
    private boolean showingClipboard = false;

    private static final int VIBRATE_MS = 20;

    @Override
    public void onCreate() {
        super.onCreate();
        clipboardManager = new ClipboardManager(this);
        shortcutManager = new ShortcutManager(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateInputView() {
        // Load preferred layout
        SharedPreferences prefs =
                getSharedPreferences(LayoutAdminFragment.PREFS, MODE_PRIVATE);
        String layoutName = prefs.getString(LayoutAdminFragment.KEY_LAYOUT, "qwerty");

        keyboardView = LayoutInflater.from(this).inflate(R.layout.keyboard_view, null);
        layoutBuilder = new KeyboardLayoutBuilder(this, keyboardView, this::onKeyPressed);
        layoutBuilder.setLayout(layoutName);
        layoutBuilder.buildQwerty();
        setupTermuxRow();
        setupSuggestionBar();
        clipboardPanel = new ClipboardPanel(this, clipboardManager, this::onClipboardItemPasted);
        return keyboardView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        updateSuggestions();
    }

    // ── Termux row ────────────────────────────────────────────────────────────
    private void setupTermuxRow() {
        Button ctrlC = keyboardView.findViewById(R.id.key_ctrl_c);
        Button ctrlZ = keyboardView.findViewById(R.id.key_ctrl_z);
        Button ctrlD = keyboardView.findViewById(R.id.key_ctrl_d);
        Button ctrlL = keyboardView.findViewById(R.id.key_ctrl_l);
        Button tab   = keyboardView.findViewById(R.id.key_tab);

        ctrlC.setOnClickListener(v -> sendCtrl('c'));
        ctrlZ.setOnClickListener(v -> sendCtrl('z'));
        ctrlD.setOnClickListener(v -> sendCtrl('d'));
        ctrlL.setOnClickListener(v -> sendCtrl('l'));
        tab.setOnClickListener(v -> sendTab());
    }

    private void sendCtrl(char key) {
        vibrate();
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        // Send CTRL + key via KeyEvent
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT));
        ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
                KeyEvent.keyCodeFromString("KEYCODE_" + Character.toUpperCase(key)), 0,
                KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON));
        ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
                KeyEvent.keyCodeFromString("KEYCODE_" + Character.toUpperCase(key)), 0,
                KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON));
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT));
    }

    private void sendTab() {
        vibrate();
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB));
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_TAB));
    }

    // ── Key press handler ────────────────────────────────────────────────────
    void onKeyPressed(String keyValue) {
        vibrate();
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (keyValue) {
            case "⌫":
                handleBackspace(ic);
                break;
            case "↵":
            case "⏎":
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;
            case "⇧":
                toggleShift();
                break;
            case "⇪":
                toggleCapsLock();
                break;
            case "123":
            case "?123":
                numMode = true;
                layoutBuilder.buildNumbers();
                break;
            case "ABC":
            case "⌨":
                numMode = false;
                layoutBuilder.buildQwerty();
                updateShiftDisplay();
                break;
            case "📋":
                toggleClipboardPanel();
                break;
            case " ":
                handleSpace(ic);
                break;
            default:
                handleCharacter(ic, keyValue);
        }
    }

    private void handleCharacter(InputConnection ic, String chars) {
        // Expand shortcuts first
        String currentWord = getCurrentWord(ic);
        String expanded = shortcutManager.expand(currentWord + chars);
        if (expanded != null) {
            // Delete the typed shortcut trigger and insert expansion
            ic.deleteSurroundingText(currentWord.length(), 0);
            ic.commitText(expanded, 1);
            resetShift();
            return;
        }
        // Normal character
        String toType = (shiftOn || capsLock) ? chars.toUpperCase() : chars.toLowerCase();
        ic.commitText(toType, 1);
        if (shiftOn && !capsLock) {
            shiftOn = false;
            layoutBuilder.updateShiftState(false, false);
        }
        // Save to clipboard manager if needed (handled by system ClipboardListener)
    }

    private void handleSpace(InputConnection ic) {
        // Check shortcut expansion on space
        String word = getCurrentWord(ic);
        if (!TextUtils.isEmpty(word)) {
            String expanded = shortcutManager.expand(word);
            if (expanded != null) {
                ic.deleteSurroundingText(word.length(), 0);
                ic.commitText(expanded + " ", 1);
                resetShift();
                return;
            }
        }
        ic.commitText(" ", 1);
    }

    private void handleBackspace(InputConnection ic) {
        CharSequence sel = ic.getSelectedText(0);
        if (!TextUtils.isEmpty(sel)) {
            ic.commitText("", 1);
        } else {
            ic.deleteSurroundingText(1, 0);
        }
    }

    private String getCurrentWord(InputConnection ic) {
        CharSequence before = ic.getTextBeforeCursor(50, 0);
        if (before == null) return "";
        int i = before.length() - 1;
        while (i >= 0 && !Character.isWhitespace(before.charAt(i))) i--;
        return before.subSequence(i + 1, before.length()).toString();
    }

    // ── Shift / Caps ─────────────────────────────────────────────────────────
    private void toggleShift() {
        if (capsLock) {
            capsLock = false;
            shiftOn = false;
        } else if (shiftOn) {
            capsLock = true;
        } else {
            shiftOn = true;
        }
        layoutBuilder.updateShiftState(shiftOn, capsLock);
    }

    private void toggleCapsLock() {
        capsLock = !capsLock;
        shiftOn = capsLock;
        layoutBuilder.updateShiftState(shiftOn, capsLock);
    }

    private void resetShift() {
        if (!capsLock) {
            shiftOn = false;
            layoutBuilder.updateShiftState(false, false);
        }
    }

    private void updateShiftDisplay() {
        layoutBuilder.updateShiftState(shiftOn, capsLock);
    }

    // ── Suggestion bar ────────────────────────────────────────────────────────
    private void setupSuggestionBar() {
        updateSuggestions();
    }

    private void updateSuggestions() {
        if (keyboardView == null) return;
        LinearLayout bar = keyboardView.findViewById(R.id.suggestion_bar);
        bar.removeAllViews();

        List<String> clips = clipboardManager.getRecentItems(5);
        if (clips.isEmpty()) return;

        for (String clip : clips) {
            TextView tv = new TextView(this);
            tv.setText(clip.length() > 20 ? clip.substring(0, 20) + "…" : clip);
            tv.setTextColor(getColor(R.color.suggestion_text));
            tv.setTextSize(12f);
            tv.setPadding(20, 0, 20, 0);
            tv.setMaxLines(1);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setOnClickListener(v -> onClipboardItemPasted(clip));
            bar.addView(tv);

            // Divider
            View div = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 8, 0, 8);
            div.setLayoutParams(lp);
            div.setBackgroundColor(getColor(R.color.suggestion_divider));
            bar.addView(div);
        }
    }

    // ── Clipboard panel ───────────────────────────────────────────────────────
    private void toggleClipboardPanel() {
        if (showingClipboard) {
            hideClipboardPanel();
        } else {
            showClipboardPanel();
        }
    }

    private void showClipboardPanel() {
        showingClipboard = true;
        setInputView(clipboardPanel.buildView(this, this::hideClipboardPanel));
    }

    private void hideClipboardPanel() {
        showingClipboard = false;
        setInputView(keyboardView);
    }

    private void onClipboardItemPasted(String text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) ic.commitText(text, 1);
        hideClipboardPanel();
    }

    // ── Utilities ─────────────────────────────────────────────────────────────
    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VIBRATE_MS);
        }
    }
}
