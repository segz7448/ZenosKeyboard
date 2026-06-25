package com.zenas.keyboard.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zenas.keyboard.R;
import com.zenas.keyboard.clipboard.ClipboardItem;
import com.zenas.keyboard.clipboard.ClipboardManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClipboardAdminFragment extends Fragment {

    private ClipboardManager manager;
    private AdminClipAdapter adapter;
    private TextView emptyView;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clipboard_admin, container, false);
        manager = new ClipboardManager(requireContext());

        RecyclerView list = v.findViewById(R.id.admin_clip_list);
        emptyView = v.findViewById(R.id.admin_clip_empty);
        Button clearAll = v.findViewById(R.id.btn_clear_all);

        List<ClipboardItem> items = manager.getAllItems();
        adapter = new AdminClipAdapter(new ArrayList<>(items), item -> {
            manager.deleteItem(item);
            adapter.remove(item);
            updateEmpty();
        });
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);
        updateEmpty();

        clearAll.setOnClickListener(btn -> {
            manager.clearAll();
            adapter.clearAll();
            updateEmpty();
        });

        return v;
    }

    private void updateEmpty() {
        if (emptyView == null || adapter == null) return;
        emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    // ── Adapter ───────────────────────────────────────────────────────────────
    static class AdminClipAdapter extends RecyclerView.Adapter<AdminClipAdapter.VH> {

        private final List<ClipboardItem> items;
        private final java.util.function.Consumer<ClipboardItem> onDelete;
        private final SimpleDateFormat sdf = new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault());

        AdminClipAdapter(List<ClipboardItem> items, java.util.function.Consumer<ClipboardItem> onDelete) {
            this.items = items;
            this.onDelete = onDelete;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_clipboard_admin, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ClipboardItem item = items.get(position);
            holder.text.setText(item.text);
            holder.time.setText(sdf.format(new Date(item.timestamp)));
            holder.delete.setOnClickListener(v -> onDelete.accept(item));
        }

        @Override public int getItemCount() { return items.size(); }

        void remove(ClipboardItem item) {
            int idx = items.indexOf(item);
            if (idx >= 0) { items.remove(idx); notifyItemRemoved(idx); }
        }

        void clearAll() {
            int s = items.size();
            items.clear();
            notifyItemRangeRemoved(0, s);
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView text, time;
            Button delete;
            VH(View v) {
                super(v);
                text = v.findViewById(R.id.admin_clip_text);
                time = v.findViewById(R.id.admin_clip_time);
                delete = v.findViewById(R.id.admin_clip_delete);
            }
        }
    }
}
