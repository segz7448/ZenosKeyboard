package com.zenas.keyboard.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zenas.keyboard.R;
import com.zenas.keyboard.shortcuts.ShortcutItem;
import com.zenas.keyboard.shortcuts.ShortcutManager;

import java.util.ArrayList;
import java.util.List;

public class ShortcutsAdminFragment extends Fragment {

    private ShortcutManager manager;
    private ShortcutAdapter adapter;
    private TextView emptyView;
    private View addForm;
    private EditText inputKeyword, inputExpansion;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shortcuts_admin, container, false);
        manager = new ShortcutManager(requireContext());

        RecyclerView list = v.findViewById(R.id.shortcuts_list);
        emptyView        = v.findViewById(R.id.shortcuts_empty);
        addForm          = v.findViewById(R.id.add_form);
        inputKeyword     = v.findViewById(R.id.input_keyword);
        inputExpansion   = v.findViewById(R.id.input_expansion);
        Button addBtn    = v.findViewById(R.id.btn_add_shortcut);
        Button saveBtn   = v.findViewById(R.id.btn_save_shortcut);
        Button cancelBtn = v.findViewById(R.id.btn_cancel_shortcut);

        adapter = new ShortcutAdapter(new ArrayList<>(manager.getAll()), item -> {
            manager.delete(item);
            adapter.remove(item);
            updateEmpty();
        });
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);
        updateEmpty();

        addBtn.setOnClickListener(b -> addForm.setVisibility(View.VISIBLE));
        cancelBtn.setOnClickListener(b -> {
            addForm.setVisibility(View.GONE);
            inputKeyword.setText("");
            inputExpansion.setText("");
        });
        saveBtn.setOnClickListener(b -> {
            String kw  = inputKeyword.getText().toString().trim();
            String exp = inputExpansion.getText().toString().trim();
            if (kw.isEmpty() || exp.isEmpty()) return;
            manager.add(kw, exp);
            ShortcutItem item = new ShortcutItem(kw, exp);
            adapter.add(item);
            addForm.setVisibility(View.GONE);
            inputKeyword.setText("");
            inputExpansion.setText("");
            updateEmpty();
        });

        return v;
    }

    private void updateEmpty() {
        if (emptyView == null || adapter == null) return;
        emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    // ── Adapter ───────────────────────────────────────────────────────────────
    static class ShortcutAdapter extends RecyclerView.Adapter<ShortcutAdapter.VH> {
        private final List<ShortcutItem> items;
        private final java.util.function.Consumer<ShortcutItem> onDelete;

        ShortcutAdapter(List<ShortcutItem> items, java.util.function.Consumer<ShortcutItem> onDelete) {
            this.items = items;
            this.onDelete = onDelete;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shortcut, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ShortcutItem item = items.get(position);
            holder.keyword.setText(item.keyword);
            holder.expansion.setText("→ " + item.expansion);
            holder.delete.setOnClickListener(v -> onDelete.accept(item));
        }

        @Override public int getItemCount() { return items.size(); }

        void remove(ShortcutItem item) {
            int idx = items.indexOf(item);
            if (idx >= 0) { items.remove(idx); notifyItemRemoved(idx); }
        }

        void add(ShortcutItem item) {
            items.add(0, item);
            notifyItemInserted(0);
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView keyword, expansion;
            Button delete;
            VH(View v) {
                super(v);
                keyword   = v.findViewById(R.id.shortcut_keyword);
                expansion = v.findViewById(R.id.shortcut_expansion);
                delete    = v.findViewById(R.id.shortcut_delete);
            }
        }
    }
}
