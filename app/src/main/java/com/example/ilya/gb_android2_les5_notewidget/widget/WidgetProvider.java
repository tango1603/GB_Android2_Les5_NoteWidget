package com.example.ilya.gb_android2_les5_notewidget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.ilya.gb_android2_les5_notewidget.R;
import com.example.ilya.gb_android2_les5_notewidget.ui.Note;
import com.example.ilya.gb_android2_les5_notewidget.ui.NoteList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ilya.gb_android2_les5_notewidget.widget.WidgetConfigActivity.PREFERENCES;

/**
 * Created by Ilya on 15.03.2017.
 */

public class WidgetProvider extends AppWidgetProvider {
    public static final String TAG = WidgetProvider.class.getSimpleName();
    final String ACTION_ON_CLICK = "com.example.ilya.gb_android2_les5_notewidget.itemonclick";
    final static String ITEM_POSITION = "item_position";
    private Timer mTimer;
    private Context mContext;
    private int[] mAppWidgetIds;
    private AppWidgetManager mAppWidgetManager;
    int mAppWidgetId;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.mContext = context;
        this.mAppWidgetIds = appWidgetIds.clone();
        this.mAppWidgetManager = appWidgetManager;
        updateWidget();


        /*if (mTimer == null) {
            mTimer = new Timer();
            this.mContext = context;
            this.mAppWidgetIds = appWidgetIds.clone();
            this.mAppWidgetManager = appWidgetManager;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, 1);
            calendar.set(Calendar.MILLISECOND, 0);
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateWidget();
                }
            }, calendar.getTime(), 1000);
        }*/
        Log.d(TAG, "onUpdate: ");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void updateWidget() {
        final int N = mAppWidgetIds.length;
        Log.d(TAG, "updateWidget: " + N);
        SimpleDateFormat format = new SimpleDateFormat("k:mm:ss", Locale.getDefault());
        String time = format.format(new Date());
        SharedPreferences options = mContext.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        for (int i = 0; i < N; ++i) {
            int color = options.getInt(String.valueOf(mAppWidgetIds[i]), -1);
            RemoteViews remoteViews = updateWidgetListView(mContext, mAppWidgetIds[i], color);
            remoteViews.setTextViewText(R.id.time_view, time);
            remoteViews.setTextColor(R.id.time_view, Color.BLACK);
            mAppWidgetManager.updateAppWidget(mAppWidgetIds[i], remoteViews);
            mAppWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetIds, R.id.listViewWidget);
        }
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId, int color) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        remoteViews.setInt(R.id.layout_widget, "setBackgroundColor", color);
        setListClick(remoteViews, context);
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.listViewWidget, svcIntent);
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);
        mAppWidgetId = appWidgetId;
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


    void setListClick(RemoteViews rv, Context context) {
        Intent listClickIntent = new Intent(context, WidgetProvider.class);
        listClickIntent.setAction(ACTION_ON_CLICK);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0, listClickIntent, 0);
        rv.setPendingIntentTemplate(R.id.listViewWidget, listClickPIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equalsIgnoreCase(ACTION_ON_CLICK)) {
            int itemPos = intent.getIntExtra(ITEM_POSITION, -1);
            if (itemPos != -1) {
                Toast.makeText(context, "Click on item " + itemPos, Toast.LENGTH_SHORT).show();
                Intent editIntent = new Intent(context, Note.class);
                editIntent.putExtra(NoteList.NOTE_POSITION, itemPos);
                editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(editIntent);
                Log.d(TAG, "onReceive: " + itemPos);
            }
        }
    }
}
