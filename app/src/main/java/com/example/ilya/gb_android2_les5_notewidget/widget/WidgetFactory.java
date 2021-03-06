package com.example.ilya.gb_android2_les5_notewidget.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.example.ilya.gb_android2_les5_notewidget.R;
import com.example.ilya.gb_android2_les5_notewidget.models.tables.TblNotes;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;


public class WidgetFactory implements RemoteViewsFactory {
    private ArrayList<ListItem> listItemList = new ArrayList<>();
    private Context context = null;

    public WidgetFactory(Context context, Intent intent) {
        this.context = context;
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    /**
     * Загружаем список из БД
     */
    public void loadListItemFromDataBase() {
        listItemList.clear();
        List<TblNotes> tblNotesList = new Select().from(TblNotes.class).queryList();
        for (int i = 0; i < tblNotesList.size(); i++) {
            if (!tblNotesList.get(i).noteName.equals("")) {
                ListItem listItem = new ListItem();
                listItem.heading = tblNotesList.get(i).noteName;
                listItem.content = tblNotesList.get(i).noteText;
                listItemList.add(listItem);
            } else {
                break;
            }
        }
    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.list_row);
        ListItem listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.heading, listItem.heading);
        remoteView.setTextViewText(R.id.content, listItem.content);
        //// TODO: 15.03.2017 сделать рендомный подмес картинок
        Intent clickIntent = new Intent();
        clickIntent.putExtra(WidgetProvider.ITEM_POSITION, position);
        remoteView.setOnClickFillInIntent(R.id.itemWidget, clickIntent);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        loadListItemFromDataBase();
    }

    @Override
    public void onDestroy() {
    }
}