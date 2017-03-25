package com.example.ilya.gb_android2_les5_notewidget.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ilya.gb_android2_les5_notewidget.Controller;
import com.example.ilya.gb_android2_les5_notewidget.R;
import com.example.ilya.gb_android2_les5_notewidget.widget.WidgetProvider;

public class NoteListActivity extends AppCompatActivity {

    private static final String TAG = "DEBUGGG";
    public static final String NOTE_POSITION = "NotePosition";
    private ListView lv_notes;
    private Intent mIntent;
    private int notePosition;
    private ArrayAdapter<String> mStringArrayAdapter;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        controller = new Controller(this);
        lv_notes = (ListView) findViewById(R.id.list_notes);
        mStringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1,
                controller.elements);
        lv_notes.setAdapter(mStringArrayAdapter);
        lv_notes.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                //обработчик выделения пунктов списка ActionMode
                Log.d(TAG, "onItemCheckedStateChanged: " + position);
                notePosition = position;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // обработка нажатия на пункт ActionMode
                switch (item.getItemId()) {
                    case R.id.menu_edit:
                        mIntent = new Intent(NoteListActivity.this, NoteActivity.class);
                        mIntent.putExtra(NOTE_POSITION, notePosition);
                        startActivity(mIntent);
                        mode.finish();
                        return true;
                    case R.id.menu_Delete:
                        controller.removeNote();
                        mStringArrayAdapter.notifyDataSetChanged();
                        mode.finish();
                        sendWidgetIntent();
                        return true;
                }
                mode.finish();
                return false;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Устанавливаем для ActionMode меню
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // вызывается при закрытии ActionMode
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // вызывается при обновлении ActionMode
                // true если меню или ActionMode обновлено иначе false
                return false;
            }
        });
        lv_notes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    }

    @Override
    protected void onResume() {
        controller.loadActState();
        mStringArrayAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add: {
                mIntent = new Intent(NoteListActivity.this, NoteActivity.class);
                startActivity(mIntent);
                return true;
            }
            case R.id.menu_clear: {
                controller.noteClear();
                mStringArrayAdapter.notifyDataSetChanged();
                sendWidgetIntent();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
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