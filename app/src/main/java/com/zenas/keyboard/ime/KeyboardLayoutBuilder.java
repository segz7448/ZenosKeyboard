package com.zenas.keyboard.ime;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zenas.keyboard.R;

import java.util.function.Consumer;

public class KeyboardLayoutBuilder {

    private final Context ctx;
    private final View root;
    private final Consumer<String> keyHandler;

    private boolean shiftOn = false;
    private boolean capsLock = false;

    // Current layout
    private String layout = "qwerty";

    private static final String[][] QWERTY_ROW1 = {{"q","w","e","r","t","y","u","i","o","p"}};
    private static final String[][] QWERTY_ROW2 = {{"a","s","d","f","g","h","j","k","l"}};
    private static final String[][] QWERTY_ROW3 = {{"z","x","c","v","b","n","m"}};
    private static final String[]   NUMBER_ROW  = {"1","2","3","4","5","6","7","8","9","0"};
    private static final String[][] NUMBERS_PANEL_ROW1 = {{"1","2","3","4","5","6","7","8","9","0"}};
    private static final String[][] NUMBERS_PANEL_ROW2 = {{"@","#","$","%","^","&","*","(",")","-"}};
    private static final String[][] NUMBERS_PANEL_ROW3 = {{"+","=","[","]","{","}","\\","|","<",">"}};
    private static final String[][] NUMBERS_PANEL_ROW4 = {{",",".","?","!","\"","'","/","~","`","_"}};

    private static final String[][] AZERTY_ROW1 = {{"a","z","e","r","t","y","u","i","o","p"}};
    private static final String[][] AZERTY_ROW2 = {{"q","s","d","f","g","h","j","k","l","m"}};
    private static final String[][] AZERTY_ROW3 = {{"w","x","c","v","b","n",",",";",":","!"}};

    private static final String[][] DVORAK_ROW1 = {{"'",",",".","p","y","f","g","c","r","l"}};
    private static final String[][] DVORAK_ROW2 = {{"a","o","e","u","i","d","h","t","n","s"}};
    private static final String[][] DVORAK_ROW3 = {{";","q","j","k","x","b","m","w","v","z"}};

    public KeyboardLayoutBuilder(Context ctx, View root, Consumer<String> keyHandler) {
        this.ctx = ctx;
        this.root = root;
        this.keyHandler = keyHandler;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public void buildQwerty() {
        buildAlpha();
    }

    private void buildAlpha() {
        String[][] row1, row2, row3;
        switch (layout) {
            case "azerty": row1 = AZERTY_ROW1; row2 = AZERTY_ROW2; row3 = AZERTY_ROW3; break;
            case "dvorak": row1 = DVORAK_ROW1; row2 = DVORAK_ROW2; row3 = DVORAK_ROW3; break;
            default:       row1 = QWERTY_ROW1; row2 = QWERTY_ROW2; row3 = QWERTY_ROW3; break;
        }

        // Number row
        buildRow(root.findViewById(R.id.row_numbers), NUMBER_ROW, 1f, false);

        // Alpha rows
        buildRow(root.findViewById(R.id.row_qwerty1), row1[0], 1f, false);
        buildRow(root.findViewById(R.id.row_qwerty2), row2[0], 1.2f, false);

        // Row 3 with Shift and Backspace
        LinearLayout row3layout = root.findViewById(R.id.row_qwerty3);
        row3layout.removeAllViews();
        addSpecialKey(row3layout, "⇧", 1.5f);
        buildRow(row3layout, row3[0], 1f, false);
        addSpecialKey(row3layout, "⌫", 1.5f);

        // Action row
        buildActionRow(false);
    }

    public void buildNumbers() {
        buildRow(root.findViewById(R.id.row_numbers), NUMBERS_PANEL_ROW1[0], 1f, false);
        buildRow(root.findViewById(R.id.row_qwerty1), NUMBERS_PANEL_ROW2[0], 1f, false);
        buildRow(root.findViewById(R.id.row_qwerty2), NUMBERS_PANEL_ROW3[0], 1f, false);

        LinearLayout row3layout = root.findViewById(R.id.row_qwerty3);
        row3layout.removeAllViews();
        buildRow(row3layout, NUMBERS_PANEL_ROW4[0], 1f, false);

        buildActionRow(true);
    }

    private void buildRow(LinearLayout container, String[] keys, float weightBonus, boolean special) {
        container.removeAllViews();
        for (String key : keys) {
            Button btn = makeKey(key, 1f, special);
            container.addView(btn);
        }
    }

    private void buildActionRow(boolean isNumMode) {
        LinearLayout actionRow = root.findViewById(R.id.row_action);
        actionRow.removeAllViews();

        // Switch mode button
        addSpecialKey(actionRow, isNumMode ? "ABC" : "?123", 1.5f);

        // Clipboard button
        addSpecialKey(actionRow, "📋", 1f);

        // Comma / period
        if (!isNumMode) {
            addKey(actionRow, ",", 1f);
        }

        // Space bar
        Button space = makeKey(" ", 4f, false);
        space.setText("SPACE");
        space.setTextSize(12f);
        actionRow.addView(space);

        if (!isNumMode) {
            addKey(actionRow, ".", 1f);
        }

        // Enter
        Button enter = makeActionKey("↵", 2f);
        actionRow.addView(enter);
    }

    private Button makeKey(String label, float weight, boolean special) {
        Button btn = new Button(ctx);
        btn.setText(label);
        btn.setTextColor(ctx.getColor(R.color.key_text));
        btn.setTextSize(16f);
        btn.setBackground(ctx.getDrawable(special ? R.drawable.key_special_bg : R.drawable.key_bg));
        btn.setAllCaps(false);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, weight);
        lp.setMargins(2, 2, 2, 2);
        btn.setLayoutParams(lp);
        btn.setPadding(0, 0, 0, 0);
        btn.setMinHeight(0);
        btn.setMinimumHeight(0);
        btn.setMinWidth(0);
        btn.setMinimumWidth(0);
        btn.setStateListAnimator(null);

        String value = label;
        btn.setOnClickListener(v -> keyHandler.accept(value));
        return btn;
    }

    private Button makeActionKey(String label, float weight) {
        Button btn = makeKey(label, weight, false);
        btn.setBackground(ctx.getDrawable(R.drawable.key_action_bg));
        btn.setTextColor(ctx.getColor(R.color.white));
        return btn;
    }

    private void addKey(LinearLayout container, String label, float weight) {
        container.addView(makeKey(label, weight, false));
    }

    private void addSpecialKey(LinearLayout container, String label, float weight) {
        Button btn = makeKey(label, weight, true);
        btn.setTextColor(ctx.getColor(R.color.brand_accent));
        btn.setTextSize(13f);
        container.addView(btn);
    }

    public void updateShiftState(boolean shift, boolean caps) {
        this.shiftOn = shift;
        this.capsLock = caps;
        updateRowCase(root.findViewById(R.id.row_numbers));
        updateRowCase(root.findViewById(R.id.row_qwerty1));
        updateRowCase(root.findViewById(R.id.row_qwerty2));
        updateRowCase(root.findViewById(R.id.row_qwerty3));

        // Update shift key appearance
        LinearLayout row3 = root.findViewById(R.id.row_qwerty3);
        if (row3.getChildCount() > 0) {
            View first = row3.getChildAt(0);
            if (first instanceof Button) {
                Button shiftBtn = (Button) first;
                if (caps) {
                    shiftBtn.setText("⇪");
                    shiftBtn.setTextColor(ctx.getColor(R.color.key_bg_pressed));
                } else if (shift) {
                    shiftBtn.setText("⇧");
                    shiftBtn.setTextColor(ctx.getColor(R.color.brand_accent));
                } else {
                    shiftBtn.setText("⇧");
                    shiftBtn.setTextColor(ctx.getColor(R.color.brand_accent));
                }
            }
        }
    }

    private void updateRowCase(LinearLayout row) {
        if (row == null) return;
        for (int i = 0; i < row.getChildCount(); i++) {
            View child = row.getChildAt(i);
            if (child instanceof Button) {
                Button btn = (Button) child;
                String text = btn.getText().toString();
                if (text.length() == 1 && Character.isLetter(text.charAt(0))) {
                    btn.setText((shiftOn || capsLock) ? text.toUpperCase() : text.toLowerCase());
                }
            }
        }
    }
}
