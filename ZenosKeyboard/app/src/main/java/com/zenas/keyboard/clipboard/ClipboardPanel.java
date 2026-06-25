package com.zenas.keyboard.clipboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zenas.keyboard.R;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClipboardPanel {

    private final ClipboardManager manager;
    private final Consumer<String> onPaste;
    private View panelView;
    private ClipAdapter adapter;

    public ClipboardPanel(Context ctx, ClipboardManager manager, Consumer<String> onPaste) {
        this.manager = manager;
        this.onPaste = onPaste;
    }

    public View buildView(Context ctx, Runnable onClose) {
        panelView = LayoutInflater.from(ctx).inflate(R.layout.panel_clipboard, null);

        RecyclerView recycler = panelView.findViewById(R.id.clipboard_recycler);
        TextView empty = panelView.findViewById(R.id.clipboard_empty);
        Button clearBtn = panelView.findViewById(R.id.btn_clear_clipboard);
        Button closeBtn = panelView.findViewById(R.id.btn_close_clipboard);

        List<ClipboardItem> items = manager.getAllItems();
        if (items.isEmpty()) {
            recycler.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }

        adapter = new ClipAdapter(new ArrayList<>(items), onPaste, item -> {
            manager.deleteItem(item);
            adapter.removeItem(item);
            if (adapter.getItemCount() == 0) {
                recycler.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(adapter);

        clearBtn.setOnClickListener(v -> {
            manager.clearAll();
            adapter.clearAll();
            recycler.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        });

        closeBtn.setOnClickListener(v -> onClose.run());

        return panelView;
    }

    // ── Adapter ───────────────────────────────────────────────────────────────
    public static class ClipAdapter extends RecyclerView.Adapter<ClipAdapter.VH> {

        private final List<ClipboardItem> items;
        private final Consumer<String> onPaste;
        private final Consumer<ClipboardItem> onDelete;

        public ClipAdapter(List<ClipboardItem> items, Consumer<String> onPaste,
                           Consumer<ClipboardItem> onDelete) {
            this.items = items;
            this.onPaste = onPaste;
            this.onDelete = onDelete;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_clipboard, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            ClipboardItem item = items.get(position);
            holder.text.setText(item.text);
            holder.text.setOnClickListener(v -> onPaste.accept(item.text));
            holder.delete.setOnClickListener(v -> onDelete.accept(item));
        }

        @Override
        public int getItemCount() { return items.size(); }

        public void removeItem(ClipboardItem item) {
            int idx = items.indexOf(item);
            if (idx >= 0) {
                items.remove(idx);
                notifyItemRemoved(idx);
            }
        }

        public void clearAll() {
            int size = items.size();
            items.clear();
            notifyItemRangeRemoved(0, size);
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView text;
            Button delete;
            VH(View v) {
                super(v);
                text = v.findViewById(R.id.clip_text);
                delete = v.findViewById(R.id.clip_delete);
            }
        }
    }
}
