package com.example.ilya.gb_android2_les5_notewidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.ilya.gb_android2_les5_notewidget.models.tables.TblNotes;
import com.example.ilya.gb_android2_les5_notewidget.models.tables.TblNotes_Table;
import com.example.ilya.gb_android2_les5_notewidget.widget.WidgetProvider;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 23.03.2017.
 */

public class Controller {

    private static final String TAG = "DEBUGGG";
    public static List<String> elements = new ArrayList<>();
    public static int notePosition;
    public static List<TblNotes> tblNotesList;
    Context mContext;

    public Controller(Context context) {
        mContext = context;
        loadActState();
    }

    /**
     * Загружаем список из БД
     */
    public void loadActState() {
        Log.d(TAG, "loadActState:");
        elements.clear();
        tblNotesList = new Select().from(TblNotes.class).queryList();
        for (int i = 0; i < tblNotesList.size(); i++) {
            if (!tblNotesList.get(i).noteName.equals("")) {
                elements.add(i, tblNotesList.get(i).noteName);
            } else {
                break;
            }
        }
    }

    /**
     * Сохранение в БД, обновление списка
     *
     * @param noteName имя заметки
     * @param noteText текст заметки
     */
    public void addElement(String noteName, String noteText) {
        elements.add(noteName);
        TblNotes tblNotes = new TblNotes();
        tblNotes.setNoteName(noteName);
        tblNotes.setNoteText(noteText);
        tblNotes.setListPosition(elements.size() - 1);
        tblNotes.save();
        updateWidget(noteName, noteText);
    }

    /**
     * Редактируем заметку
     *
     * @param notePosition позиция в списке
     * @param noteName     имя заметки
     * @param noteText     текст заметки
     */
    public void editElement(int notePosition, String noteName, String noteText) {
        elements.set(notePosition, noteName);
        SQLite.update(TblNotes.class)
                .set(TblNotes_Table.noteName.eq(noteName), TblNotes_Table.noteText.eq(noteText))
                .where(TblNotes_Table.listPosition.is(notePosition))
                .async()
                .execute();
        updateWidget(noteName, noteText);
    }

    /**
     * Удаляем все записи
     */
    public void noteClear() {
        elements.clear();
        SQLite.delete(TblNotes.class)
                .async()
                .execute();
    }

    public void updateWidget(String noteName, String noteText) {
        Log.d(TAG, "updateWidget: ");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.note_widget);
        ComponentName thisWidget = new ComponentName(mContext, WidgetProvider.class);
        if ((noteName.equals(null))&&(noteText.equals(null))) {
            remoteViews.setTextViewText(R.id.heading, noteName);
            remoteViews.setTextViewText(R.id.content, noteText);
        }
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    /**
     * Удаляем выбранную запись
     */
    public void removeNote() {
        elements.remove(notePosition);
        SQLite.delete(TblNotes.class)
                .where(TblNotes_Table.listPosition.is(notePosition))
                .async()
                .execute();
    }






}
