package com.example.ilya.gb_android2_les5_notewidget.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.ilya.gb_android2_les5_notewidget.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ilya.gb_android2_les5_notewidget.widget.WidgetConfigActivity.PREFERENCES;

/**
 * Created by Ilya on 15.03.2017.
 */

public class WidgetProvider extends AppWidgetProvider {
    public static final String TAG = WidgetProvider.class.getSimpleName();
    private Timer mTimer;
    private Context mContext;
    private int[] mAppWidgetIds;
    private AppWidgetManager mAppWidgetManager;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (mTimer == null) {
            mTimer = new Timer();
            this.mContext=context;
            this.mAppWidgetIds=appWidgetIds.clone();
            this.mAppWidgetManager=appWidgetManager;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, 1);
            calendar.set(Calendar.MILLISECOND, 0);
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateWidget();
                }
            }, calendar.getTime(), 1000);
        }
        Log.d(TAG, "onUpdate: ");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void updateWidget() {
        final int N = mAppWidgetIds.length;
        SimpleDateFormat format = new SimpleDateFormat("k:mm:ss", Locale.getDefault());
        String time = format.format(new Date());
        SharedPreferences options = mContext.getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        for (int i = 0; i < N; ++i) {
            int color = options.getInt(String.valueOf(mAppWidgetIds[i]), -1);
            RemoteViews remoteViews = updateWidgetListView(mContext, mAppWidgetIds[i],color);
            remoteViews.setTextViewText(R.id.time_view, time);
            if (color != -1) {
                remoteViews.setTextColor(R.id.time_view, Color.BLACK);
            }
            mAppWidgetManager.updateAppWidget(mAppWidgetIds[i], remoteViews);
        }
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId, int color) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        remoteViews.setInt(R.id.layout_widget,"setBackgroundColor",color);
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget, svcIntent);
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
}
