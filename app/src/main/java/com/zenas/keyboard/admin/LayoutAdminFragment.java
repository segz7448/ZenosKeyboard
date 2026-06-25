package com.zenas.keyboard.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zenas.keyboard.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.button.MaterialButton;

import android.widget.LinearLayout;
import android.widget.TextView;

public class LayoutAdminFragment extends Fragment {

    public static final String PREFS = "zenos_keyboard_prefs";
    public static final String KEY_LAYOUT = "keyboard_layout";

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Build layout programmatically (simple for now)
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(48, 48, 48, 48);
        root.setBackgroundColor(requireContext().getColor(R.color.brand_primary));

        TextView title = new TextView(requireContext());
        title.setText("Keyboard Layout");
        title.setTextColor(requireContext().getColor(R.color.white));
        title.setTextSize(18f);
        title.setPadding(0, 0, 0, 32);
        root.addView(title);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String current = prefs.getString(KEY_LAYOUT, "qwerty");

        String[] layouts = {"qwerty", "azerty", "dvorak"};
        String[] labels  = {"QWERTY (Default)", "AZERTY (French)", "Dvorak"};

        for (int i = 0; i < layouts.length; i++) {
            final String layout = layouts[i];
            MaterialButton btn = new MaterialButton(requireContext());
            btn.setText(labels[i]);
            btn.setTextSize(14f);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 120);
            lp.setMargins(0, 0, 0, 16);
            btn.setLayoutParams(lp);

            if (layout.equals(current)) {
                btn.setBackgroundColor(requireContext().getColor(R.color.brand_accent));
                btn.setTextColor(requireContext().getColor(R.color.brand_primary));
            } else {
                btn.setBackgroundColor(requireContext().getColor(R.color.surface));
                btn.setTextColor(requireContext().getColor(R.color.on_surface));
            }

            btn.setOnClickListener(v -> {
                prefs.edit().putString(KEY_LAYOUT, layout).apply();
                // Refresh visuals
                if (getParentFragment() != null || isAdded()) {
                    requireActivity().recreate();
                }
            });

            root.addView(btn);
        }

        TextView note = new TextView(requireContext());
        note.setText("Changes take effect the next time you open the keyboard.");
        note.setTextColor(requireContext().getColor(R.color.on_surface_variant));
        note.setTextSize(12f);
        note.setPadding(0, 24, 0, 0);
        root.addView(note);

        return root;
    }
}
