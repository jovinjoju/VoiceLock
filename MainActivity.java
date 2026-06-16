package com.example.voicelock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.LinearLayout;
import android.view.Gravity;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setGravity(Gravity.CENTER);
        
        Switch toggle = new Switch(this);
        toggle.setText("Enable VoiceLock ");
        layout.addView(toggle);
        setContentView(layout);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        toggle.setChecked(prefs.getBoolean("is_enabled", true));

        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("is_enabled", isChecked).apply();
        });
    }
}
