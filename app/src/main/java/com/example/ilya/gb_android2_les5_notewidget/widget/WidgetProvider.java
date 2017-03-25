package com.example.ilya.gb_android2_les5_notewidget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.ilya.gb_android2_les5_notewidget.R;
import com.example.ilya.gb_android2_les5_notewidget.ui.NoteActivity;
import com.example.ilya.gb_android2_les5_notewidget.ui.NoteListActivity;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ilya.gb_android2_les5_notewidget.widget.WidgetConfigActivity.PREFERENCES;

/**
 * Created by Ilya on 15.03.2017.
 */


public class WidgetProvider extends AppWidgetProvider {
    public static final String TAG = "DEBUGGG";
    final String ACTION_ON_CLICK = "com.example.ilya.gb_android2_les5_notewidget.itemonclick";
    final static String ITEM_POSITION = "item_position";
    public static final String ACTION_WIDGET_UPDATE = "com.example.ilya.gb_android2_les5_notewidget.widget.ACTION_UPDATE";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
        Log.d(TAG, "onUpdate: ");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.d(TAG, "updateWidget: ");
        SharedPreferences options = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        int color = options.getInt(String.valueOf(appWidgetId), -1);
        RemoteViews rv = updateWidgetListView(context, appWidgetId, color);
        setListClick(rv, context, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId, int color) {
        Log.d(TAG, "updateWidgetListView: ");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        remoteViews.setInt(R.id.layout_widget, "setBackgroundColor", color);
        setListClick(remoteViews, context, appWidgetId);
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.listViewWidget, svcIntent);
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);
        return remoteViews;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        SharedPreferences options = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = options.edit();
        for (int id : appWidgetIds) {
            editor.remove(String.valueOf(id));
        }
        editor.apply();
    }

    /**
     * Устанавливаем клик
     *
     * @param rv
     * @param context
     */
    void setListClick(RemoteViews rv, Context context, int appWidgetId) {
        Intent listClickIntent = new Intent(context, WidgetProvider.class);
        listClickIntent.setAction(ACTION_ON_CLICK);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0, listClickIntent, 0);
        rv.setPendingIntentTemplate(R.id.listViewWidget, listClickPIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent.getAction());
        //AppWidgetManager.ACTION_APPWIDGET_UPDATE
        if (intent.getAction().equalsIgnoreCase(ACTION_WIDGET_UPDATE)) {
            Log.d(TAG, "onReceive: интент прилетел!!!");
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID : ids) {
                updateWidget(context, appWidgetManager, appWidgetID);
            }
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.listViewWidget);
        }

        if (intent.getAction().equalsIgnoreCase(ACTION_ON_CLICK)) {
            int itemPos = intent.getIntExtra(ITEM_POSITION, -1);
            if (itemPos != -1) {
                Toast.makeText(context, "Click on item " + itemPos, Toast.LENGTH_SHORT).show();
                Intent editIntent = new Intent(context, NoteActivity.class);
                editIntent.putExtra(NoteListActivity.NOTE_POSITION, itemPos);
                editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(editIntent);
                Log.d(TAG, "onReceive: " + itemPos);
            }
        }
        super.onReceive(context, intent);
    }
}