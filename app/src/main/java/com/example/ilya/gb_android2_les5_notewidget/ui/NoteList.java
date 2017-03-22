package com.example.ilya.gb_android2_les5_notewidget.ui;

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

public class NoteList extends AppCompatActivity {

    private static final String TAG = "DEBUGGG";
       public static final String NOTE_POSITION = "NotePosition";
    public static final int REQUEST_CODE_NOTE_ADD = 1;
    public static final int REQUEST_CODE_NOTE_EDIT = 2;

    private ListView lv_notes;
    private Intent mIntent;
    private int notePosition;
    private ArrayAdapter<String> mStringArrayAdapter;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        controller= new Controller(this);
        lv_notes = (ListView) findViewById(R.id.list_notes);
        mStringArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_activated_1, controller.elements);
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
                        mIntent = new Intent(NoteList.this, Note.class);
                        mIntent.putExtra(NOTE_POSITION, notePosition);
                        startActivityForResult(mIntent, REQUEST_CODE_NOTE_EDIT);
                        Log.d(TAG, "onActionItemClicked: " + REQUEST_CODE_NOTE_EDIT + "  :  " + notePosition);
                        mode.finish();
                        return true;
                    case R.id.menu_Delete:
                        controller.removeNote();
                        mStringArrayAdapter.notifyDataSetChanged();
                        mode.finish();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add: {
                Log.d(TAG, "onOptionsItemSelected: " + REQUEST_CODE_NOTE_ADD);
                mIntent = new Intent(NoteList.this, Note.class);
                startActivityForResult(mIntent, REQUEST_CODE_NOTE_ADD);
                return true;
            }
            case R.id.menu_clear: {
                controller.noteClear();
                mStringArrayAdapter.notifyDataSetChanged();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // controller.loadActState();
        mStringArrayAdapter.notifyDataSetChanged();


        /*if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_NOTE_ADD: {
                    Log.d(TAG, "onActivityResult: " + REQUEST_CODE_NOTE_ADD);
                    *//*controller.noteName = data.getStringExtra(NOTE_NAME_ADD);
                    controller.noteText = data.getStringExtra(NOTE_TEXT_ADD);
                    if (!controller.noteName.isEmpty()) {
                        controller.addElement(controller.noteName, controller.noteText);

                    }*//*
                    break;
                }

                case REQUEST_CODE_NOTE_EDIT: {
                    Log.d(TAG, "onActivityResult: " + REQUEST_CODE_NOTE_EDIT);

                    break;
                }
            }
        }*/
    }
}
