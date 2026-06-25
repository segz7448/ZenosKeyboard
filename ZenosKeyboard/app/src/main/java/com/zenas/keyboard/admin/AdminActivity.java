package com.zenas.keyboard.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zenas.keyboard.R;

public class AdminActivity extends AppCompatActivity {

    private static final String[] TABS = {"📋 Clipboard", "⚡ Shortcuts", "⌨ Layout"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("⚙ Admin Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewPager2 pager = findViewById(R.id.view_pager);
        TabLayout tabs   = findViewById(R.id.tab_layout);

        pager.setAdapter(new AdminPagerAdapter(this));
        new TabLayoutMediator(tabs, pager, (tab, pos) -> tab.setText(TABS[pos])).attach();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ── Pager adapter ─────────────────────────────────────────────────────────
    static class AdminPagerAdapter extends FragmentStateAdapter {
        AdminPagerAdapter(FragmentActivity fa) { super(fa); }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new ClipboardAdminFragment();
                case 1: return new ShortcutsAdminFragment();
                case 2: return new LayoutAdminFragment();
                default: return new ClipboardAdminFragment();
            }
        }

        @Override
        public int getItemCount() { return 3; }
    }
}
