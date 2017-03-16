package com.example.ilya.gb_android2_les5_notewidget.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.ilya.gb_android2_les5_notewidget.R;

public class WidgetConfigActivity extends AppCompatActivity {
    public static final String PREFERENCES = "PREF";
    private int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resaultIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                     AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        resaultIntent = new Intent();
        resaultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        setResult(RESULT_CANCELED, resaultIntent);

        /*LinearLayout layout = (LinearLayout) findViewById(R.id.layout_widget);
        layout.setBackgroundColor();*/
    }

    public void onClick(View view) {
        if (view == null) return;
        int color = Color.parseColor(view.getTag().toString());
        SharedPreferences opions = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = opions.edit();
        editor.putInt(String.valueOf(widgetID), color);
        editor.apply();
        setResult(RESULT_OK, resaultIntent);
        finish();
    }
}