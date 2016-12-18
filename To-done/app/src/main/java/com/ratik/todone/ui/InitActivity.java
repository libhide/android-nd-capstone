package com.ratik.todone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.util.Constants;
import com.ratik.todone.R;

public class InitActivity extends AppCompatActivity implements OnTimeSetListener {

    private static final String HOUR_OF_DAY = "hour";
    private static final String MINUTE = "minute";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If a list already exists
        // Transfer UI to MainActivity
        if (Prefs.getBoolean(Constants.LIST_EXISTS, false)) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
        }

        setContentView(R.layout.activity_init);

        Button initButton = (Button) findViewById(R.id.initButton);
        initButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FormDialog().show(getSupportFragmentManager(), "FormDialog");
            }
        });
    }

    @Override
    public void onTimeSet(int hourOfDay, int minute) {
        Intent intent = new Intent(InitActivity.this, ListInputActivity.class);
        intent.putExtra(HOUR_OF_DAY, hourOfDay);
        intent.putExtra(MINUTE, minute);
        startActivity(intent);
    }
}
