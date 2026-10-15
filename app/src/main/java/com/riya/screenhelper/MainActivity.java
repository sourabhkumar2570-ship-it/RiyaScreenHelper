package com.riya.screenhelper;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(36, 48, 36, 36);
        TextView title = new TextView(this);
        title.setText("RIYA Screen Helper\n\nThis helper reads visible screen text only while Accessibility is ON. Password fields are skipped. Screen text stays on this phone; the local bridge is available only on 127.0.0.1:8765.");
        title.setTextSize(18);
        layout.addView(title);
        Button settings = new Button(this);
        settings.setText("Open Accessibility Settings");
        settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        layout.addView(settings);
        TextView note = new TextView(this);
        note.setText("Enable ‘RIYA Screen Reader’ manually. To stop reading, turn it OFF on the same Settings page.");
        note.setPadding(0, 24, 0, 0);
        layout.addView(note);
        setContentView(layout);
    }
}
