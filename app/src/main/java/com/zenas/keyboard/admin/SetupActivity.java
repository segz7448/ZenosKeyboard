package com.zenas.keyboard.admin;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zenas.keyboard.R;

import java.util.List;

public class SetupActivity extends AppCompatActivity {

    private Button btnEnable, btnDefault, btnAdmin;
    private TextView statusEnable, statusDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        btnEnable    = findViewById(R.id.btn_enable);
        btnDefault   = findViewById(R.id.btn_default);
        btnAdmin     = findViewById(R.id.btn_admin);
        statusEnable = findViewById(R.id.status_enable);
        statusDefault= findViewById(R.id.status_default);

        btnEnable.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));

        btnDefault.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showInputMethodPicker();
        });

        btnAdmin.setOnClickListener(v ->
                startActivity(new Intent(this, AdminActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }

    private void updateStatus() {
        boolean enabled = isKeyboardEnabled();
        boolean isDefault = isKeyboardDefault();

        statusEnable.setText(enabled ? "✓ Enabled" : "⏳ Pending");
        statusEnable.setTextColor(getColor(enabled ? R.color.brand_accent : R.color.on_surface_variant));

        statusDefault.setText(isDefault ? "✓ Active" : "⏳ Pending");
        statusDefault.setTextColor(getColor(isDefault ? R.color.brand_accent : R.color.on_surface_variant));
    }

    private boolean isKeyboardEnabled() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        List<InputMethodInfo> methods = imm.getEnabledInputMethodList();
        for (InputMethodInfo info : methods) {
            if (info.getPackageName().equals(getPackageName())) return true;
        }
        return false;
    }

    private boolean isKeyboardDefault() {
        String defaultIme = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        return defaultIme != null && defaultIme.startsWith(getPackageName());
    }
}
