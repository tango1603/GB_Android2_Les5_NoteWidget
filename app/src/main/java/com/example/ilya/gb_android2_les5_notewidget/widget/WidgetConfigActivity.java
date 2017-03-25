package com.example.ilya.gb_android2_les5_notewidget.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.ilya.gb_android2_les5_notewidget.R;

public class WidgetConfigActivity extends AppCompatActivity {
    public static final String PREFERENCES = "PREF";
    public static final String TAG = "DEBUGGG";
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
    }

    public void onClick(View view) {
        if (view == null) return;
        int color = Color.parseColor(view.getTag().toString());
        SharedPreferences options = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = options.edit();
        editor.putInt(String.valueOf(widgetID), color);
        editor.apply();
        sendWidgetIntent();
        setResult(RESULT_OK, resaultIntent);
        finish();
        Log.d(TAG, "onClick: положили цвет");
    }


    public void sendWidgetIntent() {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        ComponentName widgetComponent = new ComponentName(this, WidgetProvider.class);
        int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        Intent update = new Intent();
        update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        update.setAction(WidgetProvider.ACTION_WIDGET_UPDATE);
        this.sendBroadcast(update);
    }
}